package ru.parallel.octotron.core.persistence;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;

public class GhostManager implements IPersistenceManager
{
	@Override
	public void RegisterObject(ModelObject object)
	{
		// there is nothing here
	}

	@Override
	public void RegisterLink(ModelLink link)
	{
		// there is nothing here
	}

	@Override
	public void RegisterReaction(Reaction reaction)
	{
		// there is nothing here
	}

	@Override
	public void RegisterConst(ConstAttribute attribute)
	{
		// there is nothing here
	}

	@Override
	public void RegisterSensor(SensorAttribute attribute)
	{
		// there is nothing here
	}

	@Override
	public void RegisterVar(VarAttribute attribute)
	{
		// there is nothing here
	}

	@Override
	public void Finish()
	{
		// there is nothing here
	}
}
