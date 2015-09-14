package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.attributes.impl.Var;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

public class ValueIfSomeValid extends AValidCount
{
	private final long count;
	private final String attribute;

	public ValueIfSomeValid(String attribute, long count, String path, String... attributes)
		throws ExceptionParseError
	{
		super(path, attributes);
		this.count = count;
		this.attribute = attribute;
	}

	@Override
	public final AttributeList<Attribute> GetDependency(ModelObject object)
	{
		AttributeList<Attribute> tmp = super.GetDependency(object);
		tmp.add(object.GetAttribute(attribute));
		return tmp;
	}

	@Override
	public Object Compute(ModelObject object, Attribute rule_attribute)
	{
		long valid_count = (Long)super.Compute(object, rule_attribute);

		if(valid_count < count)
			return Value.invalid;

		return object.GetAttribute(attribute).GetValue().GetRaw();
	}
}
