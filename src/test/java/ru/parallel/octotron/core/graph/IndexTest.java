package ru.parallel.octotron.core.graph;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.graph.collections.LinkList;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import static org.junit.Assert.assertEquals;

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
		GraphService.Init(graph);

		GraphService.Get().EnableObjectIndex("att_obj");
		GraphService.Get().EnableLinkIndex("att_link");

		SimpleAttribute[] obj_att = {
			new SimpleAttribute("object", "ok"),
			new SimpleAttribute("att_obj", "value12345")
		};

		IndexTest.obj_factory = new ObjectFactory().Constants(obj_att);

		SimpleAttribute[] link_att = {
			new SimpleAttribute("type", "contain"),
			new SimpleAttribute("att_link", "value23456")
		};

		IndexTest.link_factory = new LinkFactory().Constants(link_att);
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
		GraphService.Get().Clean();
	}

	@Test
	public void TestExactMatchLink() throws Exception
	{
		int N = 10;

		ObjectList objs = IndexTest.obj_factory.Create(N);
		IndexTest.link_factory.EveryToEvery(objs, objs);

		LinkList found = GraphService.Get().GetLinks("att_link", "value23456");
		LinkList not_found = GraphService.Get().GetLinks("att_link", "aa");

		assertEquals(N, found.size());
		assertEquals(0, not_found.size());
	}

	@Test
	public void TestExactMatchObj() throws Exception
	{
		int N = 11;

		IndexTest.obj_factory.Create(N);

		ObjectList found = GraphService.Get().GetObjects("att_obj", "value12345");
		ObjectList not_found = GraphService.Get().GetObjects("att_obj", "aa");

		assertEquals(N, found.size());
		assertEquals(0, not_found.size());
	}

	@Test
	public void TestAllMatchLink() throws Exception
	{
		int N = 12;

		ObjectList objs = IndexTest.obj_factory.Create(N);
		IndexTest.link_factory.EveryToEvery(objs, objs);

		LinkList found = GraphService.Get().GetLinks("att_link");
		LinkList not_found = GraphService.Get().GetLinks("wrong");

		assertEquals(N, found.size());
		assertEquals(0, not_found.size());
	}

	@Test
	public void TestAllMatchObj() throws Exception
	{
		int N = 13;

		IndexTest.obj_factory.Create(N);

		ObjectList found = GraphService.Get().GetObjects("att_obj");
		ObjectList not_found = GraphService.Get().GetObjects("wrong");

		assertEquals(N, found.size());
		assertEquals(0, not_found.size());
	}

	@Test
	public void TestPartialMatchLink() throws Exception
	{
		int N = 14;

		ObjectList objs = IndexTest.obj_factory.Create(N);
		IndexTest.link_factory.EveryToEvery(objs, objs);

		LinkList found = GraphService.Get().QueryLinks("att_link", "*lue234*");

		LinkList not_found = GraphService.Get().QueryLinks("att_link", "f*lue234*");

		assertEquals(N, found.size());
		assertEquals(0, not_found.size());
	}

	@Test
	public void TestPartialMatchObj() throws Exception
	{
		int N = 15;

		IndexTest.obj_factory.Create(N);

		ObjectList found = GraphService.Get().QueryObjects("att_obj", "*lue123*");
		ObjectList not_found = GraphService.Get().QueryObjects("att_obj", "f*lue123*");

		assertEquals(N, found.size());
		assertEquals(0, not_found.size());
	}

	@Test
	public void TestSingleLink() throws Exception
	{
		ObjectList<ModelObject, ModelLink> objs = IndexTest.obj_factory.Create(2);
		IndexTest.link_factory.OneToOne(objs.get(0), objs.get(1));

		GraphService.Get().GetLink("att_link", "value23456");

		boolean catched = false;
// this must throw
		try
		{
			GraphService.Get().GetLink("att_link", "gfgf");
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
			GraphService.Get().GetLink("att_link", "value23456");
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

		GraphService.Get().GetObject("att_obj", "value12345");

		boolean catched = false;

// this must throw
		try
		{
			GraphService.Get().GetObject("att_obj", "gfgf");
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
			GraphService.Get().GetObject("att_obj", "value12345");
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}

		assertEquals(true, catched);
	}
}
