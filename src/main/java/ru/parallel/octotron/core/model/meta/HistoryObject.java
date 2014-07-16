package ru.parallel.octotron.core.model.meta;

import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.UniqueName;

public class HistoryObject extends MetaObject
{
	public static class OldPair implements UniqueName
	{
		public Object value;
		public long ctime;

		public OldPair(Object value, long ctime)
		{
			this.value = value;
			this.ctime = ctime;
		}

		@Override
		public String GetUniqName()
		{
			return "a_uniq_name";
		}
	}

	public HistoryObject(GraphService graph_service, GraphEntity base)
	{
		super(graph_service, base);
	}

	@Override
	public void Init(Object object)
	{
		OldPair pair = (OldPair) object;
		GetBaseObject().DeclareAttribute(AttributeObject.value_const, pair.value);
		GetBaseObject().DeclareAttribute(AttributeObject.ctime_const, pair.ctime);
	}

	public void Set(Object value, long ctime)
	{
		GetBaseObject().UpdateAttribute(AttributeObject.value_const, value);
		GetBaseObject().UpdateAttribute(AttributeObject.ctime_const, ctime);
	}

	public GraphAttribute GetValue()
	{
		return GetBaseObject().GetAttribute(AttributeObject.value_const);
	}

	public long GetCTime()
	{
		return GetBaseObject().GetAttribute(AttributeObject.ctime_const).GetLong();
	}
}
