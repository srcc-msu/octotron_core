package test.java;

import java.util.List;



import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.Uid;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;

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
			TestNeo4jGraph.graph = new Neo4jGraph("dbs/test_neo4j", Neo4jGraph.Op.RECREATE);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@AfterClass
	public static void Delete()
	{
		TestNeo4jGraph.graph.Shutdown();
		try
		{
			TestNeo4jGraph.graph.Delete();
		}
		catch (ExceptionSystemError e)
		{
			Assert.fail(e.getMessage());
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

		obj1 = TestNeo4jGraph.graph.AddObject();

		// in our case Save() consist of shutting down db and then loading it, so no need in Load() call.
		TestNeo4jGraph.graph.Save();
		TestNeo4jGraph.graph.GetObjectAttributes(obj1);

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
		obj1 = TestNeo4jGraph.graph.AddObject();
		obj2 = TestNeo4jGraph.graph.AddObject();

		boolean catched = false;

		try
		{
			TestNeo4jGraph.graph.SetObjectAttribute(obj1, "test_null", null);
		}
		catch(Exception e)
		{
			// this must fail - is it ok?
			catched = true;
		}

		Assert.assertEquals("succeeded to set a null value", catched, true);

		// then create two links and set attributes
		link1 = TestNeo4jGraph.graph.AddLink(obj1, obj2, "contain");
		link2 = TestNeo4jGraph.graph.AddLink(obj2, obj1, "contain");

		// set attributes
		TestNeo4jGraph.graph.SetObjectAttribute(obj1, "test_int", 1);
		TestNeo4jGraph.graph.SetObjectAttribute(obj1, "test_str", "a");

		TestNeo4jGraph.graph.SetObjectAttribute(obj2, "test_double", 1.0);
		TestNeo4jGraph.graph.SetObjectAttribute(obj2, "test_bool", true);

		TestNeo4jGraph.graph.SetLinkAttribute(link1, "test_int", 1);
		TestNeo4jGraph.graph.SetLinkAttribute(link1, "test_str", "a");

		TestNeo4jGraph.graph.SetLinkAttribute(link2, "test_double", 1.0);
		TestNeo4jGraph.graph.SetLinkAttribute(link2, "test_bool", true);

// check they exist
		Assert.assertEquals("int attribute for object"
			, TestNeo4jGraph.graph.GetObjectAttribute(obj1, "test_int"), 1);
		Assert.assertEquals("str attribute for object"
			, TestNeo4jGraph.graph.GetObjectAttribute(obj1, "test_str"), "a");

		Assert.assertEquals("float attribute for object"
			, (double) TestNeo4jGraph.graph.GetObjectAttribute(obj2, "test_double"), 1.0, 0.1);
		Assert.assertEquals("bool attribute for object"
			, TestNeo4jGraph.graph.GetObjectAttribute(obj2, "test_bool"), true);

		Assert.assertEquals("int attribute for link"
			, TestNeo4jGraph.graph.GetLinkAttribute(link1, "test_int"), 1);
		Assert.assertEquals("str attribute for link"
			, TestNeo4jGraph.graph.GetLinkAttribute(link1, "test_str"), "a");

		Assert.assertEquals("double attribute for link"
			, (double) TestNeo4jGraph.graph.GetLinkAttribute(link2, "test_double"), 1.0, 1.0);
		Assert.assertEquals("bool attribute for link"
			, TestNeo4jGraph.graph.GetLinkAttribute(link2, "test_bool"), true);
	}

	private static final int ITER = 10;

@Test
	public void RelOrder()
	{
		Uid obj1, obj2;

		obj1 = TestNeo4jGraph.graph.AddObject();
		obj2 = TestNeo4jGraph.graph.AddObject();

		for(int i = 0; i < TestNeo4jGraph.ITER; i++)
		{
			Uid link = TestNeo4jGraph.graph.AddLink(obj1, obj2, "contain");
			TestNeo4jGraph.graph.SetLinkAttribute(link, "n", i);
		}

		List<Uid> links = TestNeo4jGraph.graph.GetInLinks(obj2);

		for(int i = 0; i < TestNeo4jGraph.ITER; i++)
		{
			Assert.assertEquals("wrong edges"
				, TestNeo4jGraph.graph.GetLinkAttribute(links.get(i), "n"), i);
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

		obj1 = TestNeo4jGraph.graph.AddObject();
		obj2 = TestNeo4jGraph.graph.AddObject();

		link1 = TestNeo4jGraph.graph.AddLink(obj1
				, obj2, "contain");
		link2 = TestNeo4jGraph.graph.AddLink(obj1
				, obj2, "contain");

		link_useless = TestNeo4jGraph.graph.AddLink(obj2
				, obj1, "contain");

		TestNeo4jGraph.graph.SetLinkAttribute(link1, "test_uniq", "i am 1");
		TestNeo4jGraph.graph.SetLinkAttribute(link2, "test_uniq", "i am 2");

		TestNeo4jGraph.graph.SetLinkAttribute(link_useless, "test_uniq", "i am useless");

		List<Uid> links = TestNeo4jGraph.graph.GetOutLinks(obj1); // obj1

		Assert.assertEquals("number of edges do not match", links.size(), 2);

		Assert.assertEquals("wrong edges"
			, TestNeo4jGraph.graph.GetLinkAttribute(links.get(0), "test_uniq"), "i am 1");
		Assert.assertEquals("wrong edges"
			, TestNeo4jGraph.graph.GetLinkAttribute(links.get(1), "test_uniq"), "i am 2");
	}

/**
 *
 * */
	@Test
	public void GetInLinks()
	{
		Uid obj1, obj2, link1, link2, link_useless;

		obj1 = TestNeo4jGraph.graph.AddObject();
		obj2 = TestNeo4jGraph.graph.AddObject();

		link1 = TestNeo4jGraph.graph.AddLink(obj1
				, obj2, "contain");
		link2 = TestNeo4jGraph.graph.AddLink(obj1
				, obj2, "contain");

		link_useless = TestNeo4jGraph.graph.AddLink(obj2
				, obj1, "contain");

		TestNeo4jGraph.graph.SetLinkAttribute(link1, "test_uniq", "i am 1");
		TestNeo4jGraph.graph.SetLinkAttribute(link2, "test_uniq", "i am 2");

		TestNeo4jGraph.graph.SetLinkAttribute(link_useless, "test_uniq", "i am useless");

		List<Uid> links = TestNeo4jGraph.graph.GetInLinks(obj2); // obj2

		Assert.assertEquals("number of edges do not match", links.size(), 2);

 //order ??
		Assert.assertEquals("wrong edges"
			, TestNeo4jGraph.graph.GetLinkAttribute(links.get(0), "test_uniq")
			, "i am 1");
		Assert.assertEquals("wrong edges"
			, TestNeo4jGraph.graph.GetLinkAttribute(links.get(1), "test_uniq")
			, "i am 2");
	}

/**
 * add object and check if it was added successfully
 * */
	@Test
	public void GetObject()
	{
		Uid obj1 = null;

		obj1 = TestNeo4jGraph.graph.AddObject();
		TestNeo4jGraph.graph.SetObjectAttribute(obj1, "test", "ok");

		Assert.assertEquals("element changed after addition"
			, TestNeo4jGraph.graph.GetObjectAttribute(obj1, "test"), "ok");
	}

/**
 * add object and check if it was added successfully
 * */
	@Test
	public void GetLink()
	{
		Uid obj1, obj2, link;

		obj1 = TestNeo4jGraph.graph.AddObject();
		obj2 = TestNeo4jGraph.graph.AddObject();

		link = TestNeo4jGraph.graph.AddLink(obj1
				, obj2, "contain");

		TestNeo4jGraph.graph.SetLinkAttribute(link, "test", "ok");

		Assert.assertEquals("link changed after addition"
			, TestNeo4jGraph.graph.GetLinkAttribute(link, "test"), "ok");
	}
}
