package ru.parallel.octotron.core.graph.collections;

import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;

public class ListConverter
{
	public static LinkList<ModelObject, ModelLink> GraphToModel(LinkList<GraphObject, GraphLink> list)
	{
		LinkList<ModelObject, ModelLink> result = new LinkList<>();

		for(GraphLink link : list)
			result.add(new ModelLink(link));

		return result;
	}

	public static ObjectList<ModelObject, ModelLink> GraphToModel(ObjectList<GraphObject, GraphLink> list)
	{
		ObjectList<ModelObject, ModelLink> result = new ObjectList<>();

		for(GraphObject object : list)
			result.add(new ModelObject(object));

		return result;
	}

	public static ObjectList<GraphObject, GraphLink> FilterLabel(ObjectList<GraphObject, GraphLink> list, String label)
	{
		ObjectList<GraphObject, GraphLink> new_list = new ObjectList<>();

		for(GraphObject obj : list)
		{
			if(obj.TestLabel(label))
				new_list.add(obj);
		}

		return new_list;
	}

	public static LinkList<GraphObject, GraphLink> FilterLabel(LinkList<GraphObject, GraphLink> list, String label)
	{
		LinkList<GraphObject, GraphLink> new_list = new LinkList<>();

		for(GraphLink link : list)
		{
			if(link.Source().TestLabel(label) && link.Target().TestLabel(label))
				new_list.add(link);
		}

		return new_list;
	}
}
