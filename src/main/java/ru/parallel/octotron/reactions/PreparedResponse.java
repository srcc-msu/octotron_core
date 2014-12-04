/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.utils.AutoFormat;
import ru.parallel.utils.FileUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PreparedResponse implements Runnable
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private final Context context;

	private final Response response;

	final Map<String, Object> info = new HashMap<>();
	final Map<String, Map<String, Object>> model = new HashMap<>();
	final Map<String, Object> usr = new HashMap<>();
	final Map<String, Object> reaction = new HashMap<>();
	final List<Map<String, Object>> surround = new LinkedList<>();

	final List<String[]> scripts = new LinkedList<>();
	final List<String> specials = new LinkedList<>();

	public PreparedResponse(Context context, Response response)
	{
		this.context = context;
		this.response = response;
	}

/**
 * add attributes to the command and replace the command key by actual file<br>
 * */
	@Override
 	public void run()
	{
		String log_string = AutoFormat.FormatJson(GetRepresentation());

		if(!response.IsSuppress())
		{
			for(String[] command : scripts)
			{
				List<String> report = new LinkedList<>(Arrays.asList(command));

				report.add(log_string);

				for(String key : usr.keySet())
					report.add(key + ": " + usr.get(key));

				report.addAll(specials);

				try
				{
					FileUtils.ExecSilent(report.toArray(new String[0]));
				}
				catch(ExceptionSystemError e)
				{
					LOGGER.log(Level.SEVERE, "could not invoke reaction script: " + Arrays.toString(command), e);
				}
			}
		}
		try
		{
			FileLog file = new FileLog(context.settings.GetLogDir());
			file.Log(log_string);
			file.Close();
		}
		catch(ExceptionSystemError e)
		{
			LOGGER.log(Level.WARNING, "could not create a log entry", e);
		}
	}

	public Map<String, Object> GetRepresentation()
	{
		Map<String, Object> result = new HashMap<>();

		result.put("info", info);
		result.put("model", model);
		result.put("reaction", reaction);
		result.put("usr", usr);
		result.put("surround", surround);

		return result;
	}

	public Response GetResponse()
	{
		return response;
	}
}
