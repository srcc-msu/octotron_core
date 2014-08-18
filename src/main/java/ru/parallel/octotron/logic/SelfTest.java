/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.model.impl.ModelObjectList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.rules.MirrorLong;

public class SelfTest
{
	private final ExecutionController controller;

	private long test_iteration;

	ModelObject obj1;
	ModelObject obj2;

	public SelfTest(ExecutionController controller)
	{
		this.controller = controller;

		test_iteration = 0;
	}

	public void Init()
	{
		ModelObjectList list = ModelService.GetObjects("type", "_selftest");

		if(list.size() == 0)
		{
			obj1 = ModelService.AddObject();
			obj1.DeclareConstant("type", "_selftest");
			obj1.DeclareConstant("lid", 0);
			obj1.DeclareConstant("test_iteration", 0);

			obj2 = ModelService.AddObject();
			obj2.DeclareConstant("type", "_selftest");
			obj2.DeclareConstant("lid", 1);
			obj2.DeclareVarying(new MirrorLong("test_iteration", "lid", 0));

			ModelService.AddLink(obj1, obj2, "test");
		}
		else if(list.size() == 2)
		{
			obj1 = list.Filter("lid", 0).Only();
			obj2 = list.Filter("lid", 1).Only();
			test_iteration = obj1.GetAttribute("test_iteration").GetLong();
		}
		else
			throw new ExceptionModelFail("unexpected self-testing configuration");

		Test(); // update once
	}

	public boolean Test()
	{
		test_iteration++;

		controller.ImmediateImport(obj1, new SimpleAttribute("test_iteration", test_iteration));

		return obj2.GetAttribute("test_iteration").eq(test_iteration);
	}
}
