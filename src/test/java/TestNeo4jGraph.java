package test.java;

import java.util.List;



import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import main.java.ru.parallel.octotron.primitive.Uid;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;

/**
 * internal test case for Neo4j graph api<br>
 * do NOT use it as sample for coding something octotron-related<br>
 * for logic purpose you should work with graph through octotron.api.* interfaces<br>
 * */
public class TestNeo4jGraph extends Assert
{
	static Neo4jGraph graph;

	@BeforeClass
	public static void Init()
	{
		try
		{
			graph = new Neo4jGraph("dbs/test_neo4j", Neo4jGraph.Op.RECREATE);
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
 * add some element to the graph, save/load it and check that the elelement
 * still exists
 * */
	@Test
	public void Load()
	{
		Uid obj1;

		obj1 = graph.AddObject();

		// in our case Save() consist of shutting down db and then loading it, so no need in Load() call.
		graph.Save();
		graph.GetObjectAttributes(obj1);

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
		obj1 = graph.AddObject();
		obj2 = graph.AddObject();

		boolean catched = false;

		try
		{
			graph.SetObjectAttribute(obj1, "test_null", null);
		}
		catch(Exception e)
		{
			// this must fail - is it ok?
			catched = true;
		}

		assertEquals("succeeded to set a null value", catched, true);

		// then create two links and set attributes
		link1 = graph.AddLink(obj1, obj2, "contain");
		link2 = graph.AddLink(obj2, obj1, "contain");

		// set attributes
		graph.SetObjectAttribute(obj1, "test_int", 1);
		graph.SetObjectAttribute(obj1, "test_str", "a");

		graph.SetObjectAttribute(obj2, "test_double", 1.0);
		graph.SetObjectAttribute(obj2, "test_bool", true);

		graph.SetLinkAttribute(link1, "test_int", 1);
		graph.SetLinkAttribute(link1, "test_str", "a");

		graph.SetLinkAttribute(link2, "test_double", 1.0);
		graph.SetLinkAttribute(link2, "test_bool", true);

// check they exist
		assertEquals("int attribute for object"
			, graph.GetObjectAttribute(obj1, "test_int"), 1);
		assertEquals("str attribute for object"
			, graph.GetObjectAttribute(obj1, "test_str"), "a");

		assertEquals("float attribute for object"
			, (double)graph.GetObjectAttribute(obj2, "test_double"), 1.0, 0.1);
		assertEquals("bool attribute for object"
			, graph.GetObjectAttribute(obj2, "test_bool"), true);

		assertEquals("int attribute for link"
			, graph.GetLinkAttribute(link1, "test_int"), 1);
		assertEquals("str attribute for link"
			, graph.GetLinkAttribute(link1, "test_str"), "a");

		assertEquals("double attribute for link"
			, (double)graph.GetLinkAttribute(link2, "test_double"), 1.0, 1.0);
		assertEquals("bool attribute for link"
			, graph.GetLinkAttribute(link2, "test_bool"), true);
	}

	private static int ITER = 10;

@Test
	public void RelOrder()
	{
		Uid obj1, obj2;

		obj1 = graph.AddObject();
		obj2 = graph.AddObject();

		for(int i = 0; i < ITER; i++)
		{
			Uid link = graph.AddLink(obj1, obj2, "contain");
			graph.SetLinkAttribute(link, "n", i);
		}

		List<Uid> links = graph.GetInLinks(obj2);

		for(int i = 0; i < ITER; i++)
		{
			assertEquals("wrong edges"
				, graph.GetLinkAttribute(links.get(i), "n"), i);
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

		obj1 = graph.AddObject();
		obj2 = graph.AddObject();

		link1 = graph.AddLink(obj1
			, obj2, "contain");
		link2 = graph.AddLink(obj1
				, obj2, "contain");

		link_useless = graph.AddLink(obj2
				, obj1, "contain");

		graph.SetLinkAttribute(link1, "test_uniq", "i am 1");
		graph.SetLinkAttribute(link2, "test_uniq", "i am 2");

		graph.SetLinkAttribute(link_useless, "test_uniq", "i am useless");

		List<Uid> links = graph.GetOutLinks(obj1); // obj1

		assertEquals("number of edges do not match", links.size(), 2);

		assertEquals("wrong edges"
			, graph.GetLinkAttribute(links.get(0), "test_uniq"), "i am 1");
		assertEquals("wrong edges"
			, graph.GetLinkAttribute(links.get(1), "test_uniq"), "i am 2");
	}

/**
 *
 * */
	@Test
	public void GetInLinks()
	{
		Uid obj1, obj2, link1, link2, link_useless;

		obj1 = graph.AddObject();
		obj2 = graph.AddObject();

		link1 = graph.AddLink(obj1
			, obj2, "contain");
		link2 = graph.AddLink(obj1
			, obj2, "contain");

		link_useless = graph.AddLink(obj2
			, obj1, "contain");

		graph.SetLinkAttribute(link1, "test_uniq", "i am 1");
		graph.SetLinkAttribute(link2, "test_uniq", "i am 2");

		graph.SetLinkAttribute(link_useless, "test_uniq", "i am useless");

		List<Uid> links = graph.GetInLinks(obj2); // obj2

		assertEquals("number of edges do not match", links.size(), 2);

 //order ??
		assertEquals("wrong edges"
			, graph.GetLinkAttribute(links.get(0), "test_uniq")
			, "i am 1");
		assertEquals("wrong edges"
			, graph.GetLinkAttribute(links.get(1), "test_uniq")
			, "i am 2");
	}

/**
 * add object and check if it was added successfully
 * */
	@Test
	public void GetObject()
	{
		Uid obj1 = null;

		obj1 = graph.AddObject();
		graph.SetObjectAttribute(obj1, "test", "ok");

		assertEquals("element changed after addition"
			, graph.GetObjectAttribute(obj1, "test"), "ok");
	}

/**
 * add object and check if it was added successfully
 * */
	@Test
	public void GetLink()
	{
		Uid obj1, obj2, link;

		obj1 = graph.AddObject();
		obj2 = graph.AddObject();

		link = graph.AddLink(obj1
			, obj2, "contain");

		graph.SetLinkAttribute(link, "test", "ok");

		assertEquals("link changed after addition"
			, graph.GetLinkAttribute(link, "test"), "ok");
	}
}
