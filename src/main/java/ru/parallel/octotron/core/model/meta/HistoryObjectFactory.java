package ru.parallel.octotron.core.model.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.EObjectLabels;

public class HistoryObjectFactory extends MetaObjectFactory<HistoryObject, HistoryObject.OldPair>
{
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
