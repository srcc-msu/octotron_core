/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.*;
import ru.parallel.octotron.core.model.ModelInfo;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.attributes.EAttributeType;
import ru.parallel.octotron.persistence.graph.impl.GraphEntity;
import ru.parallel.octotron.persistence.graph.impl.GraphLink;
import ru.parallel.octotron.persistence.graph.impl.GraphObject;
import ru.parallel.octotron.persistence.neo4j.Neo4jGraph;
import ru.parallel.octotron.services.ModelService;

public class GraphCreationManager extends AbstractGraphManager
{
	public GraphCreationManager(ModelService model_service, Neo4jGraph graph)
	{
		super(model_service, graph);
	}

	@Override
	public void RegisterObject(ModelObject object)
	{
		GraphObject graph_object = graph_service.AddObject();
		graph_object.AddLabel(object.GetInfo().GetType().toString());

		graph_object.UpdateAttribute("AID", object.GetInfo().GetID());
	}

	@Override
	public void RegisterLink(ModelLink link)
	{
		GraphLink graph_object = graph_service.AddLink(
			GetGraphObject(link.GetObjects().get(0).GetInfo())
			, GetGraphObject(link.GetObjects().get(1).GetInfo())
			, link.GetInfo().GetType().name());

		graph_object.UpdateAttribute("AID", link.GetInfo().GetID());
	}

	@Override
	public void RegisterReaction(Reaction reaction)
	{
		RegisterMod(reaction);

		ModelInfo<EAttributeType> info = reaction.GetInfo();

		GraphObject graph_object = GetGraphObject(info);
		graph_object.AddLabel(info.GetType().toString());

		graph_object.UpdateAttribute("counter", reaction.GetCounter());
		graph_object.UpdateAttribute("is_suppressed", reaction.IsSuppressed());
		graph_object.UpdateAttribute("description", reaction.GetDescription());

// info
		graph_service.AddLink(GetGraphObject(info), graph_object
			, info.GetType().name());
	}

	@Override
	public void RegisterConst(Const attribute)
	{
		GraphEntity graph_entity = GetEntity(attribute.GetParent());

		graph_entity.UpdateAttribute(attribute.GetName(), attribute.GetValue().GetRaw());
	}

	private void RegisterMod(Attribute attribute)
	{
		ModelInfo<EAttributeType> info = attribute.GetInfo();

		GraphObject graph_object = graph_service.AddObject();
		graph_object.AddLabel(info.GetType().toString());

		graph_object.UpdateAttribute("AID", attribute.GetInfo().GetID());

		graph_object.UpdateAttribute("ctime", attribute.GetCTime());

		if(attribute.IsComputable())
			graph_object.UpdateAttribute("value", attribute.GetValue().GetRaw());

// info
		graph_object.UpdateAttribute("name", attribute.GetName());

		graph_service.AddLink(GetGraphObject(attribute.GetParent().GetInfo()), graph_object
			, info.GetType().name());
	}

	public void RegisterSensor(Sensor attribute)
	{
		RegisterMod(attribute);

		ModelInfo<EAttributeType> info = attribute.GetInfo();

		GraphObject graph_object = GetGraphObject(info);
		graph_object.UpdateAttribute("is_user_valid", attribute.IsUserValid());
	}

	@Override
	public void RegisterVar(Var attribute)
	{
		RegisterMod(attribute);
	}

	@Override
	public void RegisterTrigger(Trigger attribute)
	{
		RegisterMod(attribute);
	}
}
