/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.exec.services.ModelService;
import ru.parallel.octotron.generators.tmpl.*;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * advanced factory for constructing multiple edges,<br>
 * that connect multiple objects basing on certain patterns<br>
 * */
public class LinkFactory extends BaseFactory<LinkFactory>
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	public LinkFactory(ModelService service)
	{
		super(service);
	}

	private LinkFactory(ModelService service
		, List<ConstTemplate> constants
		, List<ConstTemplate> statics
		, List<SensorTemplate> sensors
		, List<VarTemplate> rules
		, List<TriggerTemplate> triggers
		, List<ReactionTemplate> reactions)
	{
		super(service, constants, statics, sensors, rules, triggers, reactions);
	}

/**
 * connect one \from object with one \to objects<br>
 * set correct type, if "type" attribute is set correctly
 * */
	public ModelLink OneToOne(ModelObject from, ModelObject to, boolean directed)
	{
// create edge
		ModelLink link = service.AddLink(from, to, directed);

// set all attributes
		link.GetBuilder(service).DeclareConst(constants);
		link.GetBuilder(service).DeclareStatic(statics);
		link.GetBuilder(service).DeclareSensor(sensors);
		link.GetBuilder(service).DeclareVar(rules);
		link.GetBuilder(service).DeclareTrigger(triggers);
		link.GetBuilder(service).AddReaction(reactions);

		link.GetBuilder(service).DeclareConst("directed", link.IsDirected());

		if(link.IsDirected())
		{
			link.GetBuilder(service).DeclareConst("source", from.GetID());
			link.GetBuilder(service).DeclareConst("target", to.GetID());
		}
		else
		{
			link.GetBuilder(service).DeclareConst("left", from.GetID());
			link.GetBuilder(service).DeclareConst("right", to.GetID());
		}

		return link;
	}

/**
 * connect all \from objects with one \to object<br>
 * adds new attributes
 * create \from.length edges<br>
 * */
	public ModelLinkList EveryToOne(ModelObjectList from, ModelObject to, boolean directed)
	{
		ModelLinkList links = new ModelLinkList();

		for(int i = 0; i < from.size(); i++)
			links.add(OneToOne(from.get(i), to, directed));

		return links;
	}

/**
 * connect one \from object with all \to objects<br>
 * create \to.length edges<br>
 * */
	public ModelLinkList OneToEvery(ModelObject from, ModelObjectList to, boolean directed)
	{
		ModelLinkList links = new ModelLinkList();

		for(int i = 0; i < to.size(); i++)
			links.add(OneToOne(from, to.get(i), directed));

		return links;
	}

/**
 * connect Nth object from \from with Nth object from \to<br>
 * create N edges, N == \from.length ==\to.length<br>
 * if \from.length != \to.length - error<br>
 * */
	public ModelLinkList EveryToEvery(ModelObjectList from, ModelObjectList to, boolean directed)
	{
		if(from.size() != to.size())
			throw new ExceptionModelFail
				("every-to-every connector, sizes do not match: from="
					+ from.size() + " to=" + to.size());

		ModelLinkList links = new ModelLinkList();

		for(int i = 0; i < from.size(); i++)
			links.add(OneToOne(from.get(i), to.get(i), directed));

		return links;
	}

/**
 * connect every object from \from list with every object from \to list<br>
 * create M*N edges, M == \from.length, N = \to.length<br>
 * */
	public ModelLinkList AllToAll(ModelObjectList from, ModelObjectList to, boolean directed)
	{
		ModelLinkList res_links = new ModelLinkList();

		for(int i = 0; i < from.size(); i++)
			res_links = res_links.append(OneToEvery(from.get(i), to, directed));

		return res_links;
	}

/**
 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
 * create L edges, L = \to.length<br>
 * \to.length must be divisible by \from.length<br>
 * */
	public ModelLinkList EveryToChunks(ModelObjectList from, ModelObjectList to, boolean directed)
	{
		if(to.size() % from.size() != 0 || to.size() < from.size())
			throw new ExceptionModelFail
				("every-to-chunks connector, sizes do not match: from="
					+ from.size() + " to=" + to.size());

		ModelLinkList links = new ModelLinkList();

		for(int i = 0; i < to.size(); i++)
			links.add(OneToOne(from.get(i / (to.size() / from.size())), to.get(i), directed));

		return links;
	}

