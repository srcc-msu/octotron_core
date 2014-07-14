package ru.parallel.octotron.neo4j.impl;

import org.junit.*;
import static org.junit.Assert.*;

import ru.parallel.octotron.core.primitive.Uid;

import java.util.LinkedList;
import java.util.List;

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
	 * add some element to the graph, save/load it and check that the elelement
	 * still exists
	 * */
	@Test
	public void Load() throws Exception
	{
		Uid obj1;

		obj1 = Neo4jGraphTest.graph.AddObject();
		Neo4jGraphTest.graph.SetObjectAttribute(obj1, "test", 1);

		// in our case Save() consist of shutting down db and then loading it, so no need in Load() call.
		Neo4jGraphTest.graph.Save();

		assertEquals("test", Neo4jGraphTest.graph.GetObjectAttributes(obj1).get(0).getLeft());
		assertEquals(1, Neo4jGraphTest.graph.GetObjectAttributes(obj1).get(0).getRight());
	}

	/**
	 * set attributes of different types for 2 links and 2 objects and
	 * check that you can get back correct values
	 * */
	@Test
	public void GetAttribute()
	{
		Uid obj1, obj2, link1, link2;

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
		Uid obj1, obj2;

		obj1 = Neo4jGraphTest.graph.AddObject();
		obj2 = Neo4jGraphTest.graph.AddObject();

		for(int i = 0; i < Neo4jGraphTest.ITER; i++)
		{
			Uid link = Neo4jGraphTest.graph.AddLink(obj1, obj2, "contain");
			Neo4jGraphTest.graph.SetLinkAttribute(link, "n", i);
		}

		List<Uid> links = Neo4jGraphTest.graph.GetInLinks(obj2);

		for(int i = 0; i < Neo4jGraphTest.ITER; i++)
		{
			assertEquals("wrong edges"
				, Neo4jGraphTest.graph.GetLinkAttribute(links.get(i), "n"), i);
		}
	}

	/**
	 * add two links from obj1 to obj2 and and check that they are both
	 * returned by {@link Neo4jGraph#GetOutLinks(Uid)}
	 * */
	@Test
	public void GetOutLinks()
	{
		Uid obj1, obj2, link1, link2, link_useless;

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

		List<Uid> links = Neo4jGraphTest.graph.GetOutLinks(obj1); // obj1

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
		Uid obj1, obj2, link1, link2, link_useless;

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

		List<Uid> links = Neo4jGraphTest.graph.GetInLinks(obj2); // obj2

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
		Uid obj1 = Neo4jGraphTest.graph.AddObject();

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
		Uid obj1, obj2, link;

		obj1 = Neo4jGraphTest.graph.AddObject();
		obj2 = Neo4jGraphTest.graph.AddObject();

		link = Neo4jGraphTest.graph.AddLink(obj1
			, obj2, "contain");

		Neo4jGraphTest.graph.SetLinkAttribute(link, "test", "ok");

		assertEquals("link changed after addition"
			, Neo4jGraphTest.graph.GetLinkAttribute(link, "test"), "ok");
	}
}