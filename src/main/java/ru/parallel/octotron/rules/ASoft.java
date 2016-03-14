/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.exception.ExceptionParseError;
import ru.parallel.octotron.http.path.ParsedPath;
import ru.parallel.octotron.http.path.PathParser;

import java.util.Arrays;

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

	@Override
	public AttributeList<Attribute> GetDependency(ModelObject object)
	{
		AttributeList<Attribute> result = new AttributeList<>();

		ModelList<? extends ModelEntity, ?> candidates =
			parsed_path.Execute(ModelList.Single(object)).Uniq();

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
	public Object Compute(ModelObject object, Attribute rule_attribute)
	{
		Object res = GetDefaultValue();

		for(Attribute attribute : rule_attribute.GetIDependOn())
		{
			res = Accumulate(res, attribute);
		}

		return res;
	}

	protected abstract Object Accumulate(Object res, Attribute attribute);

	protected abstract Object GetDefaultValue();
}
