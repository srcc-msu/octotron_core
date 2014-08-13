package ru.parallel.octotron.core.graph;

public interface ILink<OT extends IObject<OT, LT>, LT extends ILink<OT, LT>> extends IEntity
{
	IObject<OT, LT> Target();
	IObject<OT, LT> Source();
}
