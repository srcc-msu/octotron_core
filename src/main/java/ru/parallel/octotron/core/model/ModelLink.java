package ru.parallel.octotron.core.model;

import com.sun.istack.internal.Nullable;
import ru.parallel.octotron.core.graph.ILink;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.model.attribute.AttributeObject;
import ru.parallel.octotron.core.model.attribute.AttributeObjectFactory;
import ru.parallel.octotron.core.model.attribute.DerivedObject;
import ru.parallel.octotron.core.model.attribute.SensorObject;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.rule.OctoRule;

import java.util.List;

public class ModelLink extends ModelEntity implements ILink
{
	public ModelLink(GraphService graph_service, GraphLink link)
	{
		super(graph_service, link);
	}

	@Override
	public void AddRules(List<OctoRule> rules)
	{
		for(OctoRule rule : rules)
		{
			GetBaseEntity().DeclareAttribute(rule.GetName(), rule.GetDefaultValue());
			DerivedObject.Create(GetGraphService(), this, rule);
		}
	}

	@Nullable
	@Override
	public AttributeObject GetAttributeObject(String name)
	{
		ObjectList<GraphObject, GraphLink> derived_objects
			= DerivedObject.Obtain(this, name);

		if(derived_objects.size() != 0)
			return new DerivedObject(GetGraphService(), derived_objects.Only());

		ObjectList<GraphObject, GraphLink> sensor_objects
			= SensorObject.Obtain(this, name);

		if(sensor_objects.size() != 0)
			return new SensorObject(GetGraphService(), sensor_objects.Only());

		return null;
	}

	@Override
	public void AddSensor(SimpleAttribute attribute)
	{
		GetBaseEntity().DeclareAttribute(attribute.GetName(), attribute.GetValue());
		SensorObject.Create(GetGraphService(), this, attribute);
	}

	@Override
	public ModelObject Target()
	{
		return new ModelObject(GetGraphService(), GetBaseLink().Target());
	}

	@Override
	public ModelObject Source()
	{
		return new ModelObject(GetGraphService(), GetBaseLink().Source());
	}
}
