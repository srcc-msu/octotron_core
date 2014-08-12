package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.IObject;
import ru.parallel.octotron.core.graph.collections.LinkList;
import ru.parallel.octotron.core.graph.collections.ListConverter;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public class ModelObject extends ModelEntity implements IObject
{
	public ModelObject(GraphObject object)
	{
		super(object);
	}

	@Override
	public LinkList<ModelObject, ModelLink> GetInLinks()
	{
		LinkList<GraphObject, GraphLink> links
			= GetBaseObject().GetInLinks();

		LinkList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(links, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	@Override
	public LinkList<ModelObject, ModelLink> GetOutLinks()
	{
		LinkList<GraphObject, GraphLink> links
			= GetBaseObject().GetOutLinks();

		LinkList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(links, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetInNeighbors()
	{
		ObjectList<GraphObject, GraphLink> Objects
			= GetBaseObject().GetInNeighbors();

		ObjectList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetOutNeighbors()
	{
		ObjectList<GraphObject, GraphLink> Objects
			= GetBaseObject().GetOutNeighbors();

		ObjectList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}
	@Override
	public ObjectList<ModelObject, ModelLink> GetInNeighbors(String link_name, Object link_value)
	{
		ObjectList<GraphObject, GraphLink> Objects
			= GetBaseObject().GetInNeighbors(link_name, link_value);

		ObjectList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetOutNeighbors(String link_name, Object link_value)
	{
		ObjectList<GraphObject, GraphLink> Objects
			= GetBaseObject().GetOutNeighbors(link_name, link_value);

		ObjectList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetInNeighbors(String link_name)
	{
		ObjectList<GraphObject, GraphLink> Objects
			= GetBaseObject().GetInNeighbors(link_name);

		ObjectList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetOutNeighbors(String link_name)
	{
		ObjectList<GraphObject, GraphLink> Objects
			= GetBaseObject().GetOutNeighbors(link_name);

		ObjectList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetInNeighbors(SimpleAttribute link_attribute)
	{
		return GetInNeighbors(link_attribute.GetName(), link_attribute.GetValue());
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetOutNeighbors(SimpleAttribute link_attribute)
	{
		return GetOutNeighbors(link_attribute.GetName(), link_attribute.GetValue());
	}
}
