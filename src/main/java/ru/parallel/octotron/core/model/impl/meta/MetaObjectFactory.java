package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.collections.ListConverter;
import ru.parallel.octotron.core.graph.impl.*;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.UniqueName;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;


// TODO make cache
public abstract class MetaObjectFactory<T extends MetaObject, V extends UniqueName>
{
	protected MetaObjectFactory() {}

	protected abstract T CreateInstance(GraphObject meta_object);
	protected abstract String GetLabel();

	public T Create(GraphEntity parent, V object)
	{
		GraphObject meta_object
			= Create(parent, object.GetUniqName(), GetLabel());

		T derived_object = CreateInstance(meta_object);

		derived_object.Init(object);
		return derived_object;
	}

	public List<T> ObtainAll(GraphEntity parent)
	{
		GraphObjectList candidates
			= Candidates(parent, GetLabel());

		List<T> result = new LinkedList<>();

		for(GraphObject object : candidates)
		{
			result.add(CreateInstance(object));
		}

		return result;
	}

	public List<T> ObtainAll(GraphEntity parent, String name)
	{
		GraphObjectList candidates
			= Candidates(parent, name, GetLabel());

		List<T> result = new LinkedList<>();

		for(GraphObject object : candidates)
		{
			result.add(CreateInstance(object));
		}

		return result;
	}

	public T Obtain(GraphEntity parent, String name)
	{
		return CreateInstance(Candidates(parent, name, GetLabel()).Only());
	}

	@Nullable
	public T TryObtain(GraphEntity parent, String name)
	{
		List<T> meta_objects
			= ObtainAll(parent, name);

		if(meta_objects.size() == 1)
			return meta_objects.get(0);

		return null;
	}

	private static GraphObject Create(GraphEntity parent, String name, String label)
	{
		if(parent.GetUID().getType() == EEntityType.OBJECT)
			return CreateObjectMeta((GraphObject) parent, name, label);
		else if(parent.GetUID().getType() == EEntityType.LINK)
			return CreateLinkMeta((GraphLink) parent, name, label);
		else
			throw new ExceptionModelFail("wtf"); // TODO
	}

	private static GraphObjectList Candidates(GraphEntity parent, String label)
	{
		if(parent.GetUID().getType() == EEntityType.OBJECT)
			return GetObjectMetas((GraphObject) parent, label);
		else if(parent.GetUID().getType() == EEntityType.LINK)
			return GetLinkMetas((GraphLink) parent, label);
		else
			throw new ExceptionModelFail("wtf"); // TODO
	}

	private static GraphObjectList Candidates(GraphEntity parent, String name, String label)
	{
		if(parent.GetUID().getType() == EEntityType.OBJECT)
			return GetObjectMetas((GraphObject) parent, name, label);
		else if(parent.GetUID().getType() == EEntityType.LINK)
			return GetLinkMetas((GraphLink) parent, name, label);
		else
			throw new ExceptionModelFail("wtf"); // TODO
	}

	private static final String meta_const = "_meta";

	private static GraphObject CreateObjectMeta(GraphObject parent, String name, String label)
	{
		GraphObject object = GraphService.Get().AddObject();
		object.AddLabel(label);

		GraphLink link = GraphService.Get().AddLink(parent, object, label);
		link.DeclareAttribute(meta_const, name);

		return object;
	}

	private static GraphObjectList GetObjectMetas(GraphObject parent, String name, String label)
	{
		return ListConverter.FilterLabel(parent.GetOutNeighbors(meta_const, name), label);
	}

	private static GraphObjectList GetObjectMetas(GraphObject parent, String label)
	{
		return ListConverter.FilterLabel(parent.GetOutNeighbors(), label);
	}

	private static final String owner_const = "_owner_AID";
	private static final String name_const = "_owner_name";

	private static GraphObject CreateLinkMeta(GraphLink parent, String name, String label)
	{
		GraphObject object = GraphService.Get().AddObject();
		object.AddLabel(label);

		object.DeclareAttribute(owner_const, parent.GetAttribute("AID").GetLong());
		object.DeclareAttribute(name_const, name);

		return object;
	}

	private static GraphObjectList GetLinkMetas(GraphLink parent, String name, String label)
	{
		long AID = parent.GetAttribute("AID").GetLong();

		return GraphService.Get().GetAllLabeledNodes(label)
			.Filter(owner_const, AID).Filter(name_const, name);
	}

	private static GraphObjectList GetLinkMetas(GraphLink parent, String label)
	{
		long AID = parent.GetAttribute("AID").GetLong();

		return GraphService.Get().GetAllLabeledNodes(label)
			.Filter(owner_const, AID);
	}
}
