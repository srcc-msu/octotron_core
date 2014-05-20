/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import java.util.ArrayList;
import java.util.List;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.OctoRule;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.utils.ObjectList;

/**
 * Basic Vertex factory, creates one or multiple edges<br>
 * */
public class ObjectFactory extends BaseFactory<ObjectFactory>
{
	private ObjectFactory(GraphService graph_service
		, List<SimpleAttribute> attributes
		, List<OctoRule> rules
		, List<OctoReaction> reactions)
	{
		super(graph_service, attributes, rules, reactions);
	}

	private ObjectFactory(GraphService graph_service
		, ArrayList<SimpleAttribute> attributes
		, ArrayList<OctoRule> rules
		, ArrayList<OctoReaction> reactions)
	{
		super(graph_service, attributes, rules, reactions);
	}

	public ObjectFactory(GraphService graph_service)
	{
		super(graph_service);
	}

	/**
	 * create single vertices
	 * */
	public OctoObject Create()
	{
		OctoObject vertex = graph_service.AddObject();

		vertex.DeclareAttributes(attributes);
		vertex.AddRules(rules);
		vertex.AddReactions(reactions);

		return vertex;
	}

	/**
	 * create \count vertices with additional attributes
	 * */
	public ObjectList Create(int count)
	{
		ObjectList vertices = new ObjectList();

		for(int i = 0; i < count; i++)
			vertices.add(this.Create());

		return vertices;
	}

	@Override
	protected ObjectFactory Clone(List<SimpleAttribute> new_attributes
		, List<OctoRule> new_rules
		, List<OctoReaction> new_reactions)
	{
		return new ObjectFactory(graph_service, new_attributes, new_rules, new_reactions);
	}
}
