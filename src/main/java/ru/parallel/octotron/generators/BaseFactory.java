/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Rule;
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
/**
 * attribute template that will be used for all created entities<br>
 * must be cloned<br>
 * */
	protected final List<SimpleAttribute> constants;
	protected final List<SimpleAttribute> sensors;
	protected final List<Rule> rules;
	protected final List<Reaction> reactions;

	protected BaseFactory(List<SimpleAttribute> constants
		, List<SimpleAttribute> sensors
		, List<Rule> rules
		, List<Reaction> reactions)
	{
		this.constants = constants;
		this.sensors = sensors;
		this.rules = rules;
		this.reactions = reactions;
	}

	protected BaseFactory()
	{
		this(new LinkedList<SimpleAttribute>()
			, new LinkedList<SimpleAttribute>()
			, new LinkedList<Rule>()
			, new LinkedList<Reaction>());
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

	public T Varyings(Rule... addition)
	{
		List<Rule> new_rules = new LinkedList<>(rules);
		new_rules.addAll(Arrays.asList(addition));

		return Clone(constants, sensors, new_rules, reactions);
	}

	public T Reactions(Reaction... addition)
	{
		List<Reaction> new_reactions = new LinkedList<>(reactions);
		new_reactions.addAll(Arrays.asList(addition));

		return Clone(constants, sensors, rules, new_reactions);
	}

	protected abstract T Clone(
		List<SimpleAttribute> new_constants
		, List<SimpleAttribute> new_sensors
		, List<Rule> new_rules
		, List<Reaction> new_reactions);
}
