package ru.parallel.octotron.core;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;

public class TestNode extends Assert
{
	static Neo4jGraph graph;
	static ObjectFactory obj_factory;
	static LinkFactory link_factory;
	private static GraphService graph_service;

	@BeforeClass
	public static void Init()
		throws ExceptionSystemError
	{
		TestNode.graph = new Neo4jGraph("dbs/test_node", Neo4jGraph.Op.RECREATE);
		TestNode.graph_service = new GraphService(TestNode.graph);

		SimpleAttribute[] obj_att = {
			new SimpleAttribute("object", "ok")
		};

		TestNode.obj_factory = new ObjectFactory(TestNode.graph_service).Attributes(obj_att);

		SimpleAttribute[] link_att = {
			new SimpleAttribute("link", "ok"),
			new SimpleAttribute("type", "contain"),
		};

		TestNode.link_factory = new LinkFactory(TestNode.graph_service).Attributes(link_att);
	}

	@AfterClass
	public static void Delete()
	{
		TestNode.graph.Shutdown();
		try
		{
			TestNode.graph.Delete();
		}
		catch (ExceptionSystemError e)
		{
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * check in links
	 * currently checks only if count matches, it is not correct
	 * */
	@Test
	public void GetInLinks()
	{
		int N = 10;

		OctoObject node = TestNode.obj_factory.Create();

		TestNode.link_factory.EveryToOne(TestNode.obj_factory.Create(N), node);

		Assert.assertEquals("in links not match, any type"
			, node.GetInLinks().size(), N);

		Assert.assertEquals("in links not match, exact type"
			, node.GetInLinks().Filter("link").size(), N);

		Assert.assertEquals("in links not match, exact type and value"
			, node.GetInLinks().Filter("link", "ok").size(), N);

		Assert.assertEquals("in links not match, exact type, wrong value"
			, node.GetInLinks().Filter("link", "fail").size(), 0);

		Assert.assertEquals("in links not match, wrong type, wrong value"
			, node.GetInLinks().Filter("fail", "fail").size(), 0);
	}

	@Test
	public void GetOutLinks()
	{
		int N = 10;

		OctoObject node = TestNode.obj_factory.Create();

		TestNode.link_factory.OneToEvery(node, TestNode.obj_factory.Create(N));

		Assert.assertEquals("out links not match, any type"
			, node.GetOutLinks().size(), N);

		Assert.assertEquals("out links not match, exact type"
			, node.GetOutLinks().Filter("link").size(), N);

		Assert.assertEquals("out links not match, exact type and value"
			, node.GetOutLinks().Filter("link", "ok").size(), N);

		Assert.assertEquals("out links not match, exact type, wrong value"
			, node.GetOutLinks().Filter("link", "fail").size(), 0);

		Assert.assertEquals("out links not match, wrong type, wrong value"
			, node.GetOutLinks().Filter("fail", "fail").size(), 0);
	}

	/**
	 * check different neighbors
	 * currently checks only if count matches, it is not correct
	 * do not check links
	 * */
	@Test
	public void GetInNeighbors()
	{
		int N = 10;

		OctoObject node = TestNode.obj_factory.Create();

		TestNode.link_factory.EveryToOne(TestNode.obj_factory.Create(N), node);

		Assert.assertEquals("in neighbors not match, any type"
			, node.GetInNeighbors()
				.size(), N);

		Assert.assertEquals("in neighbors not match, exact type"
			, node.GetInNeighbors()
				.Filter("object").size(), N);

		Assert.assertEquals("in neighbors not match, exact type and value"
			, node.GetInNeighbors()
				.Filter("object", "ok").size(), N);

		Assert.assertEquals("in neighbors not match, exact type, wrong value"
			, node.GetInNeighbors()
				.Filter("object", "fail").size(), 0);

		Assert.assertEquals("in neighbors not match, wrong type, wrong value"
			, node.GetInNeighbors()
				.Filter("fail", "fail").size(), 0);
	}

	@Test
	public void GetOutNeighbors()
	{
		int N = 10;

		OctoObject node = TestNode.obj_factory.Create();

		TestNode.link_factory.OneToEvery(node, TestNode.obj_factory.Create(N));

		Assert.assertEquals("in neighbors not match, any type"
			, node.GetOutNeighbors()
				.size(), N);

		Assert.assertEquals("in neighbors not match, exact type"
			, node.GetOutNeighbors()
				.Filter("object").size(), N);

		Assert.assertEquals("in neighbors not match, exact type and value"
			, node.GetOutNeighbors()
				.Filter("object", "ok").size(), N);

		Assert.assertEquals("in neighbors not match, exact type, wrong value"
			, node.GetOutNeighbors()
				.Filter("object", "fail").size(), 0);

		Assert.assertEquals("in neighbors not match, wrong type, wrong value"
			, node.GetOutNeighbors()
				.Filter("fail", "fail").size(), 0);

	}

/**
 * set different attributes for the object and check if
 * we can get correct values
 * */
	@Test
	public void GetAttribute()
	{
		OctoObject node = TestNode.obj_factory.Create();

		node.DeclareAttribute("test_long", 1);
		node.DeclareAttribute("test_str", "a");

		node.DeclareAttribute("test_double", 1.0);
		node.DeclareAttribute("test_bool", true);

		Assert.assertEquals("int attribute for object"
			, node.GetAttribute("test_long").GetLong(), Long.valueOf(1));
		Assert.assertEquals("int attribute for object"
			, node.GetAttribute("test_str").GetString(), "a");

		Assert.assertEquals("int attribute for object"
			, node.GetAttribute("test_double").GetDouble(), 1.0, 0.1);
		Assert.assertEquals("int attribute for object"
			, node.GetAttribute("test_bool").GetBoolean(), true);
	}

/**
 * set attribute, remove it and ensure it does not exists
 * */
	@Test
	public void RemoveAttribute()
	{
		OctoObject node = TestNode.obj_factory.Create();

		node.DeclareAttribute("test_test", 1);
		node.RemoveAttribute("test_test");

		Assert.assertEquals("attribute presents - wrong"
			, node.TestAttribute("test_test"), false);
	}

/**
 * test if attribute set and remove works with test
 * */
	@Test
	public void TestAttribute()
	{
		OctoObject node = TestNode.obj_factory.Create();

		node.DeclareAttribute("test_test", 1);
		Assert.assertEquals("attribute not presents - wrong"
			, node.TestAttribute("test_test"), true);

		node.RemoveAttribute("test_test");
		Assert.assertEquals("attribute presents - wrong"
			, node.TestAttribute("test_test"), false);
	}
}