/**
 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
 * create L edges, L = \to.length<br>
 * for last object M can be less than usual
 * */
	public ModelLinkList EveryToChunks_LastLess(ModelObjectList from, ModelObjectList to, boolean directed)
	{
		int chunk = (to.size() / from.size() + 1);
		int diff = to.size() - chunk * from.size();

		if(diff > chunk || to.size() < from.size())
			throw new ExceptionModelFail
				("every-to-chunks-less connector, sizes do not match: from="
					+ from.size() + " to=" + to.size());

		ModelLinkList links = new ModelLinkList();

		for(int i = 0; i < to.size(); i++)
			links.add(OneToOne(from.get(i / chunk), to.get(i), directed));

		return links;
	}

	/**
	 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
	 * create L edges, L = \to.length<br>
	 * M comes from \size arrays
	 * */
	public ModelLinkList ChunksToEvery_Guided(ModelObjectList from, ModelObjectList to, boolean directed
		, int[] sizes)
	{
		if(to.size() != sizes.length)
			throw new ExceptionModelFail
				("ChunksToEvery_Guided connector, not enough elements in guiding array: from="
					+ from.size() + " sizes.length=" + sizes.length);

		ModelLinkList links = new ModelLinkList();

		int counter = 0;

		for(int i = 0; i < to.size(); i++)
		{
			if(counter > from.size() || counter + sizes[i] > from.size())
				throw new ExceptionModelFail
					("ChunksToEvery_Guided connector, too many elements specified: "
						+ "to: " + to.size() + " from: " + from.size() + " sizes: " + Arrays.toString(sizes));

			links = links.append(EveryToOne(
				from.range(counter, counter + sizes[i]), to.get(i), directed));

			counter += sizes[i];
		}

		if(counter < from.size())
			LOGGER.log(Level.WARNING,"ChunksToEvery_Guided connector, not all elements used");

		return links;
	}

/**
 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
 * create L edges, L = \to.length<br>
 * \to.length must be divisible by \from.length<br>
 * */
	public ModelLinkList ChunksToEvery(ModelObjectList from, ModelObjectList to, boolean directed)
	{
		if(from.size() % to.size() != 0 || from.size() < to.size())
			throw new ExceptionModelFail
				("chunks-to-all connector, sizes do not match: from="
					+ from.size() + " to=" + to.size());

		ModelLinkList links = new ModelLinkList();

		for(int i = 0; i < from.size(); i++)
			links.add(OneToOne(from.get(i), to.get(i / (from.size() / to.size())), directed));

		return links;
	}

	/**
	 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
	 * create L edges, L = \to.length<br>
	 * \to.length must be divisible by \from.length<br>
	 * */
	public ModelLinkList ChunksToEvery_LastLess(ModelObjectList from, ModelObjectList to, boolean directed)
	{
		int chunk = (from.size() / to.size() + 1);
		int diff = from.size() - chunk * to.size();

		if(diff > chunk || from.size() < to.size())
			throw new ExceptionModelFail
				("chunks-to-all connector, sizes do not match: from="
					+ from.size() + " to=" + to.size() + " diff=" + diff + " chunk=" + chunk);

		ModelLinkList links = new ModelLinkList();

		for(int i = 0; i < from.size(); i++)
			links.add(OneToOne(from.get(i), to.get(i / chunk), directed));

		return links;
	}

	/**
	 * connect Nth object from \from with [Kth, Kth+M] objects from \to<br>
	 * create L edges, L = \to.length<br>
	 * M comes from \size arrays
	 * */
	public ModelLinkList EveryToChunks_Guided(ModelObjectList from, ModelObjectList to, boolean directed
		, int[] sizes)
	{
		if(from.size() != sizes.length)
			throw new ExceptionModelFail
				("EveryToChunks_Guided connector, not enough elements in guiding array: from="
					+ from.size() + " sizes.length=" + sizes.length);

		ModelLinkList links = new ModelLinkList();

		int counter = 0;

		for(int i = 0; i < from.size(); i++)
		{
			if(counter > to.size() || counter + sizes[i] > to.size())
				throw new ExceptionModelFail(
					"EveryToChunks_Guided connector, too many elements specified: "
						+ "to: " + to.size() + " from: " + from.size() + " sizes: " + Arrays.toString(sizes));

			links = links.append(OneToEvery(
				from.get(i), to.range(counter, counter + sizes[i]), directed));

			counter += sizes[i];
		}

		if(counter < to.size())
			LOGGER.log(Level.WARNING, "EveryToChunks_Guided connector, not all elements used");

		return links;
	}

	@Override
	protected LinkFactory Clone(
		List<ConstTemplate> new_constants
		, List<ConstTemplate> new_statics
		, List<SensorTemplate> new_sensors
		, List<VarTemplate> new_rules
		, List<TriggerTemplate> triggers
		, List<ReactionTemplate> new_reactions)
	{
		return new LinkFactory(service, new_constants, new_statics, new_sensors, new_rules, triggers, new_reactions);
	}
}
