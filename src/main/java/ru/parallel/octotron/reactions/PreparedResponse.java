/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.utils.AutoFormat;
import ru.parallel.utils.FileUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {
 *     "info" :
 *     {
 *         "time" : timestamp,
 *         "event" : response.GetStatus()
 *     }
 *     "model" :
 *     {
 *         "entity" :
 *         {
 *             entity.GetAttributes()
 *         },
 *         "attribute" :
 *         {
 *             "AID" : reaction.GetAttribute().GetName(),
 *             "name" : reaction.GetAttribute().GetID()
 *         }
 *         "reaction" :
 *         {
 *             "AID" : reaction.GetID(),
 *             "template" : reaction.GetTemplate()
 *         }
 *     }
 *     "usr" :
 *     {
 *         "place" : "10.1.1.1", // for place, type may vary
 *         "tag" : "TEMPERATURE", // for general classification
 *         "descr" : "bad temp on node", // for accurate classification
 *         "msg" : "bad temp on node 10.1.1.1 : 35C" // msg for user
 *     }
 *     "surround" : // for in/out and self
 *     [
 *         {
 *             "entity.AID" : 333,
 *             "attribute.AID" : 444,
 *             "attribute.name" : "memory",
 *             "reaction.AID" : 333,
 *             "reaction.type" : ERROR,
 *             "reaction.place" : #place
 *             "reaction.tag" : #tag
 *         },
 *         {...},
 *     ]
 * */
 public class PreparedResponse implements Runnable
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private final Context context;
	private final boolean suppress_script;

	final Map<String, Object> info = new HashMap<>();
	final Map<String, Map<String, Object>> model = new HashMap<>();
	final Map<String, Object> usr = new HashMap<>();
	final List<Map<String, Object>> surround = new LinkedList<>();

	final List<String[]> scripts = new LinkedList<>();
	public List<String> specials = new LinkedList<>();

	public PreparedResponse(Context context, boolean suppress_script)
	{
		this.context = context;
		this.suppress_script = suppress_script;
	}

/**
 * add attributes to the command and replace the command key by actual file<br>
 * */
	@Override
 	public void run()
	{
		if(!suppress_script)
		{
			for(String[] command : scripts)
			{
				try
				{
					FileUtils.ExecSilent(command);
				}
				catch (ExceptionSystemError e)
				{
					LOGGER.log(Level.SEVERE, "could not invoke reaction script: " + Arrays.toString(command), e);
				}
			}
		}

		try
		{
			FileLog file = new FileLog(context.settings.GetLogDir());
			file.Log(GetFullString());
			file.Close();
		}
		catch(ExceptionSystemError e)
		{
			LOGGER.log(Level.WARNING, "could not create a log entry", e);
		}
	}

	public String GetFullString()
	{
		Map<String, Object> result = new HashMap<>();

		result.put("info", info);
		result.put("model", model);
		result.put("usr", usr);
		result.put("surround", surround);

		return AutoFormat.PrintJson(result);
	}
}
