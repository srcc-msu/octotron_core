/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.OctoRule;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.utils.JavaUtils;

public class UpdatedRecently extends OctoRule
{
	private static final long serialVersionUID = -5796823312858284235L;
	private final String measured_attribute;
	private long threshold = 0l;

	public UpdatedRecently(String name, String measured_attribute, long threshold)
	{
		super(name) ;
		this.measured_attribute = measured_attribute;
		this.threshold = threshold;
	}

	@Override
	public AttributeList<IMetaAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IMetaAttribute> result = new AttributeList<>();

		result.add(entity.GetMetaAttribute(measured_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IMetaAttribute attr = entity.GetMetaAttribute(measured_attribute);

		if(!attr.IsValid() || attr.GetCTime() == 0)
			return GetDefaultValue();

		long update_time = attr.GetATime();
		long cur_time = JavaUtils.GetTimestamp();

		return cur_time - update_time < threshold;
	}

	@Override
	public Object GetDefaultValue()
	{
		return false;
	}
}
