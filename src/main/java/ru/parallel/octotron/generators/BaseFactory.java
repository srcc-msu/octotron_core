/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.exec.services.ModelService;

import ru.parallel.octotron.generators.tmpl.ConstTemplate;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;
import ru.parallel.octotron.generators.tmpl.VarTemplate;
import ru.parallel.octotron.generators.tmpl.SensorTemplate;

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
	protected final List<SensorTemplate> sensors;
	protected final List<VarTemplate> rules;
	protected final List<ReactionTemplate> reactions;

	protected BaseFactory(ModelService service, List<ConstTemplate> constants
		, List<SensorTemplate> sensors
		, List<VarTemplate> rules
		, List<ReactionTemplate> reactions)
	{
		this.service = service;
		this.constants = constants;
		this.sensors = sensors;
		this.rules = rules;
		this.reactions = reactions;
	}

	protected BaseFactory(ModelService service)
	{
		this(service
			, new LinkedList<ConstTemplate>()
			, new LinkedList<SensorTemplate>()
			, new LinkedList<VarTemplate>()
			, new LinkedList<ReactionTemplate>());
	}

	public T Constants(ConstTemplate... addition)
	{
		List<ConstTemplate> new_constants = new LinkedList<>(constants);
		new_constants.addAll(Arrays.asList(addition));

		return Clone(new_constants, sensors, rules, reactions);
	}

	public T Sensors(SensorTemplate... addition)
	{
		List<SensorTemplate> new_sensors = new LinkedList<>(sensors);
		new_sensors.addAll(Arrays.asList(addition));

		return Clone(constants, new_sensors, rules, reactions);
	}

	public T Vars(VarTemplate... addition)
	{
		List<VarTemplate> new_rules = new LinkedList<>(rules);
		new_rules.addAll(Arrays.asList(addition));

		return Clone(constants, sensors, new_rules, reactions);
	}

	public T Reactions(ReactionTemplate... addition)
	{
		List<ReactionTemplate> new_reactions = new LinkedList<>(reactions);
		new_reactions.addAll(Arrays.asList(addition));

		return Clone(constants, sensors, rules, new_reactions);
	}

	protected abstract T Clone(
		List<ConstTemplate> new_constants
		, List<SensorTemplate> new_sensors
		, List<VarTemplate> new_rules
		, List<ReactionTemplate> new_reactions);
}
