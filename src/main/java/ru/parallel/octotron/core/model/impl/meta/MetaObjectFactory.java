package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.*;
import ru.parallel.octotron.core.primitive.EEntityType;
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

	public final T Create(GraphEntity parent, V object)
	{
		GraphObject meta_object = Create(parent, object.GetUniqName(), GetLabel());

		T derived_object = CreateInstance(meta_object);

		derived_object.Init(object);
		return derived_object;
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

	private static final SimpleAttribute meta_type_const = new SimpleAttribute("type", "_meta");
	public static final String owner_aid_const = "_owner_AID";
	private static final String owner_name_const = "_owner_name";

	private static final SimpleAttribute object_type_const = new SimpleAttribute("owner_type", "object");
	private static final SimpleAttribute link_type_const = new SimpleAttribute("owner_type", "link");

	private static GraphObject CreateObjectMeta(GraphObject parent, String name, String label)
	{
		GraphObject object = GraphService.Get().AddObject();
		object.AddLabel(label);
		object.DeclareAttribute(object_type_const);
		object.DeclareAttribute(owner_aid_const, parent.GetAttribute("AID").GetLong());
		object.DeclareAttribute(owner_name_const, name);

		GraphLink link = GraphService.Get().AddLink(parent, object, meta_type_const);
		link.DeclareAttribute(meta_type_const);
		link.DeclareAttribute(label, name);

		return object;
	}

	private static GraphObject CreateLinkMeta(GraphLink parent, String name, String label)
	{
		GraphObject object = GraphService.Get().AddObject();
		object.AddLabel(label);
		object.DeclareAttribute(link_type_const);
		object.DeclareAttribute(owner_aid_const, parent.GetAttribute("AID").GetLong());
		object.DeclareAttribute(owner_name_const, name);

		return object;
	}

// ----------------
// test
// ----------------

	public final boolean Test(GraphEntity parent, String name)
	{
		if(parent.GetUID().getType() == EEntityType.OBJECT)
			return Test((GraphObject) parent, name, GetLabel());
		else if(parent.GetUID().getType() == EEntityType.LINK)
			return Test((GraphLink) parent, name, GetLabel());
		else
			throw new ExceptionModelFail("wtf"); // TODO
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

	private static boolean Test(GraphLink parent, String name, String label)
	{
		long AID = parent.GetAttribute("AID").GetLong();

		GraphObjectList objects = GraphService.Get().GetAllLabeledNodes(label)
			.Filter(owner_aid_const, AID).Filter(owner_name_const, name);

		if(objects.size() > 1)
			throw new ExceptionModelFail("ambiguous meta objects for attribute: " + name);
		else if(objects.size() == 1)
			return true;

		return false;
	}

// ----------------
// obtain
// ----------------

	public final T Obtain(GraphEntity parent, String name)
	{
		return CreateInstance(Obtain(parent, name, GetLabel()));
	}

	private static GraphObject Obtain(GraphEntity parent, String name, String label)
	{
		if(parent.GetUID().getType() == EEntityType.OBJECT)
			return Obtain((GraphObject) parent, name, label);
		else if(parent.GetUID().getType() == EEntityType.LINK)
			return Obtain((GraphLink) parent, name, label);
		else
			throw new ExceptionModelFail("wtf"); // TODO
	}


	private static GraphObject Obtain(GraphObject parent, String name, String label)
	{
		return parent.GetOutLinks().Filter(label, name).Only().Target();
	}

	private static GraphObject Obtain(GraphLink parent, String name, String label)
	{
		long AID = parent.GetAttribute("AID").GetLong();

		return GraphService.Get().GetAllLabeledNodes(label)
			.Filter(owner_aid_const, AID).Filter(owner_name_const, name).Only();
	}

// ----------------
// obtain all
// ----------------

	public final List<T> ObtainAll(GraphEntity parent)
	{
		List<T> result = new LinkedList<>();

		for(GraphObject object : ObtainAll(parent, GetLabel()))
			result.add(CreateInstance(object));

		return result;
	}

	private static GraphObjectList ObtainAll(GraphEntity parent, String label)
	{
		if(parent.GetUID().getType() == EEntityType.OBJECT)
			return ObtainAll((GraphObject) parent, label);
		else if(parent.GetUID().getType() == EEntityType.LINK)
			return ObtainAll((GraphLink) parent, label);
		else
			throw new ExceptionModelFail("wtf"); // TODO
	}

	private static GraphObjectList ObtainAll(GraphObject parent, String label)
	{
		return parent.GetOutLinks().Filter(label).Target();
	}

	private static GraphObjectList ObtainAll(GraphLink parent, String label)
	{
		long AID = parent.GetAttribute("AID").GetLong();

		return GraphService.Get().GetAllLabeledNodes(label)
			.Filter(owner_aid_const, AID);
	}

// ----------------
// get parent
// ----------------

	public static GraphEntity GetParent(GraphObject meta)
	{
		if(meta.TestAttribute(object_type_const))
		{
			return meta.GetInNeighbors(meta_type_const).Only();
		}
		else if(meta.TestAttribute(link_type_const))
		{
			return GraphService.Get().GetLink("AID", meta.GetAttribute(owner_aid_const).GetLong());
		}
		else
			throw new ExceptionModelFail("WTF");
	}

	public static String GetName(GraphObject meta)
	{
		return meta.GetAttribute(owner_name_const).GetString();
	}
}
