package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public class VarAttribute extends AbstractModAttribute
{
	protected Rule rule;
	protected AttributeList<IModelAttribute> dependency;

	@Override
	public VarAttributeBuilder GetBuilder(ModelService service)
	{
		service.CheckModification();

		return new VarAttributeBuilder(service, this);
	}

	public VarAttribute(ModelEntity parent, String name, Rule rule)
	{
		super(EAttributeType.VAR, parent, name
			, SimpleAttribute.ConformType(rule.GetDefaultValue()));

		this.rule = rule;

		dependency = new AttributeList<>();
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
		Object new_value = rule.Compute(GetParent());

		if(new_value == null)
			return false;

		return super.Update(new_value);
	}

	public Iterable<IModelAttribute> GetDependency()
	{
		return dependency;
	}
}
