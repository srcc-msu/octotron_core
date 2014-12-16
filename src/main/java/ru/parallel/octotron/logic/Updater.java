package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.exec.services.ReactionService;

import java.util.Collection;

public class Updater implements Runnable
{
	private final ReactionService reaction_service;
	private final SensorAttribute sensor;
	private final boolean check_reactions;

	public Updater(ReactionService reaction_service, SensorAttribute sensor
		, boolean check_reactions)
	{
		this.reaction_service = reaction_service;
		this.sensor = sensor;
		this.check_reactions = check_reactions;
	}

	private static Collection<VarAttribute> GetDependFromList(Collection<? extends IModelAttribute> attributes)
	{
		Collection<VarAttribute> result = new AttributeList<>();

		for(IModelAttribute attribute : attributes)
		{
			result.addAll(attribute.GetDependOnMe());
		}

		return result;
	}

	private static Collection<IModelAttribute> ProcessVars(SensorAttribute changed)
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

		if(check_reactions)
		{
			result.add(sensor);

			reaction_service.CheckReactions(result);
			reaction_service.CheckReaction(sensor.GetTimeoutReaction());
		}
	}
}
