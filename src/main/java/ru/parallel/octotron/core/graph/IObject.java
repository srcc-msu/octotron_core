package ru.parallel.octotron.core.graph;

import ru.parallel.octotron.core.collections.LinkList;
import ru.parallel.octotron.core.collections.ObjectList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public interface IObject<OT extends IObject<OT, LT>, LT extends ILink<OT, LT>> extends IEntity
{
	LinkList<OT, LT> GetInLinks();
	LinkList<OT, LT> GetOutLinks();

	ObjectList<OT, LT> GetInNeighbors();
	ObjectList<OT, LT> GetOutNeighbors();

	ObjectList<OT, LT> GetInNeighbors(String link_name, Object link_value);
	ObjectList<OT, LT> GetOutNeighbors(String link_name, Object link_value);

	ObjectList<OT, LT> GetInNeighbors(String link_name);
	ObjectList<OT, LT> GetOutNeighbors(String link_name);

	ObjectList<OT, LT> GetInNeighbors(SimpleAttribute link_attribute);
	ObjectList<OT, LT> GetOutNeighbors(SimpleAttribute link_attribute);
}
