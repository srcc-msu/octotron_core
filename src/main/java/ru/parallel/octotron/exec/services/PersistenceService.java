package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.persistence.IPersistenceManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PersistenceService
{
	private final ConcurrentLinkedQueue<Collection<? extends IModelAttribute>> to_update
		= new ConcurrentLinkedQueue<>();

	final IPersistenceManager persistence_manager;

	public PersistenceService(IPersistenceManager persistence_manager)
	{
		this.persistence_manager = persistence_manager;
	}

	public boolean Update()
	{
		Collection<? extends IModelAttribute> list = to_update.poll();

		if(list == null)
			return false;

		persistence_manager.RegisterUpdate(list);

		return true;
	}

	public void RegisterReaction(Reaction reaction)
	{
		persistence_manager.RegisterReaction(reaction);
	}

	public void RegisterConst(ConstAttribute attribute)
	{
		persistence_manager.RegisterConst(attribute);
	}

	public void RegisterSensor(SensorAttribute attribute)
	{
		persistence_manager.RegisterSensor(attribute);

	}

	public void RegisterVar(VarAttribute attribute)
	{
		persistence_manager.RegisterVar(attribute);
	}

	public void Finish()
	{
		persistence_manager.Finish();
	}

	public void Wipe()
	{
		persistence_manager.Wipe();
	}
}
