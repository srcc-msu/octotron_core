package ru.parallel.octotron.core.persistence;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;

public class GhostManager implements IPersistenceManager
{
	@Override
	public void AddObject(ModelObject object)
	{
		// there is nothing here
	}

	@Override
	public void AddLink(ModelLink link)
	{
		// there is nothing here
	}

	@Override
	public void AddReaction(Reaction reaction)
	{
		// there is nothing here
	}

	@Override
	public void RegisterConst(ModelService model_service, ConstAttribute attribute)
	{
		// there is nothing here
	}
	@Override
	public void Finish()
	{
		// there is nothing here
	}
}
