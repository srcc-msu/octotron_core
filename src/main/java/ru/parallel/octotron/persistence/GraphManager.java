/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence;

import com.google.common.collect.Iterables;
import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.*;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelInfo;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.persistence.graph.impl.GraphEntity;
import ru.parallel.octotron.persistence.graph.impl.GraphLink;
import ru.parallel.octotron.persistence.graph.impl.GraphObject;
import ru.parallel.octotron.persistence.graph.impl.GraphService;
import ru.parallel.octotron.persistence.neo4j.Neo4jGraph;
import ru.parallel.octotron.services.impl.ModelService;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphManager implements IPersistenceManager
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	final ModelService model_service;

	private Neo4jGraph graph;
	private GraphService graph_service;

	public GraphManager(ModelService model_service, String path, int port)
		throws ExceptionSystemError
	{
		this.model_service = model_service;

		if(model_service.GetMode() == ModelService.EMode.CREATION)
			graph = new Neo4jGraph(path, Neo4jGraph.Op.RECREATE, true, port);
		else
			graph = new Neo4jGraph(path, Neo4jGraph.Op.LOAD, true, port);

		graph.GetIndex().EnableLinkIndex("AID");
		graph.GetIndex().EnableObjectIndex("AID");

		graph_service = new GraphService(graph);
	}

	private GraphObject GetGraphObject(ModelInfo<?> id)
	{
		return graph_service.GetObject("AID", id.GetID());
	}

	private GraphObject CheckGraphObject(ModelInfo<?> id, String check_label)
	{
		GraphObject graph_object = graph_service.GetObject("AID", id.GetID());

		if(!graph_object.TestLabel(check_label))
			throw new ExceptionModelFail("graph check failed, mismatched labels for AID: "
				+ id.GetID() + " required label: " + check_label);

		return graph_object;
	}

	private GraphLink GetLink(ModelLink id)
	{
		return graph_service.GetLink("AID", id.GetInfo().GetID());
	}

	private GraphEntity GetEntity(ModelEntity id)
	{
		Collection<GraphLink> links = graph_service.GetLinks("AID", id.GetInfo().GetID());
		Collection<GraphObject> objects = graph_service.GetObjects("AID", id.GetInfo().GetID());

		if(!links.isEmpty() && !objects.isEmpty())
			throw new ExceptionModelFail("found few entities with the same id: " + id.GetInfo().GetID());

		if(objects.size() == 1)
			return Iterables.get(objects, 0);
		else if(objects.size() > 1)
			throw new ExceptionModelFail("found multiple objects with AID: " + id.GetInfo().GetID());

		if(links.size() == 1)
			return Iterables.get(links, 0);
		else if(links.size() > 1)
			throw new ExceptionModelFail("found multiple links with AID: " + id.GetInfo().GetID());

		throw new ExceptionModelFail("could not get entity for AID: " + id.GetInfo().GetID());
	}

	@Override
	public void RegisterObject(ModelObject object)
	{
		if(model_service.GetMode() == ModelService.EMode.CREATION)
		{
			GraphObject graph_object = graph_service.AddObject();
			graph_object.AddLabel(object.GetInfo().GetType().toString());

			graph_object.UpdateAttribute("AID", object.GetInfo().GetID());
		}
		else if(model_service.GetMode() == ModelService.EMode.LOAD)
		{
			CheckGraphObject(object.GetInfo(), object.GetInfo().GetType().toString());
		}
		else if(model_service.GetMode() == ModelService.EMode.OPERATION)
		{
			throw new ExceptionModelFail("no database modification in operational mode");
		}
	}

	@Override
	public void RegisterLink(ModelLink link)
	{
		if(model_service.GetMode() == ModelService.EMode.CREATION)
		{
			GraphLink graph_object = graph_service.AddLink(
				GetGraphObject(link.GetObjects().get(0).GetInfo())
				, GetGraphObject(link.GetObjects().get(1).GetInfo())
				, link.GetInfo().GetType().name());

			graph_object.UpdateAttribute("AID", link.GetInfo().GetID());
		}
		else if(model_service.GetMode() == ModelService.EMode.LOAD)
		{
			GetLink(link);
		}
		else if(model_service.GetMode() == ModelService.EMode.OPERATION)
		{
			throw new ExceptionModelFail("no database modification in operational mode");
		}
	}

	@Override
	public void RegisterReaction(Reaction reaction)
	{
		RegisterMod(reaction);

		ModelInfo<EAttributeType> info = reaction.GetInfo();

		if(model_service.GetMode() == ModelService.EMode.CREATION)
		{
			GraphObject graph_object = GetGraphObject(info);
			graph_object.AddLabel(info.GetType().toString());

			graph_object.UpdateAttribute("counter", reaction.GetCounter());
			graph_object.UpdateAttribute("is_suppressed", reaction.IsSuppressed());
			graph_object.UpdateAttribute("description", reaction.GetDescription());

// info
			graph_service.AddLink(GetGraphObject(info), graph_object
				, info.GetType().name());
		}
		else if(model_service.GetMode() == ModelService.EMode.LOAD)
		{
			GraphObject graph_object = CheckGraphObject(info, info.GetType().toString());

			reaction.SetCounter((Long) graph_object.GetAttribute("counter"));
			reaction.SetSuppressed((Boolean) graph_object.GetAttribute("is_suppressed"));
			reaction.SetDescription((String) graph_object.GetAttribute("description"));
		}
		else if(model_service.GetMode() == ModelService.EMode.OPERATION)
		{
			GraphObject graph_object = GetGraphObject(info);

			graph_object.UpdateAttribute("counter", reaction.GetCounter());
			graph_object.UpdateAttribute("is_suppressed", reaction.IsSuppressed());
			graph_object.UpdateAttribute("description", reaction.GetDescription());
		}
	}

	@Override
	public void RegisterConst(Const attribute)
	{
		if(model_service.GetMode() == ModelService.EMode.CREATION)
		{
			GraphEntity graph_entity = GetEntity(attribute.GetParent());

			graph_entity.UpdateAttribute(attribute.GetName(), attribute.GetValue().GetRaw());
		}
		else if(model_service.GetMode() == ModelService.EMode.LOAD)
		{
			GraphEntity graph_entity = GetEntity(attribute.GetParent());

			Object new_value = graph_entity.GetAttribute(attribute.GetName());

			attribute.UpdateValue(Value.Construct(new_value));
		}
		else if(model_service.GetMode() == ModelService.EMode.OPERATION)
		{
			throw new ExceptionModelFail("no database modification in operational mode");
		}
	}

	private void RegisterMod(Attribute attribute)
	{
		ModelInfo<EAttributeType> info = attribute.GetInfo();

		if(model_service.GetMode() == ModelService.EMode.CREATION)
		{
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
		else if(model_service.GetMode() == ModelService.EMode.LOAD)
		{
			GraphObject graph_object = CheckGraphObject(info, info.GetType().toString());

			attribute.SetCTime((Long)graph_object.GetAttribute("ctime"));

			if(graph_object.TestAttribute("value"))
				attribute.UpdateValue(Value.Construct(graph_object.GetAttribute("value")));
			else
				attribute.UpdateValue(Value.undefined);
		}
		else if(model_service.GetMode() == ModelService.EMode.OPERATION)
		{
			GraphObject graph_object = GetGraphObject(info);

			graph_object.UpdateAttribute("ctime", attribute.GetCTime());

			if(attribute.IsComputable())
				graph_object.UpdateAttribute("value", attribute.GetValue().GetRaw());
		}
	}

	public void RegisterSensor(Sensor attribute)
	{
		RegisterMod(attribute);

		ModelInfo<EAttributeType> info = attribute.GetInfo();

		if(model_service.GetMode() == ModelService.EMode.CREATION)
		{
			GraphObject graph_object = GetGraphObject(info);
			graph_object.UpdateAttribute("is_user_valid", attribute.IsUserValid());
		}
		else if(model_service.GetMode() == ModelService.EMode.LOAD)
		{
			GraphObject graph_object = GetGraphObject(info);
			attribute.SetIsUserValid((Boolean) graph_object.GetAttribute("is_user_valid"));
		}
		else if(model_service.GetMode() == ModelService.EMode.OPERATION)
		{
			GraphObject graph_object = GetGraphObject(info);
			graph_object.UpdateAttribute("is_user_valid", attribute.IsUserValid());
		}
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

	private static final String DEPENDS = "DEPENDS";

	@Override
	public void MakeRuleDependency(ModelEntity entity)
	{
		for(Attribute attribute : entity.GetAttributes())
		{
			if(attribute.GetInfo().GetType() == EAttributeType.CONST)
				continue;

			GraphObject object = GetGraphObject(attribute.GetInfo());

			for(Attribute i_depend_on : attribute.GetIDependOn())
			{
				GraphObject dependency_object = GetGraphObject(i_depend_on.GetInfo());

				graph_service.AddLink(object, dependency_object, DEPENDS);
			}
		}
	}

	@Override
	public void Finish()
	{
		graph.GetTransaction().ForceWrite(); // TODO: not needed?

		graph.Shutdown();
	}

	@Override
	public void Wipe()
	{
		graph.Shutdown();

		try
		{
			graph.Delete();
		}
		catch(ExceptionSystemError e)
		{
			LOGGER.log(Level.SEVERE, "could not wipe Neo4j folder", e);
		}

		LOGGER.log(Level.INFO, "Neo4j folder wiped");
	}
}
