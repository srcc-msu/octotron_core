/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core;

import ru.parallel.octotron.primitive.EEntityType;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.Uid;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.utils.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * provides additional features over raw graph interface<br>
 * such as: static object, aid counter, meta attributes and arrays<br>
 * ensures all objects have a correct AID<br>
 * provides meta-attributes mechanic
 * */
public final class GraphService
{
	private static final String NEXT_AID = "_static_next_AID";

	private final ru.parallel.octotron.core.IGraph graph;

	private ru.parallel.octotron.core.OctoObject static_obj = null;

	public GraphService(IGraph graph)
	{
		this.graph = graph;
		InitStatic();
	}

/**
 * return next value for AID counter from static object<br>
 * increment and store new value in db<br>
 * */
	private long NextAID()
	{
		if(!TestAttribute(static_obj, GraphService.NEXT_AID))
			SetAttribute(static_obj, GraphService.NEXT_AID, 0L);

		long next_AID = GetAttribute(static_obj, GraphService.NEXT_AID).GetLong();
		SetAttribute(static_obj, GraphService.NEXT_AID, next_AID + 1);

		return next_AID;
	}

//---------------------------------
//          STATIC
//---------------------------------
	private static final String STATIC_PREFIX = "_static_";

	public static boolean IsStaticName(String name)
	{
		return name.startsWith(GraphService.STATIC_PREFIX);
	}

/**
 * create static object if it does not exist<br>
 * otherwise - find existing in the graph <br>
 * */
	private void InitStatic()
	{
		if(static_obj == null)
		{
			EnableObjectIndex("type");
			OctoObjectList objects = GetObjects("type", GraphService.STATIC_PREFIX);

			if(objects.size() == 0)
			{
// have to do it manually - no AID yet
				static_obj = new OctoObject(this, graph.AddObject());

				SetRawAttribute(static_obj, "type", GraphService.STATIC_PREFIX);
				SetRawAttribute(static_obj, "AID", NextAID());
			}
			else
				static_obj = objects.Only();
		}
	}

	private void DeleteStatic()
	{
		graph.DeleteObject(static_obj.GetUID());
		static_obj = null;
	}

	public OctoObject GetStatic()
	{
		return static_obj;
	}

//---------------------
//       LINKS AND OBJECTS
//---------------------
	public OctoLink AddLink(OctoObject source, OctoObject target, String link_type)
	{
		OctoLink link = new OctoLink(this, graph.AddLink(source.GetUID(), target.GetUID(), link_type));
		SetAttribute(link, "AID", NextAID());

		return link;
	}

	public OctoObject AddObject()
	{
		OctoObject object = new OctoObject(this, graph.AddObject());
		SetAttribute(object, "AID", NextAID());

		return object;
	}

	public void Delete(OctoEntity entity)
	{
		Uid uid = entity.GetUID();

		if(uid.getType() == EEntityType.OBJECT)
		{
			if(IsStaticObject(entity))
				throw new ExceptionModelFail("can not delete static object");

			graph.DeleteObject(uid);
		}
		else if(uid.getType() == EEntityType.LINK)
		{
			graph.DeleteLink(uid);
		}
		else
			throw new ExceptionModelFail("unknown entity type");
	}

	public OctoLinkList GetInLinks(OctoObject object)
	{
		OctoLinkList list = new OctoLinkList();

		for(Uid uid : graph.GetInLinks(object.GetUID()))
			list.add(new OctoLink(this, uid));

		return list;
	}

	public OctoLinkList GetOutLinks(OctoObject object)
	{
		OctoLinkList list = new OctoLinkList();

		for(Uid uid : graph.GetOutLinks(object.GetUID()))
			list.add(new OctoLink(this, uid));

		return list;
	}

//--------------------
//    RAW ATTRIBUTES
//--------------------
	private boolean TestRawAttribute(OctoEntity entity, String name)
	{
		Uid uid = entity.GetUID();

		if(uid.getType() == EEntityType.OBJECT)
		{
			return graph.TestObjectAttribute(uid, name);
		}
		else if(uid.getType() == EEntityType.LINK)
		{
			return graph.TestLinkAttribute(uid, name);
		}
		else
			throw new ExceptionModelFail("unknown entity type");
	}

	private void DeleteRawAttribute(OctoEntity entity, String name)
	{
		Uid uid = entity.GetUID();

		if(uid.getType() == EEntityType.OBJECT)
		{
			graph.DeleteObjectAttribute(uid, name);
		}
		else if(uid.getType() == EEntityType.LINK)
		{
			graph.DeleteLinkAttribute(uid, name);
		}
		else
			throw new ExceptionModelFail("unknown entity type");
	}

