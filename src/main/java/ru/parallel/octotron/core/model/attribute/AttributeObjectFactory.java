package ru.parallel.octotron.core.model.attribute;

import com.sun.istack.internal.Nullable;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

public class AttributeObjectFactory
{
	@Nullable
	public static AttributeObject Obtain(GraphService graph_service, GraphObject object)
	{
		if(object.TestLabel(EObjectLabels.SENSOR.toString()))
			return new SensorObject(graph_service, object);
		else if(object.TestLabel(EObjectLabels.DERIVED.toString()))
			return new DerivedObject(graph_service, object);
		else
			throw new ExceptionModelFail("not an attribute object");
	}
}
