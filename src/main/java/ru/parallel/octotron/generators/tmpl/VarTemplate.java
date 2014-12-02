package ru.parallel.octotron.generators.tmpl;

import ru.parallel.octotron.core.logic.LogicID;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.primitive.ELogicalType;

public class VarTemplate extends LogicID<ELogicalType>
{
	public final String name;
	public final Rule rule;

	public VarTemplate(String name, Rule rule)
	{
		super(ELogicalType.RULE_TEMPLATE);
		this.name = name;
		this.rule = rule;
	}
}
