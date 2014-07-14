/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.graph.collections.ObjectList;

import java.util.ArrayList;
import java.util.List;

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
	public ModelObject Create()
	{
		ModelObject object = new ModelObject(graph_service.AddObject());

		object.DeclareAttributes(attributes);
		object.AddRules(rules);
		object.AddReactions(reactions);

		return object;
	}

	/**
	 * create \count vertices with additional attributes
	 * */
	public ObjectList<ModelObject, ModelLink> Create(int count)
	{
		ObjectList<ModelObject, ModelLink> objects = new ObjectList();

		for(int i = 0; i < count; i++)
			objects.add(this.Create());

		return objects;
	}

	@Override
	protected ObjectFactory Clone(List<SimpleAttribute> new_attributes
		, List<OctoRule> new_rules
		, List<OctoReaction> new_reactions)
	{
		return new ObjectFactory(graph_service, new_attributes, new_rules, new_reactions);
	}
}
