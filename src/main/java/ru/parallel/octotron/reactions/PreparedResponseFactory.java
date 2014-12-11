/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import com.google.common.collect.Iterators;
import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.exec.services.ModelData;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EModelType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.http.path.ParsedPath;
import ru.parallel.octotron.http.path.PathParser;
import ru.parallel.utils.JavaUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreparedResponseFactory
{
	private final Context context;

	public PreparedResponseFactory(Context context)
	{
		this.context = context;
	}

	public PreparedResponse Construct(ModelEntity entity, Reaction reaction, Response response)
	{
		PreparedResponse prepared_response = new PreparedResponse(context, response);

		FillInfo(prepared_response, response);
		FillModel(prepared_response, entity);
		FillUsr(prepared_response, entity, response);
		FillReaction(prepared_response, reaction);

		FillSurround(prepared_response, entity);

		FillCommands(prepared_response, entity, response);
		FillSpecial(prepared_response, entity, reaction);

		return prepared_response;
	}

	private void FillReaction(PreparedResponse prepared_response, Reaction reaction)
	{
		prepared_response.reaction.putAll(reaction.GetLongRepresentation());
	}

	private void FillSurround(PreparedResponse prepared_response
		, ModelEntity entity)
	{
		ModelLinkList links = new ModelLinkList();
		ModelObjectList objects = new ModelObjectList();

		if(entity.GetType() == EModelType.LINK)
		{
			ModelLink link = (ModelLink) entity;

			links.add(link);
			objects.add(link.Source());
			objects.add(link.Target());
		}
		else if(entity.GetType() == EModelType.OBJECT)
		{
			ModelObject object = (ModelObject) entity;

			links = links.append(object.GetInLinks());
			links = links.append(object.GetOutLinks());

			objects = objects.append(object.GetInNeighbors()).Uniq();
			objects = objects.append(object.GetOutNeighbors()).Uniq();
		}

		Iterator<ModelEntity> it = Iterators.concat(links.iterator(), objects.iterator());

		while(it.hasNext())
		{
			ModelEntity surround = it.next();
			for(PreparedResponse surround_responses : surround.GetPreparedResponses())
			{
				Map<String, Object> map = surround_responses.GetShortRepresentation();

				map.remove("surround");

				prepared_response.surround.add(map);
			}
		}
	}

	private void FillCommands(PreparedResponse prepared_response
		, ModelEntity entity, Response response)
	{
		for(String[] command : response.GetCommands())
		{
			String key = command[0];

			String actual_name = context.settings.GetScriptByKey(key);

			if(actual_name == null)
				throw new ExceptionModelFail("there is no script with key: " + key);

			List<String> result = new LinkedList<>();
			result.add(actual_name);

			// skip 1st
			for(int i = 1; i < command.length; i++)
			{
				result.add(ComposeString(command[i], entity, context.model_data));
			}

			result.add(response.GetStatus().toString());

			prepared_response.scripts.add(result.toArray(new String[0]));
		}
	}

	private void FillModel(PreparedResponse prepared_response, ModelEntity entity)
	{
		prepared_response.model.put("entity", entity.GetLongRepresentation());
	}

	private void FillInfo(PreparedResponse prepared_response, Response response)
	{
		prepared_response.info.put("time", JavaUtils.GetTimestamp());
		prepared_response.info.put("status", response.GetStatus().toString());
	}

	private void FillUsr(PreparedResponse prepared_response, ModelEntity entity, Response response)
	{
		Map<String, String> messages = response.GetMessages();

		for(String tag : messages.keySet())
			prepared_response.usr.put(tag, ComposeString(messages.get(tag), entity, context.model_data));
	}

	private void FillSpecial(PreparedResponse prepared_response
		, ModelEntity entity, Reaction reaction)
	{
		String suppress = String.format("to suppress this reaction: http://%s:%d/modify/suppress?path=obj(AID==%d)&template_id=%d&description=spam"
			, context.settings.GetHost(), context.settings.GetPort()
			, entity.GetID(), reaction.GetTemplate().GetID());
		prepared_response.specials.add(suppress);
	}

	private static final String NOT_FOUND = "<%s:not_found>";

	private static final Pattern PATTERN_NAME_PATH = Pattern.compile("\\{([^:{}]+):([^:{}]+)\\}");
	private static final Pattern PATTERN_NAME = Pattern.compile("\\{([^{}]+)\\}");

	private static String ReplaceWithPath(String path, String name
		, ModelEntity entity, ModelData model_data)
	{
		String where;

		if(entity.GetType() == EModelType.OBJECT)
			where = String.format("obj(AID==%d).%s.uniq()", entity.GetID(), path);
		else
			where = String.format("link(AID==%d).%s.uniq()", entity.GetID(), path);

		ParsedPath parsed_path;
		ModelList<? extends ModelEntity, ?> targets;

		try
		{
			parsed_path = PathParser.Parse(where);
			targets = parsed_path.Execute(ModelList.Single(entity), model_data);
		}
		catch (ExceptionParseError exceptionParseError)
		{
			return String.format(NOT_FOUND, name);
		}

		String result = "";

		if(targets.size() > 0)
		{
			String prefix = "";
			for(ModelEntity target : targets)
			{
				if(!target.TestAttribute(name))
					continue;

				result += prefix + target.GetAttribute(name).GetStringValue();
				prefix = ",";
			}

			return result.replaceAll("\"", "");
		}
		else
			return String.format(NOT_FOUND, name);
	}

	private static String ReplaceSimple(String name, ModelEntity entity)
	{
		if(entity.TestAttribute(name))
			return entity.GetAttribute(name).GetStringValue().replaceAll("\"", "");
		else
			return String.format(NOT_FOUND, name);
	}

	public static String ComposeString(String string, ModelEntity entity
		, ModelData model_data)
	{
		String result = string;

		Matcher matcher = PATTERN_NAME_PATH.matcher(result);

		while(matcher.find())
		{
			String path = matcher.group(1);
			String name = matcher.group(2);
			String all = matcher.group(0);

			result = result.replace(all
				, ReplaceWithPath(path, name, entity, model_data));
		}

		Matcher simple_matcher = PATTERN_NAME.matcher(result);

		while(simple_matcher.find())
		{
			String name = simple_matcher.group(1);
			String all = simple_matcher.group(0);

			result = result.replace(all, ReplaceSimple(name, entity));
		}

		return result;
	}
}
