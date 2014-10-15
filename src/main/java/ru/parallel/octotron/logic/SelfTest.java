/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.EDependencyType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.rules.AggregateLongSum;

public class SelfTest
{
	private long test_iteration = 0;

	ModelObject obj1;
	ModelObject obj2;

	public void Init(ExecutionController controller)
	{
		ModelService service = controller.GetContext().model_service;

		obj1 = service.AddObject();
		obj1.GetBuilder(service).DeclareConst("type", "_selftest");
		obj1.GetBuilder(service).DeclareSensor("test_iteration", 0L);

		obj2 = service.AddObject();
		obj2.GetBuilder(service).DeclareConst("type", "_selftest");
		obj2.GetBuilder(service).DeclareVar("check", new AggregateLongSum(EDependencyType.ALL, "test_iteration"));

		service.AddLink(obj1, obj2);
	}

	public boolean Test(ExecutionController controller)
	{
		test_iteration++;

		controller.ImmediateImport(obj1, new SimpleAttribute("test_iteration", test_iteration));

		return obj2.GetAttribute("check").eq(test_iteration);
	}
}
