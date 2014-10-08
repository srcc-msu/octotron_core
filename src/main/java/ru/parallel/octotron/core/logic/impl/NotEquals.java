package ru.parallel.octotron.core.logic.impl;

import ru.parallel.octotron.core.logic.ReactionTemplate;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

public class NotEquals extends ReactionTemplate
{
	private static final long serialVersionUID = 1456789487657834621L;

	public NotEquals(String check_name, Object check_value)
	{
		super(check_name, check_value);
	}

	@Override
	public boolean ReactionNeeded(ModelEntity object)
	{
		IModelAttribute attr = object.GetAttribute(GetCheckName());

		if(!attr.IsValid())
			return false;

		return attr.ne(GetCheckValue());
	}
}
