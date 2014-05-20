/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import java.util.Arrays;
import java.util.List;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.core.OctoLink;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.OctoRule;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionDBError;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.utils.LinkList;
import ru.parallel.octotron.utils.ObjectList;

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

@Override
	public LinkFactory Rules(OctoRule... params)
	{
		throw new ExceptionModelFail("links do not have rules");
	}

@Override
	public LinkFactory Reactions(OctoReaction... params)
	{
		throw new ExceptionModelFail("links do not have reactions");
	}

/**
 * connect one \from object with one \to objects<br>
 * set correct type, if "type" attribute is set correctly
 * @throws ExceptionDBError
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
		OctoLink new_edge = graph_service.AddLink(from, to, (String)type.GetValue());

// set all attributes
		new_edge.DeclareAttributes(attributes);

		new_edge.DeclareAttribute("source", from.GetAttribute("AID").GetLong());
		new_edge.DeclareAttribute("target", to.GetAttribute("AID").GetLong());

		return new_edge;
	}

/**
 * connect all \from objects with one \to object<br>
 * adds new attributes
 * create \from.length edges<br>
 * */
	public LinkList EveryToOne(ObjectList from, OctoObject to)
	{
		LinkList links = new LinkList();

		for(int i = 0; i < from.size(); i++)
			links.add(OneToOne(from.get(i), to));

		return links;
	}

/**
 * connect one \from object with all \to objects<br>
 * create \to.length edges<br>
 * @throws ExceptionDBError
 * */
	public LinkList OneToEvery(OctoObject from, ObjectList to)
	{
		LinkList links = new LinkList();

		for(int i = 0; i < to.size(); i++)
			links.add(OneToOne(from, to.get(i)));

		return links;
	}

/**
 * connect Nth object from \from with Nth object from \to<br>
 * create N edges, N == \from.length ==\to.length<br>
 * if \from.length != \to.length - error<br>
 * @throws ExceptionDBError
 * */
	public LinkList EveryToEvery(ObjectList from, ObjectList to)
	{
		if(from.size() != to.size())
			throw new ExceptionModelFail
				("all-to-all connector, sizes do not match: from="
					+ from.size() + " to=" + to.size());

		LinkList links = new LinkList();

		for(int i = 0; i < from.size(); i++)
			links.add(OneToOne(from.get(i), to.get(i)));

		return links;
	}

/**
 * connect every object from \from list with every object from \to list<br>
 * create M*N edges, M == \from.length, N = \to.length<br>
 * */
	public LinkList AllToAll(ObjectList from, ObjectList to)
	{
		LinkList res_links = new LinkList();

		for(int i = 0; i < from.size(); i++)
			res_links.append(OneToEvery(from.get(i), to));

		return res_links;
	}

/**
 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
 * create L edges, L = \to.length<br>
 * \to.length must be divisible by \from.length<br>
 * @throws ExceptionDBError
 * */
	public LinkList EveryToChunks(ObjectList from, ObjectList to)
	{
		if(to.size() % from.size() != 0 || to.size() < from.size())
			throw new ExceptionModelFail
				("every-to-chunks connector, sizes do not match: from="
					+ from.size() + " to=" + to.size());

		LinkList links = new LinkList();

		for(int i = 0; i < to.size(); i++)
			links.add(OneToOne(from.get(i / (to.size() / from.size())), to.get(i)));

		return links;
	}

/**
 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
 * create L edges, L = \to.length<br>
 * for last object M can be less than usual
 * @throws ExceptionDBError
 * */
	public LinkList EveryToChunks_LastLess(ObjectList from, ObjectList to)
	{
		int chunk = (to.size() / from.size() + 1);
		int diff = to.size() - chunk * from.size();

		if(diff > chunk || to.size() < from.size())
			throw new ExceptionModelFail
				("every-to-chunks-less connector, sizes do not match: from="
					+ from.size() + " to=" + to.size());

		LinkList links = new LinkList();

		for(int i = 0; i < to.size(); i++)
			links.add(OneToOne(from.get(i / chunk), to.get(i)));

		return links;
	}

	/**
	 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
	 * create L edges, L = \to.length<br>
	 * M comes from \size arrays
	 * @throws ExceptionDBError
	 * */
	public LinkList ChunksToEvery_Guided(ObjectList from, ObjectList to
		, int[] sizes)
	{
		if(to.size() != sizes.length)
			throw new ExceptionModelFail
				("ChunksToEvery_Guided connector, not enough elements in guiding array: from="
					+ from.size() + " sizes.length=" + sizes.length);

		LinkList links = new LinkList();

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
 * @throws ExceptionDBError
 * */
	public LinkList ChunksToEvery(ObjectList from, ObjectList to)
	{
		if(from.size() % to.size() != 0 || from.size() < to.size())
			throw new ExceptionModelFail
				("chunks-to-all connector, sizes do not match: from="
					+ from.size() + " to=" + to.size());

		LinkList links = new LinkList();

		for(int i = 0; i < from.size(); i++)
			links.add(OneToOne(from.get(i), to.get(i / (from.size() / to.size()))));

		return links;
	}

	/**
	 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
	 * create L edges, L = \to.length<br>
	 * \to.length must be divisible by \from.length<br>
	 * @throws ExceptionDBError
	 * */
	public LinkList ChunksToEvery_LastLess(ObjectList from, ObjectList to
		, Object... addition)
	{
		int chunk = (from.size() / to.size() + 1);
		int diff = from.size() - chunk * to.size();

		if(diff > chunk || from.size() < to.size())
			throw new ExceptionModelFail
				("chunks-to-all connector, sizes do not match: from="
					+ from.size() + " to=" + to.size() + " diff=" + diff + " chunk=" + chunk);

		LinkList links = new LinkList();

		for(int i = 0; i < from.size(); i++)
			links.add(OneToOne(from.get(i), to.get(i / chunk)));

		return links;
	}

	/**
	 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
	 * create L edges, L = \to.length<br>
	 * M comes from \size arrays
	 * @throws ExceptionDBError
	 * */
	public LinkList EveryToChunks_Guided(ObjectList from, ObjectList to
		, int[] sizes)
	{
		if(from.size() != sizes.length)
			throw new ExceptionModelFail
				("EveryToChunks_Guided connector, not enough elements in guiding array: from="
					+ from.size() + " sizes.length=" + sizes.length);

		LinkList links = new LinkList();

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
