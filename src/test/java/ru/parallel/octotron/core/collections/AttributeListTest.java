package ru.parallel.octotron.core.collections;

import org.junit.*;
import ru.parallel.octotron.core.model.IAttribute;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;

import static org.junit.Assert.assertEquals;

public class AttributeListTest
{
	private static ModelObject object;

	@BeforeClass
	public static void Init() throws Exception
	{
		ModelService.Init(ModelService.EMode.CREATION);
	}

	@Before
	public void Create()
	{
		AttributeListTest.object = ModelService.Get().AddObject();
	}

	@After
	public void Clean()
	{
		AttributeListTest.object = null;
	}

	@Test
	public void TestAdd()
	{
		AttributeList<IAttribute> list = new AttributeList<>();

		assertEquals("list is no empty", list.size(), 0);
		object.GetBuilder().DeclareConst("test", 0);
		list.add(AttributeListTest.object.GetAttribute("test"));
		assertEquals("list has no elements", list.size(), 1);

		IAttribute elem = list.get(0);
		assertEquals("add not worked correctly", elem.eq(0), true);
	}

	@Test
	public void TestGet()
	{
		AttributeList<IAttribute> list = new AttributeList<>();

		object.GetBuilder().DeclareConst("test1", 0);
		list.add(AttributeListTest.object.GetAttribute("test1"));
		object.GetBuilder().DeclareConst("test2", 1.0);
		list.add(AttributeListTest.object.GetAttribute("test2"));
		object.GetBuilder().DeclareConst("test3", "test");
		list.add(AttributeListTest.object.GetAttribute("test3"));

		assertEquals("got something wrong", list.get(0).eq(0), true);
		assertEquals("got something wrong", list.get(1).eq(1.0), true);
		assertEquals("got something wrong", list.get(2).eq("test"), true);
	}

	@Test
	public void TestIterate()
	{
		AttributeList<IAttribute> list = new AttributeList<>();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			object.GetBuilder().DeclareConst("test" + i, i);
			list.add(AttributeListTest.object.GetAttribute("test" + i));
		}

		int i = 0;
		for(IAttribute att : list)
		{
			assertEquals("got something wrong", att.eq(i), true);
			i++;
		}
	}

	@Test
	public void TestSize()
	{
		AttributeList<IAttribute> list = new AttributeList<>();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			object.GetBuilder().DeclareConst("test" + i, i);
			list.add(AttributeListTest.object.GetAttribute("test" + i));
			assertEquals("got something wrong", list.size(), i + 1);
		}
	}

	@Test
	public void TestOps()
	{
		AttributeList<IAttribute> list = new AttributeList<>();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			object.GetBuilder().DeclareConst("test" + i, i);
			list.add(AttributeListTest.object.GetAttribute("test" + i));
		}

		AttributeList<IAttribute> le = list.le(5);
		AttributeList<IAttribute> lt = list.lt(5);
		AttributeList<IAttribute> ge = list.ge(5);
		AttributeList<IAttribute> gt = list.gt(5);
		AttributeList<IAttribute> eq = list.eq(5);
		AttributeList<IAttribute> ne = list.ne(5);

		assertEquals("le size failed", le.size(), 6);
		assertEquals("lt size failed", lt.size(), 5);
		assertEquals("ge size failed", ge.size(), 5);
		assertEquals("gt size failed", gt.size(), 4);
		assertEquals("eq size failed", eq.size(), 1);
		assertEquals("ne size failed", ne.size(), 9);

		for(IAttribute att : le)
			assertEquals("le failed", att.le(5), true);

		for(IAttribute att : lt)
			assertEquals("lt failed", att.lt(5), true);

		for(IAttribute att : ge)
			assertEquals("ge failed", att.ge(5), true);

		for(IAttribute att : gt)
			assertEquals("gt failed", att.gt(5), true);

		for(IAttribute att : eq)
			assertEquals("eq failed", att.eq(5), true);

		for(IAttribute att : ne)
			assertEquals("ne failed", att.ne(5), true);
	}

	@Test
	public void TestSort()
	{
		AttributeList<IAttribute> list = new AttributeList<>();

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
}