package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.persistence.GhostManager;
import ru.parallel.octotron.core.persistence.GraphManager;
import ru.parallel.octotron.core.persistence.IPersistenceManager;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.GlobalSettings;

public final class ModelService
{
	public static enum EMode
	{
		CREATION, LOAD, OPERATION
	}

	private ModelData model_data;
	private EMode mode;
	private IPersistenceManager manager;

	public ModelService(ModelData model_data, EMode mode, GlobalSettings settings)
		throws ExceptionSystemError
	{
		this.model_data = model_data;
		this.mode = mode;

		if(settings.IsDb())
			manager = new GraphManager(this, settings.GetDbPath() + "/" + settings.GetModelName());
		else
			manager = new GhostManager();
	}

	public ModelService(EMode mode, String path)
		throws ExceptionSystemError
	{
		this.model_data = new ModelData();
		this.mode = mode;

		manager = new GraphManager(this, path);
	}

	public EMode GetMode()
	{
		return mode;
	}

	public void CheckModification()
	{
		if(GetMode() == ModelService.EMode.OPERATION)
			throw new ExceptionModelFail("model modification is not allowed in operational mode");
	}

	public void Operate()
	{
		MakeRuleDependency();
		mode = EMode.OPERATION;
		manager.Operate();
	}

	private void MakeRuleDependency()
	{
		for(ModelObject object : model_data.GetAllObjects())
		{
			for(VarAttribute attribute : object.GetVar())
			{
				attribute.GetBuilder(this).ConnectDependency();
				manager.MakeRuleDependency(attribute);
			}
		}
	}

// ---------------------

	public ModelLink AddLink(ModelObject source, ModelObject target)
	{
		CheckModification();

		ModelLink link = new ModelLink(source, target);
		manager.RegisterLink(link);

		model_data.links.add(link);

		source.GetBuilder(this).AddOutLink(link);
		target.GetBuilder(this).AddInLink(link);

		link.GetBuilder(this).DeclareConst("AID", link.GetID());

		return link;
	}

	public ModelObject AddObject()
	{
		CheckModification();

		ModelObject object = new ModelObject();
		manager.RegisterObject(object);

		model_data.objects.add(object);

		object.GetBuilder(this).DeclareConst("AID", object.GetID());

		return object;
	}

// ---------------------

	public void EnableLinkIndex(String name)
	{
		CheckModification();

		model_data.cache.EnableLinkIndex(name, model_data.links);
	}

	public void EnableObjectIndex(String name)
	{
		CheckModification();

		model_data.cache.EnableObjectIndex(name, model_data.objects);
	}

// ---------------------

	public void RegisterReaction(Reaction reaction)
	{
		manager.RegisterReaction(reaction);
	}

	public void RegisterConst(ConstAttribute attribute)
	{
		manager.RegisterConst(attribute);
	}

	public void RegisterSensor(SensorAttribute attribute)
	{
		manager.RegisterSensor(attribute);
	}

	public void RegisterVar(VarAttribute attribute)
	{
		manager.RegisterVar(attribute);
	}

	public void Finish()
	{
		manager.Finish();
		manager = null;
	}
}
