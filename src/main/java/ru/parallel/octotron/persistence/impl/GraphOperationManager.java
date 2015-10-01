/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.*;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelInfo;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.persistence.graph.impl.GraphObject;
import ru.parallel.octotron.persistence.neo4j.Neo4jGraph;
import ru.parallel.octotron.services.impl.ModelService;

public class GraphOperationManager extends AbstractGraphManager
{
	public GraphOperationManager(ModelService model_service, Neo4jGraph graph)
	{
		super(model_service, graph);
	}

	@Override
	public void RegisterObject(ModelObject object)
	{
		throw new ExceptionModelFail("no database modification in operational mode");
	}

	@Override
	public void RegisterLink(ModelLink link)
	{
		throw new ExceptionModelFail("no database modification in operational mode");
	}

	@Override
	public void RegisterReaction(Reaction reaction)
	{
		RegisterMod(reaction);

		ModelInfo<EAttributeType> info = reaction.GetInfo();

		GraphObject graph_object = GetGraphObject(info);

		graph_object.UpdateAttribute("counter", reaction.GetCounter());
		graph_object.UpdateAttribute("is_suppressed", reaction.IsSuppressed());
		graph_object.UpdateAttribute("description", reaction.GetDescription());
	}

	@Override
	public void RegisterConst(Const attribute)
	{
		throw new ExceptionModelFail("no database modification in operational mode");
	}

	private void RegisterMod(Attribute attribute)
	{
		ModelInfo<EAttributeType> info = attribute.GetInfo();

		GraphObject graph_object = GetGraphObject(info);

		graph_object.UpdateAttribute("ctime", attribute.GetCTime());

		if(attribute.IsComputable())
			graph_object.UpdateAttribute("value", attribute.GetValue().GetRaw());
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

	@Override
	public void MakeRuleDependency(ModelEntity entity)
	{
		throw new ExceptionModelFail("no database modification in operational mode");
	}
}
