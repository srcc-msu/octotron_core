/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.exec;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.impl.PersistenStorage;
import main.java.ru.parallel.octotron.logic.ExecutionControler;
import main.java.ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import main.java.ru.parallel.utils.FileUtils;
import main.java.ru.parallel.utils.JavaUtils;

/**
 * main executable function<br>
 * */
public class StartOctotron
{
	static private final int EXIT_ERROR = 1;
	private static final int PROCESS_CHUNK = 1024; // seems ok

/**
 * main executable function<br>
 * uses one input parameter from args - path to the configuration file<br>
 * see documentation for details about config file<br>
 * */
	public static void main(String[] args)
	{
		String fname = null; // insert your config file path here - for debug purpose

		if(fname == null)
		{
			if(args.length != 1)
			{
				System.err.println("specify the config file");
				System.exit(EXIT_ERROR);
			}

			fname = args[0];
		}

		System.out.println("statring octotron using config file: " + fname);

		GlobalSettings settings = null;

		try
		{
			String json_config = FileUtils.FileToString(fname);
			settings = new GlobalSettings(json_config);
			CheckConfig(settings);
		}
		catch(ExceptionSystemError e)
		{
			System.err.println(e.getMessage());
			System.exit(EXIT_ERROR);
		}

		Run(settings);
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
		ExecutionControler exec_control = null;

		String path = settings.GetDbPath() + settings.GetDbName();

// --- create
		try
		{
			graph = new Neo4jGraph(path + "_neo4j", Neo4jGraph.Op.LOAD);

			exec_control = new ExecutionControler(graph, new GraphService(graph), settings);

			PersistenStorage.INSTANCE.Load(path);

			ProcessStart(settings);
		}
		catch(Exception start_exception)
		{
			ProcessCrash(settings, start_exception, "start");
			return;
		}

// --- main loop
		Exception loop_exception = MainLoop(settings, exec_control);

		if(loop_exception != null)
		{
			ProcessCrash(settings, loop_exception, "mainloop");
		}

// --- shutdown
		Exception shutdown_exception = Shutdown(settings, graph, exec_control);

		if(shutdown_exception != null)
		{
			ProcessCrash(settings, shutdown_exception, "shutdown");
		}
	}

/**
 * run the main program loop<br>
 * if it crashes - returns exception, otherwise returns nothing<br>
 * @param exec_control
 * */
	private static Exception MainLoop(GlobalSettings settings, ExecutionControler exec_control)
	{
		System.out.println("main loop started");

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
	public static Exception Shutdown(GlobalSettings settings, Neo4jGraph graph, ExecutionControler exec_control)
	{
		String path = settings.GetDbPath() + settings.GetDbName();

		try
		{
			PersistenStorage.INSTANCE.Save(path);

			if(exec_control != null)
				exec_control.Finish();

			if(graph != null)
				graph.Shutdown();
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
			error += elem.toString() + System.lineSeparator();

		System.err.println(error);

		String error_file = "crash_" + JavaUtils.GetDate() + "_" + JavaUtils.GetTimestamp() + "_" + suffix + ".txt";

		try
		{
			FileUtils.SaveToFile(error_file, error);
		}
		catch (Exception e) // giving up now - exception during exception processing..
		{
			System.err.println(e);
		}

		try
		{
			String script = settings.GetScriptByKey("on_crash");

			if(script != null)
				FileUtils.ExecSilent(true, script, error_file);
		}
		catch (ExceptionSystemError e) // giving up now - exception during exception processing..
		{
			System.err.println(e);
		}
	}
}
