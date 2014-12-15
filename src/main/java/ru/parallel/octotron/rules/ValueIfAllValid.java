package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class ValueIfAllValid extends AInvalidCount // WTF?
{
	private final String attribute;

	public ValueIfAllValid(String attribute, EDependencyType dependency, String... attributes)
	{
		super(dependency, attributes);
		this.attribute = attribute;
	}

	@Override
	public final AttributeList<IModelAttribute> GetDependency(ModelObject object)
	{
		AttributeList<IModelAttribute> tmp = super.GetDependency(object);
		tmp.add(object.GetAttribute(attribute));
		return tmp;
	}

	@Override
	public Object Compute(ModelObject object)
	{
		long invalid_count = (Long)super.Compute(object);

		if(invalid_count > 0)
			return Value.invalid;

		return object.GetAttribute(attribute).GetValue().GetRaw();
	}
}
