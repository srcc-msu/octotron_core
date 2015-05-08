/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.primitive.EModelType;
import ru.parallel.octotron.exec.services.ModelService;


public class ModelObject extends ModelEntity
{
	final ModelLinkList out_links = new ModelLinkList();
	final ModelLinkList in_links = new ModelLinkList();
	final ModelLinkList undirected_links = new ModelLinkList();

	final ModelObjectList out_neighbors = new ModelObjectList();
	final ModelObjectList in_neighbors = new ModelObjectList();
	final ModelObjectList undirected_neighbors = new ModelObjectList();

	public ModelObject()
	{
		super(EModelType.OBJECT);
	}

	@Override
	public ModelObjectBuilder GetBuilder(ModelService service)
	{
		return new ModelObjectBuilder(service, this);
	}

//--------

	public ModelLinkList GetInLinks()
	{
		return in_links;
	}

	public ModelLinkList GetOutLinks()
	{
		return out_links;
	}

	public ModelLinkList GetUndirectedLinks()
	{
		return undirected_links;
	}

	public ModelLinkList GetAllLinks()
	{
		return new ModelLinkList()
			.append(in_links)
			.append(out_links)
			.append(undirected_links);
	}

//--------

	public ModelObjectList GetInNeighbors()
	{
		return in_neighbors;
	}

	public ModelObjectList GetOutNeighbors()
	{
		return out_neighbors;
	}

	public ModelObjectList GetUndirectedNeighbors()
	{
		return undirected_neighbors;
	}

	public ModelObjectList GetAllNeighbors()
	{
		return new ModelObjectList()
			.append(in_neighbors)
			.append(out_neighbors)
			.append(undirected_neighbors);
	}
//--------

	public ModelObjectList GetInNeighbors(String link_name, Value link_value)
	{
		return in_links.Filter(link_name, link_value).Source();
	}

	public ModelObjectList GetOutNeighbors(String link_name, Value link_value)
	{
		return out_links.Filter(link_name, link_value).Target();
	}

	public ModelObjectList GetUndirectedNeighbors(String link_name, Value link_value)
	{
		return undirected_links.Filter(link_name, link_value).Target();
	}

	public ModelObjectList GetAllNeighbors(String link_name, Value link_value)
	{
		return GetAllLinks().Filter(link_name, link_value).Other(this);
	}

//--------

	public ModelObjectList GetInNeighbors(String link_name, Object link_value)
	{
		return GetInNeighbors(link_name, Value.Construct(link_value));
	}

	public ModelObjectList GetOutNeighbors(String link_name, Object link_value)
	{
		return GetOutNeighbors(link_name, Value.Construct(link_value));
	}

	public ModelObjectList GetUndirectedNeighbors(String link_name, Object link_value)
	{
		return GetUndirectedNeighbors(link_name, Value.Construct(link_value));
	}

	public ModelObjectList GetAllNeighbors(String link_name, Object link_value)
	{
		return GetAllNeighbors(link_name, Value.Construct(link_value));
	}

//--------

	public ModelObjectList GetInNeighbors(String link_name)
	{
		return in_links.Filter(link_name).Source();
	}

	public ModelObjectList GetOutNeighbors(String link_name)
	{
		return out_links.Filter(link_name).Target();
	}

	public ModelObjectList GetUndirectedNeighbors(String link_name)
	{
		return undirected_links.Filter(link_name).Target();
	}

	public ModelObjectList GetAllNeighbors(String link_name)
	{
		return GetAllLinks().Filter(link_name).Other(this);
	}
}
