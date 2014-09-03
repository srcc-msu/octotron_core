package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.model.impl.AttributeDecorator;
import ru.parallel.octotron.core.model.impl.attribute.ConstantAttribute;
import ru.parallel.octotron.core.model.impl.attribute.SensorAttribute;
import ru.parallel.octotron.core.model.impl.attribute.VaryingAttribute;
import ru.parallel.octotron.core.model.impl.meta.SensorObject;
import ru.parallel.octotron.core.model.impl.meta.SensorObjectFactory;
import ru.parallel.octotron.core.model.impl.meta.VaryingObject;
import ru.parallel.octotron.core.model.impl.meta.VaryingObjectFactory;

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
		return new SensorAttribute(GetParent(), GetBaseAttribute()
			, SensorObjectFactory.INSTANCE.Obtain(GetParent().GetBaseEntity(), GetName()));
	}

	public VaryingAttribute ToVarying()
	{
		return new VaryingAttribute(GetParent(), GetBaseAttribute()
			, VaryingObjectFactory.INSTANCE.Obtain(GetParent().GetBaseEntity(), GetName()));
	}

	public IMetaAttribute ToMeta()
	{
		SensorObject sensor_meta = SensorObjectFactory.INSTANCE.TryObtain(GetParent().GetBaseEntity(), GetName());

		if(sensor_meta != null)
			return new SensorAttribute(GetParent(), GetBaseAttribute(), sensor_meta);

		VaryingObject varying_meta = VaryingObjectFactory.INSTANCE.TryObtain(GetParent().GetBaseEntity(), GetName());

		if(varying_meta != null)
			return new VaryingAttribute(GetParent(), GetBaseAttribute(), varying_meta);

		return ToConstant();
	}
}
