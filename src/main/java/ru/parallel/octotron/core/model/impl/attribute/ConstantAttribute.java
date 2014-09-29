/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.impl.attribute;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.logic.Marker;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.impl.meta.ReactionObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.LinkedList;
import java.util.List;

public class ConstantAttribute extends ModelAttribute implements IMetaAttribute
{
	public ConstantAttribute(ModelEntity parent, GraphAttribute attribute)
	{
		super(parent, attribute);
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

	@Override
	public Object GetLastValue()
	{
		return GetValue();
	}

	@Override
	public long GetCTime()
	{
		return 0;
	}

	@Override
	public long GetATime()
	{
		return 0;
	}

	@Override
	public double GetSpeed()
	{
		return 0.0;
	}

	@Override
	public boolean IsValid()
	{
		return true;
	}

	public void SetValid()
	{
		throw new ExceptionModelFail("constant object");
	}
	public void SetInvalid()
	{
		throw new ExceptionModelFail("constant object");
	}

	public void AddReaction(Reaction reaction)
	{
		throw new ExceptionModelFail("can not add reaction to constant attribute");
	}

	public List<ReactionObject> GetReactions()
	{
		return new LinkedList<>();
	}

	public List<Response> ProcessReactions()
	{
		return new LinkedList<>();
	}

	public List<Response> GetCurrentReactions()
	{
		return new LinkedList<>();
	}

	public List<Marker> GetMarkers()
	{
		return new LinkedList<>();
	}

	@Override
	public long AddMarker(Reaction reaction, String description, boolean suppress)
	{
		throw new ExceptionModelFail("constant object");
	}

	@Override
	public void DeleteMarker(long id)
	{
		throw new ExceptionModelFail("constant object");
	}
}
