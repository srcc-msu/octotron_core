package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.UniqueID;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

public class GraphManager
{
	public enum ELinkType
	{
		MODEL_LINK
	}

	private Neo4jGraph graph;
	private GraphService graph_service;
	private ModelService.EMode mode;

	public GraphManager(ModelService.EMode mode, String path)
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
			reaction.SetStat((Long)graph_object.GetAttribute("stat"));
			reaction.SetSuppress((Boolean)graph_object.GetAttribute("suppress"));
			reaction.SetDescription((String)graph_object.GetAttribute("descr"));
		}
		else
		{
			throw new ExceptionModelFail("no database modification in operational mode");
		}
	}


	private GraphObject GetObject(UniqueID<?> id)
	{
		return graph_service.GetObject("AID", id.GetID());
	}

	private GraphLink GetLink(UniqueID<?> id)
	{
		return graph_service.GetLink("AID", id.GetID());
	}
}
