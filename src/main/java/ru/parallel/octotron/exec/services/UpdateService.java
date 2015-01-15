package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.exec.Context;

import java.util.Collection;

/**
 * it processes sensors import, varyings modification and, reactions processing
 * the only service, that modifies the model
 * */
public class UpdateService extends BGService
{
	private final ReactionService reaction_service;
	private final PersistenceService persistence_service;

	public UpdateService(String prefix, Context context, ReactionService reaction_service, PersistenceService persistence_service)
	{
		super(context, new BGExecutorService(prefix, DEFAULT_QUEUE_LIMIT));

		this.reaction_service = reaction_service;
		this.persistence_service = persistence_service;
	}

	public void Update(SensorAttribute sensor, boolean check_reactions)
	{
		executor.execute(new Updater(sensor, check_reactions));
	}

	public void ImmediateUpdate(SensorAttribute sensor, boolean check_reactions)
	{
		new ImmediateUpdater(sensor, check_reactions).run();
	}

	class Updater implements Runnable
	{
		protected final SensorAttribute sensor;
		protected final boolean check_reactions;

		public Updater(SensorAttribute sensor
			, boolean check_reactions)
		{
			this.sensor = sensor;
			this.check_reactions = check_reactions;
		}

		protected Collection<VarAttribute> GetDependFromList(Collection<? extends IModelAttribute> attributes)
		{
			Collection<VarAttribute> result = new AttributeList<>();

			for(IModelAttribute attribute : attributes)
			{
				result.addAll(attribute.GetDependOnMe());
			}

			return result;
		}

		protected Collection<IModelAttribute> ProcessVars(SensorAttribute changed)
		{
			Collection<IModelAttribute> result = new AttributeList<>();

			Collection<VarAttribute> depend_from_changed = changed.GetDependOnMe();

			do
			{
				Collection<VarAttribute> new_changed = new AttributeList<>();

				for(VarAttribute var : depend_from_changed)
				{
					if(!var.AreDepsDefined())
						continue;

					var.Update();
					new_changed.add(var);
				}

				result.addAll(new_changed);
				depend_from_changed = GetDependFromList(new_changed);
			}
			while(depend_from_changed.size() != 0);

			return result;
		}

		@Override
		public void run()
		{
			Collection<IModelAttribute> result = ProcessVars(sensor);

			result.add(sensor);

			if(check_reactions)
			{

				reaction_service.CheckReactions(result);
				reaction_service.CheckReaction(sensor.GetTimeoutReaction());
			}

			persistence_service.UpdateAttributes(result);
		}
	}

	/**
	 * this one is not very immediate - db and scripts are still called in background
	 * */
	public class ImmediateUpdater extends Updater
	{
		public ImmediateUpdater(SensorAttribute sensor, boolean check_reactions)
		{
			super(sensor, check_reactions);
		}

		@Override
		public void run()
		{
			Collection<IModelAttribute> result = ProcessVars(sensor);

			result.add(sensor);

			if(check_reactions)
			{

				reaction_service.CheckReactions(result);
				reaction_service.CheckReaction(sensor.GetTimeoutReaction());
			}

			persistence_service.UpdateAttributes(result);
		}
	}
}
