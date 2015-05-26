/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence;

import com.google.common.collect.Iterables;
import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.attributes.impl.*;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.IUniqueID;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.services.ModelService;
import ru.parallel.octotron.persistence.graph.impl.GraphEntity;
import ru.parallel.octotron.persistence.graph.impl.GraphLink;
import ru.parallel.octotron.persistence.graph.impl.GraphObject;
import ru.parallel.octotron.persistence.graph.impl.GraphService;
import ru.parallel.octotron.persistence.neo4j.Neo4jGraph;

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

	private GraphObject GetObject(IUniqueID<?> id)
	{
		return graph_service.GetObject("AID", id.GetID());
	}

	private GraphObject CheckObject(IUniqueID<?> id, String check_label)
	{
		GraphObject graph_object = graph_service.GetObject("AID", id.GetID());

		if(!graph_object.TestLabel(check_label))
			throw new ExceptionModelFail("graph check failed, mismatched labels for AID: "
				+ id.GetID() + " required label: " + check_label);

		return graph_object;
	}

	private GraphLink GetLink(IUniqueID<?> id)
	{
		return graph_service.GetLink("AID", id.GetID());
	}

	private GraphEntity GetEntity(IUniqueID<?> id)
	{
		Collection<GraphLink> links = graph_service.GetLinks("AID", id.GetID());
		Collection<GraphObject> objects = graph_service.GetObjects("AID", id.GetID());

		if(!links.isEmpty() && !objects.isEmpty())
			throw new ExceptionModelFail("found few entities with the same id: " + id.GetID());

		if(objects.size() == 1)
			return Iterables.get(objects, 0);
		else if(objects.size() > 1)
			throw new ExceptionModelFail("found multiple objects with AID: " + id.GetID());

		if(links.size() == 1)
			return Iterables.get(links, 0);
		else if(links.size() > 1)
			throw new ExceptionModelFail("found multiple links with AID: " + id.GetID());

		throw new ExceptionModelFail("could not get entity for AID: " + id.GetID());
	}

	@Override
	public void RegisterObject(ModelObject object)
	{
		if(model_service.GetMode() == ModelService.EMode.CREATION)
		{
			GraphObject graph_object = graph_service.AddObject();
			graph_object.AddLabel(object.GetType().toString());

			graph_object.UpdateAttribute("AID", object.GetID());
		}
		else if(model_service.GetMode() == ModelService.EMode.LOAD)
		{
			CheckObject(object, object.GetType().toString());
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
				GetObject(link.GetObjects().get(0))
				, GetObject(link.GetObjects().get(1))
				, link.GetType().name());

			graph_object.UpdateAttribute("AID", link.GetID());
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

		if(model_service.GetMode() == ModelService.EMode.CREATION)
		{
			GraphObject graph_object = GetObject(reaction);
			graph_object.AddLabel(reaction.GetType().toString());

			graph_object.UpdateAttribute("stat", reaction.GetGlobalStat());

			graph_object.UpdateAttribute("suppress", reaction.GetSuppress());
			graph_object.UpdateAttribute("descr", reaction.GetDescription());

// info
			graph_service.AddLink(GetObject(reaction), graph_object
				, reaction.GetType().name());
		}
		else if(model_service.GetMode() == ModelService.EMode.LOAD)
		{
			GraphObject graph_object = CheckObject(reaction, reaction.GetType().toString());

			reaction.SetGlobalStat((Long) graph_object.GetAttribute("stat"));
			reaction.SetSuppress((Boolean) graph_object.GetAttribute("suppress"));
			reaction.SetDescription((String) graph_object.GetAttribute("descr"));
		}
		else if(model_service.GetMode() == ModelService.EMode.OPERATION)
		{
			GraphObject graph_object = GetObject(reaction);

			graph_object.UpdateAttribute("stat", reaction.GetGlobalStat());

			graph_object.UpdateAttribute("suppress", reaction.GetSuppress());
			graph_object.UpdateAttribute("descr", reaction.GetDescription());
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

			attribute.SetValue(Value.Construct(new_value));
		}
		else if(model_service.GetMode() == ModelService.EMode.OPERATION)
		{
			throw new ExceptionModelFail("no database modification in operational mode");
		}
	}

	private void RegisterMod(Attribute attribute)
	{
		if(model_service.GetMode() == ModelService.EMode.CREATION)
		{
			GraphObject graph_object = graph_service.AddObject();
			graph_object.AddLabel(attribute.GetType().toString());

			graph_object.UpdateAttribute("AID", attribute.GetID());

			graph_object.UpdateAttribute("ctime", attribute.GetCTime());

			if(attribute.GetValue().IsComputable())
				graph_object.UpdateAttribute("value", attribute.GetValue().GetRaw());

// info
			graph_object.UpdateAttribute("name", attribute.GetName());

			graph_service.AddLink(GetObject(attribute.GetParent()), graph_object
				, attribute.GetType().name());
		}
		else if(model_service.GetMode() == ModelService.EMode.LOAD)
		{
			GraphObject graph_object = CheckObject(attribute, attribute.GetType().toString());

			attribute.SetCTime((Long)graph_object.GetAttribute("ctime"));

			if(graph_object.TestAttribute("value"))
				attribute.SetValue(Value.Construct(graph_object.GetAttribute("value")));
			else
				attribute.SetValue(Value.undefined);
		}
		else if(model_service.GetMode() == ModelService.EMode.OPERATION)
		{
			GraphObject graph_object = GetObject(attribute);

			graph_object.UpdateAttribute("ctime", attribute.GetCTime());

			if(attribute.GetValue().IsComputable())
				graph_object.UpdateAttribute("value", attribute.GetValue().GetRaw());
		}
	}

	public void RegisterSensor(Sensor attribute)
	{
		RegisterMod(attribute);

		if(model_service.GetMode() == ModelService.EMode.CREATION)
		{
			GraphObject graph_object = GetObject(attribute);
			graph_object.UpdateAttribute("is_user_valid", attribute.IsUserValid());
		}
		else if(model_service.GetMode() == ModelService.EMode.LOAD)
		{
			GraphObject graph_object = GetObject(attribute);
			attribute.SetIsUserValid((Boolean) graph_object.GetAttribute("is_user_valid"));
		}
		else if(model_service.GetMode() == ModelService.EMode.OPERATION)
		{
			GraphObject graph_object = GetObject(attribute);
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
			if(attribute.GetType() == EAttributeType.CONST)
				continue;

			GraphObject object = GetObject(attribute);

			for(Attribute i_depend_on : attribute.GetIDependOn())
			{
				GraphObject dependency_object = GetObject(i_depend_on);

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
