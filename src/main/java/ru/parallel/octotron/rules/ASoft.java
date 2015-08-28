/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.http.path.ParsedPath;
import ru.parallel.octotron.http.path.PathParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class ASoft extends ObjectRule
{
	private final String[] attributes;
	private final ParsedPath parsed_path;

	ASoft(String path, String... attributes)
		throws ExceptionParseError
	{
		this.parsed_path = PathParser.Parse(path);
		this.attributes = Arrays.copyOf(attributes, attributes.length);
	}

	Map<ModelObject, ModelList<? extends ModelEntity, ?>> cache = new HashMap<>();

	// TODO: this is slow, add clone and caching or something
	ModelList<? extends ModelEntity, ?> GetCandidates(ModelObject object)
	{

		if(cache.get(object) == null)
			cache.put(object, parsed_path.Execute(ModelList.Single(object)).Uniq());

		return cache.get(object);
	}

	@Override
	public AttributeList<Attribute> GetDependency(ModelObject object)
	{
		AttributeList<Attribute> result = new AttributeList<>();

		ModelList<? extends ModelEntity, ?> candidates = GetCandidates(object);

		for(ModelEntity obj : candidates)
			for(String tmp : attributes)
			{
				if(!obj.TestAttribute(tmp))
					continue;

				result.add(obj.GetAttribute(tmp));
			}

		return result;
	}

	@Override
	public Object Compute(ModelObject object)
	{
		Object res = GetDefaultValue();

		ModelList<? extends ModelEntity, ?> candidates = GetCandidates(object);

		for(ModelEntity obj : candidates)
			for(String tmp : attributes)
			{
				if(!obj.TestAttribute(tmp))
					continue;

				Attribute attribute = obj.GetAttribute(tmp);

				res = Accumulate(res, attribute);
			}

		return res;
	}

	protected abstract Object Accumulate(Object res, Attribute attribute);

	protected abstract Object GetDefaultValue();
}
