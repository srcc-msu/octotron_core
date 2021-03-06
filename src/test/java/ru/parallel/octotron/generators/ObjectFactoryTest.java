package ru.parallel.octotron.generators;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.generators.tmpl.ConstTemplate;

import static org.junit.Assert.assertEquals;

public class ObjectFactoryTest extends GeneralTest
{
	private static ObjectFactory obj_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		ObjectFactoryTest.obj_factory = new ObjectFactory()
			.Constants(new ConstTemplate("object", "ok"));
	}

/**
 * check that object factory creates required amount of objects
 * with given property
 * */
	@Test
	public void TestObjectsCreate()
	{
		final int N = 10; // some testing param

		ModelObjectList obj = ObjectFactoryTest.obj_factory.Create(N);
		assertEquals("created more objects", obj.size(), N);

		for(int i = 0; i < N; i++)
		{
			assertEquals("ok", obj.get(i).GetAttribute("object").GetString());
		}
	}

	@Test
	public void TestSequence()
	{
		final int N = 100;
		final int K = 23;

		ModelObjectList objects = ObjectFactoryTest.obj_factory.Create(N);

		Enumerator.Sequence(objects, "test1");

		for(int i = 0; i < N; i++)
			assertEquals(i, (long)objects.get(i).GetAttribute("test1").GetLong());

		Enumerator.Sequence(objects, "test2", K);

		for(int i = 0; i < N; i++)
			assertEquals(i % K, (long)objects.get(i).GetAttribute("test2").GetLong());
	}
}
