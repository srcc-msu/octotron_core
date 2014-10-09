package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public class ModelObject extends ModelEntity
{
	public static class ModelObjectBuilder extends ModelEntityBuilder<ModelObject>
	{
		ModelObjectBuilder(ModelObject object)
		{
			super(object);
		}

		public void AddOutLink(ModelLink link)
		{
			entity.out_links.add(link);
			entity.out_neighbors.add(link.Target());
		}

		public void AddInLink(ModelLink link)
		{
			entity.in_links.add(link);
			entity.in_neighbors.add(link.Source());
		}
	}

	@Override
	public ModelObjectBuilder GetBuilder()
	{
		return new ModelObjectBuilder(this);
	}

	private ModelLinkList out_links;
	private ModelLinkList in_links;

	private ModelObjectList out_neighbors;
	private ModelObjectList in_neighbors;

	public ModelObject()
	{
		super(EEntityType.OBJECT);

		out_links = new ModelLinkList();
		in_links = new ModelLinkList();

		out_neighbors = new ModelObjectList();
		in_neighbors = new ModelObjectList();
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
