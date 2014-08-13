package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.collections.LinkList;
import ru.parallel.octotron.core.collections.ListConverter;
import ru.parallel.octotron.core.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public abstract class ModelService
{
	public static ObjectList<ModelObject, ModelLink> GetObjects(SimpleAttribute attr)
	{
		ObjectList<GraphObject, GraphLink> result = GraphService.Get().GetObjects(attr);
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static ObjectList<ModelObject, ModelLink> GetObjects(String name, Object value)
	{
		ObjectList<GraphObject, GraphLink> result = GraphService.Get().GetObjects(name, value);
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static ObjectList<ModelObject, ModelLink> GetObjects(String name)
	{
		ObjectList<GraphObject, GraphLink> result = GraphService.Get().GetObjects(name);
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static LinkList<ModelObject, ModelLink> GetLinks(SimpleAttribute attr)
	{
		LinkList<GraphObject, GraphLink> result = GraphService.Get().GetLinks(attr);
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static LinkList<ModelObject, ModelLink> GetLinks(String name, Object value)
	{
		LinkList<GraphObject, GraphLink> result = GraphService.Get().GetLinks(name, value);
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static LinkList<ModelObject, ModelLink> GetLinks(String name)
	{
		LinkList<GraphObject, GraphLink> result = GraphService.Get().GetLinks(name);
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static ObjectList<ModelObject, ModelLink> GetAllObjects()
	{
		ObjectList<GraphObject, GraphLink> result = GraphService.Get().GetAllObjects();
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static LinkList<ModelObject, ModelLink> GetAllLinks()
	{
		LinkList<GraphObject, GraphLink> result = GraphService.Get().GetAllLinks();
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
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
}
