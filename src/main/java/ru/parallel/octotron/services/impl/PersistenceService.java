package ru.parallel.octotron.services.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.*;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.persistence.GhostManager;
import ru.parallel.octotron.persistence.GraphManager;
import ru.parallel.octotron.persistence.IPersistenceManager;
import ru.parallel.octotron.services.BGExecutorService;
import ru.parallel.octotron.services.BGService;
import ru.parallel.octotron.services.ServiceLocator;

import java.util.Collection;
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

	public void InitGraph(final String db_path, final int port)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						persistence_manager = new GraphManager(ServiceLocator.INSTANCE.GetModelService(), db_path, port);
					} catch (ExceptionSystemError e)
					{
						LOGGER.log(Level.SEVERE, "could not init database", e);

						throw new ExceptionModelFail(e);
					}
				}
			});

		executor.WaitAll();
	}

	public void InitDummy()
	{
		this.persistence_manager = new GhostManager();
	}

	public void UpdateAttributes(final Collection<? extends Attribute> attributes)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						for(Attribute attribute : attributes)
						{
							EAttributeType type = attribute.GetInfo().GetType();

							if(type == EAttributeType.SENSOR)
								persistence_manager.RegisterSensor((Sensor) attribute);
							else if(type == EAttributeType.VAR)
								persistence_manager.RegisterVar((Var) attribute);
							else
								throw new ExceptionModelFail("unsupported attribute: " + type);
						}
					}
					catch(Exception e)
					{
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	public void UpdateReactions(final Collection<Reaction> reactions)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						for(Reaction reaction : reactions)
							persistence_manager.RegisterReaction(reaction);
					} catch(Exception e)
					{
						LOGGER.log(Level.WARNING, "", e);
					}
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

	public void WaitAll()
	{
		executor.WaitAll();
	}
}
