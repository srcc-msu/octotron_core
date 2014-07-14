package ru.parallel.octotron.core.graph;

import ru.parallel.octotron.core.graph.collections.LinkList;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public interface IObject extends IEntity
{
	public LinkList GetInLinks();
	public LinkList GetOutLinks();

	public ObjectList GetInNeighbors();
	public ObjectList GetOutNeighbors();

	public ObjectList GetInNeighbors(String link_name, Object link_value);
	public ObjectList GetOutNeighbors(String link_name, Object link_value);

	public ObjectList GetInNeighbors(String link_name);
	public ObjectList GetOutNeighbors(String link_name);

	public ObjectList GetInNeighbors(SimpleAttribute link_attribute);
	public ObjectList GetOutNeighbors(SimpleAttribute link_attribute);
}
