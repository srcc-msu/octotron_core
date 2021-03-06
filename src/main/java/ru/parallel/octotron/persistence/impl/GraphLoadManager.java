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
import ru.parallel.octotron.persistence.graph.impl.GraphObject;
import ru.parallel.octotron.persistence.neo4j.Neo4jGraph;
import ru.parallel.octotron.services.ModelService;

public class GraphLoadManager extends AbstractGraphManager
{
	public GraphLoadManager(ModelService model_service, Neo4jGraph graph)
	{
		super(model_service, graph);
	}

	@Override
	public void RegisterObject(ModelObject object)
	{
		CheckGraphObject(object.GetInfo(), object.GetInfo().GetType().toString());
	}

	@Override
	public void RegisterLink(ModelLink link)
	{
		GetLink(link);
	}

	@Override
	public void RegisterReaction(Reaction reaction)
	{
		RegisterMod(reaction);

		ModelInfo<EAttributeType> info = reaction.GetInfo();

		GraphObject graph_object = CheckGraphObject(info, info.GetType().toString());

		reaction.SetCounter((Long) graph_object.GetAttribute("counter"));
		reaction.SetSuppressed((Boolean) graph_object.GetAttribute("is_suppressed"));
		reaction.SetDescription((String) graph_object.GetAttribute("description"));
	}

	@Override
	public void RegisterConst(Const attribute)
	{
		GraphEntity graph_entity = GetEntity(attribute.GetParent());

		Object new_value = graph_entity.GetAttribute(attribute.GetName());

		attribute.Update(Value.Construct(new_value), 0);
	}

	private void RegisterMod(Attribute attribute)
	{
		ModelInfo<EAttributeType> info = attribute.GetInfo();

		GraphObject graph_object = CheckGraphObject(info, info.GetType().toString());

		if(graph_object.TestAttribute("value"))
			attribute.Update(Value.Construct(graph_object.GetAttribute("value")), (Long) graph_object.GetAttribute("ctime"));
		else
			attribute.Update(Value.undefined, (Long) graph_object.GetAttribute("ctime"));
	}

	public void RegisterSensor(Sensor attribute)
	{
		RegisterMod(attribute);

		ModelInfo<EAttributeType> info = attribute.GetInfo();

		GraphObject graph_object = GetGraphObject(info);
		attribute.SetIsUserValid((Boolean) graph_object.GetAttribute("is_user_valid"));
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
