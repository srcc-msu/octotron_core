package ru.parallel.octotron.core.graph;

public interface IAttribute
{
	String GetName();
	Object GetValue();

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
}
