/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.services.impl;

import ru.parallel.octotron.core.attributes.impl.Sensor;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.logic.Statistics;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.octotron.services.Service;
import ru.parallel.octotron.services.ServiceLocator;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class RuntimeService extends Service
{
	public final Statistics stat = new Statistics();
	private boolean exit;
	private boolean db_failed = false;

	public RuntimeService(Context context)
	{
		super(context);
	}

	public void NotifyDbFailed() { db_failed = true; }
	public boolean IsDbFailed() { return db_failed; }


	public void SetExit(boolean exit)
	{
		this.exit = exit;
	}
	public boolean ShouldExit()
	{
		return exit;
	}

	public Statistics GetStat()
	{
		return stat;
	}

	/**
	 * create snapshot of all reactions with failed conditions<br>
	 * and get their description<br>
	 * */
	public List<Map<String, Object>> MakeSnapshot(boolean verbose)
	{
		List<Map<String, Object>> result = new LinkedList<>();

		for(ModelEntity entity : ServiceLocator.INSTANCE.GetModelService().GetModelData().GetAllEntities())
		{
			for(PreparedResponse response : entity.GetPreparedResponses())
			{
				result.add(response.GetRepresentation(verbose));
			}
		}

		return result;
	}

	public List<Map<String, Object>> CheckTimeout()
	{
		List<Map<String, Object>> result = new LinkedList<>();

		for(ModelEntity entity : ServiceLocator.INSTANCE.GetModelService().GetModelData().GetAllEntities())
		{
			for(Sensor sensor : entity.GetSensor())
			{
				PreparedResponse prepared_response
					= sensor.GetTimeoutReaction().GetPreparedResponseOrNull();

				if(prepared_response != null)
					result.add(prepared_response.GetShortRepresentation());
			}
		}

		return result;
	}

	public Map<String, Object> GetVersion()
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
		catch(IOException e)
		{
			throw new ExceptionSystemError(e);
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(IOException e)
			{
				LOGGER.log(Level.WARNING, "failed to close the version file");
			}
		}

		return version;
	}

	// TODO: unify? add some kind of json2csv conversion
	public String MakeCsvSnapshot()
	{
		String result = "";

		for(ModelEntity entity : ServiceLocator.INSTANCE.GetModelService().GetModelData().GetAllEntities())
		{
			for(PreparedResponse response : entity.GetPreparedResponses())
			{
				result += response.GetCsvString(",") + System.lineSeparator();
			}
		}

		return result;
	}

	private final double free_space_mb_threshold = 1024; // 1GB in MB

	public Map<String, Object> PerformSelfTest()
	{
		boolean graph_test = ServiceLocator.INSTANCE.GetModelService().PerformGraphTest();

		long free_space = new File("/").getFreeSpace();

		long free_space_mb = free_space / 1024 / 1024;

		Map<String, Object> map = new HashMap<>();

		map.put("graph_test", graph_test);
		map.put("disk_space_MB", free_space_mb);
		map.put("disk_test", free_space_mb > free_space_mb_threshold);

		return map;
	}

	@Override
	public void Finish() {}
}
