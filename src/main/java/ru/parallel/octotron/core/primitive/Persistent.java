package ru.parallel.octotron.core.primitive;

import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.model.ModelService;

public abstract class Persistent<T> extends UniqueID<T>
{
	protected GraphEntity persistent = null;

	protected Persistent(T type)
	{
		super(type);
	}

	public void InitPersistent()
	{
		persistent = ModelService.Get().GetPersistentObject(this);
	}

	public void StorePersistentAttribute(String name, Object value)
	{
		if(persistent == null)
			InitPersistent();

		persistent.UpdateAttribute(name, value);
	}

	public Object GetPersistentAttribute(String name, Object default_value)
	{
		if(persistent == null)
			InitPersistent();

		if(persistent.TestAttribute(name))
			return persistent.GetAttribute(name);

		return default_value;
	}

	public GraphEntity GetPersistent()
	{
		return persistent;
	}
}
