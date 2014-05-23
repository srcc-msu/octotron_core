package ru.parallel.octotron.core;

import org.junit.*;
import static org.junit.Assert.*;

import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.utils.OctoLinkList;
import ru.parallel.octotron.utils.OctoObjectList;

public class IndexTest
{
	private static Neo4jGraph graph;
	private static GraphService graph_service;

	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		IndexTest.graph = new Neo4jGraph( "dbs/" + IndexTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		graph_service = new GraphService(IndexTest.graph);

		IndexTest.graph_service.EnableObjectIndex("att_obj");
		IndexTest.graph_service.EnableLinkIndex("att_link");

		SimpleAttribute[] obj_att = {
			new SimpleAttribute("object", "ok"),
			new SimpleAttribute("att_obj", "value12345")
		};

		IndexTest.obj_factory = new ObjectFactory(graph_service).Attributes(obj_att);

		SimpleAttribute[] link_att = {
			new SimpleAttribute("type", "contain"),
			new SimpleAttribute("att_link", "value23456")
		};

		IndexTest.link_factory = new LinkFactory(graph_service).Attributes(link_att);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		IndexTest.graph.Shutdown();
		IndexTest.graph.Delete();
	}

	@After
	public void Clean() throws Exception
	{
		IndexTest.graph_service.Clean();
	}

	@Test
	public void TestExactMatchLink() throws Exception
	{
		int N = 10;

		OctoObjectList objs = IndexTest.obj_factory.Create(N);
		IndexTest.link_factory.EveryToEvery(objs, objs);

		OctoLinkList found = IndexTest.graph_service.GetLinks("att_link", "value23456");
		OctoLinkList not_found = IndexTest.graph_service.GetLinks("att_link", "aa");

		assertEquals(N, found.size());
		assertEquals(0, not_found.size());
	}

	@Test
	public void TestExactMatchObj() throws Exception
	{
		int N = 11;

		IndexTest.obj_factory.Create(N);

		OctoObjectList found = IndexTest.graph_service.GetObjects("att_obj", "value12345");
		OctoObjectList not_found = IndexTest.graph_service.GetObjects("att_obj", "aa");

		assertEquals(N, found.size());
		assertEquals(0, not_found.size());
	}

	@Test
	public void TestAllMatchLink() throws Exception
	{
		int N = 12;

		OctoObjectList objs = IndexTest.obj_factory.Create(N);
		IndexTest.link_factory.EveryToEvery(objs, objs);

		OctoLinkList found = IndexTest.graph_service.GetLinks("att_link");
		OctoLinkList not_found = IndexTest.graph_service.GetLinks("wrong");

		assertEquals(N, found.size());
		assertEquals(0, not_found.size());
	}

	@Test
	public void TestAllMatchObj() throws Exception
	{
		int N = 13;

		IndexTest.obj_factory.Create(N);

		OctoObjectList found = IndexTest.graph_service.GetObjects("att_obj");
		OctoObjectList not_found = IndexTest.graph_service.GetObjects("wrong");

		assertEquals(N, found.size());
		assertEquals(0, not_found.size());
	}

	@Test
	public void TestPartialMatchLink() throws Exception
	{
		int N = 14;

		OctoObjectList objs = IndexTest.obj_factory.Create(N);
		IndexTest.link_factory.EveryToEvery(objs, objs);

		OctoLinkList found = IndexTest.graph_service.QueryLinks("att_link", "*lue234*");

		OctoLinkList not_found = IndexTest.graph_service.QueryLinks("att_link", "f*lue234*");

		assertEquals(N, found.size());
		assertEquals(0, not_found.size());
	}

	@Test
	public void TestPartialMatchObj() throws Exception
	{
		int N = 15;

		IndexTest.obj_factory.Create(N);

		OctoObjectList found = IndexTest.graph_service.QueryObjects("att_obj", "*lue123*");
		OctoObjectList not_found = IndexTest.graph_service.QueryObjects("att_obj", "f*lue123*");

		assertEquals(N, found.size());
		assertEquals(0, not_found.size());
	}

	@Test
	public void TestSingleLink() throws Exception
	{
		OctoObjectList objs = IndexTest.obj_factory.Create(2);
		IndexTest.link_factory.OneToOne(objs.get(0), objs.get(1));

		IndexTest.graph_service.GetLink("att_link", "value23456");

		boolean catched = false;
// this must throw
		try
		{
			IndexTest.graph_service.GetLink("att_link", "gfgf");
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}
		assertEquals(true, catched);

		IndexTest.link_factory.OneToOne(objs.get(1), objs.get(0));
		catched = false;

// this must throw - two possible links
		try
		{
			IndexTest.graph_service.GetLink("att_link", "value23456");
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}
		assertEquals(true, catched);
	}

	@Test
	public void TestSingleObj() throws Exception
	{
		IndexTest.obj_factory.Create(1);

		IndexTest.graph_service.GetObject("att_obj", "value12345");

		boolean catched = false;

// this must throw
		try
		{
			IndexTest.graph_service.GetObject("att_obj", "gfgf");
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}
		assertEquals(true, catched);

		IndexTest.obj_factory.Create(1);
		catched = false;

// this must throw - two possible objects
		try
		{
			IndexTest.graph_service.GetObject("att_obj", "value12345");
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}

		assertEquals(true, catched);
	}
}
