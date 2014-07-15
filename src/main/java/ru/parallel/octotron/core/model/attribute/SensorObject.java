package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.graph.collections.AutoFormat;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.util.LinkedList;

public class SensorObject extends AttributeObject
{
	public SensorObject(GraphService graph_service, GraphObject object)
	{
		super(graph_service, object);
	}

	public static SensorObject Create(GraphService graph_service, ModelObject parent, SimpleAttribute attribute)
	{
		return Create(graph_service, parent, attribute.GetName(), attribute.GetValue());
	}

	public static SensorObject Create(GraphService graph_service, ModelObject parent, String name, Object value)
	{
		GraphObject object = graph_service.AddObject();
		graph_service.AddLink(parent.GetBaseObject(), object, name);
		object.AddLabel(EObjectLabels.SENSOR.toString());

		Init(object, name, value);

		return new SensorObject(graph_service, object);
	}

	private static final String owner_const = "_owner_AID";

	public static SensorObject Create(GraphService graph_service, ModelLink parent, SimpleAttribute attribute)
	{
		return Create(graph_service, parent, attribute.GetName(), attribute.GetValue());
	}

	public static SensorObject Create(GraphService graph_service, ModelLink parent, String name, Object value)
	{
		GraphObject object = graph_service.AddObject();
		object.DeclareAttribute(owner_const, parent.GetAttribute("AID").GetLong());

		object.AddLabel(EObjectLabels.SENSOR.toString());

		Init(object, name, value);

		return new SensorObject(graph_service, object);
	}

	public static ObjectList<GraphObject, GraphLink> Obtain(ModelObject parent, String name)
	{
		return parent.GetBaseObject().GetOutNeighbors("type", name);
	}

	public static ObjectList<GraphObject, GraphLink> Obtain(ModelLink parent, String name)
	{
		return parent.GetGraphService().GetAllLabeledNodes(EObjectLabels.SENSOR.toString())
			.Filter(owner_const, parent.GetBaseEntity().GetAttribute("AID").GetLong());
	}
}
