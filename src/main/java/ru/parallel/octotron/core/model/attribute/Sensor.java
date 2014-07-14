/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public class Sensor extends AbstractVaryingAttribute
{
	private Sensor(ModelEntity parent, String name)
	{
		super(parent, name);
	}

	public boolean Update(Object new_value)
	{
		return Update(new_value, true);
	}

	private static final SimpleAttribute attribute_type
		= new SimpleAttribute("type", "_derived");

	public static final Sensor TryConstruct(ModelEntity parent, String name)
	{
		ObjectList<GraphObject, GraphLink> attribute_objects = parent.GetBaseObject()
			.GetOutNeighbors(ModelAttribute.extended_attribute).Filter(attribute_type);

		if(attribute_objects.size() == 0)
			return null;

		return new Sensor(parent, name);
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
