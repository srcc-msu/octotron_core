/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.exec;

import java.io.FileNotFoundException;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.core.OctoLink;
import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.impl.PersistenStorage;
import main.java.ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionDBError;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import main.java.ru.parallel.octotron.utils.LinkList;
import main.java.ru.parallel.octotron.utils.ObjectList;
import main.java.ru.parallel.utils.FileUtils;

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
		throws ExceptionDBError, ExceptionModelFail, ExceptionSystemError

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

		FileUtils.SaveToFile(settings.GetDbPath() + settings.GetDbName() + HASH_FILE
			, Integer.toString(settings.GetHash()));

		int spec = 0;
		int usual = 0;

		ObjectList objects = graph_service.GetAllObjects();
		LinkList links = graph_service.GetAllLinks();

		for(OctoObject obj : objects)
		{
			spec += obj.GetAttributes().size();
			usual += obj.GetSpecialAttributes().size();
		}

		for(OctoLink link : links)
		{
			spec += link.GetAttributes().size();
			usual += link.GetSpecialAttributes().size();
		}

		System.out.println("Created objects: " + objects.size());
		System.out.println("Created links: " + links.size());
		System.out.println("Created attributes: " + usual);
		System.out.println("Created special attrbiutes: " + spec);

		System.out.println("done");
		graph.Shutdown();
	}
}
