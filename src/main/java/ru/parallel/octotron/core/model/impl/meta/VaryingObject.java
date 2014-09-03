package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.OctoRule;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.impl.PersistentStorage;

public class VaryingObject extends AttributeObject
{
	public VaryingObject(GraphObject object)
	{
		super(object);
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
		Init(GetBaseObject(), rule.GetName(), rule.GetDefaultValue());
		GetBaseObject().DeclareAttribute(rid_const, rule.GetID());
	}
}
