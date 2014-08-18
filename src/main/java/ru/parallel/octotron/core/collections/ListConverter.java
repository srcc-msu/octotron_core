package ru.parallel.octotron.core.collections;

import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphLinkList;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphObjectList;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.impl.ModelLinkList;
import ru.parallel.octotron.core.model.impl.ModelObjectList;
import ru.parallel.octotron.core.model.impl.attribute.VaryingAttribute;

public abstract class ListConverter
{
	public static ModelLinkList GraphToModel(GraphLinkList list)
	{
		ModelLinkList result = new ModelLinkList();

		for(GraphLink link : list)
			result.add(new ModelLink(link));

		return result;
	}

	public static ModelObjectList GraphToModel(GraphObjectList list)
	{
		ModelObjectList result = new ModelObjectList();

		for(GraphObject object : list)
			result.add(new ModelObject(object));

		return result;
	}

	public static GraphObjectList FilterLabel(GraphObjectList list, String label)
	{
		GraphObjectList new_list = new GraphObjectList();

		for(GraphObject obj : list)
		{
			if(obj.TestLabel(label))
				new_list.add(obj);
		}

		return new_list;
	}

	public static GraphLinkList FilterLabel(GraphLinkList list, String label)
	{
		GraphLinkList new_list = new GraphLinkList();

		for(GraphLink link : list)
		{
			if(link.Source().TestLabel(label) && link.Target().TestLabel(label))
				new_list.add(link);
		}

		return new_list;
	}

	public static GraphObjectList ModelToGraph(ModelObjectList objects)
	{
		GraphObjectList new_list = new GraphObjectList();

		for(ModelObject object : objects)
		{
			new_list.add(object.GetBaseObject());
		}

		return new_list;
	}

	public static GraphLinkList ModelToGraph(ModelLinkList links)
	{
		GraphLinkList new_list = new GraphLinkList();

		for(ModelLink link : links)
		{
			new_list.add(link.GetBaseLink());
		}

		return new_list;
	}

	public static <T extends ModelAttribute> AttributeList<VaryingAttribute> GetDependant(AttributeList<T> attributes)
	{
		AttributeList<VaryingAttribute> result = new AttributeList<>();

		for(T attribute : attributes)
			result = result.append(attribute.GetDependant());

		return result;
	}
}
