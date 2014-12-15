package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class RequireValid extends ASoftValidCount
{
	private final long count;
	private final Object return_value;

	public RequireValid(long count, Object return_value, EDependencyType dependency, String... attributes)
	{
		super(dependency, attributes);
		this.count = count;
		this.return_value = return_value;
	}

	@Override
	public Object Compute(ModelObject object)
	{
		long valid_count = (Long)super.Compute(object);

		if(valid_count < count)
			return Value.invalid;

		return return_value;
	}
}
