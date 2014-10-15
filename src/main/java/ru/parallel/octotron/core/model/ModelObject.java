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
	final ModelLinkList out_links;
	final ModelLinkList in_links;

	final ModelObjectList out_neighbors;
	final ModelObjectList in_neighbors;

	public ModelObject()
	{
		super(EEntityType.OBJECT);

		out_links = new ModelLinkList();
		in_links = new ModelLinkList();

		out_neighbors = new ModelObjectList();
		in_neighbors = new ModelObjectList();
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
