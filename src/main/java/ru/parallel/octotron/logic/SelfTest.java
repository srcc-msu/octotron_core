/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.rules.MirrorLong;
import ru.parallel.octotron.utils.OctoObjectList;

public class SelfTest
{
	private GraphService graph_service;
	private ExecutionController controller;

	private long test_iteration;

	OctoObject obj1;
	OctoObject obj2;

	public SelfTest(GraphService graph_service, ExecutionController controller)
	{
		this.graph_service = graph_service;
		this.controller = controller;

		test_iteration = 0;
	}

	public void Init()
	{
		OctoObjectList list = graph_service.GetObjects("type", "_selftest");

		if(list.size() == 0)
		{
			obj1 = graph_service.AddObject();
			obj1.DeclareAttribute("type", "_selftest");
			obj1.DeclareAttribute("lid", 0);
			obj1.DeclareAttribute("test_iteration", 0);

			obj2 = graph_service.AddObject();
			obj2.DeclareAttribute("type", "_selftest");
			obj2.DeclareAttribute("lid", 1);
			obj2.AddRule(new MirrorLong("test_iteration", "lid", 0));

			graph_service.AddLink(obj1, obj2, "test");
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

		if (obj2.GetAttribute("test_iteration").ne(test_iteration))
			return false;

		return true;
	}
}
