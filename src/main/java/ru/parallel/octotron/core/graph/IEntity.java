package ru.parallel.octotron.core.graph;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.Uid;

public interface IEntity<T extends IAttribute>
{
/*	IAttribute DeclareAttribute(String name, Object value);
	IAttribute DeclareAttribute(SimpleAttribute attribute);

	IAttribute UpdateAttribute(String name, Object value);
	IAttribute UpdateAttribute(SimpleAttribute att);

	void DeleteAttribute(String name);
	void Delete();
*/
	boolean TestAttribute(String name);
	boolean TestAttribute(String name, Object value);
	boolean TestAttribute(SimpleAttribute test);

	T GetAttribute(String name);
	AttributeList<T> GetAttributes();

	Uid GetUID();
}
