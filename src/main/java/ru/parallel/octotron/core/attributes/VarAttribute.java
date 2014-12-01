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
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public final class VarAttribute extends AbstractModAttribute
{
	protected final Rule rule;
	protected final AttributeList<IModelAttribute> dependency = new AttributeList<>();

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
		if(Check() == false)
			return false;

		Object new_value = rule.Compute(GetParent());

		super.Update(new_value);

		return true;
	}

	public Iterable<IModelAttribute> GetDependency()
	{
		return dependency;
	}

	/**
	 * checks for a special case - when the sensor value has not been updated<br>
	 * will only fail on not updated sensors<br>
	 * */
	@Override
	public boolean Check()
	{
		for(IModelAttribute dep_attribute : rule.GetDependency(GetParent()))
		{
			if(dep_attribute.GetType() == EAttributeType.SENSOR)
				if(((SensorAttribute)dep_attribute).GotInitialValue() == false)
					return false;
		}
		return true;
	}
}
