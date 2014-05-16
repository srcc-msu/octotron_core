package test.java;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.impl.generators.LinkFactory;
import main.java.ru.parallel.octotron.impl.generators.ObjectFactory;
import main.java.ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import main.java.ru.parallel.octotron.primitive.SimpleAttribute;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;

/**
 * check {@link OctoObject} methods
 * */
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
		graph = new Neo4jGraph("dbs/test_node", Neo4jGraph.Op.RECREATE);
		graph_service = new GraphService(graph);

		SimpleAttribute[] obj_att = new SimpleAttribute[]
		{
			new SimpleAttribute("object", "ok")
		};

		obj_factory = new ObjectFactory(graph_service).Attributes(obj_att);

		SimpleAttribute[] link_att = new SimpleAttribute[]
		{
			new SimpleAttribute("link", "ok"),
			new SimpleAttribute("type", "contain"),
		};

		link_factory = new LinkFactory(graph_service).Attributes(link_att);
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
	 * check in links
	 * currently checks only if count matches, it is not correct
	 * */
	@Test
	public void GetInLinks()
	{
		int N = 10;

		OctoObject node = obj_factory.Create();

		link_factory.EveryToOne(obj_factory.Create(N), node);

		assertEquals("in links not match, any type"
			, node.GetInLinks().size(), N);

		assertEquals("in links not match, exact type"
			, node.GetInLinks().Filter("link").size(), N);

		assertEquals("in links not match, exact type and value"
			, node.GetInLinks().Filter("link", "ok").size(), N);

		assertEquals("in links not match, exact type, wrong value"
			, node.GetInLinks().Filter("link", "fail").size(), 0);

		assertEquals("in links not match, wrong type, wrong value"
			, node.GetInLinks().Filter("fail", "fail").size(), 0);
	}

	/**
	 * @see ru.parallel.octotron.test.TestNode#GetInLinks()
	 * */
	@Test
	public void GetOutLinks()
	{
		int N = 10;

		OctoObject node = obj_factory.Create();

		link_factory.OneToEvery(node, obj_factory.Create(N));

		assertEquals("out links not match, any type"
			, node.GetOutLinks().size(), N);

		assertEquals("out links not match, exact type"
			, node.GetOutLinks().Filter("link").size(), N);

		assertEquals("out links not match, exact type and value"
			, node.GetOutLinks().Filter("link", "ok").size(), N);

		assertEquals("out links not match, exact type, wrong value"
			, node.GetOutLinks().Filter("link", "fail").size(), 0);

		assertEquals("out links not match, wrong type, wrong value"
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

		OctoObject node = obj_factory.Create();

		link_factory.EveryToOne(obj_factory.Create(N), node);

		assertEquals("in neighbors not match, any type"
			, node.GetInNeighbors()
				.size(), N);

		assertEquals("in neighbors not match, exact type"
			, node.GetInNeighbors()
				.Filter("object").size(), N);

		assertEquals("in neighbors not match, exact type and value"
			, node.GetInNeighbors()
				.Filter("object", "ok").size(), N);

		assertEquals("in neighbors not match, exact type, wrong value"
			, node.GetInNeighbors()
				.Filter("object", "fail").size(), 0);

		assertEquals("in neighbors not match, wrong type, wrong value"
			, node.GetInNeighbors()
				.Filter("fail", "fail").size(), 0);
	}

	/**
	 * @see ru.parallel.octotron.test.TestNode#GetInNeighbors()
	 * */
	@Test
	public void GetOutNeighbors()
	{
		int N = 10;

		OctoObject node = obj_factory.Create();

		link_factory.OneToEvery(node, obj_factory.Create(N));

		assertEquals("in neighbors not match, any type"
			, node.GetOutNeighbors()
				.size(), N);

		assertEquals("in neighbors not match, exact type"
			, node.GetOutNeighbors()
				.Filter("object").size(), N);

		assertEquals("in neighbors not match, exact type and value"
			, node.GetOutNeighbors()
				.Filter("object", "ok").size(), N);

		assertEquals("in neighbors not match, exact type, wrong value"
			, node.GetOutNeighbors()
				.Filter("object", "fail").size(), 0);

		assertEquals("in neighbors not match, wrong type, wrong value"
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
		OctoObject node = obj_factory.Create();

		node.DeclareAttribute("test_long", 1);
		node.DeclareAttribute("test_str", "a");

		node.DeclareAttribute("test_double", 1.0);
		node.DeclareAttribute("test_bool", true);

		assertEquals("int attribute for object"
			, node.GetAttribute("test_long").GetLong(), Long.valueOf(1));
		assertEquals("int attribute for object"
			, node.GetAttribute("test_str").GetString(), "a");

		assertEquals("int attribute for object"
			, node.GetAttribute("test_double").GetDouble(), 1.0, 0.1);
		assertEquals("int attribute for object"
			, node.GetAttribute("test_bool").GetBoolean(), true);
	}

/**
 * set attribute, remove it and ensure it does not exists
 * */
	@Test
	public void RemoveAttribute()
	{
		OctoObject node = obj_factory.Create();

		node.DeclareAttribute("test_test", 1);
		node.RemoveAttribute("test_test");

		assertEquals("attribute presents - wrong"
			, node.TestAttribute("test_test"), false);
	}

/**
 * test if attribute set and remove works with test
 * */
	@Test
	public void TestAttribute()
	{
		OctoObject node = obj_factory.Create();

		node.DeclareAttribute("test_test", 1);
		assertEquals("attribute not presents - wrong"
			, node.TestAttribute("test_test"), true);

		node.RemoveAttribute("test_test");
		assertEquals("attribute presents - wrong"
			, node.TestAttribute("test_test"), false);
	}
}
