package ru.parallel.octotron.core;

import org.junit.*;
import static org.junit.Assert.*;

import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.SimpleAttribute;

public class OctoObjectTest
{
	private static Neo4jGraph graph;
	private static GraphService graph_service;

	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		OctoObjectTest.graph = new Neo4jGraph("dbs/test_node", Neo4jGraph.Op.RECREATE);
		OctoObjectTest.graph_service = new GraphService(OctoObjectTest.graph);

		SimpleAttribute[] obj_att = {
			new SimpleAttribute("object", "ok")
		};

		OctoObjectTest.obj_factory = new ObjectFactory(OctoObjectTest.graph_service).Attributes(obj_att);

		SimpleAttribute[] link_att = {
			new SimpleAttribute("link", "ok"),
			new SimpleAttribute("type", "contain"),
		};

		OctoObjectTest.link_factory = new LinkFactory(OctoObjectTest.graph_service).Attributes(link_att);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		OctoObjectTest.graph.Shutdown();
		OctoObjectTest.graph.Delete();
	}

	/**
	 * check in links
	 * currently checks only if count matches, it is not correct
	 * */
	@Test
	public void TestGetInLinks()
	{
		int N = 10;

		OctoObject node = OctoObjectTest.obj_factory.Create();

		OctoObjectTest.link_factory.EveryToOne(OctoObjectTest.obj_factory.Create(N), node);

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

	@Test
	public void TestGetInNeighborsParam()
	{
		int N = 10;

		OctoObject node = OctoObjectTest.obj_factory.Create();

		OctoObjectTest.link_factory.EveryToOne(OctoObjectTest.obj_factory.Create(N), node);

		assertEquals("in links not match, any type"
			, node.GetInNeighbors().size(), N);

		assertEquals("in links not match, exact type"
			, node.GetInNeighbors("link").size(), N);

		assertEquals("in links not match, exact type and value"
			, node.GetInNeighbors("link", "ok").size(), N);

		assertEquals("in links not match, exact type, wrong value"
			, node.GetInNeighbors("link", "fail").size(), 0);

		assertEquals("in links not match, wrong type, wrong value"
			, node.GetInNeighbors("fail", "fail").size(), 0);
	}

	@Test
	public void TestGetOutNeighborsParam()
	{
		int N = 10;

		OctoObject node = OctoObjectTest.obj_factory.Create();

		OctoObjectTest.link_factory.OneToEvery(node, OctoObjectTest.obj_factory.Create(N));

		assertEquals("in links not match, any type"
			, node.GetOutNeighbors().size(), N);

		assertEquals("in links not match, exact type"
			, node.GetOutNeighbors("link").size(), N);

		assertEquals("in links not match, exact type and value"
			, node.GetOutNeighbors("link", "ok").size(), N);

		assertEquals("in links not match, exact type, wrong value"
			, node.GetOutNeighbors("link", "fail").size(), 0);

		assertEquals("in links not match, wrong type, wrong value"
			, node.GetOutNeighbors("fail", "fail").size(), 0);
	}

	@Test
	public void TestGetOutLinks()
	{
		int N = 10;

		OctoObject node = OctoObjectTest.obj_factory.Create();

		OctoObjectTest.link_factory.OneToEvery(node, OctoObjectTest.obj_factory.Create(N));

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
	public void TestGetInNeighbors()
	{
		int N = 10;

		OctoObject node = OctoObjectTest.obj_factory.Create();

		OctoObjectTest.link_factory.EveryToOne(OctoObjectTest.obj_factory.Create(N), node);

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

	@Test
	public void TestGetOutNeighbors()
	{
		int N = 10;

		OctoObject node = OctoObjectTest.obj_factory.Create();

		OctoObjectTest.link_factory.OneToEvery(node, OctoObjectTest.obj_factory.Create(N));

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

}
