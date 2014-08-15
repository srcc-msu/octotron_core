/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import org.apache.commons.lang3.tuple.Pair;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.collections.ListConverter;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.impl.attribute.SensorAttribute;
import ru.parallel.octotron.core.model.impl.attribute.VariableAttribute;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.util.List;

/**
 * main manager class, that does all data processing
 * */
public class ImportManager
{
	private final AttributeProcessor static_proc;

	public ImportManager()
	{
		this.static_proc = new AttributeProcessor();
	}

	public AttributeList<ModelAttribute> Process(List<Pair<ModelEntity, SimpleAttribute>> packet)
	{
		AttributeList<ModelAttribute> result = new AttributeList<>();

		AttributeList<SensorAttribute> changed = static_proc.Process(packet);

		if(changed.size() > 0)
		{
			AttributeList<VariableAttribute> changed_variables = ProcessVariables(changed/*.append(ProcessTimers())*/); // TODO: timers?
			result = result.append(changed);
			result = result.append(changed_variables);
		}

		return result;
	}

	public AttributeList<VariableAttribute> CheckChanged(AttributeList<VariableAttribute> changed)
	{
		AttributeList<VariableAttribute> result = new AttributeList<>();

		for(VariableAttribute variable : changed)
		{
			if(variable.Update())
				result.add(variable);
		}

		return result;
	}

	public AttributeList<VariableAttribute> ProcessVariables(AttributeList<SensorAttribute> changed)
	{
		AttributeList<VariableAttribute> result = new AttributeList();

		AttributeList<VariableAttribute> dependant_variables = ListConverter.GetDependant(changed);

		do
		{
			dependant_variables = CheckChanged(dependant_variables);
			result = result.append(dependant_variables);
			dependant_variables = ListConverter.GetDependant(dependant_variables);
		}
		while(dependant_variables.size() != 0);

		return result;
	}

	/*private long last_timer_check = 0;
	private static final long TIMER_CHECK_THRESHOLD = 2;

	public AttributeList<SensorAttribute> ProcessTimers()
	{
		long cur_time = JavaUtils.GetTimestamp();

		AttributeList<SensorAttribute> res = new AttributeList<>();

		if(cur_time - last_timer_check < ImportManager.TIMER_CHECK_THRESHOLD)
			return res;

		last_timer_check = cur_time;

		EntityList<ModelEntity> to_update = TimerProcessor.Process();

		for(ModelEntity entity : to_update)
		{
			if(entity.GetUID().getType() != EEntityType.OBJECT)
				throw new ExceptionModelFail("timer attribute on link is not supported yet");

			res.add(entity);
		}

		return res;
	}*/

	public void Finish() {}
}
