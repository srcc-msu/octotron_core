/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import org.python.core.Py;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.logic.RuntimeService;
import ru.parallel.utils.FileUtils;
import ru.parallel.utils.JavaUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * main executable function<br>
 * */
public class Start
{
	private final static Logger LOGGER = Logger.getLogger("octotron");
	private static final int SYS_LOG_SIZE = 10*1024*1024; // 10 MB
	private static final String LOG_DIR = "log/";

	private static void ConfigLogging()
	{
		try
		{
			FileHandler file_handler
				= new FileHandler(LOG_DIR + "octotron_%g.log"
				, SYS_LOG_SIZE, 1, true); // rotate to 1 file, allow append
			file_handler.setFormatter(new SimpleFormatter());

			LOGGER.addHandler(file_handler);
		}
		catch(IOException e)
		{
			LOGGER.log(Level.CONFIG, "could not create log file in: " + LOG_DIR, e);
		}
	}

/**
 * main executable function<br>
 * uses one input parameter from args - path to the configuration file<br>
 * see documentation for details about config file<br>
 * */
	public static void main(String[] args)
	{
		ConfigLogging();

		String config_fname;

		if(args.length != 1)
		{
			LOGGER.log(Level.SEVERE, "specify the config file");
			System.exit(1);
		}

		config_fname = args[0];

		LOGGER.log(Level.INFO, "starting Octotron using config file: " + config_fname);

		String json_config = null;
		try
		{
			json_config = FileUtils.FileToString(config_fname);
		}
		catch(ExceptionSystemError e)
		{
			LOGGER.log(Level.SEVERE, "could not load config file", e);
			System.exit(1);
		}

		Context context = null;

		try
		{
			context = Context.CreateFromConfig(json_config);
			CreateFromPython(context);
			PrintStat(context);
			CreateCache(context);
		}
		catch(Exception creation_exception)
		{
			LOGGER.log(Level.SEVERE, "could not create the model", creation_exception);
			System.exit(1);
		}

		Run(context);
	}

	private static void CreateFromPython(Context context)
		throws ExceptionSystemError
	{
		PythonInterpreter interpreter = new PythonInterpreter(null, new PySystemState());

		PySystemState sys = Py.getSystemState();

		sys.path.append(new PyString(context.settings.GetModelPath()));

		if(context.settings.GetSysPath() != null)
			sys.path.append(new PyString(context.settings.GetSysPath()));

		LOGGER.log(Level.INFO, "Creating model...");

// some magic to pass context to all python modules
		interpreter.set("context", context);
		interpreter.exec("import __builtin__");
		interpreter.exec("__builtin__.context = context");

		interpreter.execfile(context.settings.GetModelPath() + '/' + context.settings.GetModelMain());

		LOGGER.log(Level.INFO, "done");
	}

	private static void PrintStat(Context context)
	{
		int model_attributes_count = 0;

		ModelObjectList model_objects = context.model_data.GetAllObjects();
		ModelLinkList model_links = context.model_data.GetAllLinks();

		for(ModelObject obj : model_objects)
		{
			model_attributes_count += obj.GetAttributes().size();
		}

		for(ModelLink link : model_links)
		{
			model_attributes_count += link.GetAttributes().size();
		}

		LOGGER.log(Level.INFO, "Created model objects: " + model_objects.size());
		LOGGER.log(Level.INFO, "Created model links: " + model_links.size());
		LOGGER.log(Level.INFO, "Created model attributes: " + model_attributes_count);
	}

