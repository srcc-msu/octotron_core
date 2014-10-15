/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public class ModelObject extends ModelEntity
{
	final ModelLinkList out_links = new ModelLinkList();
	final ModelLinkList in_links = new ModelLinkList();

	final ModelObjectList out_neighbors = new ModelObjectList();
	final ModelObjectList in_neighbors = new ModelObjectList();

	public ModelObject()
	{
		super(EEntityType.OBJECT);
	}

	@Override
	public ModelObjectBuilder GetBuilder(ModelService service)
	{
		service.CheckModification();

		return new ModelObjectBuilder(service, this);
	}

	public ModelLinkList GetInLinks()
	{
		return in_links;
	}

	public ModelLinkList GetOutLinks()
	{
		return out_links;
	}

	public ModelObjectList GetInNeighbors()
	{
		return in_neighbors;
	}

	public ModelObjectList GetOutNeighbors()
	{
		return out_neighbors;
	}

	public ModelObjectList GetInNeighbors(String link_name, Object link_value)
	{
		return in_links.Filter(link_name, link_value).Source();
	}

	public ModelObjectList GetOutNeighbors(String link_name, Object link_value)
	{
		return out_links.Filter(link_name, link_value).Target();
	}

	public ModelObjectList GetInNeighbors(String link_name)
	{
		return in_links.Filter(link_name).Source();
	}

	public ModelObjectList GetOutNeighbors(String link_name)
	{
		return out_links.Filter(link_name).Target();
	}

	public ModelObjectList GetInNeighbors(SimpleAttribute link_attribute)
	{
		return GetInNeighbors(link_attribute.GetName(), link_attribute.GetValue());
	}

	public ModelObjectList GetOutNeighbors(SimpleAttribute link_attribute)
	{
		return GetOutNeighbors(link_attribute.GetName(), link_attribute.GetValue());
	}
}
