package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.collections.LinkList;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public class ModelService
{
	private final GraphService graph_service;

	public ModelService(GraphService graph_service)
	{
		this.graph_service = graph_service;
	}

	public ObjectList<ModelObject, ModelLink> GetObjects(SimpleAttribute attr)
	{
		return null;
	}

	public ObjectList<ModelObject, ModelLink> GetObjects(String name, Object value)
	{
		return null;
	}

	public ObjectList<ModelObject, ModelLink> GetObjects(String name)
	{
		return null;
	}

	public LinkList<ModelObject, ModelLink> GetLinks(SimpleAttribute attr)
	{
		return null;
	}

	public LinkList<ModelObject, ModelLink> GetLinks(String name, Object value)
	{
		return null;
	}

	public LinkList<ModelObject, ModelLink> GetLinks(String name)
	{
		return null;
	}

	public ObjectList<ModelObject, ModelLink> GetAllObjects()
	{
		return null;
	}

	public LinkList<ModelObject, ModelLink> GetAllLinks()
	{
		return null;
	}

	public String ExportDot(ObjectList<ModelObject, ModelLink> objects)
	{
		return null;
	}

	public String ExportDot()
	{
		return null;
	}

	public ModelObject AddObject()
	{
		return null;
	}

	public void AddLink(ModelObject obj1, ModelObject obj2, String test)
	{

	}


/*	public String ExportDot()
	{
		return graph.ExportDot(UidsFromList(GetAllObjects()));
	}

	public String ExportDot(ObjectList<GraphObject, GraphLink> list)
	{
		return graph.ExportDot(UidsFromList(list));
	}*/

}
