package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.attributes.impl.Value;

public interface IValue
{
	boolean IsDefined();
	boolean IsValid();
	boolean IsComputable();

	String ValueToString();

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
}
