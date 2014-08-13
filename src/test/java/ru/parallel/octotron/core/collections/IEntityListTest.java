package ru.parallel.octotron.core.collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IEntityListTest
{
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		IEntityListTest.graph = new Neo4jGraph( "dbs/" + IEntityListTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphService.Init(IEntityListTest.graph);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		IEntityListTest.graph.Shutdown();
		IEntityListTest.graph.Delete();
	}

	@Test
	public void testRange() throws Exception
	{
		IEntityList<GraphEntity> list = new EntityList<>() ;

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
	public void testRanges() throws Exception
	{
		IEntityList<GraphEntity> list = new EntityList<>() ;

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
	public void testFilter() throws Exception
	{
		IEntityList<GraphEntity> list = new EntityList<>() ;

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

		assertEquals(4, list.Filter("entity", 5, IEntityList.EQueryType.GT).size());
		assertEquals(12, list.Filter("entity", 4, IEntityList.EQueryType.LT).size());

		assertEquals(3, list.Filter("entity", 3, IEntityList.EQueryType.EQ).size());

		assertEquals(17, list.Filter("entity", 3, IEntityList.EQueryType.NE).size());
		assertEquals(19, list.Filter("entity", 7, IEntityList.EQueryType.NE).size());
	}

	@Test
	public void testUniq() throws Exception
	{
		IEntityList<GraphEntity> list = new EntityList<>() ;

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
	public void testAdd() throws Exception
	{
		IEntityList<GraphEntity> list = new EntityList<>() ;

		assertEquals("list is not empty", list.size(), 0);

		list.add(GraphService.Get().AddObject());
		assertEquals("list has no elements", list.size(), 1);

		list.add(GraphService.Get().AddLink(GraphService.Get().AddObject(), GraphService.Get().AddObject(), "test"));
		assertEquals("list has not get 2nd element", list.size(), 2);

		assertNotNull("add not worked correctly", list.get(0));
	}

	@Test
	public void testGet() throws Exception
	{
		IEntityList<GraphEntity> list = new EntityList<>() ;

		list.add(GraphService.Get().AddObject());
		list.add(GraphService.Get().AddLink(GraphService.Get().AddObject(), GraphService.Get().AddObject(), "test"));
		list.add(GraphService.Get().AddObject());

		assertNotNull("got something wrong", list.get(0));
		assertNotNull("got something wrong", list.get(1));
		assertNotNull("got something wrong", list.get(2));
	}

	@Test
	public void testSize() throws Exception
	{
		IEntityList<GraphEntity> list = new EntityList<>() ;

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list.add(GraphService.Get().AddObject());
			list.add(GraphService.Get().AddLink(GraphService.Get().AddObject(), GraphService.Get().AddObject(), "test"));

			assertEquals("got something wrong", list.size(), (i + 1) * 2);
		}
	}

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testOnly() throws Exception
	{
		IEntityList<GraphEntity> list = new EntityList<>() ;

		list.add(GraphService.Get().AddObject());
		assertNotNull(list.Only());

		list = new EntityList<>();

		exception.expect(ExceptionModelFail.class);
		list.Only();

		list.add(GraphService.Get().AddObject());
		list.add(GraphService.Get().AddObject());

		list.Only();
	}

	@Test
	public void TestIterate()
	{
		IEntityList<GraphEntity> list = new EntityList<>() ;

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