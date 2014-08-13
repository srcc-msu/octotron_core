package ru.parallel.octotron.core.graph;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.Uid;

public interface IEntity
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

	IAttribute GetAttribute(String name);
	AttributeList<? extends IAttribute> GetAttributes();

	Uid GetUID();
}
