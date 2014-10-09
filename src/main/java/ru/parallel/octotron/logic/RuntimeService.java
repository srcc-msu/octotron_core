package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RuntimeService
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private static SelfTest tester;

	private static final double free_space_mb_thr = 1024; // 1GB in MB

	public static String PerformSelfTest()
	{
		tester.Init();

		boolean graph_test = tester.Test();

		long free_space = new File("/").getFreeSpace();
		String free_space_res;

		long free_space_mb = free_space / 1024 / 1024;

		free_space_res = (free_space_mb > free_space_mb_thr) + " ( " + free_space_mb + "MB free )";

		StringBuilder result = new StringBuilder();

		result.append("graph test: ").append(graph_test).append(System.lineSeparator());
		result.append("disk space: ").append(free_space_res).append(System.lineSeparator());

		return result.toString();
	}

	/**
	 * create snapshot of all reactions with failed conditions<br>
	 * and get their description<br>
	 * */
	public static String MakeSnapshot()
	{
		throw new ExceptionModelFail("NIY");
		/*
		StringBuilder result = new StringBuilder();

		((Neo4jGraph)graph).GetTransaction().ForceWrite();

		for(ModelObject object : ModelService.GetAllObjects())
		{
			for(Response response : object.GetCurrentReactions())
			{
				PreparedResponse prepared_response = new PreparedResponse(response, object, JavaUtils.GetTimestamp());

				String descr = prepared_response.GetFullString();

				result.append(descr).append(System.lineSeparator());
			}
		}

		return result.toString();*/
	}

	// TODO: entities?
	public static String CheckModTime(long interval)
	{
		throw new ExceptionModelFail("NIY");

		/*StringBuilder result = new StringBuilder();

		ModelObjectList list = ModelService.GetAllObjects();

		((Neo4jGraph)graph).GetTransaction().ForceWrite();

		long cur_time = JavaUtils.GetTimestamp();

		for(ModelObject obj : list)
		{
			for(IMetaAttribute attr : obj.GetMetaAttributes())
			{
				long diff = cur_time - attr.GetCTime();

				if(diff > interval)
				{
					long aid = obj.GetAttribute("AID").GetLong();
					String type = obj.GetAttribute("type").GetString();

					result.append("[AID: ").append(aid)
						.append(", type: ").append(type)
						.append(", attribute: ").append(attr.GetName()).append("]: ")
						.append("last change: ").append(diff).append(" secs ago")
						.append(System.lineSeparator());
				}
			}
		}

		return result.toString();*/
	}

	public static Map<String, String> GetVersion()
		throws ExceptionSystemError
	{
		InputStream stream = RuntimeService.class.getResourceAsStream("/VERSION");

		if(stream == null)
			throw new ExceptionSystemError("missing VERSION file");

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		Map<String, String> version = new HashMap<>();

		try
		{
			String line;
			while((line = reader.readLine()) != null)
			{
				String[] pair = line.split("=");

				version.put(pair[0], pair[1]);
			}
		}
		catch (IOException e)
		{
			throw new ExceptionSystemError(e);
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch (IOException e)
			{
				LOGGER.log(Level.SEVERE, "failed to close the version file");
			}
		}

		return version;
	}
}
