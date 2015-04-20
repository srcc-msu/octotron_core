package ru.parallel.octotron.generators;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.generators.tmpl.ConstTemplate;

import static org.junit.Assert.assertEquals;

public class LinkFactoryTest extends GeneralTest
{
	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	private static final int N = 10; // some testing param

	@BeforeClass
	public static void Init() throws Exception
	{
		LinkFactoryTest.obj_factory = new ObjectFactory(model_service)
			.Constants(new ConstTemplate("object", "ok"));

		LinkFactoryTest.link_factory = new LinkFactory(model_service)
			.Constants(new ConstTemplate("type", "contain"));
	}

/**
 * check that link factory create correct link
 * */
	@Test
	public void TestLinkCreate()
	{
		ModelObjectList obj = LinkFactoryTest.obj_factory.Create(2);

		ModelLink link = LinkFactoryTest.link_factory.OneToOne(obj.get(0), obj.get(1), true);

		assertEquals("created something wrong"
			, link.GetAttribute("type").GetString(), "contain");
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#OneToOne} creates correct link
	 * */
	@Test
	public void TestConnectorOneToOne()
	{
		ModelObjectList obj = LinkFactoryTest.obj_factory.Create(2);

		LinkFactoryTest.link_factory.OneToOne(obj.get(0), obj.get(1), true);

		assertEquals("link does not exist"
			, obj.get(0).GetOutLinks().Filter("type", "contain").size(), 1);
		assertEquals("link does not exist"
			, obj.get(1).GetInLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#OneToEvery} creates correct link
	 * */
	@Test
	public void TestConnectorOneToAll()
	{
		ModelObject obj = LinkFactoryTest.obj_factory.Create();
		ModelObjectList objects = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);

		LinkFactoryTest.link_factory.OneToEvery(obj, objects, true);

		assertEquals("link does not exist"
			, obj.GetOutLinks().Filter("type", "contain").size(), LinkFactoryTest.N);

		for(int i = 0; i < LinkFactoryTest.N; i++)
			assertEquals("link does not exist"
				, objects.get(i).GetInLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#EveryToOne} creates correct link
	 * */
	@Test
	public void TestConnectorAllToOne()
	{
		ModelObject obj = LinkFactoryTest.obj_factory.Create();
		ModelObjectList objects = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);

		LinkFactoryTest.link_factory.EveryToOne(objects, obj, true);

		assertEquals("link does not exist"
			, obj.GetInLinks().Filter("type", "contain").size(), LinkFactoryTest.N);

		for(int i = 0; i < LinkFactoryTest.N; i++)
			assertEquals("link does not exist"
				, objects.get(i).GetOutLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#EveryToEvery} creates correct link
	 * */
	@Test
	public void TestConnectorEveryToEvery()
	{
		ModelObjectList objects1 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);
		ModelObjectList objects2 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);

		LinkFactoryTest.link_factory.EveryToEvery(objects1, objects2, true);

		for(int i = 0; i < LinkFactoryTest.N; i++)
		{
			assertEquals("link does not exist"
				, objects1.get(i).GetOutLinks().Filter("type", "contain").size(), 1);

			assertEquals("link does not exist"
				, objects2.get(i).GetInLinks().Filter("type", "contain").size(), 1);
		}
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#AllToAll} creates correct link
	 * */
	@Test
	public void TestConnectorAllToAll()
	{
		ModelObjectList objects1 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);
		ModelObjectList objects2 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);

		LinkFactoryTest.link_factory.AllToAll(objects1, objects2, true);

		for(int i = 0; i < LinkFactoryTest.N; i++)
		{
			assertEquals("link does not exist"
				, objects1.get(i).GetOutLinks().Filter("type", "contain").size(), LinkFactoryTest.N);

			assertEquals("link does not exist"
				, objects2.get(i).GetInLinks().Filter("type", "contain").size(), LinkFactoryTest.N);
		}
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#EveryToChunks} creates correct link
	 * */
	@Test
	public void TestConnectorAllToChunks()
	{
		ModelObjectList objects1 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);
		ModelObjectList objects2 = LinkFactoryTest.obj_factory.Create(2 * LinkFactoryTest.N * LinkFactoryTest.N);

		LinkFactoryTest.link_factory.EveryToChunks(objects1, objects2, true);

		for(int i = 0; i < LinkFactoryTest.N; i++)
		{
			assertEquals("link does not exist"
				, objects1.get(i).GetOutLinks().Filter("type", "contain").size(), 2 * LinkFactoryTest.N);
		}

		for(int i = 0; i < 2* LinkFactoryTest.N * LinkFactoryTest.N; i++)
		{
			assertEquals("link does not exist"
				, objects2.get(i).GetInLinks().Filter("type", "contain").size(), 1);
		}
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#ChunksToEvery} creates correct link
	 * */
	@Test
	public void TestConnectorChunksToEvery()
	{
		ModelObjectList objects1 = LinkFactoryTest.obj_factory.Create(2 * LinkFactoryTest.N * LinkFactoryTest.N);
		ModelObjectList objects2 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);

		LinkFactoryTest.link_factory.ChunksToEvery(objects1, objects2, true);

		for(int i = 0; i < 2* LinkFactoryTest.N * LinkFactoryTest.N; i++)
			assertEquals("out link does not exist"
				, objects1.get(i).GetOutLinks().Filter("type", "contain").size(), 1);

		for(int i = 0; i < LinkFactoryTest.N; i++)
			assertEquals("in link does not exist"
				, objects2.get(i).GetInLinks().Filter("type", "contain").size(), 2 * LinkFactoryTest.N);
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#ChunksToEvery_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorChunksToEvery_LastLess()
	{
		int K = 6;

		ModelObjectList objects1 = LinkFactoryTest.obj_factory.Create(2 * LinkFactoryTest.N * LinkFactoryTest.N - K);
		ModelObjectList objects2 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);

		LinkFactoryTest.link_factory.ChunksToEvery_LastLess(objects1, objects2, true);

		for(int i = 0; i < 2* LinkFactoryTest.N * LinkFactoryTest.N -K; i++)
			assertEquals("out link does not exist"
				, objects1.get(i).GetOutLinks().Filter("type", "contain").size(), 1);

		for(int i = 0; i < LinkFactoryTest.N -1; i++)
			assertEquals("in link does not exist"
				, objects2.get(i).GetInLinks().Filter("type", "contain").size(), 2 * LinkFactoryTest.N);

		assertEquals("in link does not exist"
			, objects2.get(LinkFactoryTest.N - 1).GetInLinks().Filter("type", "contain").size(), 2 * LinkFactoryTest.N - K);
	}
	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#EveryToChunks_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorEveryToChunks_LastLess()
	{
		int K = 6;

		ModelObjectList objects1 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);
		ModelObjectList objects2 = LinkFactoryTest.obj_factory.Create(2 * LinkFactoryTest.N * LinkFactoryTest.N - K);

		LinkFactoryTest.link_factory.EveryToChunks_LastLess(objects1, objects2, true);

		for(int i = 0; i < LinkFactoryTest.N -1; i++)
			assertEquals("link does not exist"
				, objects1.get(i).GetOutLinks().Filter("type", "contain").size(), 2 * LinkFactoryTest.N);

		assertEquals("link does not exist"
			, objects1.get(LinkFactoryTest.N - 1).GetOutLinks().Filter("type", "contain").size(), 2 * LinkFactoryTest.N - K);

		for(int i = 0; i < 2* LinkFactoryTest.N * LinkFactoryTest.N - K; i++)
			assertEquals("link does not exist"
				, objects2.get(i).GetInLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#ChunksToEvery_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorEveryToChunks_Guided()
	{
		int[] arr = {1,2,3,4,5,6};

		int sum = 0;
		int len = arr.length;

		for(int i : arr)
			sum += i;

		ModelObjectList objects_from1 = LinkFactoryTest.obj_factory.Create(len);
		ModelObjectList objects_from2 = LinkFactoryTest.obj_factory.Create(len);
		ModelObjectList objects_from3 = LinkFactoryTest.obj_factory.Create(len);

		ModelObjectList objects_to1 = LinkFactoryTest.obj_factory.Create(sum);
		ModelObjectList objects_to2 = LinkFactoryTest.obj_factory.Create(sum + 1);
		ModelObjectList objects_to3 = LinkFactoryTest.obj_factory.Create(sum - 1);

		LinkFactoryTest.link_factory.EveryToChunks_Guided(objects_from1, objects_to1, true, arr);
		LinkFactoryTest.link_factory.EveryToChunks_Guided(objects_from2, objects_to2, true, arr);
		System.err.println("^^ above warning is ok ^^");

		boolean detected = false;
		try
		{
			LinkFactoryTest.link_factory.EveryToChunks_Guided(objects_from3, objects_to3, true, arr);
		}
		catch(ExceptionModelFail e) { detected = true; }

		assertEquals("wrong size did not throw exception", detected, true);

		for(int i = 0; i < len; i++)
			assertEquals("out link does not exist"
				, objects_from1.get(i).GetOutLinks().Filter("type", "contain").size(), arr[i]);
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#ChunksToEvery_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorChunksToEvery_Guided()
	{
		int[] arr = {1,2,3,4,5,6};

		int sum = 0;
		int len = arr.length;

		for(int i : arr)
			sum += i;

		ModelObjectList objects_from1 = LinkFactoryTest.obj_factory.Create(sum);
		ModelObjectList objects_from2 = LinkFactoryTest.obj_factory.Create(sum + 1);
		ModelObjectList objects_from3 = LinkFactoryTest.obj_factory.Create(sum - 1);

		ModelObjectList objects_to1 = LinkFactoryTest.obj_factory.Create(len);
		ModelObjectList objects_to2 = LinkFactoryTest.obj_factory.Create(len);
		ModelObjectList objects_to3 = LinkFactoryTest.obj_factory.Create(len);

		LinkFactoryTest.link_factory.ChunksToEvery_Guided(objects_from1, objects_to1, true, arr);
		LinkFactoryTest.link_factory.ChunksToEvery_Guided(objects_from2, objects_to2, true, arr);

		System.err.println("^^ above warning is ok ^^");

		boolean detected = false;
		try
		{
			LinkFactoryTest.link_factory.ChunksToEvery_Guided(objects_from3, objects_to3, true, arr);
		}
		catch(ExceptionModelFail e) { detected = true; }

		assertEquals("wrong size did not throw exception", detected, true);

		for(int i = 0; i < len; i++)
			assertEquals("out link does not exist"
				, objects_to1.get(i).GetInLinks().Filter("type", "contain").size(), arr[i]);
	}
}
