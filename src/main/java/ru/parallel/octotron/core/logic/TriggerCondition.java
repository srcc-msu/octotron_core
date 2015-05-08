package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.model.ModelEntity;

public abstract class TriggerCondition
{
	public abstract boolean Check(ModelEntity entity);
}
