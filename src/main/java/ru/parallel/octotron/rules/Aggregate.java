/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import org.apache.commons.lang3.ArrayUtils;
import ru.parallel.octotron.core.OctoAttribute;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.core.OctoObjectRule;
import ru.parallel.octotron.primitive.EDependencyType;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.utils.OctoObjectList;

public abstract class Aggregate extends OctoObjectRule
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

	@Override
	public final EDependencyType GetDeps()
	{
		return dependency;
	}

	@Override
	public final Object Compute(OctoObject object)
	{
		Object res = GetDefaultValue();

		OctoObjectList candidates = new OctoObjectList();

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

		for(OctoObject obj : candidates.Uniq())
			for(String tmp : attributes)
			{
				OctoAttribute attribute = obj.GetAttribute(tmp);
				if(attribute.IsValid() && attribute.GetCTime() != 0)
					res = Accumulate(res, attribute);
			}

		return res;
	}

	protected abstract Object Accumulate(Object res, OctoAttribute attribute);
}
