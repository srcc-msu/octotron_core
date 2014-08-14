/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.model.impl.ModelObjectList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.rule.OctoReaction;
import ru.parallel.octotron.core.rule.OctoRule;

import java.util.List;

/**
 * Basic Vertex factory, creates one or multiple edges<br>
 * */
public class ObjectFactory extends BaseFactory<ObjectFactory>
{
	public ObjectFactory()
	{
		super();
	}

	private ObjectFactory(List<SimpleAttribute> constants
		, List<SimpleAttribute> sensors
		, List<OctoRule> rules
		, List<OctoReaction> reactions)
	{
		super(constants, sensors, rules, reactions);
	}

	/**
	 * create single vertices
	 * */
	public ModelObject Create()
	{
		ModelObject object = ModelService.AddObject();

		object.DeclareConstants(constants);
		object.DeclareSensors(sensors);
		object.DeclareVariables(rules);
		object.AddReactions(reactions);

		return object;
	}

	/**
	 * create \count vertices with additional attributes
	 * */
	public ModelObjectList Create(int count)
	{
		ModelObjectList objects = new ModelObjectList();

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
		return new ObjectFactory(new_constants, new_sensors, new_rules, new_reactions);
	}
}
