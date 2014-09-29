package ru.parallel.octotron.core.graph.impl;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.parallel.octotron.core.graph.collections.EntityList;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GraphListTest
{
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		GraphListTest.graph = new Neo4jGraph( "dbs/" + GraphListTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphService.Init(GraphListTest.graph);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		GraphListTest.graph.Shutdown();
		GraphListTest.graph.Delete();
	}

	@Test
	public void TestAppend() throws Exception
	{
		GraphList list1 = new GraphList() ;
		GraphList list2 = new GraphList() ;
		GraphList list3;

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list1.add(GraphService.Get().AddObject());
			list2.add(GraphService.Get().AddObject());
		}

		list3 = list1.append(list2);

		assertEquals("got something wrong", list1.size(), N);
		assertEquals("got something wrong", list2.size(), N);
		assertEquals("got something wrong", list3.size(), N * 2);
	}

	@Test
	public void TestRange() throws Exception
	{
		GraphList list = new GraphList() ;

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list.add(GraphService.Get().AddObject());
			list.add(GraphService.Get().AddLink(GraphService.Get().AddObject(), GraphService.Get().AddObject(), "test"));
		}

		assertEquals(N, list.range(0, N).size());
		assertEquals(N/2, list.range(0, N/2).size());
		assertEquals(N/2, list.range(N/2, N).size());

		assertEquals(1, list.range(0, 1).size());
		assertEquals(0, list.range(0, 0).size());
	}

	@Test
	public void TestRanges() throws Exception
	{
		GraphList list = new GraphList() ;

		int N = 12;

		for(int i = 0; i < N; i++)
		{
			list.add(GraphService.Get().AddObject());
			list.add(GraphService.Get().AddLink(GraphService.Get().AddObject(), GraphService.Get().AddObject(), "test"));
		}

		assertEquals(N, list.ranges(0, N/2, N/2, N).size());
		assertEquals(N/2, list.ranges(N/4, N/2, N/2, N/2+N/4).size());
		assertEquals(3, list.ranges(0, 1, 3, 4, 6, 7).size());
	}

	@Test
	public void TestFilter() throws Exception
	{
		GraphList list = new GraphList() ;

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			GraphObject object = GraphService.Get().AddObject();
			GraphLink link = GraphService.Get().AddLink(GraphService.Get().AddObject(), GraphService.Get().AddObject(), "test");

			object.DeclareAttribute("object", i/2);
			object.DeclareAttribute("entity", i/2);
			link.DeclareAttribute("link", i);
			link.DeclareAttribute("entity", i);

			list.add(object);
			list.add(link);
		}

		assertEquals(10, list.Filter("object").size());
		assertEquals(10, list.Filter("link").size());

		assertEquals(20, list.Filter("entity").size());
		assertEquals(3, list.Filter("entity", 1).size());
		assertEquals(1, list.Filter("entity", 7).size());

		assertEquals(4, list.Filter("entity", 5, EntityList.EQueryType.GT).size());
		assertEquals(12, list.Filter("entity", 4, EntityList.EQueryType.LT).size());

		assertEquals(3, list.Filter("entity", 3, EntityList.EQueryType.EQ).size());

		assertEquals(17, list.Filter("entity", 3, EntityList.EQueryType.NE).size());
		assertEquals(19, list.Filter("entity", 7, EntityList.EQueryType.NE).size());
	}

	@Test
	public void TestUniq() throws Exception
	{
		GraphList list = new GraphList() ;

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			GraphObject object = GraphService.Get().AddObject();
			GraphLink link = GraphService.Get().AddLink(GraphService.Get().AddObject(), GraphService.Get().AddObject(), "test");

			list.add(object);
			list.add(object);
			list.add(object);
			list.add(link);
			list.add(link);
		}

		assertEquals(50, list.size());
		assertEquals(20, list.Uniq().size());
	}

	@Test
	public void TestAdd() throws Exception
	{
		GraphList list = new GraphList() ;

		assertEquals("list is not empty", list.size(), 0);

		list.add(GraphService.Get().AddObject());
		assertEquals("list has no elements", list.size(), 1);

		list.add(GraphService.Get().AddLink(GraphService.Get().AddObject(), GraphService.Get().AddObject(), "test"));
		assertEquals("list has not get 2nd element", list.size(), 2);

		assertNotNull("add not worked correctly", list.get(0));
	}

	@Test
	public void TestGet() throws Exception
	{
		GraphList list = new GraphList() ;

		list.add(GraphService.Get().AddObject());
		list.add(GraphService.Get().AddLink(GraphService.Get().AddObject(), GraphService.Get().AddObject(), "test"));
		list.add(GraphService.Get().AddObject());

		assertNotNull("got something wrong", list.get(0));
		assertNotNull("got something wrong", list.get(1));
		assertNotNull("got something wrong", list.get(2));
	}

	@Test
	public void TestSize() throws Exception
	{
		GraphList list = new GraphList() ;

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list.add(GraphService.Get().AddObject());
			list.add(GraphService.Get().AddLink(GraphService.Get().AddObject(), GraphService.Get().AddObject(), "test"));

			assertEquals("got something wrong", list.size(), (i + 1) * 2);
		}
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void TestOnly() throws Exception
	{
		GraphList list = new GraphList() ;

		list.add(GraphService.Get().AddObject());
		assertNotNull(list.Only());

		list = new GraphList();

		exception.expect(ExceptionModelFail.class);
		list.Only();

		list.add(GraphService.Get().AddObject());
		list.add(GraphService.Get().AddObject());

		list.Only();
	}

	@Test
	public void TestIterate()
	{
		GraphList list = new GraphList() ;

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list.add(GraphService.Get().AddObject());
			list.add(GraphService.Get().AddLink(GraphService.Get().AddObject(), GraphService.Get().AddObject(), "test"));
		}

		int i = 0;

		for(GraphEntity entity : list)
			i++;

		assertEquals("got something wrong", N * 2, i);
	}
}