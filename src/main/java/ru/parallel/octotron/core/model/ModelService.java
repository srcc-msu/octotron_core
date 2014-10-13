package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.graph.impl.*;
import ru.parallel.octotron.core.primitive.UniqueID;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

public final class ModelService
{
	public static enum EMode
	{
		CREATION, LOAD, OPERATION
	}

	private ModelData model_data;
	private EMode mode;
	private GraphManager manager;

	public ModelService(ModelData model_data, EMode mode)
	{
		this.model_data = model_data;
		this.mode = mode;
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

	public ModelLink AddLink(ModelObject source, ModelObject target)
	{
		CheckModification();

		ModelLink link = new ModelLink(source, target);

		source.GetBuilder(this).AddOutLink(link);
		target.GetBuilder(this).AddInLink(link);

		model_data.links.add(link);
		link.GetBuilder(this).DeclareConst("AID", link.GetID());

		return link;
	}

	public ModelObject AddObject()
	{
		CheckModification();

		ModelObject object = new ModelObject();

		model_data.objects.add(object);
		object.GetBuilder(this).DeclareConst("AID", object.GetID());

		manager.AddObject(object);
		return object;
	}

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

	public static String ExportDot()
	{
		throw new ExceptionModelFail("NIY");
	}

	public static String ExportDot(ModelObjectList objects)
	{
		throw new ExceptionModelFail("NIY");
	}
}
