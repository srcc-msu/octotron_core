package ru.parallel.octotron.core.graph;

import ru.parallel.octotron.core.primitive.SimpleAttribute;

public interface IObject<T extends IAttribute> extends IEntity<T>
{
	Object GetInLinks();
	Object GetOutLinks();

	Object GetInNeighbors();
	Object GetOutNeighbors();

	Object GetInNeighbors(String link_name, Object link_value);
	Object GetOutNeighbors(String link_name, Object link_value);

	Object GetInNeighbors(String link_name);
	Object GetOutNeighbors(String link_name);

	Object GetInNeighbors(SimpleAttribute link_attribute);
	Object GetOutNeighbors(SimpleAttribute link_attribute);
}
