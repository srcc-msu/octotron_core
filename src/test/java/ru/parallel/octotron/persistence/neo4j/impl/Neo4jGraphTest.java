package ru.parallel.octotron.persistence.neo4j.impl;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.primitive.Info;
import ru.parallel.octotron.persistence.graph.EGraphType;
import ru.parallel.octotron.persistence.neo4j.Neo4jGraph;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class Neo4jGraphTest
{
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		Neo4jGraphTest.graph = new Neo4jGraph( "dbs/" + Neo4jGraphTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		Neo4jGraphTest.graph.Shutdown();
		Neo4jGraphTest.graph.Delete();
	}

	/**
	 * add some element to the graph, save/load it and check that the element
	 * still exists
	 * */
	@Test
	public void Load() throws Exception
	{
		Info<EGraphType> obj1;

		obj1 = Neo4jGraphTest.graph.AddObject();
		Neo4jGraphTest.graph.SetObjectAttribute(obj1, "test", 1);

		// in our case Save() consist of shutting down db and then loading it, so no need in Load() call.
		Neo4jGraphTest.graph.Save();

		String name = Neo4jGraphTest.graph.GetObjectAttributes(obj1).get(0);
		assertEquals("test", name);
		assertEquals(1, Neo4jGraphTest.graph.GetObjectAttribute(obj1, name));
	}

	/**
	 * set attributes of different types for 2 links and 2 objects and
	 * check that you can get back correct values
	 * */
	@Test
	public void GetAttribute()
	{
		Info<EGraphType> obj1, obj2, link1, link2;

		// first create two objects
		obj1 = Neo4jGraphTest.graph.AddObject();
		obj2 = Neo4jGraphTest.graph.AddObject();

		boolean catched = false;

		try
		{
			Neo4jGraphTest.graph.GetTransaction().ForceWrite();
			Neo4jGraphTest.graph.SetObjectAttribute(obj1, "test_null", null);
		}
		catch(Exception e)
		{
			catched = true;
		}
		Neo4jGraphTest.graph.GetTransaction().Fail(); // it will crash otherwise

		assertEquals("succeeded to set a null value", catched, true);

		// then create two links and set attributes
		link1 = Neo4jGraphTest.graph.AddLink(obj1, obj2, "contain");
		link2 = Neo4jGraphTest.graph.AddLink(obj2, obj1, "contain");

		// set attributes
		Neo4jGraphTest.graph.SetObjectAttribute(obj1, "test_int", 1);
		Neo4jGraphTest.graph.SetObjectAttribute(obj1, "test_str", "a");

		Neo4jGraphTest.graph.SetObjectAttribute(obj2, "test_double", 1.0);
		Neo4jGraphTest.graph.SetObjectAttribute(obj2, "test_bool", true);

		Neo4jGraphTest.graph.SetLinkAttribute(link1, "test_int", 1);
		Neo4jGraphTest.graph.SetLinkAttribute(link1, "test_str", "a");

		Neo4jGraphTest.graph.SetLinkAttribute(link2, "test_double", 1.0);
		Neo4jGraphTest.graph.SetLinkAttribute(link2, "test_bool", true);

// check they exist
		assertEquals("int attribute for object"
			, Neo4jGraphTest.graph.GetObjectAttribute(obj1, "test_int"), 1);
		assertEquals("str attribute for object"
			, Neo4jGraphTest.graph.GetObjectAttribute(obj1, "test_str"), "a");

		assertEquals("float attribute for object"
			, (double) Neo4jGraphTest.graph.GetObjectAttribute(obj2, "test_double"), 1.0, 0.1);
		assertEquals("bool attribute for object"
			, Neo4jGraphTest.graph.GetObjectAttribute(obj2, "test_bool"), true);

		assertEquals("int attribute for link"
			, Neo4jGraphTest.graph.GetLinkAttribute(link1, "test_int"), 1);
		assertEquals("str attribute for link"
			, Neo4jGraphTest.graph.GetLinkAttribute(link1, "test_str"), "a");

		assertEquals("double attribute for link"
			, (double) Neo4jGraphTest.graph.GetLinkAttribute(link2, "test_double"), 1.0, 1.0);
		assertEquals("bool attribute for link"
			, Neo4jGraphTest.graph.GetLinkAttribute(link2, "test_bool"), true);
	}

	private static final int ITER = 10;

	@Test
	public void RelOrder()
	{
		Info<EGraphType> obj1, obj2;

		obj1 = Neo4jGraphTest.graph.AddObject();
		obj2 = Neo4jGraphTest.graph.AddObject();

		for(int i = 0; i < Neo4jGraphTest.ITER; i++)
		{
			Info<EGraphType> link = Neo4jGraphTest.graph.AddLink(obj1, obj2, "contain");
			Neo4jGraphTest.graph.SetLinkAttribute(link, "n", i);
		}

		List<Info<EGraphType>> links = Neo4jGraphTest.graph.GetInLinks(obj2);

		for(int i = 0; i < Neo4jGraphTest.ITER; i++)
		{
			assertEquals("wrong edges"
				, Neo4jGraphTest.graph.GetLinkAttribute(links.get(i), "n"), i);
		}
	}

	@Test
	public void GetOutLinks()
	{
		Info<EGraphType> obj1, obj2, link1, link2, link_useless;

		obj1 = Neo4jGraphTest.graph.AddObject();
		obj2 = Neo4jGraphTest.graph.AddObject();

		link1 = Neo4jGraphTest.graph.AddLink(obj1
			, obj2, "contain");
		link2 = Neo4jGraphTest.graph.AddLink(obj1
			, obj2, "contain");

		link_useless = Neo4jGraphTest.graph.AddLink(obj2
			, obj1, "contain");

		Neo4jGraphTest.graph.SetLinkAttribute(link1, "test_uniq", "i am 1");
		Neo4jGraphTest.graph.SetLinkAttribute(link2, "test_uniq", "i am 2");

		Neo4jGraphTest.graph.SetLinkAttribute(link_useless, "test_uniq", "i am useless");

		List<Info<EGraphType>> links = Neo4jGraphTest.graph.GetOutLinks(obj1); // obj1

		assertEquals("number of edges do not match", links.size(), 2);

		assertEquals("wrong edges"
			, Neo4jGraphTest.graph.GetLinkAttribute(links.get(0), "test_uniq"), "i am 1");
		assertEquals("wrong edges"
			, Neo4jGraphTest.graph.GetLinkAttribute(links.get(1), "test_uniq"), "i am 2");
	}

	/**
	 *
	 * */
	@Test
	public void GetInLinks()
	{
		Info<EGraphType> obj1, obj2, link1, link2, link_useless;

		obj1 = Neo4jGraphTest.graph.AddObject();
		obj2 = Neo4jGraphTest.graph.AddObject();

		link1 = Neo4jGraphTest.graph.AddLink(obj1
			, obj2, "contain");
		link2 = Neo4jGraphTest.graph.AddLink(obj1
			, obj2, "contain");

		link_useless = Neo4jGraphTest.graph.AddLink(obj2
			, obj1, "contain");

		Neo4jGraphTest.graph.SetLinkAttribute(link1, "test_uniq", "i am 1");
		Neo4jGraphTest.graph.SetLinkAttribute(link2, "test_uniq", "i am 2");

		Neo4jGraphTest.graph.SetLinkAttribute(link_useless, "test_uniq", "i am useless");

		List<Info<EGraphType>> links = Neo4jGraphTest.graph.GetInLinks(obj2); // obj2

		assertEquals("number of edges do not match", links.size(), 2);

		List<String> check = new LinkedList<>();
		check.add((String)Neo4jGraphTest.graph.GetLinkAttribute(links.get(0), "test_uniq"));
		check.add((String)Neo4jGraphTest.graph.GetLinkAttribute(links.get(1), "test_uniq"));

		assertTrue("wrong edges", check.contains("i am 1"));
		assertTrue("wrong edges", check.contains("i am 2"));
	}

	/**
	 * add object and check if it was added successfully
	 * */
	@Test
	public void GetObject()
	{
		Info<EGraphType> obj1 = Neo4jGraphTest.graph.AddObject();

		Neo4jGraphTest.graph.SetObjectAttribute(obj1, "test", "ok");

		assertEquals("element changed after addition"
			, Neo4jGraphTest.graph.GetObjectAttribute(obj1, "test"), "ok");
	}

	/**
	 * add object and check if it was added successfully
	 * */
	@Test
	public void GetLink()
	{
		Info<EGraphType> obj1, obj2, link;

		obj1 = Neo4jGraphTest.graph.AddObject();
		obj2 = Neo4jGraphTest.graph.AddObject();

		link = Neo4jGraphTest.graph.AddLink(obj1
			, obj2, "contain");

		Neo4jGraphTest.graph.SetLinkAttribute(link, "test", "ok");

		assertEquals("link changed after addition"
			, Neo4jGraphTest.graph.GetLinkAttribute(link, "test"), "ok");
	}

	@Test
	public void TestLabel()
	{
		Info<EGraphType> obj1, obj2;

		obj1 = Neo4jGraphTest.graph.AddObject();
		obj2 = Neo4jGraphTest.graph.AddObject();

		graph.AddNodeLabel(obj1, "L1");
		graph.AddNodeLabel(obj2, "L2");

		assertTrue(graph.TestNodeLabel(obj1, "L1"));
		assertFalse(graph.TestNodeLabel(obj1, "L2"));

		assertFalse(graph.TestNodeLabel(obj2, "L1"));
		assertTrue(graph.TestNodeLabel(obj2, "L2"));

		assertEquals(1, graph.GetAllLabeledNodes("L1").size());
		assertEquals(obj1, graph.GetAllLabeledNodes("L1").get(0));

		assertEquals(1, graph.GetAllLabeledNodes("L2").size());
		assertEquals(obj2, graph.GetAllLabeledNodes("L2").get(0));
	}
}