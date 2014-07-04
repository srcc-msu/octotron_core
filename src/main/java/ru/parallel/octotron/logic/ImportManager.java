/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import org.apache.commons.lang3.tuple.Pair;
import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.primitive.EEntityType;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.utils.OctoEntityList;
import ru.parallel.utils.JavaUtils;

import java.util.List;

/**
 * main manager class, that does all data processing
 * */
public class ImportManager
{
	private final AttributeProcessor static_proc;
	private final RuleProcessor ruled_proc;

	public ImportManager()
	{
		this.static_proc = new AttributeProcessor();
		this.ruled_proc = new RuleProcessor();
	}

	/**
	 * main processing method
	 * get a single data packet
	 * calculate static attributes, that changed in graph
	 * calculate computational attributes, that changed from static
	 * invoke rules, according to all changed attributes
	 * */
	public OctoEntityList Process(List<Pair<OctoEntity, SimpleAttribute>> packet)
	{
		OctoEntityList changed = static_proc.Process(packet);

		if(changed.size() > 0)
			return ProcessRules(changed.append(ProcessTimers()).Uniq());

		return new OctoEntityList();
	}

	public OctoEntityList ProcessRules(OctoEntityList changed)
	{
		OctoEntityList rule_changed = ruled_proc.Process(changed);

		OctoEntityList changed_last = new OctoEntityList(rule_changed);
		OctoEntityList changed_now;

		while(changed_last.size() > 0)
		{
			changed_now = ruled_proc.Process(changed_last);
			rule_changed = rule_changed.append(changed_now);

			changed_last = changed_now;
		}

		return changed.append(rule_changed).Uniq();
	}

	private long last_timer_check = 0;
	private static final long TIMER_CHECK_THRESHOLD = 2;

	public OctoEntityList ProcessTimers()
	{
		long cur_time = JavaUtils.GetTimestamp();

		OctoEntityList res = new OctoEntityList();

		if(cur_time - last_timer_check < ImportManager.TIMER_CHECK_THRESHOLD)
			return res;

		last_timer_check = cur_time;

		OctoEntityList to_update = TimerProcessor.Process();

		for(OctoEntity entity : to_update)
		{
			if(entity.GetUID().getType() != EEntityType.OBJECT)
				throw new ExceptionModelFail("timer attribute on link is not supported yet");

			res.add(entity);
		}

		return res;
	}

	public void Finish() {}
}
