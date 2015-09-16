package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

public class RequireAllValid extends AInvalidCount // WTF?
{
	private final Object return_value;

	public RequireAllValid(Object return_value, String path, String... attributes)
		throws ExceptionParseError
	{
		super(path, attributes);
		this.return_value = return_value;
	}

	@Override
	public Object Compute(ModelObject object, Attribute rule_attribute)
	{
		long valid_count = (Long)super.Compute(object, rule_attribute);

		if(valid_count > 0)
			return Value.invalid;

		return return_value;
	}
}
