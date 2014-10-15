/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.utils.JavaUtils;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RuntimeService
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private static final double free_space_mb_thr = 1024; // 1GB in MB

	private static SelfTest tester = null;

	public static Map<String, Object> PerformSelfTest(ExecutionController controller)
	{
		if(tester == null)
		{
			tester = new SelfTest();
			tester.Init(controller);
		}

		boolean graph_test = tester.Test(controller);

		long free_space = new File("/").getFreeSpace();
		String free_space_res;

		long free_space_mb = free_space / 1024 / 1024;

		Map<String, Object> map = new HashMap<>();

		map.put("graph_test", graph_test);
		map.put("disk_space_MB", free_space_mb);
		map.put("disk_test", free_space_mb > free_space_mb_thr);

		return map;
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

	public static List<Map<String, Object>> CheckModTime(Context context, long interval)
	{
		List<Map<String, Object>> result = new LinkedList<>();

		ModelList<ModelEntity, ?> list = context.model_data.GetAllEntities();
		long cur_time = JavaUtils.GetTimestamp();

		for(ModelEntity entity : list)
		{
			for(SensorAttribute sensor : entity.GetSensor())
			{
				long diff = cur_time - sensor.GetCTime();

				if(diff > interval)
				{
					Map<String, Object> map = new HashMap<>();

					map.put("parent AID", entity.GetID());
					map.put("sensor name", sensor.GetName());
					map.put("sensor AID", sensor.GetID());
					map.put("not changed", diff);

					result.add(map);
				}
			}
		}

		return result;
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
				LOGGER.log(Level.WARNING, "failed to close the version file");
			}
		}

		return version;
	}
}
