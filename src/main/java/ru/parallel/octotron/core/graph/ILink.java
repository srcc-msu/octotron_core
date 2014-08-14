package ru.parallel.octotron.core.graph;

public interface ILink<T extends IAttribute> extends IEntity<T>
{
	IObject<T> Target();
	IObject<T> Source();
}
