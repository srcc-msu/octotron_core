/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.*;
import ru.parallel.octotron.core.persistence.GhostManager;
import ru.parallel.octotron.core.persistence.GraphManager;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.generators.tmpl.ConstTemplate;
import ru.parallel.octotron.logic.SelfTest;
import ru.parallel.utils.FileUtils;

import java.io.File;
import java.util.*;

public final class ModelService extends Service
{
	private final PersistenceService persistence_service;

	public static enum EMode
	{
		CREATION, LOAD, OPERATION
	}

	private EMode mode;

	private final double free_space_mb_thr = 1024; // 1GB in MB

	private SelfTest tester = null;

	public ModelService(Context context)
		throws ExceptionSystemError
	{
		super(context);

		String db_path = context.settings.GetDbPath() + "/" + context.settings.GetModelName();

		if(FileUtils.IsDirEmpty(db_path))
			mode = EMode.CREATION;
		else
			mode = EMode.LOAD;

		if(context.settings.IsDb())
			persistence_service = new PersistenceService(new GraphManager(this, db_path));
		else
			persistence_service = new PersistenceService(new GhostManager());
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

		InitSelfTest(this);

		mode = EMode.OPERATION;
	}

	private void MakeRuleDependency()
	{
		for(ModelObject object : context.model_data.GetAllObjects())
		{
			for(VarAttribute attribute : object.GetVar())
			{
				attribute.GetBuilder(this).ConnectDependency();
				persistence_service.persistence_manager.MakeRuleDependency(attribute);
			}
		}
	}

// ---------------------

	public ModelLink AddLink(ModelObject source, ModelObject target)
	{
		CheckModification();

		ModelLink link = new ModelLink(source, target);
		persistence_service.persistence_manager.RegisterLink(link);

		context.model_data.links.add(link);

		source.GetBuilder(this).AddOutLink(link);
		target.GetBuilder(this).AddInLink(link);

		link.GetBuilder(this).DeclareConst(new ConstTemplate("AID", link.GetID()));

		return link;
	}

	public ModelObject AddObject()
	{
		CheckModification();

		ModelObject object = new ModelObject();
		persistence_service.persistence_manager.RegisterObject(object);

		context.model_data.objects.add(object);

		object.GetBuilder(this).DeclareConst(new ConstTemplate("AID", object.GetID()));

		return object;
	}

// ---------------------

	public void EnableLinkIndex(String name)
	{
		CheckModification();

		context.model_data.cache.EnableLinkIndex(name, context.model_data.links);
	}

	public void EnableObjectIndex(String name)
	{
		CheckModification();

		context.model_data.cache.EnableObjectIndex(name, context.model_data.objects);
	}

	public long SetSuppress(ModelEntity entity, long template_id, boolean value, String description)
	{
		long suppressed = 0;
		long AID = -1;

		for(IModelAttribute attribute : entity.GetAttributes())
		{
			for(Reaction reaction : attribute.GetReactions())
			{
				if(reaction.GetTemplate().GetID() == template_id)
				{
					reaction.SetSuppress(value);
					reaction.SetDescription(description);
					suppressed++;
					AID = reaction.GetID();
				}
			}
		}

		if(suppressed > 1)
			throw new ExceptionModelFail("ambiguous reaction suppressing: few matches");

		return AID;
	}

	public Collection<Reaction> GetSuppressedReactions()
	{
		List<Reaction> reactions = new LinkedList<>();

		for(ModelEntity entity : context.model_data.GetAllObjects())
			for(IModelAttribute attribute : entity.GetAttributes())
				for(Reaction reaction : attribute.GetReactions())
				{
					if(reaction.GetSuppress())
						reactions.add(reaction);
				}

		for(ModelEntity entity : context.model_data.GetAllLinks())
			for(IModelAttribute attribute : entity.GetAttributes())
				for(Reaction reaction : attribute.GetReactions())
				{
					if(reaction.GetSuppress())
						reactions.add(reaction);
				}

		return reactions;
	}

	public PersistenceService GetUpdateService()
	{
		return persistence_service;
	}

	@Override
	public void Finish()
	{
		persistence_service.Finish();
	}


	public void InitSelfTest(ModelService model_service)
	{
		if(tester == null)
		{
			tester = new SelfTest();
			tester.Init(model_service);
		}
		else
			throw new ExceptionModelFail("internal error: self test has been initialized already");
	}

	public Map<String, Object> PerformSelfTest(ExecutionController controller)
	{
		if(tester == null)
			throw new ExceptionModelFail("internal error: self test is not initialized");

		boolean graph_test = tester.Test(controller);

		long free_space = new File("/").getFreeSpace();

		long free_space_mb = free_space / 1024 / 1024;

		Map<String, Object> map = new HashMap<>();

		map.put("graph_test", graph_test);
		map.put("disk_space_MB", free_space_mb);
		map.put("disk_test", free_space_mb > free_space_mb_thr);

		return map;
	}
}
