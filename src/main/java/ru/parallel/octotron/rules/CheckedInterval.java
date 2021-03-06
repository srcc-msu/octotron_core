/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.model.ModelEntity;

public class CheckedInterval extends Interval // does it smell?
{
	public CheckedInterval(String param, Object... thresholds)
	{
		super(param, thresholds);
	}

	@Override
	public Object Compute(ModelEntity entity, Attribute rule_attribute)
	{
		Object result = super.Compute(entity, rule_attribute);

		if(result.equals(Value.invalid))
			return Value.invalid;

		long interval = (Long)result;

		if(interval == 0 || interval >= thresholds.length)
			return Value.invalid;

		return interval;
	}
}
