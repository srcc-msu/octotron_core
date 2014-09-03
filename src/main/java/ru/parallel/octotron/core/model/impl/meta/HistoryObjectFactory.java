package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.primitive.EObjectLabels;

public class HistoryObjectFactory extends MetaObjectFactory<HistoryObject, HistoryObject.OldPair>
{
	private HistoryObjectFactory() { super(); }

	public static final HistoryObjectFactory INSTANCE = new HistoryObjectFactory();

	@Override
	protected HistoryObject CreateInstance(GraphObject meta_object)
	{
		return new HistoryObject(meta_object);
	}

	@Override
	protected String GetLabel()
	{
		return EObjectLabels.HISTORY.toString();
	}
}
