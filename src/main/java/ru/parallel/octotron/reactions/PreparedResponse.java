/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.IPresentable;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.services.ServiceLocator;
import ru.parallel.utils.AutoFormat;
import ru.parallel.utils.JavaUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PreparedResponse implements Runnable, IPresentable
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private static final String SHORT_LOG = "octotron.events.log";
	private static final String LONG_LOG = "octotron.events.verbose.log";
	private static final String TIMEOUT_LOG = "octotron.timeout.log";

	private final Context context;

	private final Response response;

	final Map<String, Object> info = new HashMap<>();
	final Map<String, Map<String, Object>> model = new HashMap<>();
	final Map<String, Object> usr = new HashMap<>();
	final Map<String, Object> reaction = new HashMap<>();
	final List<Map<String, Object>> surround = new LinkedList<>();

	final List<String[]> scripts = new LinkedList<>();
	final List<String> specials = new LinkedList<>();
	private final boolean is_suppressed;

	public PreparedResponse(Context context, Response response, boolean is_suppressed)
	{
		this.context = context;
		this.response = response;
		this.is_suppressed = is_suppressed;
	}

	private static final Object lock = new Object();

/**
 * add attributes to the command and replace the command key by actual file<br>
 * */
	@Override
 	public void run()
	{
		String short_log_string = AutoFormat.FormatJson(GetShortRepresentation());
		String long_log_string = AutoFormat.FormatJson(GetLongRepresentation());

		try
		{
			// TODO rework
			synchronized(lock) // this and file reopening is not efficient, but does not happen often
			{
				if(response.GetStatus() == EEventStatus.TIMEOUT)
				{
					FileLog short_log = new FileLog(context.settings.GetLogDir(), TIMEOUT_LOG);
					short_log.Log(short_log_string);
					short_log.Close();
				}
				else
				{
					FileLog short_log = new FileLog(context.settings.GetLogDir(), SHORT_LOG);
					short_log.Log(short_log_string);
					short_log.Close();

					FileLog long_log = new FileLog(context.settings.GetLogDir(), LONG_LOG);
					long_log.Log(long_log_string);
					long_log.Close();
				}
			}
		}
		catch(ExceptionSystemError e)
		{
			LOGGER.log(Level.WARNING, "could not create a log entry", e);
		}

		if(!is_suppressed)
		{
			for(String[] command : scripts)
			{
				List<String> report = new LinkedList<>(Arrays.asList(command));

				report.add(short_log_string);

				for(String key : usr.keySet())
					report.add(key + ": " + usr.get(key));

				report.addAll(specials);

				try
				{
					ServiceLocator.INSTANCE.GetScriptService().ExecSilent(report.toArray(new String[0]));
				}
				catch(ExceptionSystemError e)
				{
					LOGGER.log(Level.SEVERE, "could not invoke reaction script: " + Arrays.toString(command), e);
				}
			}
		}
	}

	public Response GetResponse()
	{
		return response;
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = GetShortRepresentation();

		result.put("model", model);
		result.put("reaction", reaction);
		result.put("surround", surround);

		return result;
	}

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = new HashMap<>();

		result.put("info", info);
		result.put("usr", usr);

		return result;
	}

	@Override
	public Map<String, Object> GetRepresentation(boolean verbose)
	{
		if(verbose)
			return GetLongRepresentation();
		return GetShortRepresentation();
	}

	public String GetCsvString(String sep)
	{
		String result = "";

		result += JavaUtils.Quotify((String)info.get("status")) + sep;
		result += info.get("time") + sep;
		result += JavaUtils.Quotify((String)usr.get("loc")) + sep;
		result += JavaUtils.Quotify((String)usr.get("msg"));

		return result;
	}
}
