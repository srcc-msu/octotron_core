/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.services.impl;

import ru.parallel.octotron.core.attributes.impl.*;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.persistence.IPersistenceManager;
import ru.parallel.octotron.persistence.IPersistenceService;
import ru.parallel.octotron.persistence.impl.GraphCreationManager;
import ru.parallel.octotron.persistence.impl.GraphLoadManager;
import ru.parallel.octotron.persistence.impl.GraphOperationManager;
import ru.parallel.octotron.persistence.neo4j.Neo4jGraph;
import ru.parallel.octotron.services.BGExecutorService;
import ru.parallel.octotron.services.BGService;
import ru.parallel.octotron.services.ServiceLocator;

import java.util.logging.Level;

/**
 * neo4j has problems, when db accessed form different threads
 * */
public class Neo4jPersistenceService extends BGService implements IPersistenceService
{
	private IPersistenceManager persistence_manager;
	private Neo4jGraph graph;
	private boolean fail = false;
	private static final String FAIL_MSG = "persistence service failed";

	public Neo4jPersistenceService(Context context, final ModelService.EMode mode)
	{
		// unlimited for the start, will be limited in when db loading is done
		super(context, new BGExecutorService("persistence", 0L));

		executor.LockOnThread();

		InitGraph(context.settings.GetDbPath() + "/" + context.settings.GetModelName()
			, context.settings.GetDbPort(), mode);
	}

	private void InitGraph(final String db_path, final int webserver_port, final ModelService.EMode mode)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						if(mode == ModelService.EMode.CREATION)
						{
							graph = new Neo4jGraph(db_path
								, Neo4jGraph.Op.RECREATE, true, webserver_port);

							graph.GetIndex().EnableLinkIndex("AID");
							graph.GetIndex().EnableObjectIndex("AID");

							persistence_manager = new GraphCreationManager(
								ServiceLocator.INSTANCE.GetModelService(), graph);
						}
						else if(mode == ModelService.EMode.LOAD)
						{
							graph = new Neo4jGraph(db_path
								, Neo4jGraph.Op.LOAD, true, webserver_port);

							graph.GetIndex().EnableLinkIndex("AID");
							graph.GetIndex().EnableObjectIndex("AID");

							persistence_manager = new GraphLoadManager(
								ServiceLocator.INSTANCE.GetModelService(), graph);
						}
						else
						{
							LOGGER.log(Level.SEVERE, "bad mode", mode);
							fail = true;
						}
					}
					catch(Exception e)
					{
						LOGGER.log(Level.SEVERE, "could not init database", e);
						fail = true;
					}
				}
			});

		executor.WaitAllTasks();
	}

	@Override
	public void MakeRuleDependency(final ModelEntity entity)
	{
		if(fail) throw new ExceptionModelFail(FAIL_MSG);

		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					if(fail) return;

					try
					{
						persistence_manager.MakeRuleDependency(entity);
					}
					catch(Exception e)
					{
						fail = true;
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	@Override
	public void RegisterLink(final ModelLink link)
	{
		if(fail) throw new ExceptionModelFail(FAIL_MSG);

		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					if(fail) return;

					try
					{
						persistence_manager.RegisterLink(link);
					}
					catch(Exception e)
					{
						fail = true;
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	@Override
	public void RegisterReaction(final Reaction reaction)
	{
		if(fail) throw new ExceptionModelFail(FAIL_MSG);

		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					if(fail) return;

					try
					{
						persistence_manager.RegisterReaction(reaction);
					}
					catch(Exception e)
					{
						fail = true;
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	@Override
	public void RegisterConst(final Const attribute)
	{
		if(fail) throw new ExceptionModelFail(FAIL_MSG);

		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					if(fail) return;

					try
					{
						persistence_manager.RegisterConst(attribute);
					}
					catch(Exception e)
					{
						fail = true;
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	@Override
	public void RegisterSensor(final Sensor attribute)
	{
		if(fail) throw new ExceptionModelFail(FAIL_MSG);

		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					if(fail) return;

					try
					{
						persistence_manager.RegisterSensor(attribute);
					}
					catch(Exception e)
					{
						fail = true;
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	@Override
	public void RegisterVar(final Var attribute)
	{
		if(fail) throw new ExceptionModelFail(FAIL_MSG);

		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					if(fail) return;

					try
					{
						persistence_manager.RegisterVar(attribute);
					} catch (Exception e)
					{
						fail = true;
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});

}
	@Override
	public void RegisterTrigger(final Trigger attribute)
	{
		if(fail) throw new ExceptionModelFail(FAIL_MSG);

		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					if(fail) return;

					try
					{
						persistence_manager.RegisterTrigger(attribute);
					}
					catch(Exception e)
					{
						fail = true;
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	@Override
	public void RegisterObject(final ModelObject object)
	{
		if(fail) throw new ExceptionModelFail(FAIL_MSG);

		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					if(fail) return;

					try
					{
						persistence_manager.RegisterObject(object);
					}
					catch(Exception e)
					{
						fail = true;
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	public void Operate()
	{
		if(fail) throw new ExceptionModelFail(FAIL_MSG);

		executor.WaitAllTasks();

		// was unlimited for creation - limit it now
		SetMaxWaiting(BGService.DEFAULT_QUEUE_LIMIT);

		ModelService model_service = ServiceLocator.INSTANCE.GetModelService();

		if(model_service.GetMode() == ModelService.EMode.CREATION)
			executor.execute(
				new Runnable()
				{
					@Override
					public void run()
					{
						if(fail) return;

						try
						{
							graph.Save();
						}
						catch(ExceptionSystemError e)
						{
							fail = true;
							LOGGER.log(Level.SEVERE, "could not save db", e);
						}
					}
				});

		executor.WaitAllTasks();

		persistence_manager = new GraphOperationManager(model_service, graph);
	}

	@Override
	public void Clean()
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					if(graph == null) return;

					try
					{
						graph.Shutdown();
						graph.Delete();
					}
					catch(Exception e)
					{
						fail = true;
						LOGGER.log(Level.WARNING, "", e);
						return;
					}

					LOGGER.log(Level.INFO, "Neo4j folder wiped");
				}
			});
	}

	@Override
	public void Finish()
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					if(graph == null) return;

					try
					{
						graph.Shutdown();
					}
					catch(Exception e)
					{
						fail = true;
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});

		super.Finish();
	}

	@Override
	public void Check()
	{
		if(fail) throw new ExceptionModelFail(FAIL_MSG);
	}
}
