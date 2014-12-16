/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;

public class CheckedInterval extends Interval // does it smell?
{
	public CheckedInterval(String param, Object... thresholds)
	{
		super(param, thresholds);
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		long result = (long) super.Compute(entity);

		if(result == 0 || result >= thresholds.length)
			return Value.invalid;

		return result;
	}
}
