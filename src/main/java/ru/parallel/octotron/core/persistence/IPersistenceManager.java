package ru.parallel.octotron.core.persistence;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;

public interface IPersistenceManager
{
	public void AddObject(ModelObject object);
	public void AddLink(ModelLink link);
	public void AddReaction(Reaction reaction);
	public void RegisterConst(ModelService model_service, ConstAttribute attribute);

	void Finish();
}
