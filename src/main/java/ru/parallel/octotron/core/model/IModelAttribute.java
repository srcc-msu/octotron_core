package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.IAttribute;
import ru.parallel.octotron.core.attributes.IAttributeBuilder;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.exec.services.ModelService;

import java.util.Collection;

public interface IModelAttribute extends IAttribute
{
	boolean IsInitialDelay();
	boolean Check();

	double GetSpeed();
	long GetCTime();

	Collection<Reaction> GetReactions();

	AttributeList<VarAttribute> GetDependOnMe();

	IAttributeBuilder GetBuilder(ModelService service);

}
