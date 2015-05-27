/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.services.impl;

import ru.parallel.octotron.core.attributes.impl.Reaction;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.exec.ModelData;
import ru.parallel.octotron.generators.tmpl.ConstTemplate;
import ru.parallel.octotron.logic.SelfTest;
import ru.parallel.octotron.services.Service;
import ru.parallel.octotron.services.ServiceLocator;
import ru.parallel.utils.FileUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public final class ModelService extends Service
{
	private final ModelData model_data = new ModelData();

	public ModelData GetModelData()
	{
		return model_data;
	}

	public static enum EMode
	{
		CREATION, LOAD, OPERATION
	}

	private EMode mode;

	public ModelService(Context context)
		throws ExceptionSystemError
	{
		super(context);

		String db_path = context.settings.GetDbPath() + "/" + context.settings.GetModelName();

		if(FileUtils.IsDirEmpty(db_path))
			mode = EMode.CREATION;
		else
			mode = EMode.LOAD;
	}

	public EMode GetMode()
	{
		return mode;
	}

	private SelfTest tester = null;

	public void InitSelfTest()
	{
		if(tester == null)
		{
			tester = new SelfTest();
			tester.Init();
		}
		else
			throw new ExceptionModelFail("internal error: self test has been initialized already");
	}

	public boolean PerformGraphTest()
	{
		return tester.Test();
	}

	public void Operate()
	{
		InitSelfTest();

		ServiceLocator.INSTANCE.GetPersistenceService().WaitAll();

		MakeRuleDependency();

		ServiceLocator.INSTANCE.GetPersistenceService().WaitAll();

		mode = EMode.OPERATION;
	}

	private void MakeRuleDependency()
	{
		for(ModelEntity entity : model_data.GetAllEntities())
		{
			entity.GetBuilder().MakeDependencies();

			ServiceLocator.INSTANCE.GetPersistenceService().MakeRuleDependency(entity);
		}
	}

//--------

	public ModelLink AddLink(ModelObject source, ModelObject target, boolean directed)
	{
		ModelLink link = new ModelLink(source, target, directed);
		ServiceLocator.INSTANCE.GetPersistenceService().RegisterLink(link);

		model_data.Add(link);

		if(directed)
		{
			source.GetBuilder().AddOutLink(link);
			target.GetBuilder().AddInLink(link);
		}
		else
		{
			source.GetBuilder().AddUndirectedLink(link);
			target.GetBuilder().AddUndirectedLink(link);
		}

		link.GetBuilder().DeclareConst(new ConstTemplate("AID", link.GetID()));

		return link;
	}

	public ModelObject AddObject()
	{
		ModelObject object = new ModelObject();
		ServiceLocator.INSTANCE.GetPersistenceService().RegisterObject(object);

		model_data.Add(object);

		object.GetBuilder().DeclareConst(new ConstTemplate("AID", object.GetID()));

		return object;
	}

//--------

	public void EnableLinkIndex(String name)
	{
		model_data.GetCache().EnableLinkIndex(name, model_data.GetAllLinks());
	}

	public void EnableObjectIndex(String name)
	{
		model_data.GetCache().EnableObjectIndex(name, model_data.GetAllObjects());
	}

	public long SetSuppress(ModelEntity entity, String name, boolean value, String description)
	{
		long suppressed = 0;
		long AID = -1;

		for(Reaction reaction : entity.GetReactions())
		{
			if(reaction.GetName().equals(name))
			{
				reaction.SetSuppress(value);
				reaction.SetDescription(description);
				suppressed++;
				AID = reaction.GetID();
			}
		}

		if(suppressed > 1)
			throw new ExceptionModelFail("ambiguous reaction suppressing: few matches");

		return AID;
	}

	public Collection<Reaction> GetSuppressedReactions()
	{
		List<Reaction> reactions = new LinkedList<>();

		for(ModelEntity entity : model_data.GetAllObjects())
			for(Reaction reaction : entity.GetReactions())
			{
				if(reaction.GetSuppress())
					reactions.add(reaction);
			}

		for(ModelEntity entity : model_data.GetAllLinks())
			for(Reaction reaction : entity.GetReactions())
			{
				if(reaction.GetSuppress())
					reactions.add(reaction);
			}

		return reactions;
	}

	public void CreateCache()
	{
		EnableObjectIndex("AID");
		EnableLinkIndex("AID");

		LOGGER.log(Level.INFO, "enabled object cache: AID");
		LOGGER.log(Level.INFO, "enabled link cache: AID");

		for(String attr : context.settings.GetObjectIndex())
		{
			EnableObjectIndex(attr);
			LOGGER.log(Level.INFO, "enabled object cache: " + attr);
		}

		for(String attr : context.settings.GetLinkIndex())
		{
			EnableLinkIndex(attr);
			LOGGER.log(Level.INFO, "enabled link cache: " + attr);
		}

		LOGGER.log(Level.INFO, "done");
	}

	@Override
	public void Finish() {}
}
