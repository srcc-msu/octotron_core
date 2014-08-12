package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.collections.LinkList;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public abstract class ModelService
{
	public static ObjectList<ModelObject, ModelLink> GetObjects(SimpleAttribute attr)
	{
		return null;
	}

	public static ObjectList<ModelObject, ModelLink> GetObjects(String name, Object value)
	{
		return null;
	}

	public static ObjectList<ModelObject, ModelLink> GetObjects(String name)
	{
		return null;
	}

	public static LinkList<ModelObject, ModelLink> GetLinks(SimpleAttribute attr)
	{
		return null;
	}

	public static LinkList<ModelObject, ModelLink> GetLinks(String name, Object value)
	{
		return null;
	}

	public static LinkList<ModelObject, ModelLink> GetLinks(String name)
	{
		return null;
	}

	public static ObjectList<ModelObject, ModelLink> GetAllObjects()
	{
		return null;
	}

	public static LinkList<ModelObject, ModelLink> GetAllLinks()
	{
		return null;
	}

	public static String ExportDot(ObjectList<ModelObject, ModelLink> objects)
	{
		return null;
	}

	public static String ExportDot()
	{
		return null;
	}

	public static ModelObject AddObject()
	{
		GraphObject object = GraphService.Get().AddObject();
		object.AddLabel(EObjectLabels.MODEL.toString());
		return new ModelObject(object);
	}

	public static ModelLink AddLink(ModelObject obj1, ModelObject obj2, String type)
	{
		GraphLink link = GraphService.Get().AddLink(obj1.GetBaseObject(), obj2.GetBaseObject(), type);
		return new ModelLink(link);
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
