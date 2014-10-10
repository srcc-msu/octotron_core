package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.graph.IGraph;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.UniqueID;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

public final class ModelData
{
	final ModelCache cache;

	ModelObjectList objects;
	ModelLinkList links;

	public ModelData()
	{
		objects = new ModelObjectList();
		links = new ModelLinkList();

		cache = new ModelCache();
	}

	public ModelObjectList GetAllObjects()
	{
		return objects;
	}

	public ModelLinkList GetAllLinks()
	{
		return links;
	}

	public ModelObjectList GetObjects(SimpleAttribute attribute)
	{
		return cache.GetObjects(attribute);
	}

	public ModelObjectList GetObjects(String name)
	{
		return cache.GetObjects(name);
	}

	public ModelLinkList GetLinks(SimpleAttribute attribute)
	{
		return cache.GetLinks(attribute);
	}

	public ModelLinkList GetLinks(String name)
	{
		return cache.GetLinks(name);
	}
}
