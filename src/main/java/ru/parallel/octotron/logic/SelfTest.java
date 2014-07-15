/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.rules.MirrorLong;

public class SelfTest
{
	private final GraphService graph_service;
	private ModelService model_service;

	private final ExecutionController controller;

	private long test_iteration;

	ModelObject obj1;
	ModelObject obj2;

	public SelfTest(GraphService graph_service, ExecutionController controller)
	{
		this.graph_service = graph_service;
		this.controller = controller;

		test_iteration = 0;
	}

	public void Init()
	{
		ObjectList<ModelObject, ModelLink> list = model_service.GetObjects("type", "_selftest");

		if(list.size() == 0)
		{
			obj1 = model_service.AddObject();
			obj1.DeclareConstant("type", "_selftest");
			obj1.DeclareConstant("lid", 0);
			obj1.DeclareConstant("test_iteration", 0);

			obj2 = model_service.AddObject();
			obj2.DeclareConstant("type", "_selftest");
			obj2.DeclareConstant("lid", 1);
			obj2.AddRule(new MirrorLong("test_iteration", "lid", 0));

			model_service.AddLink(obj1, obj2, "test");
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
