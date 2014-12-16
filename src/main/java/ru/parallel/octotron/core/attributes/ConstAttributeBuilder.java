/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.exec.services.ModelService;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;

public class ConstAttributeBuilder implements IAttributeBuilder
{
	private final ModelService service;
	private final ConstAttribute attribute;

	ConstAttributeBuilder(ModelService service, ConstAttribute attribute)
	{
		this.service = service;
		this.attribute = attribute;
	}

	@Override
	public void AddReaction(ReactionTemplate reaction_template)
	{
		throw new ExceptionModelFail(ConstAttribute.err_msg + "AddReaction");
	}

	@Override
	public void AddDependant(VarAttribute attribute)
	{
		// nothing to see here
		// throw new ExceptionModelFail(err_msg + "AddDependant");
	}

	public void ModifyValue(Value new_value)
	{
		attribute.SetValue(new_value);
	}
}
