package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.ReactionTemplate;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

public class ConstAttributeBuilder implements IAttributeBuilder
{
	private final ModelService service;

	ConstAttributeBuilder(ModelService service)
	{
		this.service = service;
	}

	@Override
	public void AddReaction(ReactionTemplate reaction_template)
	{
		throw new ExceptionModelFail(ConstAttribute.err_msg + "AddReaction");
	}

	@Override
	public void AddDependant(VarAttribute attribute)
	{
		// nothing to see here
		// throw new ExceptionModelFail(err_msg + "AddDependant");
	}
}
