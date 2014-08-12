/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import org.apache.commons.lang3.tuple.Pair;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.attribute.VariableAttribute;
import ru.parallel.octotron.core.model.attribute.SensorAttribute;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.utils.JavaUtils;

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
		AttributeList<SensorAttribute> changed = static_proc.Process(packet);

		if(changed.size() > 0)
			return ProcessRules(changed.append(ProcessTimers()));

		return new AttributeList<>();
	}

	public AttributeList<VariableAttribute> ProcessRuleWave(AttributeList<VariableAttribute> changed)
	{
		AttributeList<VariableAttribute> result = new AttributeList<>();

		for(VariableAttribute derived : changed)
		{
			if(derived.Update())
				result.append(derived.GetDependant());
		}

		return result;
	}

	public AttributeList<ModelAttribute> ProcessRules(AttributeList<SensorAttribute> changed)
	{
		AttributeList<VariableAttribute> rule_changed = new AttributeList<>();

		for(SensorAttribute sensor : changed)
			rule_changed.append(sensor.GetDependant());

		AttributeList<VariableAttribute> changed_last = new AttributeList<>(rule_changed);
		AttributeList<VariableAttribute> changed_now;

		while(changed_last.size() > 0)
		{
			changed_now = new AttributeList<>();

			for(VariableAttribute derived : changed_now)
				changed_now.append(derived.GetDependant());

			rule_changed = rule_changed.append(changed_now);

			changed_last = changed_now;
		}

		AttributeList<ModelAttribute> result = new AttributeList<>();
		return result.append(changed).append(rule_changed);
	}

	private long last_timer_check = 0;
	private static final long TIMER_CHECK_THRESHOLD = 2;

	public AttributeList<SensorAttribute> ProcessTimers()
	{
		long cur_time = JavaUtils.GetTimestamp();

		AttributeList<SensorAttribute> res = new AttributeList<>();

		/*if(cur_time - last_timer_check < ImportManager.TIMER_CHECK_THRESHOLD)
			return res;

		last_timer_check = cur_time;

		EntityList<ModelEntity> to_update = TimerProcessor.Process();

		for(ModelEntity entity : to_update)
		{
			if(entity.GetUID().getType() != EEntityType.OBJECT)
				throw new ExceptionModelFail("timer attribute on link is not supported yet");

			res.add(entity);
		}*/

		return res;
	}

	public void Finish() {}
}
