/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.exec.services.ModelService;
import ru.parallel.octotron.core.primitive.EDependencyType;

import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.rules.AggregateLongSum;

public class SelfTest
{
	private long test_iteration = 0;

	ModelObject obj1;
	ModelObject obj2;

	public void Init(ModelService model_service)
	{
		obj1 = model_service.AddObject();
		obj1.GetBuilder(model_service).DeclareConst("type", "_selftest");
		obj1.GetBuilder(model_service).DeclareSensor("test_iteration", -1, 0L);

		obj2 = model_service.AddObject();
		obj2.GetBuilder(model_service).DeclareConst("type", "_selftest");
		obj2.GetBuilder(model_service).DeclareVar("check", new AggregateLongSum(EDependencyType.ALL, "test_iteration"));

		obj1.GetSensor("test_iteration").GetBuilder(model_service).AddDependant(obj2.GetVar("check"));

		model_service.AddLink(obj1, obj2);
	}

	public boolean Test(ExecutionController controller)
	{
		test_iteration++;

		controller.update_service.ImmediateImport(obj1, "test_iteration", new Value(test_iteration));

		return obj2.GetAttribute("check").eq(new Value(test_iteration));
	}
}
