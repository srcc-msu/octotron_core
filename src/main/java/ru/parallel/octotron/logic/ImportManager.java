/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.util.List;

/**
 * main manager class, that does all data processing
 * */
public class ImportManager
{
	public static class Packet
	{
		public final ModelObject object;
		public final SimpleAttribute attribute;

		public Packet(ModelObject object, SimpleAttribute attribute)
		{
			this.object = object;
			this.attribute = attribute;
		}
	}

	private final AttributeProcessor static_proc;

	public ImportManager()
	{
		this.static_proc = new AttributeProcessor();
	}

	public AttributeList<IModelAttribute> Process(List<Packet> packet)
	{
		AttributeList<IModelAttribute> result = new AttributeList<>();

		AttributeList<SensorAttribute> changed = static_proc.Process(packet);

		if(changed.size() > 0)
		{
			AttributeList<VarAttribute> changed_varyings = ProcessVaryings(changed/*.append(ProcessTimers())*/); // TODO: timers?
			result = result.append(changed);
			result = result.append(changed_varyings);
		}

		return result;
	}

	/*public AttributeList<VarAttribute> CheckChanged(AttributeList<VarAttribute> changed)
	{
		AttributeList<VarAttribute> result = new AttributeList<>();

		for(VarAttribute varying : changed)
		{
			if(varying.Update())
				result.add(varying);
		}

		return result;
	}*/

	protected AttributeList<VarAttribute> GetDependant(AttributeList<? extends IModelAttribute> attributes)
	{
		AttributeList<VarAttribute> result = new AttributeList<>();

		for(IModelAttribute attribute : attributes)
		{
			result.addAll(attribute.GetDependant());
		}

		return result;
	}

	public AttributeList<VarAttribute> ProcessVaryings(AttributeList<SensorAttribute> changed)
	{
		AttributeList<VarAttribute> result = new AttributeList<>();

		AttributeList<VarAttribute> dependant_varyings = GetDependant(changed);

		do
		{
			for(VarAttribute dependant_varying : dependant_varyings)
			{
				if(dependant_varying.Update())
					result.add(dependant_varying);
			}

//			dependant_varyings = CheckChanged(dependant_varyings);
//			result = result.append(dependant_varyings);
			dependant_varyings = GetDependant(dependant_varyings);
		}
		while(dependant_varyings.size() != 0);

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
