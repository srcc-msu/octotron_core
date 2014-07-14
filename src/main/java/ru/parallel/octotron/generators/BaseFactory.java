/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.rule.OctoRule;
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
	protected final GraphService graph_service;

/**
 * attribute template that will be used for all created entities<br>
 * must be cloned<br>
 * */
	protected final List<SimpleAttribute> attributes;
	protected final List<OctoRule> rules;
	protected final List<OctoReaction> reactions;

	protected BaseFactory(GraphService graph_service
		, List<SimpleAttribute> attributes
		, List<OctoRule> rules
		, List<OctoReaction> reactions)
	{
		this.graph_service = graph_service;

		this.attributes = attributes;
		this.rules = rules;
		this.reactions = reactions;
	}

	protected BaseFactory(GraphService graph_service)
	{
		this(graph_service
			, new LinkedList<SimpleAttribute>()
			, new LinkedList<OctoRule>()
			, new LinkedList<OctoReaction>());
	}

	public T Attributes(SimpleAttribute... addition)
	{
		List<SimpleAttribute> new_attributes = new LinkedList<>(attributes);
		new_attributes.addAll(Arrays.asList(addition));

		return Clone(new_attributes, rules, reactions);
	}

	public T Rules(OctoRule... addition)
	{
		List<OctoRule> new_rules = new LinkedList<>(rules);
		new_rules.addAll(Arrays.asList(addition));

		return Clone(attributes, new_rules, reactions);
	}

	public T Reactions(OctoReaction... addition)
	{
		List<OctoReaction> new_reactions = new LinkedList<>(reactions);
		new_reactions.addAll(Arrays.asList(addition));

		return Clone(attributes, rules, new_reactions);
	}

	protected abstract T Clone(List<SimpleAttribute> new_attributes
		, List<OctoRule> new_rules
		, List<OctoReaction> new_reactions);
}
