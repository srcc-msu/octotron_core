package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.exec.services.ModelService;

import java.util.Collection;

public interface IModelAttribute extends IAttribute
{
	double GetSpeed();
	long GetCTime();

	Collection<Reaction> GetReactions();

	AttributeList<VarAttribute> GetDependOnMe();

	IAttributeBuilder GetBuilder(ModelService service);

}
