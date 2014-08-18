/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.impl.attribute;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.neo4j.impl.Marker;

import java.util.LinkedList;
import java.util.List;

public class ConstantAttribute extends ModelAttribute
{
	public ConstantAttribute(ModelEntity parent, String name)
	{
		super(parent, name);
	}

	@Override
	public void AddDependant(VaryingAttribute attribute) {}

	@Override
	public AttributeList<VaryingAttribute> GetDependant()
	{
		return new AttributeList<>();
	}

	@Override
	public EAttributeType GetType()
	{
		return EAttributeType.CONSTANT;
	}

	public void SetValid()
	{
		throw new ExceptionModelFail("constant object");
	}
	public void SetInvalid()
	{
		throw new ExceptionModelFail("constant object");
	}

	public void AddReaction(OctoReaction reaction)
	{
		throw new ExceptionModelFail("can not add reaction to constant attribute");
	}

	public List<OctoReaction> GetReactions()
	{
		return new LinkedList<>();
	}

	public List<OctoResponse> GetReadyReactions()
	{
		return new LinkedList<>();
	}

	public List<OctoResponse> GetExecutedReactions()
	{
		return new LinkedList<>();
	}

	public List<Marker> GetMarkers()
	{
		return new LinkedList<>();
	}

	@Override
	public long AddMarker(OctoReaction reaction, String description, boolean suppress)
	{
		throw new ExceptionModelFail("constant object");
	}

	@Override
	public void DeleteMarker(long id)
	{
		throw new ExceptionModelFail("constant object");
	}
}
