/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import org.apache.commons.lang3.ArrayUtils;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.logic.impl.ObjectRule;
import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.collections.ModelObjectList;
import ru.parallel.octotron.core.primitive.EDependencyType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

public abstract class Aggregate extends ObjectRule
{
	private static final long serialVersionUID = -1961148475047706792L;
	private final String[] attributes;
	private final EDependencyType dependency;

	public Aggregate(String name, EDependencyType dependency, String... attributes)
	{
		super(name);
		this.dependency = dependency;
		this.attributes = ArrayUtils.clone(attributes);
	}

	protected ModelObjectList GetCandidates(ModelObject object)
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

		return candidates;
	}

	@Override
	public final AttributeList<IMetaAttribute> GetDependency(ModelObject object)
	{
		AttributeList<IMetaAttribute> result = new AttributeList<>();

		ModelObjectList candidates = GetCandidates(object);

		for(ModelObject obj : candidates.Uniq())
			for(String tmp : attributes)
			{
				if(!obj.TestAttribute(tmp))
					continue;

				result.add(obj.GetMetaAttribute(tmp));
			}

		return result;
	}

	@Override
	public final Object Compute(ModelObject object)
	{
		Object res = GetDefaultValue();

		ModelObjectList candidates = GetCandidates(object);

		for(ModelObject obj : candidates.Uniq())
			for(String tmp : attributes)
			{
				if(!obj.TestAttribute(tmp))
					continue;

				IMetaAttribute attribute = obj.GetMetaAttribute(tmp);

				if(!attribute.IsValid() || attribute.GetCTime() == 0)
					return null;

				res = Accumulate(res, attribute);
			}

		return res;
	}

	protected abstract Object Accumulate(Object res, IMetaAttribute attribute);
}
