package ru.parallel.octotron.generators;

import org.junit.*;
import static org.junit.Assert.*;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.core.OctoLink;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.utils.OctoObjectList;

/**
 * test some common cases -attributes, factories
 * */
public class ObjectFactoryTest
{
	private static Neo4jGraph graph;
	private static GraphService graph_service;

	private static ObjectFactory obj_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		ObjectFactoryTest.graph = new Neo4jGraph( "dbs/"
			+ ObjectFactoryTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		ObjectFactoryTest.graph_service = new GraphService(ObjectFactoryTest.graph);

		ObjectFactoryTest.obj_factory = new ObjectFactory(ObjectFactoryTest.graph_service)
			.Attributes(new SimpleAttribute("object", "ok"));
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		ObjectFactoryTest.graph.Shutdown();
		ObjectFactoryTest.graph.Delete();
	}

/**
 * check that object factory creates required amount of objects
 * with given property
 * */
	@Test
	public void TestObjectsCreate()
	{
		final int N = 10; // some testing param

		OctoObjectList obj = ObjectFactoryTest.obj_factory.Create(N);
		assertEquals("created more objects", obj.size(), N);

		for(int i = 0; i < N; i++)
		{
			assertEquals("created something wrong"
				, obj.get(i).GetAttribute("object").GetValue(), "ok");
		}
	}

	@Test
	public void TestSequence()
	{
		final int N = 100;
		final int K = 23;

		OctoObjectList objects = ObjectFactoryTest.obj_factory.Create(N);

		Enumerator.Sequence(objects, "test1");

		for(int i = 0; i < N; i++)
			assertEquals(i, (long)objects.get(i).GetAttribute("test1").GetLong());

		Enumerator.Sequence(objects, "test2", K);

		for(int i = 0; i < N; i++)
			assertEquals(i % K, (long)objects.get(i).GetAttribute("test2").GetLong());
	}
}