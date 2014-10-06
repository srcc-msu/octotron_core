package ru.parallel.octotron.core.primitive;

public class UniqueID<T>
{
	private static long static_id = 0;

	private final long id;
	private final T type;

	public UniqueID(long id, T type)
	{
		this.type = type;
		this.id = id;
	}

	public UniqueID(T type)
	{
		this.type = type;
		this.id = static_id++;
	}

	public long GetID()
	{
		return id;
	}

	public T GetType()
	{
		return type;
	}

	@Override
	public final boolean equals(Object object)
	{
		if(!(object instanceof UniqueID))
			return false;

		UniqueID<T> cmp = ((UniqueID<T>)object);

		return id == cmp.id && type.equals(cmp.type);
	}
}
