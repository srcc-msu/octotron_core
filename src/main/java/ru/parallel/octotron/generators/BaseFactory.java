/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.logic.ReactionTemplate;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

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
	protected final List<SimpleAttribute> constants;
	protected final List<SimpleAttribute> sensors;
	protected final List<SimpleAttribute> rules;
	protected final List<ReactionTemplate> reactions;

	protected BaseFactory(ModelService service, List<SimpleAttribute> constants
		, List<SimpleAttribute> sensors
		, List<SimpleAttribute> rules
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
			, new LinkedList<SimpleAttribute>()
			, new LinkedList<SimpleAttribute>()
			, new LinkedList<SimpleAttribute>()
			, new LinkedList<ReactionTemplate>());
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

	public T Varyings(SimpleAttribute... addition)
	{
		List<SimpleAttribute> new_rules = new LinkedList<>(rules);
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
		List<SimpleAttribute> new_constants
		, List<SimpleAttribute> new_sensors
		, List<SimpleAttribute> new_rules
		, List<ReactionTemplate> new_reactions);
}
