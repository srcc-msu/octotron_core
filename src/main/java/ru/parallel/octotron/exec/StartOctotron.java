/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import java.io.File;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.impl.PersistenStorage;
import ru.parallel.octotron.logic.ExecutionController;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import ru.parallel.utils.FileUtils;
import ru.parallel.utils.JavaUtils;

/**
 * main executable function<br>
 * */
public class StartOctotron
{
	private static final int EXIT_ERROR = 1;
	private static final int PROCESS_CHUNK = 1024; // seems ok

/**
 * main executable function<br>
 * uses one input parameter from args - path to the configuration file<br>
 * see documentation for details about config file<br>
 * */
	public static void main(String[] args)
	{
		String fname = "cheb_src/config.json"; // insert your config file path here - for debug purpose

		if(fname == null)
		{
			if(args.length != 1)
			{
				System.err.println("specify the config file");
				System.exit(StartOctotron.EXIT_ERROR);
			}

			fname = args[0];
		}

		System.out.println("statring octotron using config file: " + fname);

		GlobalSettings settings = null;

		try
		{
			String json_config = FileUtils.FileToString(fname);
			settings = new GlobalSettings(json_config);
			StartOctotron.CheckConfig(settings);
		}
		catch(ExceptionSystemError e)
		{
			System.err.println(e.getMessage());
			System.exit(StartOctotron.EXIT_ERROR);
		}

		StartOctotron.Run(settings);
	}

/**
 * compare hashes to check if the new config does not match the old one<br>
 * */
	private static void CheckConfig(GlobalSettings settings)
		throws NumberFormatException, ExceptionSystemError
	{
		String path = settings.GetDbPath() + settings.GetDbName();
		int old_hash = Integer.parseInt(FileUtils.FileToString(path + DBCreator.HASH_FILE));

		if(settings.GetHash() != old_hash)
		{
			System.err.println("**********************************************************");
			System.err.println("*                        WARNING                         *");
			System.err.println("**********************************************************");
			System.err.println();
			System.err.println("config file has been changed since database creation");
			System.err.println("consistency is not guaranteed");
			System.err.println();
			System.err.println("**********************************************************");
		}
	}

/**
 * created db, start main loop and shutdown, when finished<br>
 * all errors are printed and may be reported by special scripts<br>
 * */
	private static void Run(GlobalSettings settings)
	{
		Neo4jGraph graph = null;
		ExecutionController exec_control = null;

		String path = settings.GetDbPath() + settings.GetDbName();

// --- create
		try
		{
			graph = new Neo4jGraph(path + "_neo4j", Neo4jGraph.Op.LOAD);

			exec_control = new ExecutionController(graph, new GraphService(graph), settings);

			PersistenStorage.INSTANCE.Load(path);

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
		Exception shutdown_exception = StartOctotron.Shutdown(settings, graph, exec_control);

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
		System.out.println("main loop started");

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
	public static Exception Shutdown(GlobalSettings settings, Neo4jGraph graph, ExecutionController exec_control)
	{
		String path = settings.GetDbPath() + settings.GetDbName();

		try
		{
			if(exec_control != null)
				exec_control.Finish();

			if(graph != null)
				graph.Shutdown();

			PersistenStorage.INSTANCE.Save(path);
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
 * creates the file with excpetion info<br>
 * */
	private static void ProcessCrash(GlobalSettings settings, Exception catched_exception, String suffix)
	{
		String error = catched_exception.getLocalizedMessage() + System.lineSeparator();

		for(StackTraceElement elem : catched_exception.getStackTrace())
			error += elem + System.lineSeparator();

		System.err.println(error);

		File error_file = new File("crash_" + JavaUtils.GetDate()
			+ "_" + JavaUtils.GetTimestamp() + "_" + suffix + ".txt");
		String error_fname = error_file.getAbsolutePath();

		try
		{
			FileUtils.SaveToFile(error_fname, error);
		}
		catch (Exception e) // giving up now - exception during exception processing..
		{
			System.err.println(e);
		}

		try
		{
			String script = settings.GetScriptByKey("on_crash");

			if(script != null)
				FileUtils.ExecSilent(true, script, error_fname);
		}
		catch (ExceptionSystemError e) // giving up now - exception during exception processing..
		{
			System.err.println(e);
		}
	}
}
