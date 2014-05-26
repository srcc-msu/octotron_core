package ru.parallel.octotron.rules;

import ru.parallel.octotron.primitive.SimpleAttribute;

public class MirrorString extends Mirror
{
	private String mirror_attribute;
	private SimpleAttribute mirror_parent;

	public MirrorString(String mirror_attribute, SimpleAttribute mirror_parent)
	{
		super(mirror_attribute, mirror_parent);
		this.mirror_attribute = mirror_attribute;
		this.mirror_parent = mirror_parent;
	}

	public MirrorString(String mirror_attribute, String mirror_parent_name, Object mirror_parent_value)
	{
		this(mirror_attribute
			, new SimpleAttribute(mirror_parent_name, mirror_parent_value));
	}

	@Override
	public Object GetDefaultValue()
	{
		return "";
	}
}
