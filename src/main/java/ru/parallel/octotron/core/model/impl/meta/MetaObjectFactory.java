package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.*;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.UniqueName;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.LinkedList;
import java.util.List;

public abstract class MetaObjectFactory<T extends MetaObject, V extends UniqueName>
{
	protected MetaObjectFactory() {}

	protected abstract T CreateInstance(GraphObject meta_object);
	protected abstract String GetLabel();

// ----------------
// create
// ----------------

	public final T Create(GraphObject parent, V object)
	{
		GraphObject meta_object = CreateObjectMeta(parent, object.GetUniqName(), GetLabel());

		T derived_object = CreateInstance(meta_object);

		derived_object.Init(object);
		return derived_object;
	}

	private static final SimpleAttribute meta_type_const = new SimpleAttribute("type", "_meta");
	private static final String owner_name = "owner_name";

	private static GraphObject CreateObjectMeta(GraphObject parent, String name, String label)
	{
		GraphObject object = GraphService.Get().AddObject();
		object.DeclareAttribute(owner_name, name);
		object.AddLabel(label);

		GraphLink link = GraphService.Get().AddLink(parent, object, meta_type_const);
		link.DeclareAttribute(meta_type_const);
		link.DeclareAttribute(label, name);

		return object;
	}

// ----------------
// test
// ----------------

	public final boolean Test(GraphObject parent, String name)
	{
		return Test(parent, name, GetLabel());
	}

	private static boolean Test(GraphObject parent, String name, String label)
	{
		GraphLinkList links = parent.GetOutLinks().Filter(label, name);

		if(links.size() > 1)
			throw new ExceptionModelFail("ambiguous meta objects for attribute: " + name);
		else if(links.size() == 1)
			return true;

		return false;
	}

// ----------------
// obtain
// ----------------

	public final T Obtain(GraphObject parent, String name)
	{
		return CreateInstance(Obtain(parent, name, GetLabel()));
	}

	private static GraphObject Obtain(GraphObject parent, String name, String label)
	{
		return parent.GetOutLinks().Filter(label, name).Only().Target();
	}

// ----------------
// obtain all
// ----------------

	public final List<T> ObtainAll(GraphObject parent)
	{
		List<T> result = new LinkedList<>();

		for(GraphObject object : ObtainAll(parent, GetLabel()))
			result.add(CreateInstance(object));

		return result;
	}

	private static GraphObjectList ObtainAll(GraphObject parent, String label)
	{
		return parent.GetOutLinks().Filter(label).Target();
	}

// ----------------
// get parent
// ----------------

	public static GraphObject GetParent(GraphObject meta)
	{
		return meta.GetInNeighbors(meta_type_const).Only();
	}

	public static String GetName(GraphObject meta)
	{
		return meta.GetAttribute(owner_name).GetString();
	}
}
