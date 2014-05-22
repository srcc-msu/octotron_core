package ru.parallel.octotron.generators;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
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
public class GeneratorsTest extends Assert
{
	private static Neo4jGraph graph;
	private static GraphService graph_service;

	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	static final int N = 10; // some testing param

	@BeforeClass
	public static void Init() throws Exception
	{
		GeneratorsTest.graph = new Neo4jGraph("dbs/test_primitives", Neo4jGraph.Op.RECREATE);
		GeneratorsTest.graph_service = new GraphService(GeneratorsTest.graph);

		SimpleAttribute[] obj_att = new SimpleAttribute[]
		{
			new SimpleAttribute("object", "ok")
		};

		GeneratorsTest.obj_factory = new ObjectFactory(GeneratorsTest.graph_service).Attributes(obj_att);

		SimpleAttribute[] link_att = {
			new SimpleAttribute("type", "contain")
		};

		GeneratorsTest.link_factory = new LinkFactory(GeneratorsTest.graph_service).Attributes(link_att);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		GeneratorsTest.graph.Shutdown();
		GeneratorsTest.graph.Delete();
	}

/**
 * check that object factory creates required amount of objects
 * with given property
 * */
	@Test
	public void TestFactoryAttributes()
	{
		OctoObjectList obj = null;
		OctoLink link = null;

		SimpleAttribute[] attr1 = { new SimpleAttribute("test1", 0) };
		SimpleAttribute[] attr2 = { new SimpleAttribute("test2", 0) };
		SimpleAttribute[] attr3 = { new SimpleAttribute("test3", 0) };

		ObjectFactory f1 = new ObjectFactory(GeneratorsTest.graph_service).Attributes(attr1).Attributes(attr2, attr3);
		obj = f1.Create(1);

		LinkFactory f2 = new LinkFactory(GeneratorsTest.graph_service).Attributes(attr2, attr3).Attributes(attr1);
		link = f2.Attributes(new SimpleAttribute("type", "1")).OneToOne(obj.get(0), obj.get(0));

		Assert.assertEquals("misisng obj attribute", obj.get(0).TestAttribute("test1"), true);
		Assert.assertEquals("misisng obj attribute", obj.get(0).TestAttribute("test2"), true);
		Assert.assertEquals("misisng obj attribute", obj.get(0).TestAttribute("test3"), true);

		Assert.assertEquals("misisng link attribute", link.TestAttribute("test1"), true);
		Assert.assertEquals("misisng link attribute", link.TestAttribute("test2"), true);
		Assert.assertEquals("misisng link attribute", link.TestAttribute("test3"), true);
	}

/**
 * check that object factory creates required amount of objects
 * with given property
 * */
	@Test
	public void TestObjectsCreate()
	{
		OctoObjectList obj = null;

		obj = GeneratorsTest.obj_factory.Create(GeneratorsTest.N);
		Assert.assertEquals("created more objects", obj.size(), GeneratorsTest.N);

		for(int i = 0; i < GeneratorsTest.N; i++)
		{
			Assert.assertEquals("created something wrong"
				, obj.get(i).GetAttribute("object").GetValue(), "ok");
		}
	}

/**
 * check that link factory create correct link
 * */
	@Test
	public void TestLinkCreate()
	{
		OctoObjectList obj = null;

		OctoLink link = null;

		obj = GeneratorsTest.obj_factory.Create(2);

		link = GeneratorsTest.link_factory.OneToOne(obj.get(0), obj.get(1));

		Assert.assertEquals("created something wrong"
			, link.GetAttribute("type").GetString(), "contain");
	}