	private void SetRawAttribute(OctoEntity entity, String name, Object value)
	{
		Uid uid = entity.GetUID();

		if(uid.getType() == EEntityType.OBJECT)
		{
			graph.SetObjectAttribute(uid, name, value);
		}
		else if(uid.getType() == EEntityType.LINK)
		{
			graph.SetLinkAttribute(uid, name, value);
		}
		else
			throw new ExceptionModelFail("unknown entity type");
	}

	private Object GetRawAttribute(OctoEntity entity, String name)
	{
		Uid uid = entity.GetUID();
		Object value;

		if(uid.getType() == EEntityType.OBJECT)
			value = graph.GetObjectAttribute(uid, name);
		else if(uid.getType() == EEntityType.LINK)
			value = graph.GetLinkAttribute(uid, name);
		else
			throw new ExceptionModelFail("unknown entity type");

		return value;
	}

	private List<Pair<String, Object>> GetRawAttributes(OctoEntity entity)
	{
		Uid uid = entity.GetUID();

		List<Pair<String, Object>> pairs;

		if(uid.getType() == EEntityType.OBJECT)
		{
			pairs = graph.GetObjectAttributes(uid);
		}
		else if(uid.getType() == EEntityType.LINK)
		{
			pairs = graph.GetLinkAttributes(uid);
		}
		else
			throw new ExceptionModelFail("unknown entity type");

		return pairs;
	}

//------------------
//    ATTRIBUTES
//------------------

	boolean TestAttribute(OctoEntity entity, String name)
	{
		if(IsStaticName(name))
			entity = static_obj;

		return TestRawAttribute(entity, name);
	}

	void DeleteAttribute(OctoEntity entity, String name)
	{
		if(IsStaticName(name))
			entity = static_obj;

		DeleteRawAttribute(entity, name);
	}

	OctoAttribute GetAttribute(OctoEntity entity, String name)
	{
		if(IsStaticName(name))
			entity = static_obj;

		return new OctoAttribute(this, entity, name, GetRawAttribute(entity, name));
	}

/**
 * does not show arrays and meta attributes<br>
 * */
	OctoAttributeList GetAttributes(OctoEntity entity)
	{
		List<Pair<String, Object>> pairs = GetRawAttributes(entity);

		OctoAttributeList list = new OctoAttributeList();

		for(Pair<String, Object> pair : pairs)
		{
			if(!IsMeta(pair.getKey()) && !pair.getValue().getClass().isArray())
				list.add(new OctoAttribute(this, entity, pair.getKey(), pair.getValue()));
		}

		return list;
	}

/**
 * set the attribute to the object<br>
 * if attribute is static - set the attribute to the static object instead<br>
 * */
	OctoAttribute SetAttribute(OctoEntity entity, String name, Object value)
	{
		if(IsStaticObject(entity))
			if(!IsStaticName(name))
				throw new ExceptionModelFail("properties for static object must start with " + STATIC_PREFIX);

		if(IsStaticName(name))
			entity = static_obj;

		SetRawAttribute(entity, name, value);

		return new OctoAttribute(this, entity, name, value);
	}

	private boolean IsStaticObject(OctoEntity entity)
	{
		if(entity.GetUID().getType() != EEntityType.OBJECT)
			return false;

		return entity.GetUID().getUid() == static_obj.GetUID().getUid();
	}

// ---------------------------------
//        META ATTRIBUTES
//---------------------------------

	private static final String META_PREFIX = "_meta_";

	private static String ToMeta(String attr_name, String meta_name)
	{
		return GraphService.META_PREFIX + attr_name + '_' + meta_name;
	}

	private static boolean IsMeta(String attr_name)
	{
		return attr_name.startsWith(GraphService.META_PREFIX);
	}

	public void SetMeta(OctoEntity entity, String attr_name, String meta_name, Object value)
	{
		SetRawAttribute(entity, GraphService.ToMeta(attr_name, meta_name), value);
	}

	public Object GetMeta(OctoEntity entity, String attr_name, String meta_name)
	{
		return GetRawAttribute(entity, GraphService.ToMeta(attr_name, meta_name));
	}

	public boolean TestMeta(OctoEntity entity, String attr_name, String meta_name)
	{
		return TestRawAttribute(entity, GraphService.ToMeta(attr_name, meta_name));
	}

	public void DeleteMeta(OctoEntity entity, String attr_name, String meta_name)
	{
		DeleteRawAttribute(entity, GraphService.ToMeta(attr_name, meta_name));
	}

	public SimpleAttributeList GetAllMeta(OctoEntity entity)
	{
		List<Pair<String, Object>> attributes = GetRawAttributes(entity);

		SimpleAttributeList result = new SimpleAttributeList();

		for(Pair<String, Object> attribute : attributes)
			if(IsMeta(attribute.getKey()))
				result.add(new SimpleAttribute(attribute.getKey(), attribute.getValue()));

		return result;
	}

// --------------------------------
//			CACHE
//---------------------------------

