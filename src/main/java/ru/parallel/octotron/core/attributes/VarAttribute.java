package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

public class VarAttribute extends AbstractModAttribute
{
	public static class VarAttributeBuilder extends AbstractModAttributeBuilder<VarAttribute>
	{
		VarAttributeBuilder(VarAttribute attribute)
		{
			super(attribute);
		}

		public void MakeDependant()
		{
			for(IModelAttribute dependant : attribute.rule.GetDependency(attribute.GetParent()))
			{
				dependant.GetBuilder().AddDependant(attribute);
			}
		}
	}

	@Override
	public VarAttributeBuilder GetBuilder()
	{
		return new VarAttributeBuilder(this);
	}

	protected Rule rule;

	public VarAttribute(ModelEntity parent, String name, Object value)
	{
		super(parent, name, value);
	}

	@Override
	public EAttributeType GetType()
	{
		return EAttributeType.VAR;
	}

	public Rule GetRule()
	{
		return rule;
	}

	public boolean Update()
	{
		throw new ExceptionModelFail("NIY");
	}
}
