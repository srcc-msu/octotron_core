package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.impl.AttributeDecorator;
import ru.parallel.octotron.core.model.impl.attribute.EAttributeType;
import ru.parallel.octotron.core.model.impl.attribute.VariableAttribute;
import ru.parallel.octotron.core.rule.OctoReaction;
import ru.parallel.octotron.core.rule.OctoResponse;
import ru.parallel.octotron.neo4j.impl.Marker;

import java.util.List;

public abstract class ModelAttribute extends AttributeDecorator
{
	protected final ModelEntity parent;

	public ModelAttribute(ModelEntity parent, String name)
	{
		super(parent.GetBaseEntity().GetAttribute(name));
		this.parent = parent;
	}

	public ModelEntity GetParent()
	{
		return parent;
	}

	public Object GetLastValue()
	{
		return GetValue();
	}

	protected boolean Update(Object new_value, boolean allow_overwrite)
	{
		if(ne(new_value) || GetCTime() == 0 || allow_overwrite)
		{
			SetValue(new_value);
			return true;
		}

		return false;
	}

	public long GetCTime()
	{
		return 0L;
	}
	public long GetATime()
	{
		return 0L;
	}
	public double GetSpeed()
	{
		return 0.0;
	}

	public boolean IsValid()
	{
		return true;
	}

	public abstract void SetValid();
	public abstract void SetInvalid();

	public abstract EAttributeType GetType();

	public abstract AttributeList<VariableAttribute> GetDependant();

//--------

	public abstract void AddReaction(OctoReaction reaction);

	public abstract List<OctoReaction> GetReactions();

	public abstract List<OctoResponse> GetReadyReactions();

	public abstract List<OctoResponse> GetExecutedReactions();

	public abstract List<Marker> GetMarkers();

	public abstract long AddMarker(OctoReaction reaction, String description, boolean suppress);

	public abstract void DeleteMarker(long id);
}
