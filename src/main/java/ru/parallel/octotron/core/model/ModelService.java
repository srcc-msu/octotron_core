package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.graph.impl.*;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;

public final class ModelService
{
	public static enum EMode
	{
		CREATION, LOAD, OPERATION
	}

	private ModelData model_data;
	private EMode mode;
	private GraphManager manager;

	public ModelService(ModelData model_data, EMode mode, String db_path)
		throws ExceptionSystemError
	{
		this.model_data = model_data;
		this.mode = mode;

		manager = new GraphManager(mode, db_path);
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
		MakeRuleDependencies();
		mode = EMode.OPERATION;
	}

	private void MakeRuleDependencies()
	{
		for(ModelObject object : model_data.GetAllObjects())
		{
			for(VarAttribute attribute : object.GetVar())
			{
				attribute.GetBuilder(this).MakeDependant();
			}
		}
	}

// ---------------------

	public ModelLink AddLink(ModelObject source, ModelObject target)
	{
		CheckModification();

		ModelLink link = new ModelLink(source, target);
		manager.AddLink(link);

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
		manager.AddObject(object);

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
		manager.AddReaction(reaction);
	}

	public void RegisterConst(ConstAttribute attribute)
	{
		manager.RegisterConst(this, attribute);
	}
}
