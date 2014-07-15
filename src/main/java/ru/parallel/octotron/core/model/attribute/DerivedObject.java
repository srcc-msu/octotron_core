package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.impl.PersistentStorage;

public class DerivedObject extends AttributeObject
{
	public DerivedObject(GraphService graph_service, GraphObject object)
	{
		super(graph_service, object);
	}

	private static final String rid_const = "_rid";

	public OctoRule GetRule()
	{
		return PersistentStorage.INSTANCE.GetRules().Get(GetAttribute(rid_const).GetLong());
	}

	public static DerivedObject Create(GraphService graph_service, ModelObject parent, OctoRule rule)
	{
		GraphObject object = graph_service.AddObject();
		graph_service.AddLink(parent.GetBaseObject(), object, rule.GetName());
		object.AddLabel(EObjectLabels.DERIVED.toString());

		object.DeclareAttribute(rid_const, rule.GetID());
		Init(object, rule.GetName(), rule.GetDefaultValue());

		return new DerivedObject(graph_service, object);
	}

	private static final String owner_const = "_owner_AID";

	public static DerivedObject Create(GraphService graph_service, ModelLink parent, OctoRule rule)
	{
		GraphObject object = graph_service.AddObject();
		object.DeclareAttribute(owner_const, parent.GetAttribute("AID").GetLong());

		object.AddLabel(EObjectLabels.DERIVED.toString());

		object.DeclareAttribute(rid_const, rule.GetID());
		Init(object, rule.GetName(), rule.GetDefaultValue());

		return new DerivedObject(graph_service, object);
	}

	public static ObjectList<GraphObject, GraphLink> Obtain(ModelObject parent, String name)
	{
		return parent.GetBaseObject().GetOutNeighbors("type", name);
	}

	public static ObjectList<GraphObject, GraphLink> Obtain(ModelLink parent, String name)
	{
		return parent.GetGraphService().GetAllLabeledNodes(EObjectLabels.DERIVED.toString())
			.Filter(owner_const, parent.GetBaseEntity().GetAttribute("AID").GetLong());
	}
}
