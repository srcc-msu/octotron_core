package test.java;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.core.OctoAttribute;
import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import main.java.ru.parallel.octotron.utils.AttributeList;

public class TestAttrList extends Assert
{
	static GraphService graph_service;
	static Neo4jGraph graph;
	static OctoObject static_obj;

	@BeforeClass
	public static void Init()
	{
		try
		{
			graph = new Neo4jGraph("dbs/test_neo4j", Neo4jGraph.Op.RECREATE);
			graph_service = new GraphService(graph);
			static_obj = graph_service.AddObject();
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
		catch(ExceptionSystemError e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void Add()
	{
		AttributeList list = new AttributeList();

		assertEquals("list is no empty", list.size(), 0);
		list.add(graph_service.SetAttribute(static_obj, "test", 0, 0));
		assertEquals("list has no elements", list.size(), 1);

		OctoAttribute elem = list.get(0);
		assertEquals("add not worked correctly", elem.eq(0), true);
	}

	@Test
	public void Get()
	{
		AttributeList list = new AttributeList();

		list.add(graph_service.SetAttribute(static_obj, "test1", 0, 0));
		list.add(graph_service.SetAttribute(static_obj, "test2", 1.0, 0));
		list.add(graph_service.SetAttribute(static_obj, "test3", "test", 0));

		assertEquals("got something wrong", list.get(0).eq(0), true);
		assertEquals("got something wrong", list.get(1).eq(1.0), true);
		assertEquals("got something wrong", list.get(2).eq("test"), true);
	}

	@Test
	public void Iterate()
	{
		AttributeList list = new AttributeList();

		int N = 10;

		for(int i = 0; i < N; i++)
			list.add(graph_service.SetAttribute(static_obj, "test", i, 0));

		int i = 0;
		for(OctoAttribute att : list)
		{
			assertEquals("got something wrong", att.eq(i), true);
			i++;
		}
	}

	@Test
	public void Size()
	{
		AttributeList list = new AttributeList();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list.add(graph_service.SetAttribute(static_obj, "test", i, 0));
			assertEquals("got something wrong", list.size(), i + 1);
		}
	}

	@Test
	public void Ops()
	{
		AttributeList list = new AttributeList();

		int N = 10;

		for(int i = 0; i < N; i++)
			list.add(graph_service.SetAttribute(static_obj, "test", i, 0));

		AttributeList le = list.le(5);
		AttributeList lt = list.lt(5);
		AttributeList ge = list.ge(5);
		AttributeList gt = list.gt(5);
		AttributeList eq = list.eq(5);
		AttributeList ne = list.ne(5);

		assertEquals("le size failed", le.size(), 6);
		assertEquals("lt size failed", lt.size(), 5);
		assertEquals("ge size failed", ge.size(), 5);
		assertEquals("gt size failed", gt.size(), 4);
		assertEquals("eq size failed", eq.size(), 1);
		assertEquals("ne size failed", ne.size(), 9);

		for(OctoAttribute att : le)
			assertEquals("le failed", att.le(5), true);

		for(OctoAttribute att : lt)
			assertEquals("lt failed", att.lt(5), true);

		for(OctoAttribute att : ge)
			assertEquals("ge failed", att.ge(5), true);

		for(OctoAttribute att : gt)
			assertEquals("gt failed", att.gt(5), true);

		for(OctoAttribute att : eq)
			assertEquals("eq failed", att.eq(5), true);

		for(OctoAttribute att : ne)
			assertEquals("ne failed", att.ne(5), true);
	}

	@Test
	public void Append()
	{
		AttributeList list1 = new AttributeList();
		AttributeList list2 = new AttributeList();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list1.add(graph_service.SetAttribute(static_obj, "test", i, 0));
			list2.add(graph_service.SetAttribute(static_obj, "test", N + i, 0));
		}

		list1.append(list2);

		assertEquals("got something wrong", list1.size(), N * 2);

		int i = 0;
		for(OctoAttribute att : list1)
		{
			assertEquals("got something wrong", att.eq(i), true);
			i++;
		}
	}
}
