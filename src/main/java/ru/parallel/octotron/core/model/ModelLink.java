package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.ILink;
import ru.parallel.octotron.core.graph.IObject;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.rule.OctoRule;

import java.util.List;

public class ModelLink extends ModelEntity implements ILink
{
	public ModelLink(GraphLink link)
	{
		super(link);
	}


	@Override
	public void DeclareAttributes(List<SimpleAttribute> attributes)
	{

	}

	@Override
	public void AddRules(List<OctoRule> rules)
	{

	}

	@Override
	public void AddReactions(List<OctoReaction> reactions)
	{

	}

	@Override
	public ModelObject Target()
	{
		return null;
	}

	@Override
	public ModelObject Source()
	{
		return null;
	}
}
