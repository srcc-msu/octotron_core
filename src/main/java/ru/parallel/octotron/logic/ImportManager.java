/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.logic;

import java.util.List;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.core.OctoAttribute;
import main.java.ru.parallel.octotron.core.OctoEntity;
import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.netimport.ISensorData;
import main.java.ru.parallel.octotron.primitive.EEntityType;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionImportFail;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.octotron.utils.AttributeList;
import main.java.ru.parallel.octotron.utils.ObjectList;
import main.java.ru.parallel.utils.JavaUtils;

/**
 * main manager class, that does all data processing
 * */
public class ImportManager
{
	private AttributeProcessor static_proc;
	private RuleProcessor ruled_proc;

	public ImportManager(GraphService graph_service)
	{
		this.static_proc = new AttributeProcessor(graph_service);
		this.ruled_proc = new RuleProcessor();
	}

	/**
	 * main processing method
	 * get a single data packet
	 * calculate static attributes, that changed in graph
	 * calculate computational attributes, that changed from static
	 * invoke rules, according to all changed attributes
	 * */
	public ObjectList Process(List<? extends ISensorData> packet)
		throws ExceptionImportFail
	{
		ObjectList changed = static_proc.Process(packet);

		changed.append(ProcessTimers());

		if(changed.size() <= 0)
			return new ObjectList();

		return ProcessRules(changed.Uniq());
	}

	public ObjectList ProcessRules(ObjectList changed)
		throws ExceptionImportFail
	{
		ObjectList rule_changed = ruled_proc.Process(changed).Uniq();

		ObjectList changed_last = new ObjectList(rule_changed);
		ObjectList changed_now = null;

		while(changed_last.size() > 0)
		{
			changed_now = ruled_proc.Process(changed_last).Uniq();
			rule_changed.append(changed_now);

			changed_last = changed_now;
		}

		ObjectList all_changed = new ObjectList(changed);
		all_changed.append(rule_changed).Uniq();

		return all_changed;
	}

	private long last_timer_check = 0;
	private static final long TIMER_CHECK_THRESHOLD = 2;

	public ObjectList ProcessTimers()
	{
		long cur_time = JavaUtils.GetTimestamp();

		ObjectList res = new ObjectList();

		if(cur_time - last_timer_check > TIMER_CHECK_THRESHOLD)
		{
			AttributeList timers_timed_out = TimerProcessor.Process();

			for(OctoAttribute attr : timers_timed_out)
			{
				OctoEntity parent = attr.GetParent();

				if(parent != null)
				{
					if(parent.GetUID().getType() != EEntityType.OBJECT)
						throw new ExceptionModelFail("timer attribute on link is not upported yet");

					res.add((OctoObject)parent);
				}
			}
		}

		return res;
	}

	public void Finish() {}
}
