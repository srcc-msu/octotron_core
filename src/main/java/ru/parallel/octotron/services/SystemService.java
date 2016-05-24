/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.services;

import ru.parallel.octotron.core.attributes.impl.Sensor;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.logic.Statistics;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.octotron.bg_services.ServiceLocator;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SystemService extends Service
{
	public final Statistics stat = new Statistics();
	private boolean should_stop;

	public SystemService(Context context)
	{
		super(context);
	}

	public void Terminate(boolean should_stop)
	{
		this.should_stop = should_stop;
	}
	public boolean ShouldExit()
	{
		return should_stop;
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
				if(sensor.IsOutdated())
					result.add(sensor.GetShortRepresentation());
			}
		}

		return result;
	}

	public Map<String, Object> GetVersion()
		throws ExceptionSystemError
	{
		InputStream stream = SystemService.class.getResourceAsStream("/VERSION");

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
		String result = "status,time,_id,msg" + System.lineSeparator();

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
}
