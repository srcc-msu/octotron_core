/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.rule.OctoRule;

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
	protected final ModelService model_service;

/**
 * attribute template that will be used for all created entities<br>
 * must be cloned<br>
 * */
	protected final List<SimpleAttribute> constants;
	protected final List<SimpleAttribute> sensors;
	protected final List<OctoRule> rules;
	protected final List<OctoReaction> reactions;

	protected BaseFactory(ModelService model_service
		, List<SimpleAttribute> constants
		, List<SimpleAttribute> sensors
		, List<OctoRule> rules
		, List<OctoReaction> reactions)
	{
		this.model_service = model_service;

		this.constants = constants;
		this.sensors = sensors;
		this.rules = rules;
		this.reactions = reactions;
	}

	protected BaseFactory(ModelService model_service)
	{
		this(model_service
			, new LinkedList<SimpleAttribute>()
			, new LinkedList<SimpleAttribute>()
			, new LinkedList<OctoRule>()
			, new LinkedList<OctoReaction>());
	}

	public T Constants(SimpleAttribute... addition)
	{
		List<SimpleAttribute> new_constants = new LinkedList<>(constants);
		new_constants.addAll(Arrays.asList(addition));

		return Clone(new_constants, sensors, rules, reactions);
	}

	public T Sensors(SimpleAttribute... addition)
	{
		List<SimpleAttribute> new_sensors = new LinkedList<>(sensors);
		new_sensors.addAll(Arrays.asList(addition));

		return Clone(constants, new_sensors, rules, reactions);
	}

	public T Rules(OctoRule... addition)
	{
		List<OctoRule> new_rules = new LinkedList<>(rules);
		new_rules.addAll(Arrays.asList(addition));

		return Clone(constants, sensors, new_rules, reactions);
	}

	public T Reactions(OctoReaction... addition)
	{
		List<OctoReaction> new_reactions = new LinkedList<>(reactions);
		new_reactions.addAll(Arrays.asList(addition));

		return Clone(constants, sensors, rules, new_reactions);
	}

	protected abstract T Clone(
		List<SimpleAttribute> new_constants
		, List<SimpleAttribute> new_sensors
		, List<OctoRule> new_rules
		, List<OctoReaction> new_reactions);
}
