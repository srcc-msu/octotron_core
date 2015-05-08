package ru.parallel.octotron.triggers;

import ru.parallel.octotron.core.logic.TriggerCondition;
import ru.parallel.octotron.core.model.ModelEntity;

public class Manual extends TriggerCondition
{
	public Manual()
	{
	}

	@Override
	public boolean Check(ModelEntity entity)
	{
		return false;
	}
}
