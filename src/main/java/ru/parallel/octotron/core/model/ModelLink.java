/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.primitive.EModelType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

public class ModelLink extends ModelEntity
{
	private final ModelObjectList objects = new ModelObjectList();
	private final boolean directed;

	public ModelLink(ModelObject o1, ModelObject o2, boolean directed)
	{
		super(EModelType.LINK);

		this.objects.add(o1);
		this.objects.add(o2);

		this.directed = directed;
	}

	public boolean IsDirected()
	{
		return directed;
	}

	@Override
	public ModelLinkBuilder GetBuilder()
	{
		return new ModelLinkBuilder(this);
	}

	private void DirectedOnly()
	{
		if(!directed)
			throw new ExceptionModelFail("the link has no direction");
	}

	public ModelObject Source()
	{
		DirectedOnly();

		return objects.get(0);
	}

	public ModelObject Target()
	{
		DirectedOnly();

		return objects.get(1);
	}

	public ModelObject Other(ModelObject that)
	{
		if(objects.get(0).equals(that)) return objects.get(1);
		else if(objects.get(1).equals(that)) return objects.get(0);
		else throw new ExceptionModelFail("object does not belong to this link");
	}

	public ModelObjectList GetObjects()
	{
		return new ModelObjectList(objects);
	}
}
