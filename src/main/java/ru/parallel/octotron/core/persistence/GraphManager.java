package ru.parallel.octotron.core.persistence;

import com.google.common.collect.Iterables;
import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.UniqueID;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import java.util.Collection;

public class GraphManager implements IPersistenceManager
{
	public enum ELinkType
	{
		MODEL_LINK
	}

	private ModelService.EMode mode;

	private Neo4jGraph graph;
	private GraphService graph_service;

	public GraphManager(ModelService service, ModelService.EMode mode, String path)
		throws ExceptionSystemError
	{
		this.mode = mode;

		if(mode == ModelService.EMode.CREATION)
			graph = new Neo4jGraph(path, Neo4jGraph.Op.RECREATE, true);
		else
			graph = new Neo4jGraph(path, Neo4jGraph.Op.LOAD, true);

		graph.GetIndex().EnableLinkIndex("AID");
		graph.GetIndex().EnableObjectIndex("AID");

		graph_service = new GraphService(graph);
	}

	private GraphObject GetObject(UniqueID<?> id)
	{

		return graph_service.GetObject("AID", id.GetID());
	}

	private GraphLink GetLink(UniqueID<?> id)
	{

		return graph_service.GetLink("AID", id.GetID());
	}

	private GraphEntity GetEntity(UniqueID<?> id)
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
	public void AddObject(ModelObject object)
	{

		if(mode == ModelService.EMode.CREATION)
		{
			GraphObject graph_object = graph_service.AddObject();

			graph_object.UpdateAttribute("AID", object.GetID());
		}
		else if(mode == ModelService.EMode.LOAD)
		{
			GetObject(object);
		}
		else
		{
			throw new ExceptionModelFail("no database modification in operational mode");
		}
	}

	@Override
	public void AddLink(ModelLink link)
	{

		if(mode == ModelService.EMode.CREATION)
		{
			GraphLink graph_object = graph_service.AddLink(
				GetObject(link.Source()), GetObject(link.Target())
				, GraphManager.ELinkType.MODEL_LINK.name());

			graph_object.UpdateAttribute("AID", link.GetID());
		}
		else if(mode == ModelService.EMode.LOAD)
		{
			GetLink(link);
		}
		else
		{
			throw new ExceptionModelFail("no database modification in operational mode");
		}
	}

	@Override
	public void AddReaction(Reaction reaction)
	{

		if(mode == ModelService.EMode.CREATION)
		{
			GraphObject graph_object = graph_service.AddObject();

			graph_object.UpdateAttribute("AID", reaction.GetID());

			graph_object.UpdateAttribute("state", reaction.GetState());
			graph_object.UpdateAttribute("stat", reaction.GetStat());

			graph_object.UpdateAttribute("suppress", reaction.GetSuppress());
			graph_object.UpdateAttribute("descr", reaction.GetDescription());
		}
		else if(mode == ModelService.EMode.LOAD)
		{
			GraphObject graph_object = GetObject(reaction);

			reaction.SetState((Long)graph_object.GetAttribute("state"));
			reaction.SetStat((Long) graph_object.GetAttribute("stat"));
			reaction.SetSuppress((Boolean) graph_object.GetAttribute("suppress"));
			reaction.SetDescription((String) graph_object.GetAttribute("descr"));
		}
		else
		{
			throw new ExceptionModelFail("no database modification in operational mode");
		}
	}

	@Override
	public void RegisterConst(ModelService model_service, ConstAttribute attribute)
	{

		if(mode == ModelService.EMode.CREATION)
		{
			GraphEntity graph_entity = GetEntity(attribute.GetParent());

			graph_entity.UpdateAttribute(attribute.GetName(), attribute.GetValue());
		}
		else if(mode == ModelService.EMode.LOAD)
		{
			GraphEntity graph_entity = GetEntity(attribute.GetParent());

			Object new_value = graph_entity.GetAttribute(attribute.GetName());

			attribute.GetBuilder(model_service).ModifyValue(new_value);
		}
		else
		{
			throw new ExceptionModelFail("no database modification in operational mode");
		}
	}

	@Override
	public void Finish()
	{
		graph.Shutdown();
	}
}
