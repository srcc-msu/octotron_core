/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import ru.parallel.octotron.core.graph.impl.*;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.model.impl.ModelLinkList;
import ru.parallel.octotron.core.model.impl.ModelObjectList;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.impl.PersistentStorage;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.utils.FileUtils;

import java.io.FileNotFoundException;

/**
 * DSL usage sample and also main class for T60 model creation<br>
 * */
public class DBCreator
{
	public static final String HASH_FILE = ".hash";

	private Neo4jGraph graph;

	private GlobalSettings settings;

	public DBCreator(String fname)
		throws ExceptionSystemError
	{
		String json_config = FileUtils.FileToString(fname);
		settings = new GlobalSettings(json_config);
	}

	public void Begin()
		throws ExceptionSystemError
	{
		graph = new Neo4jGraph(settings.GetDbPath() + settings.GetDbName() + "_neo4j", Neo4jGraph.Op.RECREATE);

		GraphService.Init(graph);

		graph.GetIndex().EnableObjectIndex("AID");
		graph.GetIndex().EnableLinkIndex("AID");

		System.out.println("enabled object index: AID");
		System.out.println("enabled link index: AID");

		for(String attr : settings.GetObjectIndex())
		{
			graph.GetIndex().EnableObjectIndex(attr);
			System.out.println("enabled object index: " + attr);
		}

		for(String attr : settings.GetLinkIndex())
		{
			graph.GetIndex().EnableLinkIndex(attr);
			System.out.println("enabled link index: " + attr);
		}
	}

	public void PrintStat()
	{
		int graph_attributes_count = 0;

		GraphObjectList graph_objects = GraphService.Get().GetAllObjects();
		GraphLinkList graph_links = GraphService.Get().GetAllLinks();

		for(GraphObject obj : graph_objects)
		{
			graph_attributes_count += obj.GetAttributes().size();
		}

		for(GraphLink link : graph_links)
		{
			graph_attributes_count += link.GetAttributes().size();
		}

		System.out.println("Created graph objects: " + graph_objects.size());
		System.out.println("Created graph links: " + graph_links.size());
		System.out.println("Created graph attributes: " + graph_attributes_count);

		System.out.println();

		int model_attributes_count = 0;

		ModelObjectList model_objects = ModelService.GetAllObjects();
		ModelLinkList model_links = ModelService.GetAllLinks();

/*		for(ModelObject obj : model_objects)
		{
			model_attributes_count += obj.GetAttributes().size();
		}

		for(ModelLink link : model_links)
		{
			model_attributes_count += link.GetAttributes().size();
		}*/

		System.out.println("Created model objects: " + model_objects.size());
		System.out.println("Created model links: " + model_links.size());
		System.out.println("Created model attributes: " + model_attributes_count);

		System.out.println();
	}

	public void End()
		throws ExceptionSystemError, FileNotFoundException
	{
		System.out.println("Building rule dependencies");
		ModelService.MakeRuleDependencies();

		PersistentStorage.INSTANCE.Save(settings.GetDbPath() + settings.GetDbName());

		FileUtils.SaveToFile(settings.GetDbPath() + settings.GetDbName() + DBCreator.HASH_FILE
			, Integer.toString(settings.GetHash()));

		PrintStat();

		System.out.println("done");
		graph.Shutdown();
	}
}