	private static void CreateCache(Context context)
	{
		LOGGER.log(Level.INFO, "Building cache...");

		context.model_service.EnableObjectIndex("AID");
		context.model_service.EnableLinkIndex("AID");

		LOGGER.log(Level.INFO, "enabled object cache: AID");
		LOGGER.log(Level.INFO, "enabled link cache: AID");

		for(String attr : context.settings.GetObjectIndex())
		{
			context.model_service.EnableObjectIndex(attr);
			LOGGER.log(Level.INFO, "enabled object cache: " + attr);
		}

		for(String attr : context.settings.GetLinkIndex())
		{
			context.model_service.EnableLinkIndex(attr);
			LOGGER.log(Level.INFO, "enabled link cache: " + attr);
		}

		LOGGER.log(Level.INFO, "done");
	}

/**
 * created db, start main loop and shutdown, when finished<br>
 * all errors are printed and may be reported by special scripts<br>
 * */
	private static void Run(Context context)
	{
		LOGGER.log(Level.INFO, "Building rule dependencies and running the model");

		ExecutionController controller = null;
// --- create
		try
		{
			controller = new ExecutionController(context);

			ProcessStart(context);
		}
		catch(Exception start_exception)
		{
			ProcessCrash(context, start_exception, "start");

			if(controller != null)
				controller.Finish();

			System.exit(1);
		}

		RuntimeService.PerformSelfTest(controller);

		context.model_service.Operate();

// --- main loop
		Exception loop_exception = MainLoop(context, controller);

		if(loop_exception != null)
		{
			ProcessCrash(context, loop_exception, "mainloop");
		}

// --- shutdown
		Exception shutdown_exception = Shutdown(context, controller);

		if(shutdown_exception != null)
		{
			ProcessCrash(context, shutdown_exception, "shutdown");
		}
	}

/**
 * run the main program loop<br>
 * if it crashes - returns exception, otherwise returns nothing<br>
 * */
	private static Exception MainLoop(Context context, ExecutionController controller)
	{
		LOGGER.log(Level.INFO, "main loop started");

		try
		{
			while(!controller.ShouldExit())
			{
				controller.Process();
			}

			ProcessFinish(context);
		}
		catch(Exception e)
		{
			return e;
		}

		return null;
	}

/**
 * shutdown the graph and all execution processes<br>
 * */
	public static Exception Shutdown(Context context, ExecutionController controller)
	{
		try
		{
			controller.Finish();
		}
		catch(Exception e)
		{
			return e;
		}

		return null;
	}

/**
 * reaction to normal execution start<br>
 * */
	private static void ProcessStart(Context context)
		throws ExceptionSystemError
	{
		String script = context.settings.GetScriptByKey("on_start");

		if(script != null)
			FileUtils.ExecSilent(script);
	}

/**
 * reaction to normal execution finish<br>
 * */
	private static void ProcessFinish(Context context)
		throws ExceptionSystemError
	{
		String script = context.settings.GetScriptByKey("on_finish");

		if(script != null)
			FileUtils.ExecSilent(script);
	}

/**
 * reaction to an exception<br>
 * creates the file with exception info<br>
 * */
	private static void ProcessCrash(Context context, Exception catched_exception, String suffix)
	{
		LOGGER.log(Level.SEVERE, "Octotron crashed during " + suffix, catched_exception);

		String error = catched_exception.getLocalizedMessage() + System.lineSeparator();

		for(StackTraceElement elem : catched_exception.getStackTrace())
			error += elem + System.lineSeparator();

		File error_file = new File("log/crash_" + JavaUtils.GetDate()
			+ "_" + JavaUtils.GetTimestamp() + "_" + suffix + ".txt");
		String error_fname = error_file.getAbsolutePath();

		try
		{
			FileUtils.SaveToFile(error_fname, error);
		}
		catch (Exception e) // giving up now - exception during exception processing..
		{
			LOGGER.log(Level.SEVERE, "error during crash notification", e);
		}

		try
		{
			String script = context.settings.GetScriptByKey("on_crash");

			if(script != null)
				FileUtils.ExecSilent(script, error_fname);
		}
		catch (ExceptionSystemError e) // giving up now - exception during exception processing..
		{
			LOGGER.log(Level.SEVERE, "error during crash notification", e);
		}
	}
}
