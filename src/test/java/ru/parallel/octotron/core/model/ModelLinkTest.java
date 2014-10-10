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

public class ModelLinkTest
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

		ModelLinkTest.obj_factory = new ObjectFactory(context.model_service).Constants(obj_att);

		SimpleAttribute[] link_att = {
			new SimpleAttribute("link", "ok"),
			new SimpleAttribute("type", "contain"),
		};

		ModelLinkTest.link_factory = new LinkFactory(context.model_service).Constants(link_att);
	}

	@Test
	public void TestSource()
	{
		ModelObject object1 = obj_factory.Create();
		ModelObject object2 = obj_factory.Create();

		ModelLink link1 = link_factory.OneToOne(object1, object2);
		ModelLink link2 = link_factory.OneToOne(object2, object1);

		assertEquals(object1, link1.Source());
		assertEquals(object2, link2.Source());
	}

	@Test
	public void TestTarget()
	{
		ModelObject object1 = obj_factory.Create();
		ModelObject object2 = obj_factory.Create();

		ModelLink link1 = link_factory.OneToOne(object1, object2);
		ModelLink link2 = link_factory.OneToOne(object2, object1);

		assertEquals(object2, link1.Target());
		assertEquals(object1, link2.Target());
	}
}