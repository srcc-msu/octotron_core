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
		Object result = super.Compute(entity);

		if(result.equals(Value.invalid))
			return Value.invalid;

		long interval = (Long)result;

		if(interval == 0 || interval >= thresholds.length)
			return Value.invalid;

		return interval;
	}
}
