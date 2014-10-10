package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelService;

public class VarAttributeBuilder extends AbstractModAttributeBuilder<VarAttribute>
{
	VarAttributeBuilder(ModelService service, VarAttribute attribute)
	{
		super(service, attribute);
	}

	public void MakeDependant()
	{
		for(IModelAttribute dependant : attribute.rule.GetDependency(attribute.GetParent()))
		{
			dependant.GetBuilder(service).AddDependant(attribute);
		}
	}
}
