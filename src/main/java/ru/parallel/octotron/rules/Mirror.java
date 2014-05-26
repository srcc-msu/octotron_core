package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.core.OctoObjectRule;
import ru.parallel.octotron.primitive.EDependencyType;
import ru.parallel.octotron.primitive.SimpleAttribute;

public abstract class Mirror extends OctoObjectRule
{
	private String mirror_attribute;
	private SimpleAttribute mirror_parent;

	public Mirror(String mirror_attribute, SimpleAttribute mirror_parent)
	{
		super(mirror_attribute);
		this.mirror_attribute = mirror_attribute;
		this.mirror_parent = mirror_parent;
	}

	@Override
	public final Object Compute(OctoObject object)
	{
		return object.GetInNeighbors().append(object.GetOutNeighbors())
			.Filter(mirror_parent).Only().GetAttribute(mirror_attribute).GetValue();
	}

	@Override
	public final EDependencyType GetDeps()
	{
		return EDependencyType.ALL;
	}
}
