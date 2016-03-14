/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.exception.ExceptionParseError;

public class ValueIfAllValid extends AInvalidCount // WTF?
{
	private final String attribute;

	public ValueIfAllValid(String attribute, String path, String... attributes)
		throws ExceptionParseError
	{
		super(path, attributes);
		this.attribute = attribute;
	}

	@Override
	public final AttributeList<Attribute> GetDependency(ModelObject object)
	{
		AttributeList<Attribute> tmp = super.GetDependency(object);
		tmp.add(object.GetAttribute(attribute));
		return tmp;
	}

	@Override
	public Object Compute(ModelObject object, Attribute rule_attribute)
	{
		long invalid_count = (Long)super.Compute(object, rule_attribute);

		if(invalid_count > 0)
			return Value.invalid;

		return object.GetAttribute(attribute).GetValue().GetRaw();
	}
}
