package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.graph.ILink;
import ru.parallel.octotron.core.graph.IObject;
import ru.parallel.octotron.core.graph.impl.GraphLink;

public class AttributeLink extends GraphBased implements ILink
{
	protected AttributeLink(GraphLink base)
	{
		super(base);
	}

	@Override
	public IObject Target()
	{
		return null;
	}

	@Override
	public IObject Source()
	{
		return null;
	}

/*
	private static final String sensor_link = "_sensor";
	private static final String derived_link = "_derived";
	@Override
	public OctoVar GetVar(String name)
	{
		GraphLink sensor_link = GetSensorLink(name);

		if(sensor_link != null)
			return new OctoSensor(graph_service, this, sensor_link.Target());

		GraphLink derived_link = GetDerivedLink(name);

		if(derived_link != null)
			return new OctoDerived(graph_service, this, sensor_link.Target());

		throw new ExceptionModelFail("there is no var with name: " + name);
	}

	@Override
	public boolean TestVar(String name)
	{
		GraphLink sensor_link = GetSensorLink(name);

		if(sensor_link != null)
			return true;

		GraphLink derived_link = GetDerivedLink(name);

		if(derived_link != null)
			return true;

		return false;
	}

	@Nullable
	private GraphLink GetSensorLink(String name)
	{
		OctoLinkList sensor_links = graph_service
			.GetOutLinks(this).Filter(sensor_link, name);

		if(sensor_links.size() == 0)
			return null;

		return sensor_links.Only();
	}

	@Nullable
	private GraphLink GetDerivedLink(String name)
	{
		OctoLinkList derived_links = graph_service
			.GetOutLinks(this).Filter(derived_link, name);

		if(derived_links.size() == 0)
			return null;

		return derived_links.Only();
	}*/
}
