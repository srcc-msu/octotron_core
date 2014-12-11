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
import ru.parallel.octotron.exec.services.ModelService;
import ru.parallel.octotron.core.primitive.EAttributeType;

import java.util.Collection;

public final class VarAttribute extends AbstractModAttribute
{
	protected final Rule rule;
	protected final AttributeList<IModelAttribute> i_depend_on = new AttributeList<>();

	@Override
	public VarAttributeBuilder GetBuilder(ModelService service)
	{
		service.CheckModification();

		return new VarAttributeBuilder(service, this);
	}

	public VarAttribute(ModelEntity parent, String name, Rule rule)
	{
		super(EAttributeType.VAR, parent, name, Value.undefined);

		this.rule = rule;
	}

	public Rule GetRule()
	{
		return rule;
	}

	@Override
	public final boolean IsInitialDelay()
	{
		if(!is_initial_delay)
			return false;

		for(IModelAttribute attribute : GetIDependOn())
		{
			if(attribute.IsInitialDelay())
				return true;
		}

		// we've got positive for all, cache it for future - it will not change back
		CancelInitialDelay();
		return false;
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

	public Collection<IModelAttribute> GetIDependOn()
	{
		return i_depend_on;
	}

	@Override
	public boolean Check()
	{
		return rule.CanCompute(this) && !IsInitialDelay();
	}
}
