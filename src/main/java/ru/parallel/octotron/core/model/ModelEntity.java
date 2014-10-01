package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphBased;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.logic.Marker;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.impl.attribute.ConstantAttribute;
import ru.parallel.octotron.core.model.impl.attribute.SensorAttribute;
import ru.parallel.octotron.core.model.impl.attribute.VaryingAttribute;
import ru.parallel.octotron.core.model.impl.meta.*;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.LinkedList;
import java.util.List;

public abstract class ModelEntity extends GraphBased implements IEntity<ModelAttribute>
{
	public ModelEntity(GraphEntity base)
	{
		super(base);
	}

// ---------------

	public void DeclareConstant(SimpleAttribute attribute)
	{
		DeclareConstant(attribute.GetName(), attribute.GetValue());
	}

	public void DeclareConstant(String name, Object value)
	{
		GetBaseEntity().DeclareAttribute(name, value);
	}

	public ConstantAttribute GetConstant(String name)
	{
		return GetAttribute(name).ToConstant();
	}

	public void DeclareConstants(List<SimpleAttribute> attributes)
	{
		for(SimpleAttribute attribute : attributes)
			DeclareConstant(attribute);
	}

// ---------------

	@Override
	public ModelAttribute GetAttribute(String name)
	{
		return new ModelAttribute(this, GetBaseEntity().GetAttribute(name));
	}

	public IMetaAttribute GetMetaAttribute(String name)
	{
		return GetAttribute(name).ToMeta();
	}

	public AttributeList<IMetaAttribute> GetMetaAttributes()
	{
		AttributeList<IMetaAttribute> result = new AttributeList<>();

		for(ModelAttribute attribute : GetAttributes())
			result.add(attribute.ToMeta());

		return result;
	}

	@Override
	public AttributeList<ModelAttribute> GetAttributes()
	{
		AttributeList<ModelAttribute> attributes = new AttributeList<>();

		for(GraphAttribute attribute : GetBaseEntity().GetAttributes())
		{
			attributes.add(new ModelAttribute(this, attribute));
		}

		return attributes;
	}

// -----------------------------

	@Override
	public boolean TestAttribute(String name)
	{
		return GetBaseEntity().TestAttribute(name);
	}

	@Override
	public boolean TestAttribute(String name, Object value)
	{
		return GetBaseEntity().TestAttribute(name, value);
	}

	@Override
	public boolean TestAttribute(SimpleAttribute attribute)
	{
		return GetBaseEntity().TestAttribute(attribute);
	}
}
