package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.ReactionTemplate;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.List;

public abstract class ModelEntityBuilder<T extends ModelEntity>
{
	protected final T entity;
	protected final ModelService service;

	ModelEntityBuilder(ModelService service, T entity)
	{
		this.service = service;
		this.entity = entity;
	}

	public void AddReaction(ReactionTemplate reaction_template)
	{
		entity.GetAttribute(reaction_template.GetCheckName())
			.GetBuilder(service).AddReaction(reaction_template);
	}

	public void AddReaction(List<ReactionTemplate> reactions)
	{
		for(ReactionTemplate reaction : reactions)
			AddReaction(reaction);
	}

	public void DeclareConst(String name, Object value)
	{
		if(entity.TestAttribute(name))
			throw new ExceptionModelFail("attribute already declared: " + name);

		ConstAttribute attribute = new ConstAttribute(entity, name, SimpleAttribute.ConformType(value));

		entity.attributes_map.put(name, attribute);
		entity.const_map.put(name, attribute);

		service.RegisterConst(attribute);
	}

	public void DeclareConst(SimpleAttribute attribute)
	{
		DeclareConst(attribute.GetName(), attribute.GetValue());
	}

	public void DeclareConst(Iterable<SimpleAttribute> attributes)
	{
		for(SimpleAttribute attribute : attributes)
			DeclareConst(attribute);
	}

	public void DeclareSensor(String name, Object value)
	{
		if(entity.TestAttribute(name))
			throw new ExceptionModelFail("attribute already declared: " + name);

		SensorAttribute sensor = new SensorAttribute(entity, name, SimpleAttribute.ConformType(value));

		entity.attributes_map.put(name, sensor);
		entity.sensor_map.put(name, sensor);
	}

	public void DeclareSensor(SimpleAttribute attribute)
	{
		DeclareSensor(attribute.GetName(), attribute.GetValue());
	}

	public void DeclareSensor(Iterable<SimpleAttribute> attributes)
	{
		for(SimpleAttribute attribute : attributes)
			DeclareSensor(attribute);
	}

	public void DeclareVar(Rule rule)
	{
		String name = rule.GetName();

		if(entity.TestAttribute(name))
			throw new ExceptionModelFail("attribute already declared: " + name);

		VarAttribute var = new VarAttribute(entity, name, rule);

		entity.attributes_map.put(name, var);
		entity.var_map.put(name, var);
	}

	public void DeclareVar(Iterable<Rule> rules)
	{
		for(Rule rule : rules)
			DeclareVar(rule);
	}
}
