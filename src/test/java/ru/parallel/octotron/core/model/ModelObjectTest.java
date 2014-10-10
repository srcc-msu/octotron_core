package ru.parallel.octotron.core.model;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;

import static org.junit.Assert.assertEquals;

public class ModelObjectTest
{
	private static Context context;

	@BeforeClass
	public static void InitController() throws Exception
	{
		context = Context.CreateTestContext(0);
	}

	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		SimpleAttribute[] obj_att = {
			new SimpleAttribute("object", "ok")
		};

		ModelObjectTest.obj_factory = new ObjectFactory(context.model_service).Constants(obj_att);

		SimpleAttribute[] link_att = {
			new SimpleAttribute("link", "ok"),
			new SimpleAttribute("type", "contain"),
		};

		ModelObjectTest.link_factory = new LinkFactory(context.model_service).Constants(link_att);
	}

	/**
	 * check in links
	 * currently checks only if count matches, it is not correct
	 * */
	@Test
	public void TestGetInLinks()
	{
		int N = 10;

		ModelObject node = ModelObjectTest.obj_factory.Create();

		ModelObjectTest.link_factory.EveryToOne(ModelObjectTest.obj_factory.Create(N), node);

		assertEquals("in links not match, any type"
			, node.GetInLinks().size(), N);

		assertEquals("in links not match, exact type"
			, node.GetInLinks().Filter("link").size(), N);

		assertEquals("in links not match, exact type and value"
			, node.GetInLinks().Filter("link", "ok").size(), N);

		assertEquals("in links not match, exact type, wrong value"
			, node.GetInLinks().Filter("link", "fail").size(), 0);

		assertEquals("in links not match, wrong type, wrong value"
			, node.GetInLinks().Filter("fail", "fail").size(), 0);
	}

	@Test
	public void TestGetInNeighborsParam()
	{
		int N = 10;

		ModelObject node = ModelObjectTest.obj_factory.Create();

		ModelObjectTest.link_factory.EveryToOne(ModelObjectTest.obj_factory.Create(N), node);

		assertEquals("in links not match, any type"
			, node.GetInNeighbors().size(), N);

		assertEquals("in links not match, exact type"
			, node.GetInNeighbors("link").size(), N);

		assertEquals("in links not match, exact type and value"
			, node.GetInNeighbors("link", "ok").size(), N);

		assertEquals("in links not match, exact type, wrong value"
			, node.GetInNeighbors("link", "fail").size(), 0);

		assertEquals("in links not match, wrong type, wrong value"
			, node.GetInNeighbors("fail", "fail").size(), 0);
	}

	@Test
	public void TestGetOutNeighborsParam()
	{
		int N = 10;

		ModelObject node = ModelObjectTest.obj_factory.Create();

		ModelObjectTest.link_factory.OneToEvery(node, ModelObjectTest.obj_factory.Create(N));

		assertEquals("in links not match, any type"
			, node.GetOutNeighbors().size(), N);

		assertEquals("in links not match, exact type"
			, node.GetOutNeighbors("link").size(), N);

		assertEquals("in links not match, exact type and value"
			, node.GetOutNeighbors("link", "ok").size(), N);

		assertEquals("in links not match, exact type, wrong value"
			, node.GetOutNeighbors("link", "fail").size(), 0);

		assertEquals("in links not match, wrong type, wrong value"
			, node.GetOutNeighbors("fail", "fail").size(), 0);
	}

	@Test
	public void TestGetOutLinks()
	{
		int N = 10;

		ModelObject node = ModelObjectTest.obj_factory.Create();

		ModelObjectTest.link_factory.OneToEvery(node, ModelObjectTest.obj_factory.Create(N));

		assertEquals("out links not match, any type"
			, node.GetOutLinks().size(), N);

		assertEquals("out links not match, exact type"
			, node.GetOutLinks().Filter("link").size(), N);

		assertEquals("out links not match, exact type and value"
			, node.GetOutLinks().Filter("link", "ok").size(), N);

		assertEquals("out links not match, exact type, wrong value"
			, node.GetOutLinks().Filter("link", "fail").size(), 0);

		assertEquals("out links not match, wrong type, wrong value"
			, node.GetOutLinks().Filter("fail", "fail").size(), 0);
	}

	/**
	 * check different neighbors
	 * currently checks only if count matches, it is not correct
	 * do not check links
	 * */
	@Test
	public void TestGetInNeighbors()
	{
		int N = 10;

		ModelObject node = ModelObjectTest.obj_factory.Create();

		ModelObjectTest.link_factory.EveryToOne(ModelObjectTest.obj_factory.Create(N), node);

		assertEquals(N
			, node.GetInNeighbors().size(), N);

		assertEquals(N
			, node.GetInNeighbors().Filter("object").size(), N);

		assertEquals(N
			, node.GetInNeighbors().Filter("object", "ok").size());

		assertEquals(0
			, node.GetInNeighbors().Filter("object", "fail").size());

		assertEquals(0
			, node.GetInNeighbors().Filter("fail", "fail").size());
	}

	@Test
	public void TestGetOutNeighbors()
	{
		int N = 10;

		ModelObject node = ModelObjectTest.obj_factory.Create();

		ModelObjectTest.link_factory.OneToEvery(node, ModelObjectTest.obj_factory.Create(N));

		assertEquals("in neighbors not match, any type"
			, node.GetOutNeighbors()
				.size(), N);

		assertEquals("in neighbors not match, exact type"
			, node.GetOutNeighbors()
				.Filter("object").size(), N);

		assertEquals("in neighbors not match, exact type and value"
			, node.GetOutNeighbors()
				.Filter("object", "ok").size(), N);

		assertEquals("in neighbors not match, exact type, wrong value"
			, node.GetOutNeighbors()
				.Filter("object", "fail").size(), 0);

		assertEquals("in neighbors not match, wrong type, wrong value"
			, node.GetOutNeighbors()
				.Filter("fail", "fail").size(), 0);
	}
}
