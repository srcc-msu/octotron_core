package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.ModelService;

public class AbstractModAttributeBuilder<T extends AbstractModAttribute> implements IAttributeBuilder
{
	protected final T attribute;
	protected final ModelService service;

	private AbstractModAttributeBuilder()
	{
		this.attribute = null;
		this.service = null;
	};

	AbstractModAttributeBuilder(ModelService service, T attribute)
	{
		this.service = service;
		this.attribute = attribute;
	}

	@Override
	public void AddReaction(Reaction reaction)
	{
		attribute.reactions.put(reaction.GetID(), reaction);
	}

	@Override
	public void AddDependant(VarAttribute dependant)
	{
		attribute.dependants.add(dependant);
	}
}
