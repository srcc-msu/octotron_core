/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.attributes.IAttributeBuilder;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.IPresentable;
import ru.parallel.octotron.core.primitive.IUniqueID;

public interface IAttribute extends IUniqueID<EAttributeType>, IPresentable
{
	String GetName();
	Object GetValue();

	ModelEntity GetParent();

	String GetString();
	Long GetLong();
	Double GetDouble();
	Boolean GetBoolean();
	Double ToDouble();

	boolean eq(Object new_value);
	boolean aeq(Object new_value, Object aprx);
	boolean ne(Object new_value);
	boolean gt(Object new_value);
	boolean lt(Object new_value);
	boolean ge(Object val);
	boolean le(Object val);

	public String GetStringValue();
}
