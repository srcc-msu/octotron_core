package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.logic.ExecutionController;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.utils.JavaUtils;

import java.util.*;

public abstract class AbstractModAttribute extends AbstractAttribute implements IModelAttribute
{
	public static class AbstractModAttributeBuilder<T extends AbstractModAttribute> implements IAttributeBuilder
	{
		protected final T attribute;

		public AbstractModAttributeBuilder(T attribute)
		{
			if(ModelService.Get().GetMode() != ModelService.EMode.CREATION)
				throw new ExceptionModelFail("objects creation is not allowed in operational mode");

			this.attribute = attribute;
		}

		public void AddReaction(Reaction reaction)
		{
			attribute.reactions.put(reaction.GetID(), reaction);
		}

		@Override
		public void AddDependant(VarAttribute dependant)
		{
			attribute.dependants.add(dependant);
		}
	}

	public abstract AbstractModAttributeBuilder<? extends AbstractModAttribute> GetBuilder();

// ------------------

	private History history;

	protected boolean is_valid;
	private long ctime;

	protected final Map<Long, Reaction> reactions;
	protected final AttributeList<VarAttribute> dependants;

	AbstractModAttribute(ModelEntity parent, String name, Object value)
	{
		super(parent, name, value);

		history = new History();

		is_valid = true;
		ctime = 0;

		reactions = new HashMap<>();
		dependants = new AttributeList<>();
	}

	@Override
	public boolean IsValid()
	{
		return is_valid && ctime != 0;
	}

	@Override
	public void SetValid()
	{
		is_valid = true;
	}

	@Override
	public void SetInvalid()
	{
		is_valid = false;
	}

	public Reaction GetReaction(long id)
	{
		return reactions.get(id);
	}

	@Override
	public double GetSpeed()
	{
		History.Entry last = history.GetLast();

		if(last == null)
			return 0.0;

		long cur_ctime = ctime;
		long last_ctime = last.ctime;

		if(cur_ctime - last_ctime == 0) // speed is zero
			return 0.0;

		if(last_ctime == 0) // last value was default
			return 0.0;

		double diff = ToDouble() - (Double)last.value;

		return diff / (cur_ctime - last_ctime);
	}

	protected boolean Update(Object new_value)
	{
		boolean result = value != new_value;
		history.Add(value, ctime);

		ctime = JavaUtils.GetTimestamp();
		value = new_value;

		return result;
	}

	@Override
	public AttributeList<VarAttribute> GetDependant()
	{
		return dependants;
	}

	@Override
	public Collection<Reaction> GetReactions()
	{
		return reactions.values();
	}

	@Override
	public Collection<PreparedResponse> ProcessReactions()
	{
		long time = JavaUtils.GetTimestamp();

		List<PreparedResponse> result = new LinkedList<>();

		for(Reaction reaction : GetReactions())
		{
			Response response = reaction.Process();

			if(response != null)
				result.add(new PreparedResponse(response
					, GetParent(), time, ExecutionController.Get().GetSettings()));
		}

		return result;
	}
}
