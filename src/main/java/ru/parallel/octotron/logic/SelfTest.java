/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.rules.AStrictLongSum;
import ru.parallel.octotron.services.ServiceLocator;

public class SelfTest
{
	private long test_iteration = 0;

	ModelObject obj1;
	ModelObject obj2;

	public void Init()
		throws ExceptionParseError
	{
		obj1 = ServiceLocator.INSTANCE.GetModelService().AddObject();
		obj1.GetBuilder().DeclareConst("type", "_selftest");
		obj1.GetBuilder().DeclareSensor("test_iteration", -1, 0L);

		obj2 = ServiceLocator.INSTANCE.GetModelService().AddObject();
		obj2.GetBuilder().DeclareConst("type", "_selftest");
		obj2.GetBuilder().DeclareVar("check", new AStrictLongSum("in_n", "test_iteration"));

		ServiceLocator.INSTANCE.GetModelService().AddLink(obj1, obj2, false);
	}

	public boolean Test()
	{
		test_iteration++;

		obj1.GetSensor("test_iteration").UpdateValue(test_iteration);

		return obj2.GetAttribute("check").eq(new Value(test_iteration));
	}
}
