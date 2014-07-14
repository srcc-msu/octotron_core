package ru.parallel.octotron.core.graph.collections;

import org.junit.*;
import static org.junit.Assert.*;

import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;

public class GraphAttributeListTest
{
	private static GraphService graph_service;
	private static Neo4jGraph graph;
	private static GraphObject static_obj;

	@BeforeClass
	public static void Init()
		throws ExceptionSystemError
	{
		GraphAttributeListTest.graph = new Neo4jGraph( "dbs/" + GraphAttributeListTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphAttributeListTest.graph_service = new GraphService(GraphAttributeListTest.graph);
	}

	@AfterClass
	public static void Delete()
		throws ExceptionSystemError
	{
		GraphAttributeListTest.graph.Shutdown();
		GraphAttributeListTest.graph.Delete();
	}

	@Before
	public void Create()
	{
		GraphAttributeListTest.static_obj = GraphAttributeListTest.graph_service.AddObject();
	}

	@After
	public void Clean()
	{
		GraphAttributeListTest.graph_service.Clean();
		GraphAttributeListTest.static_obj = null;
	}

	@Test
	public void TestAdd()
	{
		AttributeList<GraphAttribute> list = new AttributeList<GraphAttribute>();

		assertEquals("list is no empty", list.size(), 0);
		list.add(GraphAttributeListTest.static_obj.DeclareAttribute("test", 0));
		assertEquals("list has no elements", list.size(), 1);

		GraphAttribute elem = list.get(0);
		assertEquals("add not worked correctly", elem.eq(0), true);
	}

	@Test
	public void TestGet()
	{
		AttributeList<GraphAttribute> list = new AttributeList<GraphAttribute>();

		list.add(GraphAttributeListTest.static_obj.DeclareAttribute("test1", 0));
		list.add(GraphAttributeListTest.static_obj.DeclareAttribute("test2", 1.0));
		list.add(GraphAttributeListTest.static_obj.DeclareAttribute("test3", "test"));

		assertEquals("got something wrong", list.get(0).eq(0), true);
		assertEquals("got something wrong", list.get(1).eq(1.0), true);
		assertEquals("got something wrong", list.get(2).eq("test"), true);
	}

	@Test
	public void TestIterate()
	{
		AttributeList<GraphAttribute> list = new AttributeList<GraphAttribute>();

		int N = 10;

		for(int i = 0; i < N; i++)
			list.add(GraphAttributeListTest.static_obj.DeclareAttribute("test" + i, i));

		int i = 0;
		for(GraphAttribute att : list)
		{
			assertEquals("got something wrong", att.eq(i), true);
			i++;
		}
	}

	@Test
	public void TestSize()
	{
		AttributeList<GraphAttribute> list = new AttributeList<GraphAttribute>();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list.add(GraphAttributeListTest.static_obj.DeclareAttribute("test" + i, i));
			assertEquals("got something wrong", list.size(), i + 1);
		}
	}

	@Test
	public void TestOps()
	{
		AttributeList<GraphAttribute> list = new AttributeList<GraphAttribute>();

		int N = 10;

		for(int i = 0; i < N; i++)
			list.add(GraphAttributeListTest.static_obj.DeclareAttribute("test" + i, i));

		AttributeList<GraphAttribute> le = list.le(5);
		AttributeList<GraphAttribute> lt = list.lt(5);
		AttributeList<GraphAttribute> ge = list.ge(5);
		AttributeList<GraphAttribute> gt = list.gt(5);
		AttributeList<GraphAttribute> eq = list.eq(5);
		AttributeList<GraphAttribute> ne = list.ne(5);

		assertEquals("le size failed", le.size(), 6);
		assertEquals("lt size failed", lt.size(), 5);
		assertEquals("ge size failed", ge.size(), 5);
		assertEquals("gt size failed", gt.size(), 4);
		assertEquals("eq size failed", eq.size(), 1);
		assertEquals("ne size failed", ne.size(), 9);

		for(GraphAttribute att : le)
			assertEquals("le failed", att.le(5), true);

		for(GraphAttribute att : lt)
			assertEquals("lt failed", att.lt(5), true);

		for(GraphAttribute att : ge)
			assertEquals("ge failed", att.ge(5), true);

		for(GraphAttribute att : gt)
			assertEquals("gt failed", att.gt(5), true);

		for(GraphAttribute att : eq)
			assertEquals("eq failed", att.eq(5), true);

		for(GraphAttribute att : ne)
			assertEquals("ne failed", att.ne(5), true);
	}

	@Test
	public void TestSort()
	{
		AttributeList<GraphAttribute> list = new AttributeList<GraphAttribute>();

		list.add(GraphAttributeListTest.static_obj.DeclareAttribute("b", ""));
		list.add(GraphAttributeListTest.static_obj.DeclareAttribute("c", ""));
		list.add(GraphAttributeListTest.static_obj.DeclareAttribute("a", ""));

		list = list.AlphabeticSort();

		assertEquals("a", list.get(0).GetName());
		assertEquals("b", list.get(1).GetName());
		assertEquals("c", list.get(2).GetName());
	}
}