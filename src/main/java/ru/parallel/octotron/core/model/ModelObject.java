package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.IObject;
import ru.parallel.octotron.core.graph.collections.ListConverter;
import ru.parallel.octotron.core.graph.impl.GraphLinkList;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphObjectList;
import ru.parallel.octotron.core.model.collections.ModelLinkList;
import ru.parallel.octotron.core.model.collections.ModelObjectList;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public class ModelObject extends ModelEntity implements IObject<ModelAttribute>
{
	public ModelObject(GraphObject object)
	{
		super(object);
	}

	@Override
	public ModelLinkList GetInLinks()
	{
		GraphLinkList links
			= GetBaseObject().GetInLinks();

		GraphLinkList filtered
			= ListConverter.FilterLabel(links, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	@Override
	public ModelLinkList GetOutLinks()
	{
		GraphLinkList links
			= GetBaseObject().GetOutLinks();

		GraphLinkList filtered
			= ListConverter.FilterLabel(links, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	@Override
	public ModelObjectList GetInNeighbors()
	{
		GraphObjectList Objects
			= GetBaseObject().GetInNeighbors();

		GraphObjectList filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	@Override
	public ModelObjectList GetOutNeighbors()
	{
		GraphObjectList Objects
			= GetBaseObject().GetOutNeighbors();

		GraphObjectList filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}
	@Override
	public ModelObjectList GetInNeighbors(String link_name, Object link_value)
	{
		GraphObjectList Objects
			= GetBaseObject().GetInNeighbors(link_name, link_value);

		GraphObjectList filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	@Override
	public ModelObjectList GetOutNeighbors(String link_name, Object link_value)
	{
		GraphObjectList Objects
			= GetBaseObject().GetOutNeighbors(link_name, link_value);

		GraphObjectList filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	@Override
	public ModelObjectList GetInNeighbors(String link_name)
	{
		GraphObjectList Objects
			= GetBaseObject().GetInNeighbors(link_name);

		GraphObjectList filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	@Override
	public ModelObjectList GetOutNeighbors(String link_name)
	{
		GraphObjectList Objects
			= GetBaseObject().GetOutNeighbors(link_name);

		GraphObjectList filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	@Override
	public ModelObjectList GetInNeighbors(SimpleAttribute link_attribute)
	{
		return GetInNeighbors(link_attribute.GetName(), link_attribute.GetValue());
	}

	@Override
	public ModelObjectList GetOutNeighbors(SimpleAttribute link_attribute)
	{
		return GetOutNeighbors(link_attribute.GetName(), link_attribute.GetValue());
	}
}
