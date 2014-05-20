package ru.parallel.octotron.core;

import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.utils.LinkList;
import ru.parallel.octotron.utils.ObjectList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestIndex extends Assert
{
	static Neo4jGraph graph;
	static ObjectFactory obj_factory;
	static LinkFactory link_factory;
	private static GraphService graph_service;

	@AfterClass
	public static void Delete()
	{
		TestIndex.graph.Shutdown();
		try
		{
			TestIndex.graph.Delete();
		}
		catch (ExceptionSystemError e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@After
	public void Clean()
	{
		try
		{
			TestIndex.graph_service.Clean();
			TestIndex.graph.GetTransaction().ForceWrite();
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

/**
 * it must be called before every test to search in the fresh graph
 * */
	@BeforeClass
	public static void Init()
	{
		try
		{
			TestIndex.graph = new Neo4jGraph("dbs/test_index", Neo4jGraph.Op.RECREATE);
			TestIndex.graph_service = new GraphService(TestIndex.graph);

			SimpleAttribute[] obj_att = {
				new SimpleAttribute("object", "ok"),
				new SimpleAttribute("att_obj", "value12345")
			};

			TestIndex.obj_factory = new ObjectFactory(TestIndex.graph_service).Attributes(obj_att);

			SimpleAttribute[] link_att = {
				new SimpleAttribute("type", "contain"),
				new SimpleAttribute("att_link", "value23456")
			};

			TestIndex.link_factory = new LinkFactory(TestIndex.graph_service).Attributes(link_att);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}

		TestIndex.graph.GetIndex().EnableObjectIndex("att_obj");
		TestIndex.graph.GetIndex().EnableLinkIndex("att_link");
	}

	@Test
	public void TestExactMatchLink()
	{
		int N = 10;
		try
		{
			ObjectList objs = TestIndex.obj_factory.Create(N);
			TestIndex.link_factory.EveryToEvery(objs, objs);

			LinkList found = TestIndex.graph_service.GetLinks("att_link", "value23456");
			LinkList not_found = TestIndex.graph_service.GetLinks("att_link", "aa");

			Assert.assertEquals("some links missing", found.size(), N);
			Assert.assertEquals("excess links found", not_found.size(), 0);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void TestExactMatchObj()
	{
		int N = 11;
		try
		{
			TestIndex.obj_factory.Create(N);

			ObjectList found = TestIndex.graph_service.GetObjects("att_obj", "value12345");
			ObjectList not_found = TestIndex.graph_service.GetObjects("att_obj", "aa");

			Assert.assertEquals("some objects missing", found.size(), N);
			Assert.assertEquals("excess objects found", not_found.size(), 0);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void TestAllMatchLink()
	{
		int N = 12;
		try
		{
			ObjectList objs = TestIndex.obj_factory.Create(N);
			TestIndex.link_factory.EveryToEvery(objs, objs);

			LinkList found = TestIndex.graph_service.GetLinks("att_link");
			LinkList not_found = TestIndex.graph_service.GetLinks("wrong");

			Assert.assertEquals("some links missing", found.size(), N);
			Assert.assertEquals("excess links found", not_found.size(), 0);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void TestAllMatchObj()
	{
		int N = 13;
		try
		{
			TestIndex.obj_factory.Create(N);

			ObjectList found = TestIndex.graph_service.GetObjects("att_obj");
			ObjectList not_found = TestIndex.graph_service.GetObjects("wrong");

			Assert.assertEquals("some objects missing", found.size(), N);
			Assert.assertEquals("excess objects found", not_found.size(), 0);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void TestPartialMatchLink()
	{
		int N = 14;
		try
		{
			ObjectList objs = TestIndex.obj_factory.Create(N);
			TestIndex.link_factory.EveryToEvery(objs, objs);

			LinkList found = TestIndex.graph_service.QueryLinks("att_link", "*lue234*");

			LinkList not_found = TestIndex.graph_service.QueryLinks("att_link", "f*lue234*");

			Assert.assertEquals("some links missing", found.size(), N);
			Assert.assertEquals("excess links found", not_found.size(), 0);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void TestPartialMatchObj()
	{
		int N = 15;
		try
		{
			TestIndex.obj_factory.Create(N);

			ObjectList found = TestIndex.graph_service.QueryObjects("att_obj", "*lue123*");
			ObjectList not_found = TestIndex.graph_service.QueryObjects("att_obj", "f*lue123*");

			Assert.assertEquals("some objects missing", found.size(), N);
			Assert.assertEquals("excess objects found", not_found.size(), 0);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void TestSingleLink()
	{
		ObjectList objs = TestIndex.obj_factory.Create(2);
		TestIndex.link_factory.OneToOne(objs.get(0), objs.get(1));

		TestIndex.graph.GetIndex().GetLink("att_link", "value23456");

		boolean catched = false;
// this must throw
		try
		{
			TestIndex.graph.GetIndex().GetLink("att_link", "gfgf");
		}
		catch(ExceptionModelFail e)
		{
			catched = true;
			//it is ok
		}
		Assert.assertEquals("found link that must not present", catched, true);

		TestIndex.link_factory.OneToOne(objs.get(1), objs.get(0));
		catched = false;

// this must throw - two possible links
		try
		{
			TestIndex.graph.GetIndex().GetLink("att_link", "value23456");
		}
		catch(ExceptionModelFail e)
		{
			catched = true;
			//it is ok
		}
		Assert.assertEquals("found link that must not present", catched, true);
	}

	@Test
	public void TestSingleObj()
	{
		TestIndex.obj_factory.Create(1);

		TestIndex.graph.GetIndex().GetObject("att_obj", "value12345");

		boolean catched = false;

// this must throw
		try
		{
			TestIndex.graph.GetIndex().GetObject("att_obj", "gfgf");
		}
		catch(ExceptionModelFail e)
		{
			catched = true;
			//it is ok
		}
		Assert.assertEquals("found link that must not present", catched, true);

		TestIndex.obj_factory.Create(1);
		catched = false;

// this must throw - two possible objects
		try
		{
			TestIndex.graph.GetIndex().GetObject("att_obj", "value12345");
		}
		catch(ExceptionModelFail e)
		{
			catched = true;
			//it is ok
		}

		Assert.assertEquals("found link that must not present", catched, true);
	}
}
