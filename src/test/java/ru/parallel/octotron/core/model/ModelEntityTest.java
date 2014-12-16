package ru.parallel.octotron.core.model;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.logic.impl.Equals;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.generators.tmpl.ConstTemplate;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;
import ru.parallel.octotron.generators.tmpl.VarTemplate;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ModelEntityTest extends GeneralTest
{
	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		ConstTemplate[] obj_att = {
			new ConstTemplate("object", "ok")
		};

		ModelEntityTest.obj_factory = new ObjectFactory(model_service).Constants(obj_att);

		ConstTemplate[] link_att = {
			new ConstTemplate("link", "ok"),
			new ConstTemplate("type", "contain"),
		};

		ModelEntityTest.link_factory = new LinkFactory(model_service).Constants(link_att);
	}

	@org.junit.Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void TestEquals()
	{
		ModelEntity entity1 = ModelEntityTest.obj_factory.Create();
		ModelEntity entity2 = ModelEntityTest.obj_factory.Create();

		ModelEntity entity3 = ModelEntityTest.link_factory.OneToOne(ModelEntityTest.obj_factory.Create(), ModelEntityTest.obj_factory.Create());
		ModelEntity entity4 = ModelEntityTest.link_factory.OneToOne(ModelEntityTest.obj_factory.Create(), ModelEntityTest.obj_factory.Create());

		assertTrue (entity1.equals(entity1)); // !
		assertFalse(entity1.equals(entity2));
		assertFalse(entity1.equals(entity3));
		assertFalse(entity1.equals(entity4));

		assertFalse(entity2.equals(entity1));
		assertTrue (entity2.equals(entity2)); // !
		assertFalse(entity2.equals(entity3));
		assertFalse(entity2.equals(entity4));

		assertFalse(entity3.equals(entity1));
		assertFalse(entity3.equals(entity2));
		assertTrue (entity3.equals(entity3)); // !
		assertFalse(entity3.equals(entity4));

		assertFalse(entity4.equals(entity1));
		assertFalse(entity4.equals(entity2));
		assertFalse(entity4.equals(entity3));
		assertTrue (entity4.equals(entity4)); // !
	}


	@Test
	public void TestDeclareAttribute() throws Exception
	{
		ModelObject entity = model_service.AddObject();
		entity.GetBuilder(model_service).DeclareSensor("test", -1, "");

		boolean catched = false;

		try
		{
			entity.GetBuilder(model_service).DeclareSensor("test", -1, "");
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}
		assertTrue(catched);
	}

	@Test
	public void TestAddReaction() throws Exception
	{
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		entity.GetBuilder(model_service).DeclareSensor("test", -1, 0);
		ReactionTemplate reaction = new Equals("test", 1)
			.Response(new Response(EEventStatus.INFO, "tst#test"));

		entity.GetBuilder(model_service).AddReaction(reaction);
	}

	@Test
	public void TestAddReactions() throws Exception
	{
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		entity.GetBuilder(model_service).DeclareSensor("test", -1, 0);

		List<ReactionTemplate> reactions = new LinkedList<>();

		reactions.add(new Equals("test", 1)
			.Response(new Response(EEventStatus.INFO, "tst#test")));
		reactions.add(new Equals("test", 2)
			.Response(new Response(EEventStatus.INFO, "tst#test")));

		entity.GetBuilder(model_service).AddReaction(reactions);
	}

	private class DummyRule extends Rule
	{
		private long n = 0L;

		public long GetN()
		{
			return n;
		}

		@Override
		public Object Compute(ModelEntity entity)
		{
			return n++;
		}

		@Override
		public AttributeList<IModelAttribute> GetDependency(ModelEntity entity)
		{
			return new AttributeList<>();
		}
	}

	@Test
	public void TestAddRule()
	{
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		entity.GetBuilder(model_service).DeclareVar("test1", new DummyRule());
	}

	@Test
	public void TestAddRules()
	{
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		entity.GetBuilder(model_service).DeclareSensor("test", -1, 0);

		List<VarTemplate> rules = new LinkedList<>();

		rules.add(new VarTemplate("test1", new DummyRule()));
		rules.add(new VarTemplate("test2", new DummyRule()));

		entity.GetBuilder(model_service).DeclareVar(rules);
	}
}