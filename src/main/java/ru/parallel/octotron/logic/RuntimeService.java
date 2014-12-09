/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.impl.Timeout;
import ru.parallel.octotron.core.model.ModelData;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.utils.JavaUtils;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RuntimeService
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private static final double free_space_mb_thr = 1024; // 1GB in MB

	private static SelfTest tester = null;

	public static void InitSelfTest(ModelService model_service)
	{
		if(tester == null)
		{
			tester = new SelfTest();
			tester.Init(model_service);
		}
		else
			throw new ExceptionModelFail("internal error: self test has been initialized already");
	}

	public static Map<String, Object> PerformSelfTest(ExecutionController controller)
	{
		if(tester == null)
			throw new ExceptionModelFail("internal error: self test is not initialized");

		boolean graph_test = tester.Test(controller);

		long free_space = new File("/").getFreeSpace();

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
	public static List<Map<String, Object>> MakeSnapshot(ModelData model_data, boolean verbose)
	{
		List<Map<String, Object>> result = new LinkedList<>();

		for(ModelEntity entity : model_data.GetAllEntities())
		{
			for(PreparedResponse response : entity.GetPreparedResponses())
			{
				result.add(response.GetRepresentation(verbose));
			}
		}

		return result;
	}

	public static List<Map<String, Object>> CheckModTime(Context context, long interval)
	{
		List<Map<String, Object>> result = new LinkedList<>();

		long cur_time = JavaUtils.GetTimestamp();

		for(ModelEntity entity : context.model_data.GetAllEntities())
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

	public static Map<String, Object> GetVersion()
		throws ExceptionSystemError
	{
		InputStream stream = RuntimeService.class.getResourceAsStream("/VERSION");

		if(stream == null)
			throw new ExceptionSystemError("missing VERSION file");

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		Map<String, Object> version = new HashMap<>();

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
