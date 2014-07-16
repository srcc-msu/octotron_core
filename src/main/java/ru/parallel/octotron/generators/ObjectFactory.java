/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.rule.OctoRule;

import java.util.List;

/**
 * Basic Vertex factory, creates one or multiple edges<br>
 * */
public class ObjectFactory extends BaseFactory<ObjectFactory>
{
	private ObjectFactory(ModelService model_service
		, List<SimpleAttribute> constants
		, List<SimpleAttribute> sensors
		, List<OctoRule> rules
		, List<OctoReaction> reactions)
	{
		super(model_service, constants, sensors, rules, reactions);
	}

	public ObjectFactory(ModelService model_service)
	{
		super(model_service);
	}

	/**
	 * create single vertices
	 * */
	public ModelObject Create()
	{
		ModelObject object = model_service.AddObject();

		object.DeclareConstants(constants);
		object.AddSensors(sensors);
		object.AddRules(rules);
		object.AddReactions(reactions);

		return object;
	}

	/**
	 * create \count vertices with additional attributes
	 * */
	public ObjectList<ModelObject, ModelLink> Create(int count)
	{
		ObjectList<ModelObject, ModelLink> objects = new ObjectList<>();

		for(int i = 0; i < count; i++)
			objects.add(this.Create());

		return objects;
	}

	@Override
	protected ObjectFactory Clone(
		List<SimpleAttribute> new_constants
		, List<SimpleAttribute> new_sensors
		, List<OctoRule> new_rules
		, List<OctoReaction> new_reactions)
	{
		return new ObjectFactory(model_service, new_constants, new_sensors, new_rules, new_reactions);
	}
}
