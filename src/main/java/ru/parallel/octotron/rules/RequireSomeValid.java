/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.exception.ExceptionParseError;

public class RequireSomeValid extends AValidCount
{
	private final long count;
	private final Object return_value;

	public RequireSomeValid(long count, Object return_value, String path, String... attributes)
		throws ExceptionParseError
	{
		super(path, attributes);
		this.count = count;
		this.return_value = return_value;
	}

	@Override
	public Object Compute(ModelObject object, Attribute rule_attribute)
	{
		long valid_count = (Long)super.Compute(object, rule_attribute);

		if(valid_count < count)
			return Value.invalid;

		return return_value;
	}
}
