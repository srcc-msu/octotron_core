/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.impl.generators;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.core.OctoReaction;
import main.java.ru.parallel.octotron.core.OctoRule;
import main.java.ru.parallel.octotron.primitive.SimpleAttribute;
import main.java.ru.parallel.utils.JavaUtils;

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

	public BaseFactory(GraphService graph_service)
	{
		this(graph_service
			, new LinkedList<SimpleAttribute>()
			, new LinkedList<OctoRule>()
			, new LinkedList<OctoReaction>());
	}

	public T Attributes(SimpleAttribute... addition)
	{
		List<SimpleAttribute> new_attributes = new LinkedList<SimpleAttribute>(attributes);
		new_attributes.addAll(Arrays.asList(addition));

		return Clone(new_attributes, rules, reactions);
	}

	public T Attributes(SimpleAttribute[]... additions)
	{
		SimpleAttribute[] addition = JavaUtils.ExtendMultiArrayChecked(additions, SimpleAttribute[].class);

		List<SimpleAttribute> new_attributes = new LinkedList<SimpleAttribute>(attributes);
		new_attributes.addAll(Arrays.asList(addition));

		return Clone(new_attributes, rules, reactions);
	}

	public T Rules(OctoRule... addition)
	{
		List<OctoRule> new_rules = new LinkedList<OctoRule>(rules);
		new_rules.addAll(Arrays.asList(addition));

		return Clone(attributes, new_rules, reactions);
	}

	public T Rules(OctoRule[]... additions)
	{
		OctoRule[] addition = JavaUtils.ExtendMultiArrayChecked(additions, OctoRule[].class);

		List<OctoRule> new_rules = new LinkedList<OctoRule>(rules);
		new_rules.addAll(Arrays.asList(addition));

		return Clone(attributes, new_rules, reactions);
	}

	public T Reactions(OctoReaction... addition)
	{
		List<OctoReaction> new_reactions = new LinkedList<OctoReaction>(reactions);
		new_reactions.addAll(Arrays.asList(addition));

		return Clone(attributes, rules, new_reactions);
	}

	public T Reactions(OctoReaction[]... additions)
	{
		OctoReaction[] addition = JavaUtils.ExtendMultiArrayChecked(additions, OctoReaction[].class);

		List<OctoReaction> new_reactions = new LinkedList<OctoReaction>(reactions);
		new_reactions.addAll(Arrays.asList(addition));

		return Clone(attributes, rules, new_reactions);
	}

	protected abstract T Clone(List<SimpleAttribute> new_attributes
		, List<OctoRule> new_rules
		, List<OctoReaction> new_reactions);
}
