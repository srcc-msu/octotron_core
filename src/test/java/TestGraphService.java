package test.java;

import java.util.List;


import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.core.OctoAttribute;
import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.core.OctoReaction;
import main.java.ru.parallel.octotron.impl.generators.ObjectFactory;
import main.java.ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;

public class TestGraphService extends Assert
{
	static GraphService graph_service;
	static Neo4jGraph graph;

	@BeforeClass
	public static void Init()
	{
		try
		{
			graph = new Neo4jGraph("dbs/test_neo4j", Neo4jGraph.Op.RECREATE);
			graph_service = new GraphService(graph);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@AfterClass
	public static void Delete()
	{
		graph.Shutdown();
		try
		{
			graph.Delete();
		}
		catch (ExceptionSystemError e)
		{
			fail(e.getMessage());
		}
	}

/**
 * add rules and check if they were added successfully
 * */
	@Test
	public void AddRules()
	{
		OctoObject obj = new ObjectFactory(graph_service).Create();

		long r1 = 12;
		long r2 = 13;

		graph_service.AddToArray(obj, GraphService.RULE_PREFIX, r1);
		graph_service.AddToArray(obj, GraphService.RULE_PREFIX, r2);

		List<Long> rules = graph_service.GetArray
			(obj, GraphService.RULE_PREFIX);

		assertEquals("some rules are missing", rules.size(), 2);

		assertEquals("rule rule1 is missing", (long)rules.get(0), r1);
		assertEquals("rule rule2 is missing", (long)rules.get(1), r2);
	}

/**
 * add reactions and check if they were added successfully
 * */
	@Test
	public void AddReaction()
	{
		OctoObject obj = new ObjectFactory(graph_service).Create();

		long r1 = 22;
		long r2 = 23;

		graph_service.AddToArray(obj, GraphService.REACTION_PREFIX
			, r1);
		graph_service.AddToArray(obj, GraphService.REACTION_PREFIX
			, r2);

		List<Long> reactions = graph_service.GetArray
			(obj, GraphService.REACTION_PREFIX);

		assertEquals("some rules are missing", reactions.size(), 2);

		assertEquals("reaction react1 is missing", (long)reactions.get(0), r1);
		assertEquals("reaction react2 is missing", (long)reactions.get(1), r2);
	}

/**
 * execute reactions and check if they were added successfully
 * */
	@Test
	public void ExecuteReaction()
	{
		OctoObject obj = new ObjectFactory(graph_service).Create();

		long react = 33;

		graph_service.AddToArray(obj, GraphService.REACTION_PREFIX
			, react);

		graph_service.SetReactionExecuted(obj, react, OctoReaction.STATE_NONE);
		assertEquals("reaction value did not change"
			, graph_service.IsReactionExecuted(obj, react), OctoReaction.STATE_NONE);

		graph_service.SetReactionExecuted(obj, react, OctoReaction.STATE_STARTED);
		assertEquals("reaction value did not change"
			, graph_service.IsReactionExecuted(obj, react), OctoReaction.STATE_STARTED);

		graph_service.SetReactionExecuted(obj, react, OctoReaction.STATE_EXECUTED);
		assertEquals("reaction value did not change"
			, graph_service.IsReactionExecuted(obj, react), OctoReaction.STATE_EXECUTED);
	}

/**
 * check static attributes
 * */
	@Test
	public void StaticAttr()
	{
		OctoObject obj = new ObjectFactory(graph_service).Create();

		graph_service.DeclareStaticAttribute("_static_test1", 1);

		boolean catched = false;

		try
		{
			obj.SetAttribute("_static_test2", 2);
		}
		catch(ExceptionModelFail e)
		{
			String msg = e.getMessage();

			if(!msg.startsWith("this name"))
				fail("goo some unexpected exception");

			catched = true;
		}

		if(!catched)
			fail("was able to set static manually");

		assertEquals("globaly setted attribute not found"
			, obj.GetAttribute("_static_test1").GetLong(), Long.valueOf(1));
	}

	/**
	 * check static attributes
	 * */
	@Test
	public void InvalidAttr()
	{
		OctoObject obj1 = new ObjectFactory(graph_service).Create();
		OctoObject obj2 = new ObjectFactory(graph_service).Create();

		OctoAttribute attr1 = obj1.SetAttribute("sensor1", 1);
		OctoAttribute attr2 = obj2.SetAttribute("sensor2", 2);

		assertEquals("invalid from begining", true, attr1.IsValid());
		assertEquals("invalid from begining", true, attr2.IsValid());

		attr1.SetValid(false);

		assertEquals("state did not changed", false, attr1.IsValid());
		assertEquals("unexpected state change", true, attr2.IsValid());

		attr2.SetValid(false);

		assertEquals("state did not changed", false, attr2.IsValid());
		assertEquals("unexpected state change", false, attr1.IsValid());

		attr1.SetValid(true);
		assertEquals("state did not change", true, attr1.IsValid());

		attr2.SetValid(true);
		assertEquals("state did not change", true, attr2.IsValid());
	}
}
