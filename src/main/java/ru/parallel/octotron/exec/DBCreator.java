/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.core.OctoLink;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.impl.PersistenStorage;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.utils.OctoLinkList;
import ru.parallel.octotron.utils.OctoObjectList;
import ru.parallel.utils.FileUtils;

import java.io.FileNotFoundException;

/**
 * DSL usage sample and also main class for T60 model creation<br>
 * */
public class DBCreator
{
	public static final String HASH_FILE = ".hash";

	private Neo4jGraph graph;
	private GraphService graph_service;

	private GlobalSettings settings;

	public DBCreator(String fname)
		throws ExceptionSystemError
	{
		String json_config = FileUtils.FileToString(fname);
		settings = new GlobalSettings(json_config);
	}

	public GraphService GetGraphService()
	{
		return graph_service;
	}

	public void Begin()
		throws ExceptionSystemError
	{
		graph = new Neo4jGraph(settings.GetDbPath() + settings.GetDbName() + "_neo4j", Neo4jGraph.Op.RECREATE);
		graph_service = new GraphService(graph);

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

	public void End()
		throws ExceptionSystemError, FileNotFoundException
	{
		PersistenStorage.INSTANCE.Save(settings.GetDbPath() + settings.GetDbName());

		FileUtils.SaveToFile(settings.GetDbPath() + settings.GetDbName() + DBCreator.HASH_FILE
			, Integer.toString(settings.GetHash()));

		int count = 0;

		OctoObjectList objects = graph_service.GetAllObjects();
		OctoLinkList links = graph_service.GetAllLinks();

		for(OctoObject obj : objects)
		{
			count += obj.GetAttributes().size();
		}

		for(OctoLink link : links)
		{
			count += link.GetAttributes().size();
		}

		System.out.println("Created objects: " + objects.size());
		System.out.println("Created links: " + links.size());
		System.out.println("Created attributes: " + count);

		System.out.println("done");
		graph.Shutdown();
	}
}
