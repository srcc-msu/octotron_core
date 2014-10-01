package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.model.impl.AttributeDecorator;
import ru.parallel.octotron.core.model.impl.attribute.ConstantAttribute;
import ru.parallel.octotron.core.model.impl.attribute.SensorAttribute;
import ru.parallel.octotron.core.model.impl.attribute.VaryingAttribute;
import ru.parallel.octotron.core.model.impl.meta.SensorObjectFactory;
import ru.parallel.octotron.core.model.impl.meta.VaryingObjectFactory;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public class ModelAttribute extends AttributeDecorator
{
	protected final ModelEntity parent;

	public ModelAttribute(ModelEntity parent, GraphAttribute attribute)
	{
		super(attribute);
		this.parent = parent;
	}

	public ModelEntity GetParent()
	{
		return parent;
	}

	public ConstantAttribute ToConstant()
	{
		return new ConstantAttribute(GetParent(), GetBaseAttribute());
	}

	public SensorAttribute ToSensor()
	{
		return new SensorAttribute((ModelObject)GetParent(), GetBaseAttribute()
			, SensorObjectFactory.INSTANCE.Obtain(GetParent().GetBaseObject(), GetName()));
	}

	public VaryingAttribute ToVarying()
	{
		return new VaryingAttribute((ModelObject)GetParent(), GetBaseAttribute()
			, VaryingObjectFactory.INSTANCE.Obtain(GetParent().GetBaseObject(), GetName()));
	}

	public IMetaAttribute ToMeta()
	{
		if(SensorObjectFactory.INSTANCE.Test(GetParent().GetBaseObject(), GetName()))
			return new SensorAttribute((ModelObject)GetParent(), GetBaseAttribute()
				, SensorObjectFactory.INSTANCE.Obtain(GetParent().GetBaseObject(), GetName()));

		if(VaryingObjectFactory.INSTANCE.Test(GetParent().GetBaseObject(), GetName()))
			return new VaryingAttribute((ModelObject)GetParent(), GetBaseAttribute()
				, VaryingObjectFactory.INSTANCE.Obtain(GetParent().GetBaseObject(), GetName()));

		return ToConstant();
	}

	public String GetStringValue()
	{
		return SimpleAttribute.ValueToStr(GetBaseAttribute().GetValue());
	}
}
