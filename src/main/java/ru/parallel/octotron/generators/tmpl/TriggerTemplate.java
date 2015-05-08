package ru.parallel.octotron.generators.tmpl;

import ru.parallel.octotron.core.logic.TriggerCondition;

public class TriggerTemplate
{
	public String name;
	public TriggerCondition condition;

	public TriggerTemplate(String name, TriggerCondition condition)
	{
		this.name = name;
		this.condition = condition;
	}
}
