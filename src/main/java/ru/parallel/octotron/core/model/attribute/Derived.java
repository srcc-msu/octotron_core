/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.impl.PersistentStorage;

public class Derived extends AbstractVaryingAttribute
{
	private Derived(ModelEntity parent, String name)
	{
		super(parent, name);
	}

	private static final String rule_key_const = "_rule";

	public boolean Update()
	{
		long rule_id = meta.GetAttribute(rule_key_const).GetLong();

		OctoRule rule = PersistentStorage.INSTANCE.GetRules().Get(rule_id);
		Object new_val = rule.Compute(parent);

		return Update(new_val, false);
	}

	private static final SimpleAttribute attribute_type
		= new SimpleAttribute("type", "_derived");

	public static final Derived TryConstruct(ModelEntity parent, String name)
	{
		ObjectList<GraphObject, GraphLink> attribute_objects = parent.GetBaseObject()
			.GetOutNeighbors(ModelAttribute.extended_attribute).Filter(attribute_type);

		if(attribute_objects.size() == 0)
			return null;

		return new Derived(parent, name);
	}

	@Override
	protected AttributeObject GetMeta()
	{
		return new AttributeObject(parent.GetBaseObject()
			.GetOutNeighbors(ModelAttribute.extended_attribute, GetBase().GetName())
			.Only());
	}

	@Override
	public AttributeList<Derived> GetDependant()
	{
		return null;
	}
}
