package ru.parallel.octotron.core.graph;

public interface ILink extends IEntity
{
	IObject Target();
	IObject Source();
}
