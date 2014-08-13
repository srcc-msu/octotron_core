package ru.parallel.octotron.core.collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import static org.junit.Assert.assertEquals;

public class EntityListTest
{
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		EntityListTest.graph = new Neo4jGraph( "dbs/" + EntityListTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphService.Init(EntityListTest.graph);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		EntityListTest.graph.Shutdown();
		EntityListTest.graph.Delete();
	}

	@Test
	public void testAppend() throws Exception
	{
		EntityList<GraphEntity> list1 = new EntityList<>() ;
		EntityList<GraphEntity> list2 = new EntityList<>() ;
		EntityList<GraphEntity> list3;

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
}