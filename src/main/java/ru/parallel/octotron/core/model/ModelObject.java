package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.IObject;
import ru.parallel.octotron.core.graph.collections.LinkList;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.rule.OctoRule;

import java.util.List;

public class ModelObject extends ModelEntity implements IObject
{
	public ModelObject(GraphObject object)
	{
		super(object);
	}

	@Override
	public LinkList<ModelObject, ModelLink> GetInLinks()
	{
		return null;
	}

	@Override
	public LinkList<ModelObject, ModelLink> GetOutLinks()
	{
		return null;
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetInNeighbors()
	{
		return null;
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetOutNeighbors()
	{
		return null;
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetInNeighbors(String link_name, Object link_value)
	{
		return null;
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetOutNeighbors(String link_name, Object link_value)
	{
		return null;
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetInNeighbors(String link_name)
	{
		return null;
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetOutNeighbors(String link_name)
	{
		return null;
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetInNeighbors(SimpleAttribute link_attribute)
	{
		return null;
	}

	@Override
	public ObjectList<ModelObject, ModelLink> GetOutNeighbors(SimpleAttribute link_attribute)
	{
		return null;
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
}
