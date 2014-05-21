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

public class IndexTest extends Assert
{
	static ObjectFactory obj_factory;
	static LinkFactory link_factory;

	private static GraphService graph_service;
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		IndexTest.graph = new Neo4jGraph("dbs/test_neo4j", Neo4jGraph.Op.RECREATE);
		IndexTest.graph_service = new GraphService(IndexTest.graph);

		IndexTest.graph.GetIndex().EnableObjectIndex("att_obj");
		IndexTest.graph.GetIndex().EnableLinkIndex("att_link");

		SimpleAttribute[] obj_att = {
			new SimpleAttribute("object", "ok"),
			new SimpleAttribute("att_obj", "value12345")
		};

		IndexTest.obj_factory = new ObjectFactory(IndexTest.graph_service).Attributes(obj_att);

		SimpleAttribute[] link_att = {
			new SimpleAttribute("type", "contain"),
			new SimpleAttribute("att_link", "value23456")
		};

		IndexTest.link_factory = new LinkFactory(IndexTest.graph_service).Attributes(link_att);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		IndexTest.graph.Shutdown();
		try
		{
			IndexTest.graph.Delete();
		}
		catch (ExceptionSystemError e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@After
	public void Clean() throws Exception
	{
		graph_service.Clean();
	}

	@Test
	public void TestExactMatchLink() throws Exception
	{
		int N = 10;

		ObjectList objs = IndexTest.obj_factory.Create(N);
		IndexTest.link_factory.EveryToEvery(objs, objs);

		LinkList found = IndexTest.graph_service.GetLinks("att_link", "value23456");
		LinkList not_found = IndexTest.graph_service.GetLinks("att_link", "aa");

		Assert.assertEquals(N, found.size());
		Assert.assertEquals(0, not_found.size());
	}

	@Test
	public void TestExactMatchObj() throws Exception
	{
		int N = 11;

		IndexTest.obj_factory.Create(N);

		ObjectList found = IndexTest.graph_service.GetObjects("att_obj", "value12345");
		ObjectList not_found = IndexTest.graph_service.GetObjects("att_obj", "aa");

		Assert.assertEquals(N, found.size());
		Assert.assertEquals(0, not_found.size());
	}

	@Test
	public void TestAllMatchLink() throws Exception
	{
		int N = 12;

		ObjectList objs = IndexTest.obj_factory.Create(N);
		IndexTest.link_factory.EveryToEvery(objs, objs);

		LinkList found = IndexTest.graph_service.GetLinks("att_link");
		LinkList not_found = IndexTest.graph_service.GetLinks("wrong");

		Assert.assertEquals(N, found.size());
		Assert.assertEquals(0, not_found.size());
	}

	@Test
	public void TestAllMatchObj() throws Exception
	{
		int N = 13;

		IndexTest.obj_factory.Create(N);

		ObjectList found = IndexTest.graph_service.GetObjects("att_obj");
		ObjectList not_found = IndexTest.graph_service.GetObjects("wrong");

		Assert.assertEquals(N, found.size());
		Assert.assertEquals(0, not_found.size());
	}

	@Test
	public void TestPartialMatchLink() throws Exception
	{
		int N = 14;

		ObjectList objs = IndexTest.obj_factory.Create(N);
		IndexTest.link_factory.EveryToEvery(objs, objs);

		LinkList found = IndexTest.graph_service.QueryLinks("att_link", "*lue234*");

		LinkList not_found = IndexTest.graph_service.QueryLinks("att_link", "f*lue234*");

		Assert.assertEquals(N, found.size());
		Assert.assertEquals(0, not_found.size());
	}

	@Test
	public void TestPartialMatchObj() throws Exception
	{
		int N = 15;

		IndexTest.obj_factory.Create(N);

		ObjectList found = IndexTest.graph_service.QueryObjects("att_obj", "*lue123*");
		ObjectList not_found = IndexTest.graph_service.QueryObjects("att_obj", "f*lue123*");

		Assert.assertEquals(N, found.size());
		Assert.assertEquals(0, not_found.size());
	}

	@Test
	public void TestSingleLink() throws Exception
	{
		ObjectList objs = IndexTest.obj_factory.Create(2);
		IndexTest.link_factory.OneToOne(objs.get(0), objs.get(1));

		IndexTest.graph.GetIndex().GetLink("att_link", "value23456");

		boolean catched = false;
// this must throw
		try
		{
			IndexTest.graph.GetIndex().GetLink("att_link", "gfgf");
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}
		Assert.assertEquals(true, catched);

		IndexTest.link_factory.OneToOne(objs.get(1), objs.get(0));
		catched = false;

// this must throw - two possible links
		try
		{
			IndexTest.graph.GetIndex().GetLink("att_link", "value23456");
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}
		Assert.assertEquals(true, catched);
	}

	@Test
	public void TestSingleObj() throws Exception
	{
		IndexTest.obj_factory.Create(1);

		IndexTest.graph.GetIndex().GetObject("att_obj", "value12345");

		boolean catched = false;

// this must throw
		try
		{
			IndexTest.graph.GetIndex().GetObject("att_obj", "gfgf");
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}
		Assert.assertEquals(true, catched);

		IndexTest.obj_factory.Create(1);
		catched = false;

// this must throw - two possible objects
		try
		{
			IndexTest.graph.GetIndex().GetObject("att_obj", "value12345");
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}

		Assert.assertEquals(true, catched);
	}
}
