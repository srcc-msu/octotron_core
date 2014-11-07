/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.persistence;

import com.google.common.collect.Iterables;
import ru.parallel.octotron.core.attributes.AbstractModAttribute;
import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.IUniqueID;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import java.util.Collection;

public class GraphManager implements IPersistenceManager
{
	final ModelService model_service;

	private Neo4jGraph graph;
	private GraphService graph_service;

	public GraphManager(ModelService model_service, String path)
		throws ExceptionSystemError
	{
		this.model_service = model_service;

		if(model_service.GetMode() == ModelService.EMode.CREATION)
			graph = new Neo4jGraph(path, Neo4jGraph.Op.RECREATE, true);
		else
			graph = new Neo4jGraph(path, Neo4jGraph.Op.LOAD, true);

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
				GetObject(link.Source()), GetObject(link.Target())
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
		if(model_service.GetMode() == ModelService.EMode.CREATION)
		{
			GraphObject graph_object = graph_service.AddObject();
			graph_object.AddLabel(reaction.GetType().toString());

			graph_object.UpdateAttribute("AID", reaction.GetID());

			graph_object.UpdateAttribute("state", reaction.GetState().toString());
			graph_object.UpdateAttribute("stat", reaction.GetGlobalStat());

			graph_object.UpdateAttribute("suppress", reaction.GetSuppress());
			graph_object.UpdateAttribute("descr", reaction.GetDescription());

// info
			graph_object.UpdateAttribute("name", reaction.GetAttribute().GetName());

			graph_service.AddLink(GetObject(reaction.GetAttribute()), graph_object
				, reaction.GetType().name());
		}
		else if(model_service.GetMode() == ModelService.EMode.LOAD)
		{
			GraphObject graph_object = CheckObject(reaction, reaction.GetType().toString());

			reaction.SetState(Reaction.State.valueOf((String) graph_object.GetAttribute("state")));
			reaction.SetGlobalStat((Long) graph_object.GetAttribute("stat"));
			reaction.SetSuppress((Boolean) graph_object.GetAttribute("suppress"));
			reaction.SetDescription((String) graph_object.GetAttribute("descr"));
		}
		else if(model_service.GetMode() == ModelService.EMode.OPERATION)
		{
			GraphObject graph_object = GetObject(reaction);

			graph_object.UpdateAttribute("state", reaction.GetState().toString());
			graph_object.UpdateAttribute("stat", reaction.GetGlobalStat());

			graph_object.UpdateAttribute("suppress", reaction.GetSuppress());
			graph_object.UpdateAttribute("descr", reaction.GetDescription());
		}
	}

	@Override
	public void RegisterConst(ConstAttribute attribute)
	{
		if(model_service.GetMode() == ModelService.EMode.CREATION)
		{
			GraphEntity graph_entity = GetEntity(attribute.GetParent());

			graph_entity.UpdateAttribute(attribute.GetName(), attribute.GetValue());
		}
		else if(model_service.GetMode() == ModelService.EMode.LOAD)
		{
			GraphEntity graph_entity = GetEntity(attribute.GetParent());

			Object new_value = graph_entity.GetAttribute(attribute.GetName());

			attribute.GetBuilder(model_service).ModifyValue(new_value);
		}
		else if(model_service.GetMode() == ModelService.EMode.OPERATION)
		{
			throw new ExceptionModelFail("no database modification in operational mode");
		}
	}

	private void RegisterMod(AbstractModAttribute attribute)
	{
		if(model_service.GetMode() == ModelService.EMode.CREATION)
		{
			GraphObject graph_object = graph_service.AddObject();
			graph_object.AddLabel(attribute.GetType().toString());

			graph_object.UpdateAttribute("AID", attribute.GetID());

			graph_object.UpdateAttribute("is_valid", attribute.GetIsValid());
			graph_object.UpdateAttribute("ctime", attribute.GetCTime());
			graph_object.UpdateAttribute("value", attribute.GetValue());

// info
			graph_object.UpdateAttribute("name", attribute.GetName());

			graph_service.AddLink(GetObject(attribute.GetParent()), graph_object
				, attribute.GetType().name());
		}
		else if(model_service.GetMode() == ModelService.EMode.LOAD)
		{
			GraphObject graph_object = CheckObject(attribute, attribute.GetType().toString());

			attribute.GetBuilder(model_service)
				.SetCTime((Long)graph_object.GetAttribute("ctime"));

			attribute.GetBuilder(model_service)
				.SetValid((Boolean) graph_object.GetAttribute("is_valid"));

			attribute.GetBuilder(model_service)
				.SetValue(graph_object.GetAttribute("value"));
		}
		else if(model_service.GetMode() == ModelService.EMode.OPERATION)
		{
			GraphObject graph_object = GetObject(attribute);

			graph_object.UpdateAttribute("is_valid", attribute.GetIsValid());
			graph_object.UpdateAttribute("ctime", attribute.GetCTime());
			graph_object.UpdateAttribute("value", attribute.GetValue());
		}
	}

	@Override
	public void RegisterVar(VarAttribute attribute)
	{
		RegisterMod(attribute);
	}

	@Override
	public void RegisterSensor(SensorAttribute attribute)
	{
		RegisterMod(attribute);
	}

	@Override
	public void Finish()
	{
		graph.Shutdown();
	}

	private static final String DEPENDS = "DEPENDS";

	@Override
	public void MakeRuleDependency(VarAttribute attribute)
	{
		GraphObject object = GetObject(attribute);

		for(IModelAttribute dependency : attribute.GetDependency())
		{
			if(dependency.GetType() == EAttributeType.CONST)
				continue;

			GraphObject dependency_object = GetObject(dependency);

			graph_service.AddLink(object, dependency_object, DEPENDS);
		}
	}

	@Override
	public void Operate()
	{
		graph.GetTransaction().ForceWrite();
	}

	// TODO: executor?
	@Override
	public void RegisterUpdate(AttributeList<IModelAttribute> attributes)
	{
		for(IModelAttribute attribute : attributes)
		{
			if(attribute.GetType() == EAttributeType.SENSOR)
				RegisterSensor((SensorAttribute)attribute);
			else if(attribute.GetType() == EAttributeType.VAR)
				RegisterVar((VarAttribute)attribute);

			for(Reaction reaction : attribute.GetReactions())
				RegisterReaction(reaction);
		}

		graph.GetTransaction().ForceWrite();
	}
}
