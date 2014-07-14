/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import org.apache.commons.lang3.ArrayUtils;
import ru.parallel.octotron.core.graph.IAttribute;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.rule.OctoObjectRule;
import ru.parallel.octotron.core.primitive.EDependencyType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.graph.collections.ObjectList;

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
	public final EDependencyType GetDependency()
	{
		return dependency;
	}

	@Override
	public final Object Compute(ModelObject object)
	{
		Object res = GetDefaultValue();

		ObjectList<ModelObject, ModelLink> candidates = new ObjectList();

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

		for(ModelObject obj : candidates.Uniq())
			for(String tmp : attributes)
			{
				if(!obj.GetBaseObject().TestAttribute(tmp))
					continue;

				ModelAttribute attribute = obj.GetAttribute(tmp);
				if(attribute.IsValid() && attribute.GetCTime() != 0)
					res = Accumulate(res, attribute);
			}

		return res;
	}

	protected abstract Object Accumulate(Object res, ModelAttribute attribute);
}
