package ru.parallel.octotron.core.model;

public class ModelObjectBuilder extends ModelEntityBuilder<ModelObject>
{
	ModelObjectBuilder(ModelService service, ModelObject object)
	{
		super(service, object);
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
