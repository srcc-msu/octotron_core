package ru.parallel.octotron.core.logic.impl;

import ru.parallel.octotron.core.logic.ReactionTemplate;
import ru.parallel.octotron.core.model.IModelAttribute;

public class NotEquals extends ReactionTemplate
{
	private static final long serialVersionUID = 1456789487657834621L;

	public NotEquals(String check_name, Object check_value)
	{
		super(check_name, check_value);
	}

	@Override
	public boolean ReactionNeeded(IModelAttribute attribute)
	{
		if(!attribute.CheckValid())
			return false;

		return attribute.ne(GetCheckValue());
	}
}
