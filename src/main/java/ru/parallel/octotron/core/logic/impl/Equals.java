package ru.parallel.octotron.core.logic.impl;

import ru.parallel.octotron.core.logic.ReactionTemplate;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelObject;

public class Equals extends ReactionTemplate
{
	private static final long serialVersionUID = 8157833787643278811L;

	public Equals(String check_name, Object check_value)
	{
		super(check_name, check_value);
	}

	@Override
	public boolean ReactionNeeded(ModelObject object)
	{
		IModelAttribute attr = object.GetAttribute(GetCheckName());

		if(!attr.IsValid())
			return false;

		return attr.eq(GetCheckValue());
	}
}
