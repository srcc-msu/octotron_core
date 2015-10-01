/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence.impl;

import com.google.common.collect.Iterables;
import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelInfo;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.persistence.IPersistenceManager;
import ru.parallel.octotron.persistence.graph.impl.GraphEntity;
import ru.parallel.octotron.persistence.graph.impl.GraphLink;
import ru.parallel.octotron.persistence.graph.impl.GraphObject;
import ru.parallel.octotron.persistence.graph.impl.GraphService;
import ru.parallel.octotron.persistence.neo4j.Neo4jGraph;
import ru.parallel.octotron.services.impl.ModelService;

import java.util.Collection;
import java.util.logging.Logger;

public abstract class AbstractGraphManager implements IPersistenceManager
{
	protected final static Logger LOGGER = Logger.getLogger("octotron");

	protected final ModelService model_service;
	protected final GraphService graph_service;
	protected final Neo4jGraph graph;

	public AbstractGraphManager(ModelService model_service, Neo4jGraph graph)
	{
		this.model_service = model_service;
		this.graph_service = new GraphService(graph);
		this.graph = graph;
	}

	protected GraphObject GetGraphObject(ModelInfo<?> id)
	{
		return graph_service.GetObject("AID", id.GetID());
	}

	protected GraphObject CheckGraphObject(ModelInfo<?> id, String check_label)
	{
		GraphObject graph_object = graph_service.GetObject("AID", id.GetID());

		if(!graph_object.TestLabel(check_label))
			throw new ExceptionModelFail("graph check failed, mismatched labels for AID: "
				+ id.GetID() + " required label: " + check_label);

		return graph_object;
	}

	protected GraphLink GetLink(ModelLink id)
	{
		return graph_service.GetLink("AID", id.GetInfo().GetID());
	}

	protected GraphEntity GetEntity(ModelEntity id)
	{
		Collection<GraphLink> links = graph_service.GetLinks("AID", id.GetInfo().GetID());
		Collection<GraphObject> objects = graph_service.GetObjects("AID", id.GetInfo().GetID());

		if(!links.isEmpty() && !objects.isEmpty())
			throw new ExceptionModelFail("found few entities with the same id: " + id.GetInfo().GetID());

		if(objects.size() == 1)
			return Iterables.get(objects, 0);
		else if(objects.size() > 1)
			throw new ExceptionModelFail("found multiple objects with AID: " + id.GetInfo().GetID());

		if(links.size() == 1)
			return Iterables.get(links, 0);
		else if(links.size() > 1)
			throw new ExceptionModelFail("found multiple links with AID: " + id.GetInfo().GetID());

		throw new ExceptionModelFail("could not get entity for AID: " + id.GetInfo().GetID());
	}

	private static final String DEPENDS = "DEPENDS";

	/**
	 * check entity and create dependencies links between all attributes
	 * is ignores constants, because we do not create objects for them in the graph
	 * */
	@Override
	public void MakeRuleDependency(ModelEntity entity)
	{
		for(Attribute attribute : entity.GetAttributes())
		{
			if(attribute.GetInfo().GetType() == EAttributeType.CONST)
				continue;

			GraphObject object = GetGraphObject(attribute.GetInfo());

			for(Attribute i_depend_on : attribute.GetIDependOn())
			{
				if(i_depend_on.GetInfo().GetType() == EAttributeType.CONST)
					continue;

				GraphObject dependency_object = GetGraphObject(i_depend_on.GetInfo());

				graph_service.AddLink(object, dependency_object, DEPENDS);
			}
		}
	}
}
