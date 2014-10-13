package ru.parallel.octotron.core.persistence;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;

public interface IPersistenceManager
{
	public void RegisterObject(ModelObject object);
	public void RegisterLink(ModelLink link);
	public void RegisterReaction(Reaction reaction);
	public void RegisterConst(ConstAttribute attribute);
	void RegisterSensor(SensorAttribute attribute);
	void RegisterVar(VarAttribute attribute);

	void Finish();

}
