/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.exec.services.ModelService;
import ru.parallel.octotron.generators.tmpl.*;

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
		, List<ConstTemplate> constants
		, List<ConstTemplate> statics
		, List<SensorTemplate> sensors
		, List<VarTemplate> rules
		, List<TriggerTemplate> triggers
		, List<ReactionTemplate> reactions)
	{
		super(service, constants, statics, sensors, rules, triggers, reactions);
	}

	/**
	 * create single vertices
	 * */
	public ModelObject Create()
	{
		ModelObject object = service.AddObject();

		object.GetBuilder(service).DeclareConst(constants);
		object.GetBuilder(service).DeclareStatic(statics);
		object.GetBuilder(service).DeclareSensor(sensors);
		object.GetBuilder(service).DeclareVar(rules);
		object.GetBuilder(service).DeclareTrigger(triggers);
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
		List<ConstTemplate> new_constants
		, List<ConstTemplate> new_statics
		, List<SensorTemplate> new_sensors
		, List<VarTemplate> new_rules
		, List<TriggerTemplate> new_triggers
		, List<ReactionTemplate> new_reactions)
	{
		return new ObjectFactory(service, new_constants, new_statics, new_sensors, new_rules, new_triggers, new_reactions);
	}
}
