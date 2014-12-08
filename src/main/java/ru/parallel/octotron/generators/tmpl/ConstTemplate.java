package ru.parallel.octotron.generators.tmpl;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.logic.LogicID;
import ru.parallel.octotron.core.primitive.ELogicalType;

public class ConstTemplate extends LogicID<ELogicalType>
{
	public final String name;
	public final Value value;

	public ConstTemplate(String name, Object value)
	{
		super(ELogicalType.RULE_TEMPLATE);
		this.name = name;
		this.value = Value.Construct(value);
	}
}
