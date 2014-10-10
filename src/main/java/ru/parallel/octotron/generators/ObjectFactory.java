/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.logic.ReactionTemplate;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.util.List;

/**
 * Basic Vertex factory, creates one or multiple edges<br>
 * */
public class ObjectFactory extends BaseFactory<ObjectFactory>
{
	public ObjectFactory(ModelService service)
	{
		super(service);
	}

	private ObjectFactory(ModelService service
		, List<SimpleAttribute> constants
		, List<SimpleAttribute> sensors
		, List<Rule> rules
		, List<ReactionTemplate> reactions)
	{
		super(service, constants, sensors, rules, reactions);
	}

	/**
	 * create single vertices
	 * */
	public ModelObject Create()
	{
		ModelObject object = service.AddObject();

		object.GetBuilder(service).DeclareConst(constants);
		object.GetBuilder(service).DeclareSensor(sensors);
		object.GetBuilder(service).DeclareVar(rules);
		object.GetBuilder(service).AddReaction(reactions);

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
		, List<Rule> new_rules
		, List<ReactionTemplate> new_reactions)
	{
		return new ObjectFactory(service, new_constants, new_sensors, new_rules, new_reactions);
	}
}
