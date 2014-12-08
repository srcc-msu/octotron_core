/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.EAttributeType;

public final class VarAttribute extends AbstractModAttribute
{
	protected final Rule rule;
	protected final AttributeList<IModelAttribute> i_depend_from = new AttributeList<>();

	@Override
	public VarAttributeBuilder GetBuilder(ModelService service)
	{
		service.CheckModification();

		return new VarAttributeBuilder(service, this);
	}

	public VarAttribute(ModelEntity parent, String name, Rule rule)
	{
		super(EAttributeType.VAR, parent, name, null);

		this.rule = rule;
	}

	public Rule GetRule()
	{
		return rule;
	}

	/**
	 * if sensor check fails, no update will be performed<br>
	 * in any other case the rule will be computed
	 * */
	public boolean Update()
	{
		if(!Check())
			return false;

		super.Update(Value.Construct(rule.Compute(GetParent())));

		return true;
	}

	public Iterable<IModelAttribute> GetIDependFrom()
	{
		return i_depend_from;
	}

	@Override
	public boolean Check()
	{
		return rule.CanCompute(GetParent());
	}
}
