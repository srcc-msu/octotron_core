package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.collections.ListConverter;
import ru.parallel.octotron.core.graph.impl.*;
import ru.parallel.octotron.core.model.impl.ModelLinkList;
import ru.parallel.octotron.core.model.impl.ModelObjectList;
import ru.parallel.octotron.core.model.impl.attribute.EAttributeType;
import ru.parallel.octotron.core.model.impl.attribute.VaryingAttribute;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public abstract class ModelService
{
	public static void MakeRuleDependencies()
	{
		for(ModelObject object : GetAllObjects())
		{
			for(VaryingAttribute attribute : object.GetVaryings())
			{
				for(IMetaAttribute dep : attribute.GetRule().GetDependency(object))
				{
					if(dep.GetType() != EAttributeType.CONSTANT)
						dep.AddDependant(attribute);
				}
			}
		}
	}

	public static ModelObjectList GetObjects(SimpleAttribute attr)
	{
		GraphObjectList result = GraphService.Get().GetObjects(attr);
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static ModelObjectList GetObjects(String name, Object value)
	{
		GraphObjectList result = GraphService.Get().GetObjects(name, value);
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static ModelObjectList GetObjects(String name)
	{
		GraphObjectList result = GraphService.Get().GetObjects(name);
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static ModelLinkList GetLinks(SimpleAttribute attr)
	{
		GraphLinkList result = GraphService.Get().GetLinks(attr);
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static ModelLinkList GetLinks(String name, Object value)
	{
		GraphLinkList result = GraphService.Get().GetLinks(name, value);
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static ModelLinkList GetLinks(String name)
	{
		GraphLinkList result = GraphService.Get().GetLinks(name);
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static ModelObjectList GetAllObjects()
	{
		GraphObjectList result = GraphService.Get().GetAllObjects();
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static ModelLinkList GetAllLinks()
	{
		GraphLinkList result = GraphService.Get().GetAllLinks();
		return ListConverter.GraphToModel(ListConverter.FilterLabel(result, EObjectLabels.MODEL.toString()));
	}

	public static String ExportDot(ModelObjectList objects)
	{
		return GraphService.Get().ExportDot(ListConverter.ModelToGraph(objects));
	}

	public static String ExportDot()
	{
		return GraphService.Get().ExportDot();
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
