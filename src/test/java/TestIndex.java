package test.java;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.impl.generators.LinkFactory;
import main.java.ru.parallel.octotron.impl.generators.ObjectFactory;
import main.java.ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import main.java.ru.parallel.octotron.primitive.SimpleAttribute;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import main.java.ru.parallel.octotron.utils.LinkList;
import main.java.ru.parallel.octotron.utils.ObjectList;

public class TestIndex extends Assert
{
	static Neo4jGraph graph;
	static ObjectFactory obj_factory;
	static LinkFactory link_factory;
	private static GraphService graph_service;

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

	@After
	public void Clean()
	{
		try
		{
			graph_service.Clean();
			graph.GetTransaction().ForceWrite();
		}
		catch (Exception e)
		{
			fail(e.getMessage());
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
			graph = new Neo4jGraph("dbs/test_index", Neo4jGraph.Op.RECREATE);
			graph_service = new GraphService(graph);

			SimpleAttribute[] obj_att = new SimpleAttribute[]
			{
				new SimpleAttribute("object", "ok"),
				new SimpleAttribute("att_obj", "value12345")
			};

			obj_factory = new ObjectFactory(graph_service).Attributes(obj_att);

			SimpleAttribute[] link_att = new SimpleAttribute[]
			{
				new SimpleAttribute("type", "contain"),
				new SimpleAttribute("att_link", "value23456")
			};

			link_factory = new LinkFactory(graph_service).Attributes(link_att);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}

		graph.GetIndex().EnableObjectIndex("att_obj");
		graph.GetIndex().EnableLinkIndex("att_link");
	}

	@Test
	public void TestExactMatchLink()
	{
		int N = 10;
		try
		{
			ObjectList objs = obj_factory.Create(N);
			link_factory.EveryToEvery(objs, objs);

			LinkList found = graph_service.GetLinks("att_link", "value23456");
			LinkList not_found = graph_service.GetLinks("att_link", "aa");

			assertEquals("some links missing", found.size(), N);
			assertEquals("excess links found", not_found.size(), 0);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void TestExactMatchObj()
	{
		int N = 11;
		try
		{
			obj_factory.Create(N);

			ObjectList found = graph_service.GetObjects("att_obj", "value12345");
			ObjectList not_found = graph_service.GetObjects("att_obj", "aa");

			assertEquals("some objects missing", found.size(), N);
			assertEquals("excess objects found", not_found.size(), 0);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void TestAllMatchLink()
	{
		int N = 12;
		try
		{
			ObjectList objs = obj_factory.Create(N);
			link_factory.EveryToEvery(objs, objs);

			LinkList found = graph_service.GetLinks("att_link");
			LinkList not_found = graph_service.GetLinks("wrong");

			assertEquals("some links missing", found.size(), N);
			assertEquals("excess links found", not_found.size(), 0);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void TestAllMatchObj()
	{
		int N = 13;
		try
		{
			obj_factory.Create(N);

			ObjectList found = graph_service.GetObjects("att_obj");
			ObjectList not_found = graph_service.GetObjects("wrong");

			assertEquals("some objects missing", found.size(), N);
			assertEquals("excess objects found", not_found.size(), 0);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void TestPartialMatchLink()
	{
		int N = 14;
		try
		{
			ObjectList objs = obj_factory.Create(N);
			link_factory.EveryToEvery(objs, objs);

			LinkList found = graph_service.QueryLinks("att_link", "*lue234*");

			LinkList not_found = graph_service.QueryLinks("att_link", "f*lue234*");

			assertEquals("some links missing", found.size(), N);
			assertEquals("excess links found", not_found.size(), 0);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void TestPartialMatchObj()
	{
		int N = 15;
		try
		{
			obj_factory.Create(N);

			ObjectList found = graph_service.QueryObjects("att_obj", "*lue123*");
			ObjectList not_found = graph_service.QueryObjects("att_obj", "f*lue123*");

			assertEquals("some objects missing", found.size(), N);
			assertEquals("excess objects found", not_found.size(), 0);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void TestSingleLink()
	{
		try
		{
			ObjectList objs = obj_factory.Create(2);
			link_factory.OneToOne(objs.get(0), objs.get(1));

			try
			{
// this must not throw exception
				graph.GetIndex().GetLink("att_link", "value23456");
			}
			catch(ExceptionModelFail e)
			{
				fail(e.getMessage());
			}

			boolean catched = false;
// this must throw
			try
			{
				graph.GetIndex().GetLink("att_link", "gfgf");
			}
			catch(ExceptionModelFail e)
			{
				catched = true;
				//it is ok
			}
			assertEquals("found link that must not present", catched, true);

			link_factory.OneToOne(objs.get(1), objs.get(0));
			catched = false;

// this must throw - two possible links
			try
			{
				graph.GetIndex().GetLink("att_link", "value23456");
			}
			catch(ExceptionModelFail e)
			{
				catched = true;
				//it is ok
			}
			assertEquals("found link that must not present", catched, true);

		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void TestSingleObj()
	{
		try
		{
			obj_factory.Create(1);

			try
			{
// this must not throw exception
				graph.GetIndex().GetObject("att_obj", "value12345");
			}
			catch(ExceptionModelFail e)
			{
				fail(e.getMessage());
			}

			boolean catched = false;

// this must throw
			try
			{
				graph.GetIndex().GetObject("att_obj", "gfgf");
			}
			catch(ExceptionModelFail e)
			{
				catched = true;
				//it is ok
			}
			assertEquals("found link that must not present", catched, true);

			obj_factory.Create(1);
			catched = false;

// this must throw - two possible objects
			try
			{
				graph.GetIndex().GetObject("att_obj", "value12345");
			}
			catch(ExceptionModelFail e)
			{
				catched = true;
				//it is ok
			}

			assertEquals("found link that must not present", catched, true);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
