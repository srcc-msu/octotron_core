package ru.parallel.octotron.core.graph;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.Uid;

public interface IEntity
{
	IAttribute GetAttribute(String name);
	AttributeList GetAttributes();

	IAttribute SetAttribute(String name, Object value);
	IAttribute SetAttribute(SimpleAttribute att);

	void RemoveAttribute(String name);
	boolean TestAttribute(String name);
	boolean TestAttribute(String name, Object value);
	boolean TestAttribute(SimpleAttribute test);

	IAttribute DeclareAttribute(String name, Object value);
	IAttribute DeclareAttribute(SimpleAttribute att);

	Uid GetUID();
	void Delete();
}
