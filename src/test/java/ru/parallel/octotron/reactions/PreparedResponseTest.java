package ru.parallel.octotron.reactions;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.core.attributes.impl.Reaction;
import ru.parallel.octotron.generators.tmpl.ReactionAction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.generators.tmpl.ConstTemplate;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;
import ru.parallel.octotron.generators.tmpl.SensorTemplate;

import static org.junit.Assert.assertEquals;
import static ru.parallel.octotron.reactions.PreparedResponseFactory.ComposeString;

public class PreparedResponseTest extends GeneralTest
{
	private static ModelObject entity;
	private static Reaction reaction;
	private static PreparedResponseFactory factory;

	private static ObjectFactory obj_factory;

	@BeforeClass
	public static void Init()
		throws Exception
	{
		PreparedResponseTest.obj_factory = new ObjectFactory()
			.Constants(new ConstTemplate("type", "test"))
			.Constants(new ConstTemplate("ggg", "7"))
			.Sensors(new SensorTemplate("test", -1, 1));

		PreparedResponseTest.factory = new PreparedResponseFactory(context);

		entity = obj_factory.Create();

		entity.GetBuilder().AddReaction(new ReactionTemplate("test_r", new ReactionAction()));
		reaction = entity.GetReactions().iterator().next();
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void TestReplace() throws Exception
	{
		assertEquals("1 gg", ComposeString("{test} gg", entity));

		assertEquals("!1", ComposeString("!{test}", entity));

		assertEquals("{test gg", ComposeString("{test gg", entity));
		assertEquals("test gg}", ComposeString("test gg}", entity));

		assertEquals("<test gg:not_found>{", ComposeString("{test gg}{", entity));

		assertEquals("<st gg:not_found>{", ComposeString("{te:st gg}{", entity));
	}

	@Test
	public void Test1() throws Exception
	{
		Response response = new Response(EEventStatus.INFO, "tst1#TAG");

		PreparedResponse prepared_response = factory.Construct(entity, reaction, response);

		assertEquals("TAG", prepared_response.usr.get("tst1"));
	}

	@Test
	public void Test2() throws Exception
	{
		Response response = new Response(EEventStatus.INFO, "tst2#{test} {type}");

		PreparedResponse prepared_response = factory.Construct(entity, reaction, response);

		assertEquals("1 test", prepared_response.usr.get("tst2"));
	}

	@Test
	public void Test3() throws Exception
	{
		ModelEntity entity = obj_factory.Create();
		Response response = new Response(EEventStatus.INFO, "tst3#{ggg} {type} {fail}");

		PreparedResponse prepared_response = factory.Construct(entity, reaction, response);

		assertEquals("7 test <fail:not_found>", prepared_response.usr.get("tst3"));
	}

	@Test
	public void Test4() throws Exception
	{
		ModelEntity entity = obj_factory.Create();
		Response response = (new Response(EEventStatus.INFO, "tst4#t{est")).Msg("tst2#tes}t");

		PreparedResponse prepared_response = factory.Construct(entity, reaction, response);

		assertEquals("t{est" , prepared_response.usr.get("tst4"));
		assertEquals("tes}t" , prepared_response.usr.get("tst2"));
	}
}