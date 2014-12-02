/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.IPresentable;
import ru.parallel.octotron.core.primitive.IUniqueID;

public interface IAttribute extends IUniqueID<EAttributeType>, IPresentable
{
	String GetName();
	Value GetValue();

	ModelEntity GetParent();

	String GetString();
	Long GetLong();
	Double GetDouble();
	Boolean GetBoolean();
	Double ToDouble();

	boolean eq(Value new_value);
	boolean aeq(Value new_value, Value aprx);
	boolean ne(Value new_value);
	boolean gt(Value new_value);
	boolean lt(Value new_value);
	boolean ge(Value val);
	boolean le(Value val);

	public String GetStringValue();
}
