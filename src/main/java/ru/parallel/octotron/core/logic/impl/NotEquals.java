package ru.parallel.octotron.core.logic.impl;

import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;

public class NotEquals extends Reaction
{
	private static final long serialVersionUID = 1456789487657834621L;

	public NotEquals(String check_name, Object check_value)
	{
		super(check_name, check_value);
	}

	@Override
	public boolean ReactionNeeded(ModelObject object)
	{
		IMetaAttribute attr = object.GetMetaAttribute(GetCheckName());

		if(!attr.IsValid())
			return false;

		return attr.ne(GetCheckValue());
	}
}
