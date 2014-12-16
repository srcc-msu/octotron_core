/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.exec.services.ModelService;

import java.util.Collection;

public final class VarAttribute extends AbstractModAttribute
{
	protected final Rule rule;
	protected final AttributeList<IModelAttribute> i_depend_on = new AttributeList<>();

	protected boolean deps_defined = false;

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

	public final boolean AreDepsDefined()
	{
		if(deps_defined)
			return true;

		for(IModelAttribute attribute : i_depend_on)
		{
			if(!attribute.GetValue().IsDefined())
			{
				deps_defined = false;
				return false;
			}
		}

		// we've got positive for all, cache it for future - it will not change back
		deps_defined = true;
		return true;
	}

	/**
	 * if sensor check fails, no update will be performed<br>
	 * in any other case the rule will be computed
	 * */
	public void Update()
	{
		super.Update(Value.Construct(rule.Compute(GetParent())));
	}

	public Collection<IModelAttribute> GetIDependOn()
	{
		return i_depend_on;
	}
}