	private OctoLinkList LinksFromUid(List<Uid> uids)
	{
		OctoLinkList list = new OctoLinkList();

		for(Uid uid : uids)
			list.add(new OctoLink(this, uid));

		return list;
	}

	private OctoObjectList ObjectsFromUid(List<Uid> uids)
	{
		OctoObjectList list = new OctoObjectList();

		for(Uid uid : uids)
			list.add(new OctoObject(this, uid));

		return list;
	}

	public void EnableLinkIndex(String name)
	{
		graph.GetIndex().EnableLinkIndex(name);
	}

	public void EnableObjectIndex(String name)
	{
		graph.GetIndex().EnableObjectIndex(name);
	}

	public OctoLinkList GetAllLinks()
	{
		return LinksFromUid(graph.GetAllLinks());
	}

	public OctoObjectList GetAllObjects()
	{
		OctoObjectList result = new OctoObjectList();

		for(OctoObject object : ObjectsFromUid(graph.GetAllObjects()))
			if(!IsStaticObject(object))
				result.add(object);

		return result;
	}

	public OctoLink GetLink(SimpleAttribute att)
	{
		return GetLink(att.GetName(), att.GetValue());
	}

	public OctoLink GetLink(String name, Object value)
	{
		Uid uid = graph.GetIndex().GetLink(name, value);
		return new OctoLink(this, uid);
	}

	public OctoLinkList GetLinks(SimpleAttribute att)
	{
		return GetLinks(att.GetName(), att.GetValue());
	}

	public OctoLinkList GetLinks(String name)
	{
		return LinksFromUid(graph.GetIndex().GetLinks(name));
	}

	public OctoLinkList GetLinks(String name, Object value)
	{
		return LinksFromUid(graph.GetIndex().GetLinks(name, value));
	}

	public OctoObject GetObject(SimpleAttribute att)
	{
		return GetObject(att.GetName(), att.GetValue());
	}

	public OctoObject GetObject(String name, Object value)
	{
		Uid uid = graph.GetIndex().GetObject(name, value);
		return new OctoObject(this, uid);
	}

	public OctoObjectList GetObjects(SimpleAttribute att)
	{
		return GetObjects(att.GetName(), att.GetValue());
	}

	public OctoObjectList GetObjects(String name)
	{
		return ObjectsFromUid(graph.GetIndex().GetObjects(name));
	}

	public OctoObjectList GetObjects(String name, Object value)
	{
		return ObjectsFromUid(graph.GetIndex().GetObjects(name, value));
	}

	public OctoLinkList QueryLinks(String name, String value)
	{
		return LinksFromUid(graph.GetIndex().QueryLinks(name, value));
	}

	public OctoObjectList QueryObjects(String name, String value)
	{
		return ObjectsFromUid(graph.GetIndex().QueryObjects(name, value));
	}

	public OctoObject GetLinkTarget(OctoLink link)
	{
		return new OctoObject(this, graph.GetLinkTarget(link.GetUID()));
	}

	public OctoObject GetLinkSource(OctoLink link)
	{
		return new OctoObject(this, graph.GetLinkSource(link.GetUID()));
	}

// ---------------------------------
//			ARRAYS
// ---------------------------------

	public void SetArray(OctoEntity entity, String prefix, List<Long> list)
	{
		Long[] array = Arrays.copyOf(list.toArray(), list.size(), Long[].class);

		SetRawAttribute(entity, prefix, ArrayUtils.toPrimitive(array));
	}

	public List<Long> GetArray(OctoEntity entity, String prefix)
	{
		if(!TestAttribute(entity, prefix))
			return new LinkedList<>();

		Object value = GetRawAttribute(entity, prefix);
		long[] ints = (long[])value;

		return new LinkedList<>(Arrays.asList(ArrayUtils.toObject(ints)));
	}

	public void AddToArray(OctoEntity entity, String prefix, Long value)
	{
		List<Long> list = GetArray(entity, prefix);
		list.add(value);
		SetArray(entity, prefix, list);
	}

	public void CleanArray(OctoEntity entity, String prefix)
	{
		DeleteAttribute(entity, prefix);
	}

// ---------------------------------
//			 Utility
// ---------------------------------

	public void Clean()
	{
		for(OctoObject obj : GetAllObjects())
			Delete(obj);

		for(OctoLink link : GetAllLinks())
			Delete(link);

		DeleteStatic();
		InitStatic();
	}

	public String ExportDot()
	{
		return graph.ExportDot(GetAllObjects());
	}

	public String ExportDot(OctoObjectList list)
	{
		return graph.ExportDot(list);
	}
}
