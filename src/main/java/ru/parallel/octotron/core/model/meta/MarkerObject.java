package ru.parallel.octotron.core.model.meta;

import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.neo4j.impl.Marker;

public class MarkerObject extends MetaObject
{
	private static final String mark_id_const = "_mark_id";
	private static final String mark_r_id_const = "_mark_r_id";
	private static final String mark_descr_const = "_mark_descr";
	private static final String mark_suppress_const = "_mark_suppress";

	public MarkerObject(GraphService graph_service, GraphEntity base)
	{
		super(graph_service, base);
	}

	@Override
	public void Init(Object object)
	{
		Marker marker = (Marker) object;

		GetBaseObject().DeclareAttribute(mark_id_const, marker.GetID());
		GetBaseObject().DeclareAttribute(mark_r_id_const, marker.GetTarget());
		GetBaseObject().DeclareAttribute(mark_descr_const, marker.GetDescription());
		GetBaseObject().DeclareAttribute(mark_suppress_const, marker.IsSuppress());
	}

	public Marker GetMarker()
	{
		long id = GetBaseObject().GetAttribute(mark_id_const).GetLong();
		long rid = GetBaseObject().GetAttribute(mark_r_id_const).GetLong();
		String descr = GetBaseObject().GetAttribute(mark_descr_const).GetString();
		boolean suppress = GetBaseObject().GetAttribute(mark_suppress_const).GetBoolean();

		return new Marker(id, rid, descr, suppress);
	}
}
