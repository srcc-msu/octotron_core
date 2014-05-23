package ru.parallel.octotron.utils;

import org.junit.*;
import static org.junit.Assert.*;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.core.OctoAttribute;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;

public class OctoAttributeListTest
{
	private static GraphService graph_service;
	private static Neo4jGraph graph;
	private static OctoObject static_obj;

	@BeforeClass
	public static void Init()
		throws ExceptionSystemError
	{
		OctoAttributeListTest.graph = new Neo4jGraph( "dbs/" + OctoAttributeListTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		OctoAttributeListTest.graph_service = new GraphService(OctoAttributeListTest.graph);
	}

	@AfterClass
	public static void Delete()
		throws ExceptionSystemError
	{
		OctoAttributeListTest.graph.Shutdown();
		OctoAttributeListTest.graph.Delete();
	}

	@Before
	public void Create()
	{
		OctoAttributeListTest.static_obj = OctoAttributeListTest.graph_service.AddObject();
	}

	@After
	public void Clean()
	{
		OctoAttributeListTest.graph_service.Clean();
		OctoAttributeListTest.static_obj = null;
	}

	@Test
	public void TestAdd()
	{
		OctoAttributeList list = new OctoAttributeList();

		assertEquals("list is no empty", list.size(), 0);
		list.add(OctoAttributeListTest.static_obj.DeclareAttribute("test", 0));
		assertEquals("list has no elements", list.size(), 1);

		OctoAttribute elem = list.get(0);
		assertEquals("add not worked correctly", elem.eq(0), true);
	}

	@Test
	public void TestGet()
	{
		OctoAttributeList list = new OctoAttributeList();

		list.add(OctoAttributeListTest.static_obj.DeclareAttribute("test1", 0));
		list.add(OctoAttributeListTest.static_obj.DeclareAttribute("test2", 1.0));
		list.add(OctoAttributeListTest.static_obj.DeclareAttribute("test3", "test"));

		assertEquals("got something wrong", list.get(0).eq(0), true);
		assertEquals("got something wrong", list.get(1).eq(1.0), true);
		assertEquals("got something wrong", list.get(2).eq("test"), true);
	}

	@Test
	public void TestIterate()
	{
		OctoAttributeList list = new OctoAttributeList();

		int N = 10;

		for(int i = 0; i < N; i++)
			list.add(OctoAttributeListTest.static_obj.DeclareAttribute("test" + i, i));

		int i = 0;
		for(OctoAttribute att : list)
		{
			assertEquals("got something wrong", att.eq(i), true);
			i++;
		}
	}

	@Test
	public void TestSize()
	{
		OctoAttributeList list = new OctoAttributeList();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list.add(OctoAttributeListTest.static_obj.DeclareAttribute("test" + i, i));
			assertEquals("got something wrong", list.size(), i + 1);
		}
	}

	@Test
	public void TestOps()
	{
		OctoAttributeList list = new OctoAttributeList();

		int N = 10;

		for(int i = 0; i < N; i++)
			list.add(OctoAttributeListTest.static_obj.DeclareAttribute("test" + i, i));

		OctoAttributeList le = list.le(5);
		OctoAttributeList lt = list.lt(5);
		OctoAttributeList ge = list.ge(5);
		OctoAttributeList gt = list.gt(5);
		OctoAttributeList eq = list.eq(5);
		OctoAttributeList ne = list.ne(5);

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
	public void TestSort()
	{
		OctoAttributeList list = new OctoAttributeList();

		list.add(OctoAttributeListTest.static_obj.DeclareAttribute("b", ""));
		list.add(OctoAttributeListTest.static_obj.DeclareAttribute("c", ""));
		list.add(OctoAttributeListTest.static_obj.DeclareAttribute("a", ""));

		list = list.AlphabeticSort();

		assertEquals("a", list.get(0).GetName());
		assertEquals("b", list.get(1).GetName());
		assertEquals("c", list.get(2).GetName());
	}
}