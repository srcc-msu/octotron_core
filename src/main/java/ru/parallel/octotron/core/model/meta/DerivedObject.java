package ru.parallel.octotron.core.model.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.impl.PersistentStorage;

public class DerivedObject extends AttributeObject
{
	public DerivedObject(GraphService graph_service, GraphObject object)
	{
		super(graph_service, object);
	}

	private static final String rid_const = "_rid";

	public OctoRule GetRule()
	{
		return PersistentStorage.INSTANCE.GetRules().Get(GetAttribute(rid_const).GetLong());
	}


	@Override
	public void Init(Object object)
	{
		OctoRule rule = (OctoRule) object;
		super.Init(GetBaseObject(), rule.GetName(), rule.GetDefaultValue());
		GetBaseObject().DeclareAttribute(rid_const, rule.GetID());
	}
}
