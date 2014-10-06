/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.logic.ExecutionController;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
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
public class StartOctotron
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private static final int EXIT_ERROR = 1;
	private static final int PROCESS_CHUNK = 1024; // seems ok
	private static final int SYS_LOG_SIZE = 10*1024*1024; // 10 MB

	private static void ConfigLogging()
	{
		try
		{
			FileHandler file_handler
				= new FileHandler("log/octotron_%g.log"
					, SYS_LOG_SIZE, 1, true); // rotate to 1 file, allow append
			file_handler.setFormatter(new SimpleFormatter());

			LOGGER.addHandler(file_handler);
		}
		catch(IOException e)
		{
			LOGGER.log(Level.CONFIG, "could not create log file", e);
		}
	}


// for debugging
	private static String debug_config = null;
	private static boolean bootstrap = false;

/**
 * main executable function<br>
 * uses one input parameter from args - path to the configuration file<br>
 * see documentation for details about config file<br>
 * */
	public static void main(String[] args)
	{
		ConfigLogging();

		String config_fname;

		if(debug_config == null)
		{
			if (args.length != 1)
			{
				LOGGER.log(Level.SEVERE, "specify the config file");
				System.exit(StartOctotron.EXIT_ERROR);
			}

			config_fname = args[0];
		}
		else
			config_fname = debug_config;

		LOGGER.log(Level.INFO, "starting Octotron using config file: " + config_fname);

		GlobalSettings settings = null;

		try
		{
			String json_config = FileUtils.FileToString(config_fname);
			settings = new GlobalSettings(json_config);
			StartOctotron.CheckConfig(settings);
		}
		catch(ExceptionSystemError e)
		{
			LOGGER.log(Level.SEVERE, "could not load config file", e);
			System.exit(StartOctotron.EXIT_ERROR);
		}

		StartOctotron.Run(settings);
	}

/**
 * compare hashes to check if the new config does not match the old one<br>
 * */
	private static void CheckConfig(GlobalSettings settings)
		throws ExceptionSystemError
	{
		String path = settings.GetDbPath() + settings.GetDbName();
		int old_hash = Integer.parseInt(FileUtils.FileToString(path + DBCreator.HASH_FILE));

		if(settings.GetHash() != old_hash)
			LOGGER.log(Level.CONFIG, "config file has been changed since database creation consistency is not guaranteed");
	}

/**
 * created db, start main loop and shutdown, when finished<br>
 * all errors are printed and may be reported by special scripts<br>
 * */
	private static void Run(GlobalSettings settings)
	{
		Neo4jGraph graph;
		ExecutionController exec_control = null;

		String path = settings.GetDbPath() + settings.GetDbName();

// --- create
		try
		{
			graph = new Neo4jGraph(path + "_neo4j", Neo4jGraph.Op.LOAD, bootstrap);
			ModelService.Init(graph);

			exec_control = new ExecutionController(settings);

			StartOctotron.ProcessStart(settings);
		}
		catch(Exception start_exception)
		{
			StartOctotron.ProcessCrash(settings, start_exception, "start");

			if(exec_control != null)
				exec_control.Finish();

			return;
		}

// --- main loop
		Exception loop_exception = StartOctotron.MainLoop(settings, exec_control);

		if(loop_exception != null)
		{
			StartOctotron.ProcessCrash(settings, loop_exception, "mainloop");
		}

// --- shutdown
		Exception shutdown_exception = StartOctotron.Shutdown(settings, exec_control);

		if(shutdown_exception != null)
		{
			StartOctotron.ProcessCrash(settings, shutdown_exception, "shutdown");
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
				exec_control.Process(StartOctotron.PROCESS_CHUNK); // it may sleep inside
			}

			StartOctotron.ProcessFinish(settings);
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
		String path = settings.GetDbPath() + settings.GetDbName();

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
