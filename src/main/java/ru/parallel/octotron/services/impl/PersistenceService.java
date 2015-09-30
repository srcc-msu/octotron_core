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
import ru.parallel.octotron.persistence.GhostManager;
import ru.parallel.octotron.persistence.GraphManager;
import ru.parallel.octotron.persistence.IPersistenceManager;
import ru.parallel.octotron.persistence.graph.impl.GraphService;
import ru.parallel.octotron.persistence.neo4j.Neo4jGraph;
import ru.parallel.octotron.services.BGExecutorService;
import ru.parallel.octotron.services.BGService;
import ru.parallel.octotron.services.ServiceLocator;

import java.util.logging.Level;

public class PersistenceService extends BGService implements IPersistenceManager // WTF?
{
	private IPersistenceManager persistence_manager;

	public PersistenceService(Context context)
	{
		// unlimited for the start, will be limited in when db loading is done
		super(context, new BGExecutorService("persistance", 0L));

		executor.LockOnThread();
	}

	public void InitGraph(final String db_path, final int webserver_port, final ModelService.EMode mode)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						Neo4jGraph graph = new Neo4jGraph(db_path
							, (mode == ModelService.EMode.CREATION) ? Neo4jGraph.Op.RECREATE : Neo4jGraph.Op.LOAD
							, true, webserver_port);

						graph.GetIndex().EnableLinkIndex("AID");
						graph.GetIndex().EnableObjectIndex("AID");

						persistence_manager = new GraphManager(ServiceLocator.INSTANCE.GetModelService(), graph);
					} catch (ExceptionSystemError e)
					{
						LOGGER.log(Level.SEVERE, "could not init database", e);

						throw new ExceptionModelFail(e);
					}
				}
			});

		executor.WaitAllTasks();
	}

	public void InitDummy()
	{
		this.persistence_manager = new GhostManager();
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
					try
					{
						persistence_manager.Finish();
					} catch(Exception e)
					{
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});

		super.Finish();
	}

	@Override
	public void MakeRuleDependency(final ModelEntity entity)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						persistence_manager.MakeRuleDependency(entity);
					} catch (Exception e)
					{
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	@Override
	public void RegisterLink(final ModelLink link)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						persistence_manager.RegisterLink(link);
					} catch(Exception e)
					{
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	@Override
	public void RegisterReaction(final Reaction reaction)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						persistence_manager.RegisterReaction(reaction);
					} catch(Exception e)
					{
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	@Override
	public void RegisterConst(final Const attribute)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						persistence_manager.RegisterConst(attribute);
					} catch (Exception e)
					{
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	@Override
	public void RegisterSensor(final Sensor attribute)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						persistence_manager.RegisterSensor(attribute);
					} catch(Exception e)
					{
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	@Override
	public void RegisterVar(final Var attribute)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						persistence_manager.RegisterVar(attribute);
					} catch(Exception e)
					{
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});

}
	@Override
	public void RegisterTrigger(final Trigger attribute)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						persistence_manager.RegisterTrigger(attribute);
					} catch(Exception e)
					{
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	@Override
	public void RegisterObject(final ModelObject object)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						persistence_manager.RegisterObject(object);
					} catch(Exception e)
					{
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	@Override
	public void Wipe()
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						persistence_manager.Wipe();
					} catch(Exception e)
					{
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	public void WaitAllTasks()
	{
		executor.WaitAllTasks();
	}
}
