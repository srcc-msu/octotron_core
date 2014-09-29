package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.storage.PersistentStorage;

public class VaryingObject extends AttributeObject
{
	public VaryingObject(GraphObject object)
	{
		super(object);
	}

	private static final String rid_const = "_rid";

	public Rule GetRule()
	{
		return PersistentStorage.INSTANCE.GetRules().Get(GetAttribute(rid_const).GetLong());
	}

	@Override
	public void Init(Object object)
	{
		Rule rule = (Rule) object;
		Init(GetBaseObject());
		GetBaseObject().DeclareAttribute(rid_const, rule.GetID());
	}
}
