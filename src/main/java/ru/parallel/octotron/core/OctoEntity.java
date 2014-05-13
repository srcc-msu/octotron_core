/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.core;

import java.util.List;

import main.java.ru.parallel.octotron.primitive.SimpleAttribute;
import main.java.ru.parallel.octotron.primitive.Uid;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionDBError;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.octotron.utils.AttributeList;
import main.java.ru.parallel.utils.JavaUtils;

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
		throws ExceptionModelFail, ExceptionDBError
	{
		return GetAttribute(att.GetName()).Update(att.GetValue());
	}

	public OctoAttribute GetAttribute(String name)
		throws ExceptionModelFail
	{
		return graph_service.GetAttribute(this, name);
	}

	public AttributeList GetAttributes()
		throws ExceptionModelFail
	{
		return graph_service.GetAttributes(this);
	}

	public OctoAttribute SetAttribute(String name, Object value)
		throws ExceptionModelFail, ExceptionDBError
	{
		return graph_service.SetAttribute(this, name, value, JavaUtils.GetTimestamp());
	}

	public void SetAttribute(SimpleAttribute att)
		throws ExceptionModelFail, ExceptionDBError
	{
		SetAttribute(att.GetName(), att.GetValue());
	}

	public void SetAttributes(List<SimpleAttribute> attributes)
		throws ExceptionModelFail, ExceptionDBError
	{
		for(SimpleAttribute att : attributes)
			SetAttribute(att);
	}

	public void DeclareAttribute(String name, Object value)
		throws ExceptionModelFail, ExceptionDBError
	{
		graph_service.SetAttribute(this, name, value, 0);
	}

	public void DeclareAttribute(SimpleAttribute att)
		throws ExceptionModelFail, ExceptionDBError
	{
		DeclareAttribute(att.GetName(), att.GetValue());
	}

	public void DeclareAttributes(List<SimpleAttribute> attributes)
		throws ExceptionModelFail, ExceptionDBError
	{
		for(SimpleAttribute att : attributes)
		{
			if(graph_service.IsStaticName(att.GetName()))
				graph_service.DeclareStaticAttribute(att.GetName(), att.GetValue());
			else
				DeclareAttribute(att);
		}
	}

	public AttributeList GetSpecialAttributes()
		throws ExceptionModelFail
	{
		return graph_service.GetSpecialAttributes(this);
	}

	public GraphService GetGraph()
	{
		return graph_service;
	}

	public void Delete()
		throws ExceptionModelFail
	{
		graph_service.Delete(this);
	}

	public void RemoveAttribute(String name)
		throws ExceptionModelFail
	{
		graph_service.DeleteAttribute(this, name);
	}

	public boolean TestAttribute(String name)
		throws ExceptionModelFail
	{
		return graph_service.TestAttribute(this, name);
	}
}
