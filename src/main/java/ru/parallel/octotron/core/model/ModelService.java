/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.logic.impl.Timeout;
import ru.parallel.octotron.core.persistence.GhostManager;
import ru.parallel.octotron.core.persistence.GraphManager;
import ru.parallel.octotron.core.persistence.IPersistenceManager;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.GlobalSettings;
import ru.parallel.octotron.generators.tmpl.ConstTemplate;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;
import ru.parallel.octotron.logic.Importer;
import ru.parallel.octotron.logic.RuntimeService;
import ru.parallel.utils.FileUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class ModelService
{
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

		for(ModelEntity entity : model_data.GetAllObjects())
			for(IModelAttribute attribute : entity.GetAttributes())
				for(Reaction reaction : attribute.GetReactions())
				{
					if(reaction.GetSuppress())
						reactions.add(reaction);
				}

		for(ModelEntity entity : model_data.GetAllLinks())
			for(IModelAttribute attribute : entity.GetAttributes())
				for(Reaction reaction : attribute.GetReactions())
				{
					if(reaction.GetSuppress())
						reactions.add(reaction);
				}

		return reactions;
	}

	public static enum EMode
	{
		CREATION, LOAD, OPERATION
	}

	private final ModelData model_data;
	private EMode mode;
	private IPersistenceManager persistence_manager;

	public ModelService(ModelData model_data, GlobalSettings settings)
		throws ExceptionSystemError
	{
		this.model_data = model_data;

		String db_path = settings.GetDbPath() + "/" + settings.GetModelName();

		if(FileUtils.IsDirEmpty(db_path))
			mode = EMode.CREATION;
		else
			mode = EMode.LOAD;

		if(settings.IsDb())
			persistence_manager = new GraphManager(this, db_path);
		else
			persistence_manager = new GhostManager();
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

		UpdateDefinedSensors();

		RuntimeService.InitSelfTest(this);

		mode = EMode.OPERATION;
		persistence_manager.Operate();
	}

	private void UpdateDefinedSensors()
	{
		for(ModelObject object : model_data.GetAllObjects())
		{
			for(SensorAttribute sensor : object.GetSensor())
			{
				if(sensor.GetValue().IsDefined())
					Importer.ProcessVars(sensor);
			}
		}
	}

	private void MakeRuleDependency()
	{
		for(ModelObject object : model_data.GetAllObjects())
		{
			for(VarAttribute attribute : object.GetVar())
			{
				attribute.GetBuilder(this).ConnectDependency();
				persistence_manager.MakeRuleDependency(attribute);
			}
		}
	}

// ---------------------

	public ModelLink AddLink(ModelObject source, ModelObject target)
	{
		CheckModification();

		ModelLink link = new ModelLink(source, target);
		persistence_manager.RegisterLink(link);

		model_data.links.add(link);

		source.GetBuilder(this).AddOutLink(link);
		target.GetBuilder(this).AddInLink(link);

		link.GetBuilder(this).DeclareConst(new ConstTemplate("AID", link.GetID()));

		return link;
	}

	public ModelObject AddObject()
	{
		CheckModification();

		ModelObject object = new ModelObject();
		persistence_manager.RegisterObject(object);

		model_data.objects.add(object);

		object.GetBuilder(this).DeclareConst(new ConstTemplate("AID", object.GetID()));

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
		persistence_manager.RegisterReaction(reaction);
	}

	public void RegisterConst(ConstAttribute attribute)
	{
		persistence_manager.RegisterConst(attribute);
	}

	public void RegisterSensor(SensorAttribute attribute)
	{
		persistence_manager.RegisterSensor(attribute);

		ReactionTemplate timeout_reaction = null;
		try
		{
			timeout_reaction = new Timeout(attribute.GetName())
				.Response(new Response(EEventStatus.INFO)
					.Msg("tag", "TIMEOUT")
					.Msg("descr", "sensor value has not been updated in required time")
					.Msg("loc", "AID = {AID}")
					.Msg("msg", "sensor(" + attribute.GetName() + ") value has not been updated in required time"));
		}
		catch (ExceptionParseError exceptionParseError)
		{
			throw new ExceptionModelFail("internal error in builtin reaction description");
		}

		attribute.GetBuilder(this).SetTimeoutReaction(timeout_reaction);
	}

	public void RegisterVar(VarAttribute attribute)
	{
		persistence_manager.RegisterVar(attribute);
	}

	public void RegisterUpdate(Collection<? extends IModelAttribute> attributes)
	{
		persistence_manager.RegisterUpdate(attributes);
	}

	public void Finish()
	{
		persistence_manager.Finish();
		persistence_manager = null;
	}

	public void Wipe()
	{
		persistence_manager.Wipe();
		persistence_manager = null;
	}
}
