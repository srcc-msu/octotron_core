package ru.parallel.octotron.core.model;

import com.sun.istack.internal.Nullable;
import ru.parallel.octotron.core.graph.IObject;
import ru.parallel.octotron.core.graph.collections.LinkList;
import ru.parallel.octotron.core.graph.collections.ListConverter;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.model.attribute.AttributeObject;
import ru.parallel.octotron.core.model.attribute.AttributeObjectFactory;
import ru.parallel.octotron.core.model.attribute.DerivedObject;
import ru.parallel.octotron.core.model.attribute.SensorObject;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.rule.OctoRule;

import java.util.List;

public class ModelObject extends ModelEntity implements IObject
{
	public ModelObject(GraphService graph_service, GraphObject object)
	{
		super(graph_service, object);
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

	@Override
	public LinkList<ModelObject, ModelLink> GetInLinks()
	{
		LinkList<GraphObject, GraphLink> links
			= GetBaseObject().GetInLinks();

		LinkList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(links, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(GetGraphService(), filtered);
	}

	@Override
	public LinkList<ModelObject, ModelLink> GetOutLinks()
	{
		LinkList<GraphObject, GraphLink> links
			= GetBaseObject().GetOutLinks();

		LinkList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(links, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(GetGraphService(), filtered);
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetInNeighbors()
	{
		ObjectList<GraphObject, GraphLink> Objects
			= GetBaseObject().GetInNeighbors();

		ObjectList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(GetGraphService(), filtered);
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetOutNeighbors()
	{
		ObjectList<GraphObject, GraphLink> Objects
			= GetBaseObject().GetOutNeighbors();

		ObjectList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(GetGraphService(), filtered);
	}
	@Override
	public ObjectList<ModelObject, ModelLink> GetInNeighbors(String link_name, Object link_value)
	{
		ObjectList<GraphObject, GraphLink> Objects
			= GetBaseObject().GetInNeighbors(link_name, link_value);

		ObjectList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(GetGraphService(), filtered);
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetOutNeighbors(String link_name, Object link_value)
	{
		ObjectList<GraphObject, GraphLink> Objects
			= GetBaseObject().GetOutNeighbors(link_name, link_value);

		ObjectList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(GetGraphService(), filtered);
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetInNeighbors(String link_name)
	{
		ObjectList<GraphObject, GraphLink> Objects
			= GetBaseObject().GetInNeighbors(link_name);

		ObjectList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(GetGraphService(), filtered);
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetOutNeighbors(String link_name)
	{
		ObjectList<GraphObject, GraphLink> Objects
			= GetBaseObject().GetOutNeighbors(link_name);

		ObjectList<GraphObject, GraphLink> filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(GetGraphService(), filtered);
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
}
