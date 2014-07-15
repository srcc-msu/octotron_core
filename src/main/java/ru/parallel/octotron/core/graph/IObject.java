package ru.parallel.octotron.core.graph;

import ru.parallel.octotron.core.graph.collections.LinkList;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public interface IObject
{
	LinkList GetInLinks();
	LinkList GetOutLinks();

	ObjectList GetInNeighbors();
	ObjectList GetOutNeighbors();

	ObjectList GetInNeighbors(String link_name, Object link_value);
	ObjectList GetOutNeighbors(String link_name, Object link_value);

	ObjectList GetInNeighbors(String link_name);
	ObjectList GetOutNeighbors(String link_name);

	ObjectList GetInNeighbors(SimpleAttribute link_attribute);
	ObjectList GetOutNeighbors(SimpleAttribute link_attribute);
}
