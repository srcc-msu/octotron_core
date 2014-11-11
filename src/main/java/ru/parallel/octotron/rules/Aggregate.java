/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.logic.impl.ObjectRule;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EDependencyType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.Arrays;

public abstract class Aggregate extends ObjectRule
{
	private final String[] attributes;
	private final EDependencyType dependency;

	Aggregate(EDependencyType dependency, String... attributes)
	{
		this.dependency = dependency;
		this.attributes = Arrays.copyOf(attributes, attributes.length);
	}

	// TODO: this is slow, add clone and caching or something
	ModelObjectList GetCandidates(ModelObject object)
	{
		ModelObjectList candidates = new ModelObjectList();

		switch(dependency)
		{
			case SELF:
				candidates.add(object);
				break;

			case OUT:
				candidates = object.GetOutNeighbors();
				break;

			case IN:
				candidates = object.GetInNeighbors();
				break;

			case ALL:
				candidates.add(object);
				candidates = candidates.append(object.GetInNeighbors());
				candidates = candidates.append(object.GetOutNeighbors());
				break;

			default:
				throw new ExceptionModelFail("unknown dependency: " + dependency);
		}

		return candidates.Uniq();
	}

	@Override
	public final AttributeList<IModelAttribute> GetDependency(ModelObject object)
	{
		AttributeList<IModelAttribute> result = new AttributeList<>();

		ModelObjectList candidates = GetCandidates(object);

		for(ModelObject obj : candidates)
			for(String tmp : attributes)
			{
				if(!obj.TestAttribute(tmp))
					continue;

				result.add(obj.GetAttribute(tmp));
			}

		return result;
	}

	@Override
	public final Object Compute(ModelObject object)
	{
		Object res = GetDefaultValue();

		ModelObjectList candidates = GetCandidates(object);

		for(ModelObject obj : candidates)
			for(String tmp : attributes)
			{
				if(!obj.TestAttribute(tmp))
					continue;

				IModelAttribute attribute = obj.GetAttribute(tmp);

				res = Accumulate(res, attribute);
			}

		return res;
	}

	protected abstract Object Accumulate(Object res, IModelAttribute attribute);
}
