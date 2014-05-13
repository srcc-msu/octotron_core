/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.core;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

import org.apache.commons.lang3.ArrayUtils;

import main.java.ru.parallel.octotron.primitive.EEntityType;
import main.java.ru.parallel.octotron.primitive.SimpleAttribute;
import main.java.ru.parallel.octotron.primitive.Uid;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionDBError;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.octotron.utils.AttributeList;
import main.java.ru.parallel.octotron.utils.LinkList;
import main.java.ru.parallel.octotron.utils.ObjectList;

public class GraphService
{
	public static final String RULE_PREFIX = "_RULE_";
	public static final String REACTION_PREFIX = "_REACTION_";
	public static final String MARKER_PREFIX = "_MARKER_";

	private static final String REACTION_EXECUTED = "_executed_";
	private static final String INVALID_ATTRIBUTE = "_invalid_";

	private static final String STATIC_PREFIX = "_static_";
	private static final String TIME_PREFIX = "_time_";

	private static final String NEXT_AID = "_static_next_AID";

	private IGraph graph;

	private OctoObject static_obj = null;

	public GraphService(IGraph graph)
		throws ExceptionModelFail, ExceptionDBError
	{
		this.graph = graph;
		InitStatic();
	}

	public long NextAID()
	{
		if(!TestAttribute(static_obj, NEXT_AID))
			DeclareStaticAttribute(NEXT_AID, 0L);

		long next_AID = GetStaticAttribute(NEXT_AID).GetLong();
		DeclareStaticAttribute(NEXT_AID, next_AID + 1);

		return next_AID;
	}

	public OctoLink AddLink(OctoObject source, OctoObject target, String link_type)
		throws ExceptionModelFail, ExceptionDBError
	{
		OctoLink link = new OctoLink(this, graph.AddLink(source.GetUID(), target.GetUID(), link_type));

		link.SetAttribute("source", source.GetAttribute("AID").GetLong());
		link.SetAttribute("target", target.GetAttribute("AID").GetLong());

		return link;
	}

	public OctoObject AddObject()
	{
		return new OctoObject(this, graph.AddObject());
	}

	public void Delete(OctoEntity entity)
		throws ExceptionModelFail
	{
		if(entity.GetUID().getUid() == static_obj.GetUID().getUid())
		{
			System.err.println("i refuse to delete my own static object");
			return;
		}

		Uid uid = entity.GetUID();

		if(uid.getType() == EEntityType.OBJECT)
		{
			graph.DeleteObject(uid);
		}
		else if(uid.getType() == EEntityType.LINK)
		{
			graph.DeleteLink(uid);
		}
		else
			throw new ExceptionModelFail("unknown entity type");
	}

	public boolean TestAttribute(OctoEntity entity, String name)
		throws ExceptionModelFail
	{
		if(name.startsWith(STATIC_PREFIX))
			return TestStaticAttribute(name);

		return TestRawAttribute(entity, name);
	}

	private boolean TestRawAttribute(OctoEntity entity, String name)
		throws ExceptionModelFail
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

	public void DeleteAttribute(OctoEntity entity, String name)
		throws ExceptionModelFail
	{
		DeleteRawAttribute(entity, name);
	}

	private void DeleteRawAttribute(OctoEntity entity, String name)
		throws ExceptionModelFail
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

	public OctoAttribute GetAttribute(OctoEntity entity, String name)
		throws ExceptionModelFail
	{
		if(name.startsWith(STATIC_PREFIX))
			return GetStaticAttribute(name);

		long change_time = 0;

		Object value = GetRawAttribute(entity, name);

		if(!IsSpecialName(name))
			change_time = (long)GetRawAttribute(entity, TIME_PREFIX + name);

		return new OctoAttribute(name, value, change_time, entity);
	}

	public AttributeList GetAttributes(OctoEntity entity)
		throws ExceptionModelFail
	{
		List<Object[]> pairs = GetRawAttributes(entity);

		AttributeList list = new AttributeList();

		for(Object[] pair : pairs)
		{
			String name = (String)pair[0];

			if(!IsSpecialName(name))
			{
				long change_time = (long)GetRawAttribute(entity, TIME_PREFIX + name);
				list.add(new OctoAttribute(name, pair[1], change_time, entity));
			}
		}

		return list;
	}

	public LinkList GetInLinks(OctoObject object)
		throws ExceptionModelFail
	{
		LinkList list = new LinkList();

		for(Uid uid : graph.GetInLinks(object.GetUID()))
			list.add(new OctoLink(this, uid));

		return list;
	}

	private Object GetRawAttribute(OctoEntity entity, String name)
		throws ExceptionModelFail
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

