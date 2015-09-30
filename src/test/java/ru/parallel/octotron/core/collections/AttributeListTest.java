package ru.parallel.octotron.core.collections;

import org.junit.Before;
import org.junit.Test;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.model.ModelObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AttributeListTest extends GeneralTest
{
	private static ModelObject object;

	@Before
	public void Create()
	{
		AttributeListTest.object = model_service.AddObject();
	}

	@Test
	public void TestAdd()
	{
		AttributeList<Attribute> list = new AttributeList<>();

		assertEquals("list is no empty", list.size(), 0);
		object.GetBuilder().DeclareConst("test", 0);
		list.add(AttributeListTest.object.GetAttribute("test"));
		assertEquals("list has no elements", list.size(), 1);

		Attribute elem = list.get(0);
		assertEquals("add not worked correctly", elem.eq((0)), true);
	}

	@Test
	public void TestGet()
	{
		AttributeList<Attribute> list = new AttributeList<>();

		object.GetBuilder().DeclareConst("test1", 0);
		list.add(AttributeListTest.object.GetAttribute("test1"));
		object.GetBuilder().DeclareConst("test2", 1.0);
		list.add(AttributeListTest.object.GetAttribute("test2"));
		object.GetBuilder().DeclareConst("test3", "test");
		list.add(AttributeListTest.object.GetAttribute("test3"));

		assertEquals(list.get(0).eq(0), true);
		assertEquals(list.get(1).eq(1.0), true);
		assertEquals(list.get(2).eq("test"), true);
	}

	@Test
	public void TestIterate()
	{
		AttributeList<Attribute> list = new AttributeList<>();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			object.GetBuilder().DeclareConst("test" + i, i);
			list.add(AttributeListTest.object.GetAttribute("test" + i));
		}

		int i = 0;
		for(Attribute att : list)
		{
			assertEquals(att.eq(i), true);
			i++;
		}
	}

	@Test
	public void TestSize()
	{
		AttributeList<Attribute> list = new AttributeList<>();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			object.GetBuilder().DeclareConst("test" + i, i);
			list.add(AttributeListTest.object.GetAttribute("test" + i));
			assertEquals(list.size(), i + 1);
		}
	}

	@Test
	public void TestOps()
	{
		AttributeList<Attribute> list = new AttributeList<>();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			object.GetBuilder().DeclareConst("test" + i, i);
			list.add(AttributeListTest.object.GetAttribute("test" + i));
		}

		AttributeList<Attribute> le = list.le(5);
		AttributeList<Attribute> lt = list.lt(5);
		AttributeList<Attribute> ge = list.ge(5);
		AttributeList<Attribute> gt = list.gt(5);
		AttributeList<Attribute> eq = list.eq(5);
		AttributeList<Attribute> ne = list.ne(5);

		assertEquals("le size failed", le.size(), 6);
		assertEquals("lt size failed", lt.size(), 5);
		assertEquals("ge size failed", ge.size(), 5);
		assertEquals("gt size failed", gt.size(), 4);
		assertEquals("eq size failed", eq.size(), 1);
		assertEquals("ne size failed", ne.size(), 9);

		for(Attribute att : le)
			assertEquals("le failed", att.le(5), true);

		for(Attribute att : lt)
			assertEquals("lt failed", att.lt(5), true);

		for(Attribute att : ge)
			assertEquals("ge failed", att.ge(5), true);

		for(Attribute att : gt)
			assertEquals("gt failed", att.gt(5), true);

		for(Attribute att : eq)
			assertEquals("eq failed", att.eq(5), true);

		for(Attribute att : ne)
			assertEquals("ne failed", att.ne(5), true);
	}

	@Test
	public void TestSort()
	{
		AttributeList<Attribute> list = new AttributeList<>();

		object.GetBuilder().DeclareConst("b", "");
		list.add(AttributeListTest.object.GetAttribute("b"));
		object.GetBuilder().DeclareConst("c", "");
		list.add(AttributeListTest.object.GetAttribute("c"));
		object.GetBuilder().DeclareConst("a", "");
		list.add(AttributeListTest.object.GetAttribute("a"));

		list = list.AlphabeticSort();

		assertEquals("a", list.get(0).GetName());
		assertEquals("b", list.get(1).GetName());
		assertEquals("c", list.get(2).GetName());
	}

	@Test
	public void TestFrom()
	{
		object.GetBuilder().DeclareConst("c", "");
		object.GetBuilder().DeclareSensor("s", 0, "");

		AttributeList<Attribute> attributes = new AttributeList<>();

		attributes.add(object.GetConst("c"));
		attributes.add(object.GetSensor("s"));

		assertEquals(AttributeList.From(), new AttributeList<Attribute>());
		assertNotEquals(AttributeList.From(object.GetSensor("s")), new AttributeList<Attribute>());
		assertEquals(AttributeList.From(object.GetConst("c"), object.GetSensor("s")), attributes);
	}
}