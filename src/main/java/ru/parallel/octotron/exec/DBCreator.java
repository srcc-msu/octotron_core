/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.storage.PersistentStorage;
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

		ModelService.Init(graph);

		ModelService.Get().EnableObjectIndex("AID");
		ModelService.Get().EnableLinkIndex("AID");

		System.out.println("enabled object index: AID");
		System.out.println("enabled link index: AID");

		for(String attr : settings.GetObjectIndex())
		{
			ModelService.Get().EnableObjectIndex(attr);
			System.out.println("enabled object index: " + attr);
		}

		for(String attr : settings.GetLinkIndex())
		{
			ModelService.Get().EnableLinkIndex(attr);
			System.out.println("enabled link index: " + attr);
		}
	}

	public void PrintStat()
	{
		int model_attributes_count = 0;

		ModelObjectList model_objects = ModelService.Get().GetAllObjects();
		ModelLinkList model_links = ModelService.Get().GetAllLinks();

		for(ModelObject obj : model_objects)
		{
			model_attributes_count += obj.GetAttributes().size();
		}

		for(ModelLink link : model_links)
		{
			model_attributes_count += link.GetAttributes().size();
		}

		System.out.println("Created model objects: " + model_objects.size());
		System.out.println("Created model links: " + model_links.size());
		System.out.println("Created model attributes: " + model_attributes_count);

		System.out.println();
	}

	public void End()
		throws ExceptionSystemError, FileNotFoundException
	{
		System.out.println("Building rule dependencies");
		ModelService.Get().MakeRuleDependencies();

		PersistentStorage.INSTANCE.Save(settings.GetDbPath() + settings.GetDbName());

		FileUtils.SaveToFile(settings.GetDbPath() + settings.GetDbName() + DBCreator.HASH_FILE
			, Integer.toString(settings.GetHash()));

		PrintStat();

		System.out.println("done");
		graph.Shutdown();
	}
}
