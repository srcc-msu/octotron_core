package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.persistence.GhostManager;
import ru.parallel.octotron.core.persistence.GraphManager;
import ru.parallel.octotron.core.persistence.IPersistenceManager;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;

import java.util.Collection;
import java.util.logging.Level;

public class PersistenceService extends BGService implements IPersistenceManager // WTF?
{
	private IPersistenceManager persistence_manager;

	public PersistenceService(String prefix, Context context)
	{
		// unlimited for the start, will be limited in when db loading is done
		super(context, new BGExecutorService(prefix, 0L));

		executor.LockOnThread();
	}

	public void InitGraph(final ModelService model_service, final String db_path)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						persistence_manager = new GraphManager(model_service, db_path);
					}
					catch(ExceptionSystemError e)
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

	public void UpdateAttributes(final Collection<? extends IModelAttribute> attributes)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						for(IModelAttribute attribute : attributes)
							if(attribute.GetType() == EAttributeType.SENSOR)
								persistence_manager.RegisterSensor((SensorAttribute) attribute);
							else if(attribute.GetType() == EAttributeType.VAR)
								persistence_manager.RegisterVar((VarAttribute) attribute);
					} catch(Exception e)
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
	public void MakeRuleDependency(final VarAttribute attribute)
	{
		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						persistence_manager.MakeRuleDependency(attribute);
					} catch(Exception e)
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
	public void RegisterConst(final ConstAttribute attribute)
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
					} catch(Exception e)
					{
						LOGGER.log(Level.WARNING, "", e);
					}
				}
			});
	}

	@Override
	public void RegisterSensor(final SensorAttribute attribute)
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
	public void RegisterVar(final VarAttribute attribute)
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
