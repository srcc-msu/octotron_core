package ru.parallel.octotron.generators.tmpl;

public class ReactionTemplate
{
	public final String name;
	public final ReactionAction reaction;

	public ReactionTemplate(String name, ReactionAction reaction)
	{
		this.name = name;
		this.reaction = reaction;
	}
}
