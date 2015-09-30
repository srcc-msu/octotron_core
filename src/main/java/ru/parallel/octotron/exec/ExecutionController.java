/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import org.python.core.Py;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import ru.parallel.octotron.core.attributes.impl.Sensor;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.services.BGService;
import ru.parallel.octotron.services.ServiceLocator;
import ru.parallel.utils.JavaUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecutionController
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	public final Context context;

	public ExecutionController(Context context)
		throws ExceptionSystemError
	{
		this.context = context;

		ServiceLocator.INSTANCE = new ServiceLocator(context);
	}

	private void UpdateDefinedSensors()
	{
		boolean old_mode = ServiceLocator.INSTANCE.GetReactionService().IsSilent();

		ServiceLocator.INSTANCE.GetReactionService().SetSilent(true);

		for(ModelObject object : ServiceLocator.INSTANCE.GetModelService().GetModelData().GetAllObjects())
		{
			for(Sensor sensor : object.GetSensor())
			{
				if(sensor.IsComputable())
					sensor.UpdateDependant();
			}
		}

		ServiceLocator.INSTANCE.GetReactionService().SetSilent(old_mode);
	}

	public void CreateFromPython()
	{
		PythonInterpreter interpreter = new PythonInterpreter(null, new PySystemState());

		PySystemState sys = Py.getSystemState();

		sys.path.append(new PyString(context.settings.GetModelPath()));

		if(context.settings.GetSysPath() != null)
			sys.path.append(new PyString(context.settings.GetSysPath()));

		/*
// some magic to pass context to all python modules
		interpreter.set("context", context);
		interpreter.set("model_service", model_service);
		interpreter.exec("import __builtin__");
		interpreter.exec("__builtin__.context = context");
		interpreter.exec("__builtin__.model_service = model_service");*/

		interpreter.execfile(context.settings.GetModelPath() + '/' + context.settings.GetModelMain());

		UpdateDefinedSensors();

		ServiceLocator.INSTANCE.GetPersistenceService().WaitAllTasks();

		// was unlimited for creation - limit it now
		ServiceLocator.INSTANCE.GetPersistenceService().SetMaxWaiting(BGService.DEFAULT_QUEUE_LIMIT);
	}

	private long last_outdated_check = JavaUtils.GetTimestamp(); // do not run at start

	public void Process()
		throws InterruptedException
	{
		long current_time = JavaUtils.GetTimestamp();

		if(context.settings.GetOutdatedCheckInterval() > 0)
			if(current_time - last_outdated_check > context.settings.GetOutdatedCheckInterval())
			{
				// TODO: scheduler?
				last_outdated_check = current_time;
				ServiceLocator.INSTANCE.GetOutdatedCheckerService().PerformCheck();
			}

		ServiceLocator.INSTANCE.GetRuntimeService().GetStat().Process();

		Thread.sleep(1);
	}

	public void Finish()
	{
		LOGGER.log(Level.INFO, "waiting for all tasks to finish");

		ServiceLocator.INSTANCE.Finish();

		ServiceLocator.INSTANCE = null;

		LOGGER.log(Level.INFO, "all processing finished");
	}
}