	/**
	 * check that {@link LinkFactory#OneToOne} creates correct link
	 * */
	@Test
	public void TestConnectorOneToOne()
	{
		OctoObjectList obj = GeneratorsTest.obj_factory.Create(2);

		GeneratorsTest.link_factory.OneToOne(obj.get(0), obj.get(1));

		Assert.assertEquals("link does not exist"
			, obj.get(0).GetOutLinks().Filter("type", "contain").size(), 1);
		Assert.assertEquals("link does not exist"
			, obj.get(1).GetInLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link LinkFactory#OneToEvery} creates correct link
	 * */
	@Test
	public void TestConnectorOneToAll()
	{
		OctoObject obj = GeneratorsTest.obj_factory.Create();
		OctoObjectList objs = GeneratorsTest.obj_factory.Create(GeneratorsTest.N);

		GeneratorsTest.link_factory.OneToEvery(obj, objs);

		Assert.assertEquals("link does not exist"
			, obj.GetOutLinks().Filter("type", "contain").size(), GeneratorsTest.N);

		for(int i = 0; i < GeneratorsTest.N; i++)
			Assert.assertEquals("link does not exist"
				, objs.get(i).GetInLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link LinkFactory#EveryToOne} creates correct link
	 * */
	@Test
	public void TestConnectorAllToOne()
	{
		OctoObject obj = GeneratorsTest.obj_factory.Create();
		OctoObjectList objs = GeneratorsTest.obj_factory.Create(GeneratorsTest.N);

		GeneratorsTest.link_factory.EveryToOne(objs, obj);

		Assert.assertEquals("link does not exist"
			, obj.GetInLinks().Filter("type", "contain").size(), GeneratorsTest.N);

		for(int i = 0; i < GeneratorsTest.N; i++)
			Assert.assertEquals("link does not exist"
				, objs.get(i).GetOutLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link LinkFactory#EveryToEvery} creates correct link
	 * */
	@Test
	public void TestConnectorEveryToEvery()
	{
		OctoObjectList objs1 = GeneratorsTest.obj_factory.Create(GeneratorsTest.N);
		OctoObjectList objs2 = GeneratorsTest.obj_factory.Create(GeneratorsTest.N);

		GeneratorsTest.link_factory.EveryToEvery(objs1, objs2);

		for(int i = 0; i < GeneratorsTest.N; i++)
		{
			Assert.assertEquals("link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 1);

			Assert.assertEquals("link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 1);
		}
	}

	/**
	 * check that {@link LinkFactory#AllToAll} creates correct link
	 * */
	@Test
	public void TestConnectorAllToAll()
	{
		OctoObjectList objs1 = GeneratorsTest.obj_factory.Create(GeneratorsTest.N);
		OctoObjectList objs2 = GeneratorsTest.obj_factory.Create(GeneratorsTest.N);

		GeneratorsTest.link_factory.AllToAll(objs1, objs2);

		for(int i = 0; i < GeneratorsTest.N; i++)
		{
			Assert.assertEquals("link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), GeneratorsTest.N);

			Assert.assertEquals("link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), GeneratorsTest.N);
		}
	}

	/**
	 * check that {@link LinkFactory#EveryToChunks} creates correct link
	 * */
	@Test
	public void TestConnectorAllToChunks()
	{
		OctoObjectList objs1 = GeneratorsTest.obj_factory.Create(GeneratorsTest.N);
		OctoObjectList objs2 = GeneratorsTest.obj_factory.Create(2 * GeneratorsTest.N * GeneratorsTest.N);

		GeneratorsTest.link_factory.EveryToChunks(objs1, objs2);

		for(int i = 0; i < GeneratorsTest.N; i++)
		{
			Assert.assertEquals("link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 2* GeneratorsTest.N);
		}

		for(int i = 0; i < 2* GeneratorsTest.N * GeneratorsTest.N; i++)
		{
			Assert.assertEquals("link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 1);
		}
	}

	/**
	 * check that {@link LinkFactory#ChunksToEvery} creates correct link
	 * */
	@Test
	public void TestConnectorChunksToEvery()
	{
		OctoObjectList objs1 = GeneratorsTest.obj_factory.Create(2 * GeneratorsTest.N * GeneratorsTest.N);
		OctoObjectList objs2 = GeneratorsTest.obj_factory.Create(GeneratorsTest.N);

		GeneratorsTest.link_factory.ChunksToEvery(objs1, objs2);

		for(int i = 0; i < 2* GeneratorsTest.N * GeneratorsTest.N; i++)
			Assert.assertEquals("out link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 1);

		for(int i = 0; i < GeneratorsTest.N; i++)
			Assert.assertEquals("in link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 2* GeneratorsTest.N);
	}

	/**
	 * check that {@link LinkFactory#ChunksToEvery_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorChunksToEvery_LastLess()
	{
		int K = 6;

		OctoObjectList objs1 = GeneratorsTest.obj_factory.Create(2 * GeneratorsTest.N * GeneratorsTest.N - K);
		OctoObjectList objs2 = GeneratorsTest.obj_factory.Create(GeneratorsTest.N);

		GeneratorsTest.link_factory.ChunksToEvery_LastLess(objs1, objs2);

		for(int i = 0; i < 2* GeneratorsTest.N * GeneratorsTest.N -K; i++)
			Assert.assertEquals("out link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 1);

		for(int i = 0; i < GeneratorsTest.N -1; i++)
			Assert.assertEquals("in link does not exist"
					, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 2* GeneratorsTest.N);

		Assert.assertEquals("in link does not exist"
				, objs2.get(GeneratorsTest.N -1).GetInLinks().Filter("type", "contain").size(), 2* GeneratorsTest.N -K);
	}
	/**
	 * check that {@link LinkFactory#EveryToChunks_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorEveryToChunks_LastLess()
	{
		int K = 6;

		OctoObjectList objs1 = GeneratorsTest.obj_factory.Create(GeneratorsTest.N);
		OctoObjectList objs2 = GeneratorsTest.obj_factory.Create(2 * GeneratorsTest.N * GeneratorsTest.N - K);

		GeneratorsTest.link_factory.EveryToChunks_LastLess(objs1, objs2);

		for(int i = 0; i < GeneratorsTest.N -1; i++)
			Assert.assertEquals("link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 2* GeneratorsTest.N);

		Assert.assertEquals("link does not exist"
			, objs1.get(GeneratorsTest.N -1).GetOutLinks().Filter("type", "contain").size(), 2* GeneratorsTest.N - K);

		for(int i = 0; i < 2* GeneratorsTest.N * GeneratorsTest.N - K; i++)
			Assert.assertEquals("link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link LinkFactory#ChunksToEvery_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorEveryToChunks_Guided()
	{
		int[] arr = {1,2,3,4,5,6};

		int sum = 0;
		int len = arr.length;

		for(int i : arr)
			sum += i;

		OctoObjectList objs_from1 = GeneratorsTest.obj_factory.Create(len);
		OctoObjectList objs_from2 = GeneratorsTest.obj_factory.Create(len);
		OctoObjectList objs_from3 = GeneratorsTest.obj_factory.Create(len);

		OctoObjectList objs_to1 = GeneratorsTest.obj_factory.Create(sum);
		OctoObjectList objs_to2 = GeneratorsTest.obj_factory.Create(sum + 1);
		OctoObjectList objs_to3 = GeneratorsTest.obj_factory.Create(sum - 1);

		GeneratorsTest.link_factory.EveryToChunks_Guided(objs_from1, objs_to1, arr);
		GeneratorsTest.link_factory.EveryToChunks_Guided(objs_from2, objs_to2, arr);
		System.err.println("^^ above warning is ok ^^");

		boolean detected = false;
		try
		{
			GeneratorsTest.link_factory.EveryToChunks_Guided(objs_from3, objs_to3, arr);
		}
		catch(ExceptionModelFail e) { detected = true; }

		Assert.assertEquals("wrong size did not throw exception", detected, true);

		for(int i = 0; i < len; i++)
			Assert.assertEquals("out link does not exist"
				, objs_from1.get(i).GetOutLinks().Filter("type", "contain").size(), arr[i]);
	}

	/**
	 * check that {@link LinkFactory#ChunksToEvery_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorChunksToEvery_Guided()
	{
		int[] arr = {1,2,3,4,5,6};

		int sum = 0;
		int len = arr.length;

		for(int i : arr)
			sum += i;

		OctoObjectList objs_from1 = GeneratorsTest.obj_factory.Create(sum);
		OctoObjectList objs_from2 = GeneratorsTest.obj_factory.Create(sum + 1);
		OctoObjectList objs_from3 = GeneratorsTest.obj_factory.Create(sum - 1);

		OctoObjectList objs_to1 = GeneratorsTest.obj_factory.Create(len);
		OctoObjectList objs_to2 = GeneratorsTest.obj_factory.Create(len);
		OctoObjectList objs_to3 = GeneratorsTest.obj_factory.Create(len);

		GeneratorsTest.link_factory.ChunksToEvery_Guided(objs_from1, objs_to1, arr);
		GeneratorsTest.link_factory.ChunksToEvery_Guided(objs_from2, objs_to2, arr);

		System.err.println("^^ above warning is ok ^^");

		boolean detected = false;
		try
		{
			GeneratorsTest.link_factory.ChunksToEvery_Guided(objs_from3, objs_to3, arr);
		}
		catch(ExceptionModelFail e) { detected = true; }

		Assert.assertEquals("wrong size did not throw exception", detected, true);

		for(int i = 0; i < len; i++)
			Assert.assertEquals("out link does not exist"
				, objs_to1.get(i).GetInLinks().Filter("type", "contain").size(), arr[i]);
	}
}
