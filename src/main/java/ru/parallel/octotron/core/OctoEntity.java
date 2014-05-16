/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.core;

import java.util.List;

import main.java.ru.parallel.octotron.primitive.SimpleAttribute;
import main.java.ru.parallel.octotron.primitive.Uid;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.octotron.utils.AttributeList;

/**
 * some entity, that resides in model<br>
 * implements {@link OctoEntity} interface<br>
 * all operations with it go through the \graph interface, no caching<br>
 * */
public abstract class OctoEntity
{
/**
 * graph that stores this entity<br>
 * */
	protected GraphService graph_service;

/**
 * unique identifier of the entity<br>
 * needed to access it from the \graph<br>
 * */
	private Uid uid;

	public OctoEntity(GraphService graph_service)
	{
		this(graph_service, null);
	}

/**
 * this constructor MUST not be used for creating new items -<br>
 * it is needed to obtain the existing from the \graph<br>
 * */
	public OctoEntity(GraphService graph_service, Uid uid)
	{
		this.graph_service = graph_service;
		this.uid = uid;
	}

	public final Uid GetUID()
	{
		return uid;
	}

	public boolean UpdateAttribute(SimpleAttribute att)
	{
		return UpdateAttribute(att.GetName(), att.GetValue());
	}

	public boolean UpdateAttribute(String name, Object value)
	{
		return GetAttribute(name).Update(value);
	}

	public OctoAttribute GetAttribute(String name)
	{
		return graph_service.GetAttribute(this, name);
	}

	public AttributeList GetAttributes()
	{
		return graph_service.GetAttributes(this);
	}

	public OctoAttribute DeclareAttribute(String name, Object value)
	{
		if(!graph_service.IsStaticName(name) && TestAttribute(name))
			throw new ExceptionModelFail("attribute " + name + " already declared");

		return graph_service.SetAttribute(this, name, SimpleAttribute.ConformType(value));
	}

	public OctoAttribute DeclareAttribute(SimpleAttribute att)
	{
		return DeclareAttribute(att.GetName(), att.GetValue());
	}

	public void DeclareAttributes(List<SimpleAttribute> attributes)
	{
		for(SimpleAttribute att : attributes)
		{
			DeclareAttribute(att);
		}
	}

	public void Delete()
	{
		graph_service.Delete(this);
	}

	public void RemoveAttribute(String name)
	{
		graph_service.DeleteAttribute(this, name);
	}

	public boolean TestAttribute(String name)
	{
		return graph_service.TestAttribute(this, name);
	}
}
