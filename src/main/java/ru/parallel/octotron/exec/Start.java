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
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.logic.ExecutionController;
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

	private static final int PROCESS_CHUNK = 1024; // seems ok

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

		GlobalSettings settings = null;

		try
		{
			String json_config = FileUtils.FileToString(config_fname);
			settings = new GlobalSettings(json_config);
		}
		catch(ExceptionSystemError e)
		{
			LOGGER.log(Level.SEVERE, "could not load config file", e);
			System.exit(1);
		}

		try
		{
			Create(settings);
		}
		catch(Exception creation_exception)
		{
			LOGGER.log(Level.SEVERE, "could not create the model", creation_exception);
			System.exit(1);
		}

		ModelService.Get().Operate();

		Run(settings);
	}

	public static void Begin(GlobalSettings settings)
		throws ExceptionSystemError
	{
		if(settings.IsDb())
		{
			if(FileUtils.IsDirEmpty(settings.GetDbPath() + settings.GetModelName()))
			{
				ModelService.Init(ModelService.EMode.CREATION, settings.GetDbPath(), settings.GetModelName());
				LOGGER.log(Level.INFO, "No DB found, creating a new in: " + settings.GetDbPath());
			}
			else
			{
				ModelService.Init(ModelService.EMode.LOAD, settings.GetDbPath(), settings.GetModelName());
				LOGGER.log(Level.INFO, "DB found, attempting to use data from: " + settings.GetDbPath());
			}
		}
		else
		{
			ModelService.Init(ModelService.EMode.CREATION);
			LOGGER.log(Level.INFO, "No DB settings, starting Octotron without DB");
			LOGGER.log(Level.WARNING, "All data will be lost when execution ends!");
		}
	}

	public static void Create(GlobalSettings settings)
		throws ExceptionSystemError
	{
		Begin(settings);

		PythonInterpreter interpreter = new PythonInterpreter(null, new PySystemState());

		PySystemState sys = Py.getSystemState();

		sys.path.append(new PyString(settings.GetModelPath()));

		if(settings.GetSysPath() != null)
			sys.path.append(new PyString(settings.GetSysPath()));

		ModelService.Init(ModelService.EMode.CREATION);

		LOGGER.log(Level.INFO, "Creating model...");

		interpreter.execfile(settings.GetModelPath() + '/' + settings.GetModelMain());

		LOGGER.log(Level.INFO, "done");

		End(settings);
	}

	public static void PrintStat()
	{
		int model_attributes_count = 0;

		ModelObjectList model_objects = ModelService.Get().GetAllObjects();
		ModelLinkList model_links = ModelService.Get().GetAllLinks();

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

	public static void End(GlobalSettings settings)
	{
		LOGGER.log(Level.INFO, "Building rule dependencies...");
		ModelService.Get().MakeRuleDependencies();

		PrintStat();

		LOGGER.log(Level.INFO, "done");

// -------------

		LOGGER.log(Level.INFO, "Building cache...");

		ModelService.Get().EnableObjectIndex("AID");
		ModelService.Get().EnableLinkIndex("AID");

		LOGGER.log(Level.INFO, "enabled object cache: AID");
		LOGGER.log(Level.INFO, "enabled link cache: AID");

		for(String attr : settings.GetObjectIndex())
		{
			ModelService.Get().EnableObjectIndex(attr);
			LOGGER.log(Level.INFO, "enabled object cache: " + attr);
		}

		for(String attr : settings.GetLinkIndex())
		{
			ModelService.Get().EnableLinkIndex(attr);
			LOGGER.log(Level.INFO, "enabled link cache: " + attr);
		}

		LOGGER.log(Level.INFO, "done");
	}

/**
 * created db, start main loop and shutdown, when finished<br>
 * all errors are printed and may be reported by special scripts<br>
 * */
	private static void Run(GlobalSettings settings)
	{
		ExecutionController exec_control = null;
// --- create
		try
		{
			exec_control = new ExecutionController(settings);

			ProcessStart(settings);
		}
		catch(Exception start_exception)
		{
			ProcessCrash(settings, start_exception, "start");

			if(exec_control != null)
				exec_control.Finish();

			System.exit(1);
		}

// --- main loop
		Exception loop_exception = MainLoop(settings, exec_control);

		if(loop_exception != null)
		{
			ProcessCrash(settings, loop_exception, "mainloop");
		}

// --- shutdown
		Exception shutdown_exception = Shutdown(settings, exec_control);

		if(shutdown_exception != null)
		{
			ProcessCrash(settings, shutdown_exception, "shutdown");
		}
	}

/**
 * run the main program loop<br>
 * if it crashes - returns exception, otherwise returns nothing<br>
 * */
	private static Exception MainLoop(GlobalSettings settings, ExecutionController exec_control)
	{
		LOGGER.log(Level.INFO, "main loop started");

		try
		{
			while(!exec_control.ShouldExit())
			{
				exec_control.Process(PROCESS_CHUNK); // it may sleep inside
			}

			ProcessFinish(settings);
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
	public static Exception Shutdown(GlobalSettings settings, ExecutionController exec_control)
	{
		String path = settings.GetDbPath() + settings.GetModelMain();

		try
		{
			if(exec_control != null)
				exec_control.Finish();

			ModelService.Finish();
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
	private static void ProcessStart(GlobalSettings settings)
		throws ExceptionSystemError
	{
		String script = settings.GetScriptByKey("on_start");

		if(script != null)
			FileUtils.ExecSilent(true, script);
	}

/**
 * reaction to normal execution finish<br>
 * */
	private static void ProcessFinish(GlobalSettings settings)
		throws ExceptionSystemError
	{
		String script = settings.GetScriptByKey("on_finish");

		if(script != null)
			FileUtils.ExecSilent(true, script);
	}

/**
 * reaction to an exception<br>
 * creates the file with exception info<br>
 * */
	private static void ProcessCrash(GlobalSettings settings, Exception catched_exception, String suffix)
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
			String script = settings.GetScriptByKey("on_crash");

			if(script != null)
				FileUtils.ExecSilent(true, script, error_fname);
		}
		catch (ExceptionSystemError e) // giving up now - exception during exception processing..
		{
			LOGGER.log(Level.SEVERE, "error during crash notification", e);
		}
	}
}
