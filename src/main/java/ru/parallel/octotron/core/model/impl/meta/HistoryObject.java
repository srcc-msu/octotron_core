package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.primitive.UniqueName;

public class HistoryObject extends MetaObject
{
	static final String value_const = "_value";
	static final String ctime_const = "_ctime";

	public static class OldPair implements UniqueName
	{
		public final Object value;
		public final long ctime;

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

	public HistoryObject(GraphEntity base)
	{
		super(base);
	}

	@Override
	public void Init(Object object)
	{
		OldPair pair = (OldPair) object;
		GetBaseObject().DeclareAttribute(value_const, pair.value);
		GetBaseObject().DeclareAttribute(ctime_const, pair.ctime);
	}

	public void Set(Object value, long ctime)
	{
		GetBaseObject().UpdateAttribute(value_const, value);
		GetBaseObject().UpdateAttribute(ctime_const, ctime);
	}

	public GraphAttribute GetValue()
	{
		return GetBaseObject().GetAttribute(value_const);
	}

	public long GetCTime()
	{
		return GetBaseObject().GetAttribute(ctime_const).GetLong();
	}
}
