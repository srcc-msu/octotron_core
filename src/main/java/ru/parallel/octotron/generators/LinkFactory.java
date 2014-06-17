/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.*;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.utils.OctoLinkList;
import ru.parallel.octotron.utils.OctoObjectList;

import java.util.Arrays;
import java.util.List;

/**
 * advanced factory for constructing multiple edges,<br>
 * that connect multiple objects basing on certain patterns<br>
 * */
public class LinkFactory extends BaseFactory<LinkFactory>
{
	public LinkFactory(GraphService graph_service)
	{
		super(graph_service);
	}

	private LinkFactory(GraphService graph_service
		, List<SimpleAttribute> attributes
		, List<OctoRule> rules
		, List<OctoReaction> reactions)
	{
		super(graph_service, attributes, rules, reactions);
	}
/*
@Override
	public LinkFactory Rules(OctoRule... params)
	{
		throw new ExceptionModelFail("links do not have rules");
	}

@Override
	public LinkFactory Reactions(OctoReaction... params)
	{
		throw new ExceptionModelFail("links do not have reactions");
	}*/

/**
 * connect one \from object with one \to objects<br>
 * set correct type, if "type" attribute is set correctly
 * */
	public OctoLink OneToOne(OctoObject from, OctoObject to)
	{
// find type attribute
		SimpleAttribute type = null;

		for(SimpleAttribute att : attributes)
		{
			if(att.GetName().equals("type"))
				type = att;
		}

		if(type == null)
			throw new ExceptionModelFail("link type not set");

// create edge
		OctoLink link = graph_service.AddLink(from, to, (String)type.GetValue());

// set all attributes
		link.DeclareAttributes(attributes);
		link.AddRules(rules);
		link.AddReactions(reactions);

		link.DeclareAttribute("source", from.GetAttribute("AID").GetLong());
		link.DeclareAttribute("target", to.GetAttribute("AID").GetLong());

		return link;
	}

/**
 * connect all \from objects with one \to object<br>
 * adds new attributes
 * create \from.length edges<br>
 * */
	public OctoLinkList EveryToOne(OctoObjectList from, OctoObject to)
	{
		OctoLinkList links = new OctoLinkList();

		for(int i = 0; i < from.size(); i++)
			links.add(OneToOne(from.get(i), to));

		return links;
	}

/**
 * connect one \from object with all \to objects<br>
 * create \to.length edges<br>
 * */
	public OctoLinkList OneToEvery(OctoObject from, OctoObjectList to)
	{
		OctoLinkList links = new OctoLinkList();

		for(int i = 0; i < to.size(); i++)
			links.add(OneToOne(from, to.get(i)));

		return links;
	}

/**
 * connect Nth object from \from with Nth object from \to<br>
 * create N edges, N == \from.length ==\to.length<br>
 * if \from.length != \to.length - error<br>
 * */
	public OctoLinkList EveryToEvery(OctoObjectList from, OctoObjectList to)
	{
		if(from.size() != to.size())
			throw new ExceptionModelFail
				("all-to-all connector, sizes do not match: from="
					+ from.size() + " to=" + to.size());

		OctoLinkList links = new OctoLinkList();

		for(int i = 0; i < from.size(); i++)
			links.add(OneToOne(from.get(i), to.get(i)));

		return links;
	}

/**
 * connect every object from \from list with every object from \to list<br>
 * create M*N edges, M == \from.length, N = \to.length<br>
 * */
	public OctoLinkList AllToAll(OctoObjectList from, OctoObjectList to)
	{
		OctoLinkList res_links = new OctoLinkList();

		for(int i = 0; i < from.size(); i++)
			res_links.append(OneToEvery(from.get(i), to));

		return res_links;
	}

/**
 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
 * create L edges, L = \to.length<br>
 * \to.length must be divisible by \from.length<br>
 * */
	public OctoLinkList EveryToChunks(OctoObjectList from, OctoObjectList to)
	{
		if(to.size() % from.size() != 0 || to.size() < from.size())
			throw new ExceptionModelFail
				("every-to-chunks connector, sizes do not match: from="
					+ from.size() + " to=" + to.size());

		OctoLinkList links = new OctoLinkList();

		for(int i = 0; i < to.size(); i++)
			links.add(OneToOne(from.get(i / (to.size() / from.size())), to.get(i)));

		return links;
	}

/**
 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
 * create L edges, L = \to.length<br>
 * for last object M can be less than usual
 * */
	public OctoLinkList EveryToChunks_LastLess(OctoObjectList from, OctoObjectList to)
	{
		int chunk = (to.size() / from.size() + 1);
		int diff = to.size() - chunk * from.size();

		if(diff > chunk || to.size() < from.size())
			throw new ExceptionModelFail
				("every-to-chunks-less connector, sizes do not match: from="
					+ from.size() + " to=" + to.size());

		OctoLinkList links = new OctoLinkList();

		for(int i = 0; i < to.size(); i++)
			links.add(OneToOne(from.get(i / chunk), to.get(i)));

		return links;
	}

