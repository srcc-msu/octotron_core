package ru.parallel.octotron.core.primitive;

public interface IUniqueID<T>
{
	long GetID();
	T GetType();
}
