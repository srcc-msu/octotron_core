/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators.tmpl;

import ru.parallel.octotron.core.attributes.impl.Trigger;
import ru.parallel.octotron.core.model.ModelEntity;

public final class ReactionCase
{
	public final String trigger_name;
	public final long repeat;
	public final long delay;

	public ReactionCase(String trigger_name, long repeat, long delay)
	{
		this.trigger_name = trigger_name;
		this.repeat = repeat;
		this.delay = delay;
	}

	public ReactionCase(String trigger_name)
	{
		this(trigger_name, 0, 0);
	}

	public boolean Match(ModelEntity entity, long current_time)
	{
		Trigger trigger = entity.GetTrigger(trigger_name);

		return trigger.IsTriggered()
			&& trigger.GetRepeat() >= repeat
			&& trigger.GetDelay(current_time) >= delay;
	}
}
