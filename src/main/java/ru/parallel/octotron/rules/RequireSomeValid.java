package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class RequireSomeValid extends AValidCount
{
	private final long count;
	private final Object return_value;

	public RequireSomeValid(long count, Object return_value, EDependencyType dependency, String... attributes)
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
