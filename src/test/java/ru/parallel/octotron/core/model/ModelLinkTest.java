package ru.parallel.octotron.core.model;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.generators.tmpl.ConstTemplate;

import static org.junit.Assert.assertEquals;

public class ModelLinkTest extends GeneralTest
{
	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		ConstTemplate[] obj_att = {
			new ConstTemplate("object", "ok")
		};

		ModelLinkTest.obj_factory = new ObjectFactory(model_service).Constants(obj_att);

		ConstTemplate[] link_att = {
			new ConstTemplate("link", "ok"),
			new ConstTemplate("type", "contain"),
		};

		ModelLinkTest.link_factory = new LinkFactory(model_service).Constants(link_att);
	}

	@Test
	public void TestSource()
	{
		ModelObject object1 = obj_factory.Create();
		ModelObject object2 = obj_factory.Create();

		ModelLink link1 = link_factory.OneToOne(object1, object2, true);
		ModelLink link2 = link_factory.OneToOne(object2, object1, true);

		assertEquals(object1, link1.Source());
		assertEquals(object2, link2.Source());
	}

	@Test
	public void TestTarget()
	{
		ModelObject object1 = obj_factory.Create();
		ModelObject object2 = obj_factory.Create();

		ModelLink link1 = link_factory.OneToOne(object1, object2, true);
		ModelLink link2 = link_factory.OneToOne(object2, object1, true);

		assertEquals(object2, link1.Target());
		assertEquals(object1, link2.Target());
	}
}