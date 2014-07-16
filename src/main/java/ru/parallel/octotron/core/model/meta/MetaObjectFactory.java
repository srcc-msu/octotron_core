package ru.parallel.octotron.core.model.meta;

import com.sun.istack.internal.Nullable;
import ru.parallel.octotron.core.graph.collections.ListConverter;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.UniqueName;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.LinkedList;
import java.util.List;


// TODO make cache
public abstract class MetaObjectFactory<T extends MetaObject, V extends UniqueName>
{
	protected abstract T CreateInstance(GraphService graph_service, GraphObject meta_object);
	protected abstract String GetLabel();

	public T Create(GraphService graph_service, GraphEntity parent, V object)
	{
		GraphObject meta_object
			= Create(graph_service, parent, object.GetUniqName(), GetLabel());

		T derived_object = CreateInstance(graph_service, meta_object);

		derived_object.Init(object);
		return derived_object;
	}

	public List<T> ObtainAll(GraphService graph_service, GraphEntity parent)
	{
		ObjectList<GraphObject, GraphLink> candidates
			= Candidates(graph_service, parent, GetLabel());

		List<T> result = new LinkedList<>();

		for(GraphObject object : candidates)
		{
			result.add(CreateInstance(graph_service, object));
		}

		return result;
	}

	public List<T> ObtainAll(GraphService graph_service, GraphEntity parent, String name)
	{
		ObjectList<GraphObject, GraphLink> candidates
			= Candidates(graph_service, parent, name, GetLabel());

		List<T> result = new LinkedList<>();

		for(GraphObject object : candidates)
		{
			result.add(CreateInstance(graph_service, object));
		}

		return result;
	}

	public T Obtain(GraphService graph_service, GraphEntity parent, String name)
	{
		return CreateInstance(graph_service
			, Candidates(graph_service, parent, name, GetLabel()).Only());
	}

	@Nullable
	public T TryObtain(GraphService graph_service, GraphEntity parent, String name)
	{
		List<T> meta_objects
			= ObtainAll(graph_service, parent, name);

		if(meta_objects.size() == 1)
			return meta_objects.get(0);

		return null;
	}

	private static GraphObject Create(GraphService graph_service, GraphEntity parent, String name, String label)
	{
		if(parent.GetUID().getType() == EEntityType.OBJECT)
			return CreateObjectMeta(graph_service, (GraphObject) parent, name, label);
		else if(parent.GetUID().getType() == EEntityType.LINK)
			return CreateLinkMeta(graph_service, (GraphLink) parent, name, label);
		else
			throw new ExceptionModelFail("wtf"); // TODO
	}

	private static ObjectList<GraphObject, GraphLink> Candidates(GraphService graph_service, GraphEntity parent, String label)
	{
		if(parent.GetUID().getType() == EEntityType.OBJECT)
			return GetObjectMetas(graph_service, (GraphObject) parent, label);
		else if(parent.GetUID().getType() == EEntityType.LINK)
			return GetLinkMetas(graph_service, (GraphLink) parent, label);
		else
			throw new ExceptionModelFail("wtf"); // TODO
	}

	private static ObjectList<GraphObject, GraphLink> Candidates(GraphService graph_service, GraphEntity parent, String name, String label)
	{
		if(parent.GetUID().getType() == EEntityType.OBJECT)
			return GetObjectMetas(graph_service, (GraphObject) parent, name, label);
		else if(parent.GetUID().getType() == EEntityType.LINK)
			return GetLinkMetas(graph_service, (GraphLink) parent, name, label);
		else
			throw new ExceptionModelFail("wtf"); // TODO
	}

	private static final String meta_const = "_meta";

	private static GraphObject CreateObjectMeta(GraphService graph_service, GraphObject parent, String name, String label)
	{
		GraphObject object = graph_service.AddObject();
		object.AddLabel(label);

		GraphLink link = graph_service.AddLink(parent, object, label);
		link.DeclareAttribute(meta_const, name);

		return object;
	}

	private static ObjectList<GraphObject, GraphLink> GetObjectMetas(GraphService graph_service, GraphObject parent, String name, String label)
	{
		return ListConverter.FilterLabel(parent.GetOutNeighbors(meta_const, name), label);
	}

	private static ObjectList<GraphObject, GraphLink> GetObjectMetas(GraphService graph_service, GraphObject parent, String label)
	{
		return ListConverter.FilterLabel(parent.GetOutNeighbors(), label);
	}

	private static final String owner_const = "_owner_AID";
	private static final String name_const = "_owner_name";

	private static GraphObject CreateLinkMeta(GraphService graph_service, GraphLink parent, String name, String label)
	{
		GraphObject object = graph_service.AddObject();
		object.AddLabel(label);

		object.DeclareAttribute(owner_const, parent.GetAttribute("AID").GetLong());
		object.DeclareAttribute(name_const, name);

		return object;
	}

	private static ObjectList<GraphObject, GraphLink> GetLinkMetas(GraphService graph_service, GraphLink parent, String name, String label)
	{
		long AID = parent.GetAttribute("AID").GetLong();

		return graph_service.GetAllLabeledNodes(label)
			.Filter(owner_const, AID).Filter(name_const, name);
	}

	private static ObjectList<GraphObject, GraphLink> GetLinkMetas(GraphService graph_service, GraphLink parent, String label)
	{
		long AID = parent.GetAttribute("AID").GetLong();

		return graph_service.GetAllLabeledNodes(label)
			.Filter(owner_const, AID);
	}
}
