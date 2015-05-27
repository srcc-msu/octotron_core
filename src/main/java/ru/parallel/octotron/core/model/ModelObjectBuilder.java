/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

public class ModelObjectBuilder extends ModelEntityBuilder<ModelObject>
{
	ModelObjectBuilder(ModelObject object)
	{
		super(object);
	}

	public void AddOutLink(ModelLink link)
	{
		entity.out_links.add(link);
		entity.out_neighbors.add(link.Target());
	}

	public void AddInLink(ModelLink link)
	{
		entity.in_links.add(link);
		entity.in_neighbors.add(link.Source());
	}

	public void AddUndirectedLink(ModelLink link)
	{
		entity.undirected_links.add(link);
		entity.undirected_neighbors.add(link.Other(entity));
	}
}
