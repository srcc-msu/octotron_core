package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public class ModelObject extends ModelEntity
{
	private ModelLinkList out_links;
	private ModelLinkList in_links;

	private ModelObjectList out_neighbors;
	private ModelObjectList in_neighbors;

	public ModelObject()
	{
		super(EEntityType.OBJECT);
	}

	public ModelLinkList GetInLinks()
	{
		return in_links;
	}

	public ModelLinkList GetOutLinks()
	{
		return out_links;
	}

	public ModelObjectList GetInNeighbors()
	{
		return in_neighbors;
	}

	public ModelObjectList GetOutNeighbors()
	{
		return out_neighbors;
	}

	public ModelObjectList GetInNeighbors(String link_name, Object link_value)
	{
		return in_links.Filter(link_name, link_value).Source();
	}

	public ModelObjectList GetOutNeighbors(String link_name, Object link_value)
	{
		return out_links.Filter(link_name, link_value).Target();
	}

	public ModelObjectList GetInNeighbors(String link_name)
	{
		return in_links.Filter(link_name).Source();
	}

	public ModelObjectList GetOutNeighbors(String link_name)
	{
		return out_links.Filter(link_name).Target();
	}

	public ModelObjectList GetInNeighbors(SimpleAttribute link_attribute)
	{
		return GetInNeighbors(link_attribute.GetName(), link_attribute.GetValue());
	}

	public ModelObjectList GetOutNeighbors(SimpleAttribute link_attribute)
	{
		return GetOutNeighbors(link_attribute.GetName(), link_attribute.GetValue());
	}
}
