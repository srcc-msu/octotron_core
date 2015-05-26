/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.exec.services.ModelService;
import ru.parallel.octotron.generators.tmpl.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract factory for constructing entities with<br>
 * predefined set of \attributes for the given \graph<br>
 *<br>
 * */
public abstract class BaseFactory<T>
{
	protected final ModelService service;
/**
 * attribute template that will be used for all created entities<br>
 * must be cloned<br>
 * */
	protected final List<ConstTemplate> constants;
	protected final List<ConstTemplate> statics;
	protected final List<SensorTemplate> sensors;
	protected final List<VarTemplate> rules;
	protected final List<TriggerTemplate> triggers;
	protected final List<ReactionTemplate> reactions;

	protected BaseFactory(ModelService service
		, List<ConstTemplate> constants
		, List<ConstTemplate> statics
		, List<SensorTemplate> sensors
		, List<VarTemplate> rules
		, List<TriggerTemplate> triggers
		, List<ReactionTemplate> reactions)
	{
		this.service = service;
		this.constants = constants;
		this.statics = statics;
		this.sensors = sensors;
		this.rules = rules;
		this.triggers = triggers;
		this.reactions = reactions;
	}

	protected BaseFactory(ModelService service)
	{
		this(service
			, new LinkedList<ConstTemplate>()
			, new LinkedList<ConstTemplate>()
			, new LinkedList<SensorTemplate>()
			, new LinkedList<VarTemplate>()
			, new LinkedList<TriggerTemplate>()
			, new LinkedList<ReactionTemplate>());
	}

	public T Constants(ConstTemplate... addition)
	{
		List<ConstTemplate> new_constants = new LinkedList<>(constants);
		new_constants.addAll(Arrays.asList(addition));

		return Clone(new_constants, statics, sensors, rules, triggers, reactions);
	}

	public T Statics(ConstTemplate... addition)
	{
		List<ConstTemplate> new_statics = new LinkedList<>(statics);
		new_statics.addAll(Arrays.asList(addition));

		return Clone(constants, new_statics, sensors, rules, triggers, reactions);
	}

	public T Sensors(SensorTemplate... addition)
	{
		List<SensorTemplate> new_sensors = new LinkedList<>(sensors);
		new_sensors.addAll(Arrays.asList(addition));

		return Clone(constants, statics, new_sensors, rules, triggers, reactions);
	}

	public T Vars(VarTemplate... addition)
	{
		List<VarTemplate> new_rules = new LinkedList<>(rules);
		new_rules.addAll(Arrays.asList(addition));

		return Clone(constants, statics, sensors, new_rules, triggers, reactions);
	}

	public T Reactions(ReactionTemplate... addition)
	{
		List<ReactionTemplate> new_reactions = new LinkedList<>(reactions);
		new_reactions.addAll(Arrays.asList(addition));

		return Clone(constants, statics, sensors, rules, triggers, new_reactions);
	}

	public T Triggers(TriggerTemplate... addition)
	{
		List<TriggerTemplate> new_triggers = new LinkedList<>(triggers);
		new_triggers.addAll(Arrays.asList(addition));

		return Clone(constants, statics, sensors, rules, new_triggers, reactions);
	}

	protected abstract T Clone(
		List<ConstTemplate> new_constants
		, List<ConstTemplate> new_statics
		, List<SensorTemplate> new_sensors
		, List<VarTemplate> new_rules
		, List<TriggerTemplate> triggers
		, List<ReactionTemplate> new_reactions);
}
