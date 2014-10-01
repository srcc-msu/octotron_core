package ru.parallel.octotron.core.graph;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.Uid;

public interface IEntity<T extends IAttribute>
{
	boolean TestAttribute(String name);
	boolean TestAttribute(String name, Object value);
	boolean TestAttribute(SimpleAttribute test);

	T GetAttribute(String name);
	AttributeList<T> GetAttributes();

	Uid GetUID();
}