	private List<Object[]> GetRawAttributes(OctoEntity entity)
		throws ExceptionModelFail
	{
		Uid uid = entity.GetUID();

		List<Object[]> pairs;

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

	public boolean IsSpecialName(String name)
	{
		return name.startsWith(TIME_PREFIX)
			|| name.startsWith(STATIC_PREFIX)

			|| name.startsWith(REACTION_EXECUTED)
			|| name.startsWith(INVALID_ATTRIBUTE)

			|| name.startsWith(RULE_PREFIX)
			|| name.startsWith(REACTION_PREFIX)
			|| name.startsWith(MARKER_PREFIX);
	}

	public AttributeList GetSpecialAttributes(OctoEntity entity)
		throws ExceptionModelFail
	{
		List<Object[]> pairs = GetRawAttributes(entity);

		AttributeList list = new AttributeList();

		for(Object[] pair : pairs)
		{
			String name = (String)pair[0];

			if(IsSpecialName(name) && !IsArrayName(name))
			{
				list.add(new OctoAttribute(name, pair[1], 0, entity));
			}
		}

		return list;
	}

// ---------------------------------
//		  INVALIDATION
//---------------------------------

	public boolean IsValid(OctoEntity entity, String name)
		throws ExceptionModelFail
	{
		return !TestAttribute(entity, INVALID_ATTRIBUTE + name);
	}

	public void SetValid(OctoEntity entity, String name, boolean value)
		throws ExceptionModelFail, ExceptionDBError
	{
		if(value)
		{
			if(TestAttribute(entity, INVALID_ATTRIBUTE + name))
				DeleteAttribute(entity, INVALID_ATTRIBUTE + name);
		}
		else
			SetRawAttribute(entity, INVALID_ATTRIBUTE + name, "invalid");
	}

// ---------------------------------
//			STATIC
//---------------------------------

	public void DeclareStaticAttribute(String name, Object value)
		throws ExceptionModelFail, ExceptionDBError
	{
		if(!name.startsWith(STATIC_PREFIX))
			throw new ExceptionModelFail
				("static attribute must starts with prefix: " + STATIC_PREFIX);

		SetRawAttribute(static_obj, name, value);
	}

	public boolean TestStaticAttribute(String name)
		throws ExceptionModelFail
	{
		if(!name.startsWith(STATIC_PREFIX))
			throw new ExceptionModelFail
				("static attribute must starts with prefix: " + STATIC_PREFIX);

		return TestRawAttribute(static_obj, name);
	}

	public boolean IsStaticName(String name)
		throws ExceptionModelFail
	{
		return name.startsWith(STATIC_PREFIX);
	}

	public OctoAttribute GetStaticAttribute(String name)
		throws ExceptionModelFail, ExceptionDBError
	{
		if(!name.startsWith(STATIC_PREFIX))
			throw new ExceptionModelFail
				("static attribute must starts with prefix: " + STATIC_PREFIX);

		return new OctoAttribute(name, GetRawAttribute(static_obj, name), 0, static_obj);
	}

	private void InitStatic()
		throws ExceptionModelFail, ExceptionDBError
	{
		if(static_obj == null)
		{
			EnableObjectIndex("type");
			ObjectList objects = GetObjects("type", STATIC_PREFIX);

			if(objects.size() == 0)
			{
				static_obj = AddObject();

				SetAttribute(static_obj, "type", STATIC_PREFIX, 0);

				SetAttribute(static_obj, "AID", NextAID(), 0);
			}
			else
				static_obj = objects.Only();
		}
	}

// ---------------------------------
//			CACHE
//---------------------------------

	private LinkList LinksFromUid(List<Uid> uids)
	{
		List<OctoLink> list = new LinkedList<OctoLink>();

		for(Uid uid : uids)
			list.add(new OctoLink(this, uid));

		return new LinkList(list);
	}

	private ObjectList ObjectsFromUid(List<Uid> uids)
	{
		List<OctoObject> list = new LinkedList<OctoObject>();

		for(Uid uid : uids)
			list.add(new OctoObject(this, uid));

		return new ObjectList(list);
	}

	public void EnableLinkIndex(String name)
	{
		graph.GetIndex().EnableLinkIndex(name);
	}

	public void EnableObjectIndex(String name)
	{
		graph.GetIndex().EnableObjectIndex(name);
	}

	public LinkList GetAllLinks()
	{
		return LinksFromUid(graph.GetAllLinks());
	}

	public ObjectList GetAllObjects()
	{
		return ObjectsFromUid(graph.GetAllObjects());
	}

	public OctoLink GetLink(OctoAttribute att)
		throws ExceptionModelFail
	{
		return GetLink(att.GetName(), att.GetValue());
	}

	public OctoLink GetLink(String name, Object value)
		throws ExceptionModelFail
	{
		Uid uid = graph.GetIndex().GetLink(name, value);
		return new OctoLink(this, uid);
	}

	public LinkList GetLinks(SimpleAttribute att)
	{
		return GetLinks(att.GetName(), att.GetValue());
	}

	public LinkList GetLinks(String name)
	{
		return LinksFromUid(graph.GetIndex().GetLinks(name));
	}

	public LinkList GetLinks(String name, Object value)
	{
		return LinksFromUid(graph.GetIndex().GetLinks(name, value));
	}

	public OctoObject GetObject(SimpleAttribute att)
		throws ExceptionModelFail
	{
		return GetObject(att.GetName(), att.GetValue());
	}

	public OctoObject GetObject(String name, Object value)
		throws ExceptionModelFail
	{
		Uid uid = graph.GetIndex().GetObject(name, value);
		return new OctoObject(this, uid);
	}

	public ObjectList GetObjects(SimpleAttribute att)
	{
		return GetObjects(att.GetName(), att.GetValue());
	}

	public ObjectList GetObjects(String name)
	{
		return ObjectsFromUid(graph.GetIndex().GetObjects(name));
	}

	public ObjectList GetObjects(String name, Object value)
	{
		return ObjectsFromUid(graph.GetIndex().GetObjects(name, value));
	}

	public LinkList GetOutLink(OctoObject object)
		throws ExceptionModelFail
	{
		LinkList list = new LinkList();

		for(Uid uid : graph.GetOutLinks(object.GetUID()))
			list.add(new OctoLink(this, uid));

		return list;
	}

	public LinkList QueryLinks(String name, String value)
	{
		return LinksFromUid(graph.GetIndex().QueryLinks(name, value));
	}

	public ObjectList QueryObjects(String name, String value)
	{
		return ObjectsFromUid(graph.GetIndex().QueryObjects(name, value));
	}

	public OctoAttribute SetAttribute(OctoEntity entity, String name
		, Object value, long change_time)
			throws ExceptionModelFail, ExceptionDBError
	{
		if(IsSpecialName(name))
			throw new ExceptionModelFail("this name is reserved for service puprose: " + name);

		SetRawAttribute(entity, name, value);
		SetRawAttribute(entity, TIME_PREFIX + name, change_time);

		return new OctoAttribute(name, value, change_time, entity);
	}

	private void SetRawAttribute(OctoEntity entity, String name, Object value)
		throws ExceptionModelFail, ExceptionDBError
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

	public OctoObject GetLinkTarget(OctoLink link)
		throws ExceptionModelFail
	{
		return new OctoObject(this, graph.GetLinkTarget(link.GetUID()));
	}

	public OctoObject GetLinkSource(OctoLink link)
		throws ExceptionModelFail
	{
		return new OctoObject(this, graph.GetLinkSource(link.GetUID()));
	}

// ---------------------------------
//				REACTIONS
// ---------------------------------

	public int IsReactionExecuted(OctoEntity entity, long key)
		throws ExceptionModelFail
	{
		return (int)GetRawAttribute(entity, REACTION_EXECUTED + key);
	}

	public void SetReactionExecuted(OctoEntity entity, long id, int res)
		throws ExceptionModelFail, ExceptionDBError
	{
		SetRawAttribute(entity, REACTION_EXECUTED + id, res);
	}

// ---------------------------------
//			ARRAYS
// ---------------------------------

	private boolean IsArrayName(String name)
	{
		return name.startsWith(RULE_PREFIX)
			|| name.startsWith(REACTION_PREFIX)
			|| name.startsWith(MARKER_PREFIX);
	}

	public void SetArray(OctoEntity entity, String prefix, List<Long> list)
		throws ExceptionModelFail, ExceptionDBError
	{
		Long[] array = Arrays.copyOf(list.toArray(), list.size(), Long[].class);

		SetRawAttribute(entity, prefix, ArrayUtils.toPrimitive(array));
	}

	public List<Long> GetArray(OctoEntity entity, String prefix)
		throws ExceptionModelFail, ExceptionDBError
	{
		if(!TestAttribute(entity, prefix))
			return new LinkedList<Long>();

		Object value = GetRawAttribute(entity, prefix);
		long[] ints = (long[])value;

		return new LinkedList<Long>(Arrays.asList(ArrayUtils.toObject(ints)));
	}

	public void AddToArray(OctoEntity entity, String prefix, Long value)
		throws ExceptionModelFail, ExceptionDBError
	{
		List<Long> list = GetArray(entity, prefix);
		list.add(value);
		SetArray(entity, prefix, list);
	}

	public void CleanArray(OctoEntity entity, String prefix)
		throws ExceptionModelFail, ExceptionDBError
	{
		DeleteAttribute(entity, prefix);
	}

	public void Clean()
	{
		for(OctoObject obj : GetAllObjects())
		{
			if(obj.GetUID().getUid() != static_obj.GetUID().getUid())
				obj.Delete();
		}
	}

// ---------------------------------
//			 Utility
// ---------------------------------

	public String ExportDot()
	{
		return graph.ExportDot(GetAllObjects());
	}

	public String ExportDot(ObjectList list)
	{
		return graph.ExportDot(list);
	}
}
