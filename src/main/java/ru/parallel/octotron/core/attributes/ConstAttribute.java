/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ConstAttribute extends AbstractAttribute implements IModelAttribute
{
	static final String err_msg = "unsupported operation on const attribute: ";

	@Override
	public ConstAttributeBuilder GetBuilder(ModelService service)
	{
		service.CheckModification();

		return new ConstAttributeBuilder(service, this);
	}

	public ConstAttribute(ModelEntity parent, String name, Object value)
	{
		super(EAttributeType.CONST, parent, name, value);
	}

	@Override
	public Reaction GetReaction(long id)
	{
		throw new ExceptionModelFail(err_msg + "GetReaction");
	}

	@Override
	public boolean CheckValid()
	{
		return true;
	}

	@Override
	public void SetValid() { throw new ExceptionModelFail(err_msg + "SetValid"); }
	@Override
	public void SetInvalid() { throw new ExceptionModelFail(err_msg + "SetInvalid"); }

	@Override
	public AttributeList<VarAttribute> GetDependant()
	{
		return new AttributeList<>();
	}

	@Override
	public double GetSpeed()
	{
		return 0.0;
	}

	@Override
	public Collection<Reaction> GetReactions()
	{
		return new LinkedList<>();
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = new HashMap<>();

		result.put("AID", GetID());
		result.put("parent", GetParent().GetID());

		result.put("name", GetName());
		result.put("value", GetValue());

		return result;
	}

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = new HashMap<>();

		result.put(GetName(), GetValue());

		return result;
	}
}