	/**
	 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
	 * create L edges, L = \to.length<br>
	 * M comes from \size arrays
	 * */
	public OctoLinkList ChunksToEvery_Guided(OctoObjectList from, OctoObjectList to
		, int[] sizes)
	{
		if(to.size() != sizes.length)
			throw new ExceptionModelFail
				("ChunksToEvery_Guided connector, not enough elements in guiding array: from="
					+ from.size() + " sizes.length=" + sizes.length);

		OctoLinkList links = new OctoLinkList();

		int counter = 0;

		for(int i = 0; i < to.size(); i++)
		{
			if(counter > from.size() || counter + sizes[i] > from.size())
				throw new ExceptionModelFail
					("ChunksToEvery_Guided connector, too many elements specified: "
						+ "to: " + to.size() + " from: " + from.size() + " sizes: " + Arrays.toString(sizes));

			links.append(EveryToOne(
				from.range(counter, counter + sizes[i]), to.get(i)));

			counter += sizes[i];
		}

		if(counter < from.size())
			System.err.println
				("warning: ChunksToEvery_Guided connector, not all elements used");

		return links;
	}

/**
 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
 * create L edges, L = \to.length<br>
 * \to.length must be divisible by \from.length<br>
 * */
	public OctoLinkList ChunksToEvery(OctoObjectList from, OctoObjectList to)
	{
		if(from.size() % to.size() != 0 || from.size() < to.size())
			throw new ExceptionModelFail
				("chunks-to-all connector, sizes do not match: from="
					+ from.size() + " to=" + to.size());

		OctoLinkList links = new OctoLinkList();

		for(int i = 0; i < from.size(); i++)
			links.add(OneToOne(from.get(i), to.get(i / (from.size() / to.size()))));

		return links;
	}

	/**
	 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
	 * create L edges, L = \to.length<br>
	 * \to.length must be divisible by \from.length<br>
	 * */
	public OctoLinkList ChunksToEvery_LastLess(OctoObjectList from, OctoObjectList to)
	{
		int chunk = (from.size() / to.size() + 1);
		int diff = from.size() - chunk * to.size();

		if(diff > chunk || from.size() < to.size())
			throw new ExceptionModelFail
				("chunks-to-all connector, sizes do not match: from="
					+ from.size() + " to=" + to.size() + " diff=" + diff + " chunk=" + chunk);

		OctoLinkList links = new OctoLinkList();

		for(int i = 0; i < from.size(); i++)
			links.add(OneToOne(from.get(i), to.get(i / chunk)));

		return links;
	}

	/**
	 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
	 * create L edges, L = \to.length<br>
	 * M comes from \size arrays
	 * */
	public OctoLinkList EveryToChunks_Guided(OctoObjectList from, OctoObjectList to
		, int[] sizes)
	{
		if(from.size() != sizes.length)
			throw new ExceptionModelFail
				("EveryToChunks_Guided connector, not enough elements in guiding array: from="
					+ from.size() + " sizes.length=" + sizes.length);

		OctoLinkList links = new OctoLinkList();

		int counter = 0;

		for(int i = 0; i < from.size(); i++)
		{
			if(counter > to.size() || counter + sizes[i] > to.size())
				throw new ExceptionModelFail(
					"EveryToChunks_Guided connector, too many elements specified: "
						+ "to: " + to.size() + " from: " + from.size() + " sizes: " + Arrays.toString(sizes));

			links.append(OneToEvery(
				from.get(i), to.range(counter, counter + sizes[i])));

			counter += sizes[i];
		}

		if(counter < to.size())
			System.err.println
				("warning: EveryToChunks_Guided connector, not all elements used");

		return links;
	}

	@Override
	protected LinkFactory Clone(List<SimpleAttribute> new_attributes
		, List<OctoRule> new_rules
		, List<OctoReaction> new_reactions)
	{
		return new LinkFactory(graph_service, new_attributes, new_rules, new_reactions);
	}
}
