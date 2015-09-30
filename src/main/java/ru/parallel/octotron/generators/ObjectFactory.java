/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.generators.tmpl.*;
import ru.parallel.octotron.services.ServiceLocator;
import ru.parallel.octotron.services.impl.ModelService;

import java.util.List;

/**
 * Basic Vertex factory, creates one or multiple edges<br>
 * */
public class ObjectFactory extends BaseFactory<ObjectFactory>
{
	public ObjectFactory() {}

	private ObjectFactory(List<ConstTemplate> constants
		, List<ConstTemplate> statics
		, List<SensorTemplate> sensors
		, List<VarTemplate> rules
		, List<TriggerTemplate> triggers
		, List<ReactionTemplate> reactions)
	{
		super(constants, statics, sensors, rules, triggers, reactions);
	}

	/**
	 * create single vertices
	 * */
	public ModelObject Create()
	{
		ModelService service = ServiceLocator.INSTANCE.GetModelService();
		ModelObject object = service.AddObject();

		object.GetBuilder().DeclareConst(constants);
		object.GetBuilder().DeclareStatic(statics);
		object.GetBuilder().DeclareSensor(sensors);
		object.GetBuilder().DeclareVar(rules);
		object.GetBuilder().DeclareTrigger(triggers);
		object.GetBuilder().DeclareReaction(reactions);

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
		return new ObjectFactory(new_constants, new_statics, new_sensors, new_rules, new_triggers, new_reactions);
	}
}
