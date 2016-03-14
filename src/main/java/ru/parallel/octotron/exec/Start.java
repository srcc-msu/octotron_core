/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.exception.ExceptionSystemError;
import ru.parallel.octotron.bg_services.ServiceLocator;
import ru.parallel.octotron.services.ExecutionService;
import ru.parallel.utils.AntiDuplicateLoggingFilter;
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

	private final static int MB = 1024 * 1024;

	private static final int SYS_LOG_SIZE = 10*MB; // 10 MB // orly?

	private static final int DUPLICATES_LOGGING_THRESHOLD = 10000; // 10 seconds in ms

	private static void ConfigLogging(String log_dir, boolean antispam)
	{
		try
		{
			FileHandler file_handler
				= new FileHandler(log_dir + "octotron.system.log.%g"
				, SYS_LOG_SIZE, 10, true); // rotate to 10 files, allow append
			file_handler.setFormatter(new SimpleFormatter());

			LOGGER.addHandler(file_handler);
		}
		catch(IOException e)
		{
			LOGGER.log(Level.CONFIG, "could not create log file in: " + log_dir, e);
		}

		if(antispam)
			LOGGER.setFilter(new AntiDuplicateLoggingFilter(DUPLICATES_LOGGING_THRESHOLD));
	}

/**
 * main executable function<br>
 * uses one input parameter from args - path to the configuration file<br>
 * see documentation for details about config file<br>
 * */
	public static void main(String[] args)
	{
		String config_fname;

		if(args.length != 1)
		{
			LOGGER.log(Level.SEVERE, "specify the config file");
			System.exit(1);
		}

		config_fname = args[0];

		LOGGER.log(Level.INFO, "starting Octotron using config file: " + config_fname);

		Context context = CreateContext(config_fname);
		ConfigLogging(context.settings.GetLogDir(), false);

		ExecutionService controller = Create(context);
		Run(controller, context);
	}

	private static Context CreateContext(String config_fname)
	{
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

		return Context.CreateFromConfig(json_config);
	}

	private static ExecutionService Create(Context context)
	{
		ExecutionService controller = null;

		try
		{
			controller = new ExecutionService(context);

			LOGGER.log(Level.INFO, "creating model...");

			controller.CreateFromPython();

			LOGGER.log(Level.INFO, "model statistics:");

			PrintStat();

			LOGGER.log(Level.INFO, "building cache...");

			ServiceLocator.INSTANCE.GetModelService().CreateCache();
		}
		catch(Exception creation_exception)
		{
			LOGGER.log(Level.SEVERE, "could not create the model", creation_exception);

			ServiceLocator.INSTANCE.GetPersistenceService().Clean(); // clean neo4j dir on unsuccessful creation

			if(controller != null)
				controller.Finish();

			System.exit(1);
		}

		return controller;
	}

	private static void PrintStat()
	{
		int model_attributes_count = 0;

		ModelObjectList model_objects = ServiceLocator.INSTANCE.GetModelService().GetModelData().GetAllObjects();
		ModelLinkList model_links = ServiceLocator.INSTANCE.GetModelService().GetModelData().GetAllLinks();

		for(ModelObject obj : model_objects)
		{
			model_attributes_count += obj.GetAttributes().size();
		}

		for(ModelLink link : model_links)
		{
			model_attributes_count += link.GetAttributes().size();
		}

		LOGGER.log(Level.INFO, "created model objects: " + model_objects.size());
		LOGGER.log(Level.INFO, "created model links: " + model_links.size());
		LOGGER.log(Level.INFO, "created model attributes: " + model_attributes_count);
	}

/**
 * created db, start main loop and shutdown, when finished<br>
 * all errors are printed and may be reported by special scripts<br>
 * */
	private static void Run(ExecutionService controller, Context context)
	{
		try
		{
			LOGGER.log(Level.INFO, "building rule dependencies and running the model");

			ServiceLocator.INSTANCE.GetModelService().Operate();
			ServiceLocator.INSTANCE.GetHttpService();

			ProcessStart(context);
		}
		catch(Exception start_exception)
		{
			ProcessCrash(context, start_exception, "start");

			if(controller != null)
				controller.Finish();

			System.exit(1);
		}

//-------- main loop
		Exception loop_exception = MainLoop(context, controller);

		if(loop_exception != null)
		{
			ProcessCrash(context, loop_exception, "mainloop");
		}

//-------- shutdown
		Exception shutdown_exception = Shutdown(controller);

		if(shutdown_exception != null)
		{
			ProcessCrash(context, shutdown_exception, "shutdown");
		}
	}

/**
 * run the main program loop<br>
 * if it crashes - returns exception, otherwise returns nothing<br>
 * */
	private static Exception MainLoop(Context context, ExecutionService controller)
	{
		LOGGER.log(Level.INFO, "main loop started");

		try
		{
			while(!ServiceLocator.INSTANCE.GetRuntimeService().ShouldExit())
			{
				controller.Process();

				ServiceLocator.INSTANCE.GetPersistenceService().Check();
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
	public static Exception Shutdown(ExecutionService controller)
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
		String script = context.settings.GetScriptByKeyOrNull("on_start");

		if(script != null)
			ServiceLocator.INSTANCE.GetScriptService().ExecSilent(script);
	}

/**
 * reaction to normal execution finish<br>
 * */
	private static void ProcessFinish(Context context)
		throws ExceptionSystemError
	{
		String script = context.settings.GetScriptByKeyOrNull("on_finish");

		if(script != null)
			ServiceLocator.INSTANCE.GetScriptService().ExecSilent(script);
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

		String fname = String.format("/octotron.crash.log.%s.%s.%d"
			, suffix, JavaUtils.GetDate(), JavaUtils.GetTimestamp());

		File error_file = new File(context.settings.GetLogDir() + fname);
		String error_fname = error_file.getAbsolutePath();

		try
		{
			FileUtils.SaveToFile(error_fname, error);
		}
		catch(Exception e) // giving up now - exception during exception processing..
		{
			LOGGER.log(Level.SEVERE, "error during crash notification", e);
		}

		try
		{
			String script = context.settings.GetScriptByKeyOrNull("on_crash");

			if(script != null)
				ServiceLocator.INSTANCE.GetScriptService().ExecSilent(script, error_fname);
		}
		catch(ExceptionSystemError e) // giving up now - exception during exception processing..
		{
			LOGGER.log(Level.SEVERE, "error during crash notification", e);
		}
	}
}
