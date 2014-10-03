package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractModAttribute extends AbstractAttribute implements IModelAttribute
{
	public static class AbstractModAttributeBuilder<T extends AbstractModAttribute> implements IAttributeBuilder
	{
		protected final T attribute;

		public AbstractModAttributeBuilder(T attribute)
		{
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

	public abstract AbstractModAttributeBuilder GetBuilder();

// ------------------

	private History history;

	protected final Map<Long, Reaction> reactions;
	protected boolean is_valid;
	protected final AttributeList<VarAttribute> dependants;

	private long ctime;

	AbstractModAttribute(ModelEntity parent, String name, Object value)
	{
		super(parent, name, value);

		reactions = new HashMap<>();
		dependants = new AttributeList<>();
	}

	@Override
	public boolean IsValid()
	{
		return is_valid;
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
		List<History.Entry> entries = history.Get();

		if(entries.size() == 0)
			return 0.0;

		History.Entry last = entries.get(0);

		long cur_ctime = ctime;
		long last_ctime = last.ctime;

		if(cur_ctime - last_ctime == 0) // speed is zero
			return 0.0;

		if(last_ctime == 0) // last value was default
			return 0.0;

		double diff = ToDouble() - (Double)last.value;

		return diff / (cur_ctime - last_ctime);
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
	public Collection<Response> ProcessReactions()
	{
		return null;
	}
}
