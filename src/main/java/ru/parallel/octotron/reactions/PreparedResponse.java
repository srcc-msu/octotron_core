/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import ru.parallel.octotron.core.primitive.IPresentable;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
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
 *             "const" : {...},
 *             "sensor" : {...},
 *             "var" : {...},
 *         },
 *         "attribute" :
 *         {
 *             "AID" : reaction.GetAttribute().GetName(),
 *             "name" : reaction.GetAttribute().GetID(),
 *             "value" : reaction.GetAttribute().GetValue()
 *         }
 *         "reaction" :
 *         {
 *             "AID" : reaction.GetID(),
 *             "repeated" : reaction.GetRepeat(),
 *             "suppressed" : reaction.IsSuppressed(),
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
	private final Reaction reaction;

	private final Response response;

	final Map<String, Object> info = new HashMap<>();
	final Map<String, Map<String, Object>> model = new HashMap<>();
	final Map<String, Object> usr = new HashMap<>();
	final List<Map<String, Object>> surround = new LinkedList<>();

	final List<String[]> scripts = new LinkedList<>();
	public List<String> specials = new LinkedList<>();

	public PreparedResponse(Context context, Reaction reaction, Response response)
	{
		this.context = context;
		this.reaction = reaction;
		this.response = response;
	}

/**
 * add attributes to the command and replace the command key by actual file<br>
 * */
	@Override
 	public void run()
	{
		String log_string = AutoFormat.PrintJson(GetRepresentation());

		if(!response.IsSuppress())
		{
			for(String[] command : scripts)
			{
				List<String> call = new LinkedList<>(Arrays.asList(command));

				call.add(log_string);

				for(String key : usr.keySet())
					call.add(key + ": " + usr.get(key));

				call.addAll(specials);

				try
				{
					FileUtils.ExecSilent(call.toArray(new String[0]));
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
		result.put("reaction", reaction.GetID());
		result.put("usr", usr);
		result.put("surround", surround);

		return result;
	}

	public Reaction GetReaction()
	{
		return reaction;
	}

	public Response GetResponse()
	{
		return response;
	}
}
