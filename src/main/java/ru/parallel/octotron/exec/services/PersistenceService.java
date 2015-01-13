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
import java.util.concurrent.*;
import java.util.logging.Level;

import static ru.parallel.utils.JavaUtils.ShutdownExecutor;

public class PersistenceService extends Service implements IPersistenceManager // WTF?
{
	private final ConcurrentLinkedQueue<Collection<? extends IModelAttribute>> to_update
		= new ConcurrentLinkedQueue<>();

	private final ThreadPoolExecutor persistence_executor;

	private IPersistenceManager persistence_manager;

	public PersistenceService(Context context)
	{
		super(context);

		persistence_executor = new ThreadPoolExecutor(1, 1,
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

		persistence_executor.setThreadFactory(
			new ThreadFactory()
			{
				private long created = 0;
				@Override
				public Thread newThread(Runnable r)
				{
					if(created == 2)
						throw new ExceptionModelFail("db thread failed");

					created ++;

					return new Thread(r);
				}
			});
	}

	public void WaitAll()
	{
		while(persistence_executor.getQueue().size() > 0)
		{
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException ignore) {}
		}
	}

	public void InitGraph(final ModelService model_service, final String db_path)
	{
		Future<?> future = persistence_executor.submit(
			new Callable<Object>()
			{
				@Override
				public Object call() throws ExceptionSystemError
				{
					persistence_manager = new GraphManager(model_service, db_path);
					return true;
				}
			});

		try
		{
			future.get();
		}
		catch(InterruptedException e)
		{
			LOGGER.log(Level.SEVERE, "database initialization has been interrupted o_O", e);
		}
		catch(ExecutionException e)
		{
			LOGGER.log(Level.SEVERE, "could not init database", e);
			throw new ExceptionModelFail(e);
		}
	}

	public void InitDummy()
	{
		this.persistence_manager = new GhostManager();
	}

	public void UpdateAttributes(final Collection<? extends IModelAttribute> attributes)
	{
		persistence_executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						for(IModelAttribute attribute : attributes)
							if(attribute.GetType() == EAttributeType.SENSOR)
								persistence_manager.RegisterSensor((SensorAttribute)attribute);
							else if(attribute.GetType() == EAttributeType.VAR)
								persistence_manager.RegisterVar((VarAttribute)attribute);
					}
					catch(Exception e) {LOGGER.log(Level.WARNING, "", e);}
				}
			});
	}

	public void UpdateReactions(final Collection<Reaction> reactions)
	{
		persistence_executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						for(Reaction reaction : reactions)
						persistence_manager.RegisterReaction(reaction);
					}
					catch(Exception e) {LOGGER.log(Level.WARNING, "", e);}
				}
			});
	}

	@Override
	public void Finish()
	{
		WaitAll();

		persistence_executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try { persistence_manager.Finish();}
					catch(Exception e) {LOGGER.log(Level.WARNING, "", e);}
				}
			});

		ShutdownExecutor(persistence_executor);
	}

	@Override
	public void MakeRuleDependency(final VarAttribute attribute)
	{
		persistence_executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try { persistence_manager.MakeRuleDependency(attribute);}
					catch(Exception e) {LOGGER.log(Level.WARNING, "", e);}
				}
			});
	}

	@Override
	public void RegisterLink(final ModelLink link)
	{
		persistence_executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try { persistence_manager.RegisterLink(link);}
					catch(Exception e) {LOGGER.log(Level.WARNING, "", e);}
				}
			});
	}

	@Override
	public void RegisterReaction(final Reaction reaction)
	{
		persistence_executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try { persistence_manager.RegisterReaction(reaction);}
					catch(Exception e) {LOGGER.log(Level.WARNING, "", e);}
				}
			});
	}

	@Override
	public void RegisterConst(final ConstAttribute attribute)
	{
		persistence_executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try { persistence_manager.RegisterConst(attribute);}
					catch(Exception e) {LOGGER.log(Level.WARNING, "", e);}
				}
			});
	}

	@Override
	public void RegisterSensor(final SensorAttribute attribute)
	{
		persistence_executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try { persistence_manager.RegisterSensor(attribute);}
					catch(Exception e) {LOGGER.log(Level.WARNING, "", e);}
				}
			});
	}

	@Override
	public void RegisterVar(final VarAttribute attribute)
	{
		persistence_executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try { persistence_manager.RegisterVar(attribute);}
					catch(Exception e) {LOGGER.log(Level.WARNING, "", e);}
				}
			});
	}

	@Override
	public void RegisterObject(final ModelObject object)
	{
		persistence_executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try { persistence_manager.RegisterObject(object);}
					catch(Exception e) {LOGGER.log(Level.WARNING, "", e);}
				}
			});
	}

	@Override
	public void Wipe()
	{
		persistence_executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try { persistence_manager.Wipe();}
					catch(Exception e) {LOGGER.log(Level.WARNING, "", e);}
				}
			});
	}
}
