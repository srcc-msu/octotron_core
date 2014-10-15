package ru.parallel.octotron.reactions;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.generators.ObjectFactory;

import static org.junit.Assert.*;
import static ru.parallel.octotron.reactions.PreparedResponse.ComposeString;

/**
 * AIDS may differ if you run a single test.. TODO
 * */
public class PreparedResponseTest
{
	private static Context context;

	@BeforeClass
	public static void InitController() throws Exception
	{
		context = Context.CreateTestContext(0);
	}

	private static ObjectFactory obj_factory;

	@BeforeClass
	public static void Init()
		throws Exception
	{
		PreparedResponseTest.obj_factory = new ObjectFactory(context.model_service)
			.Constants(new SimpleAttribute("type", "test"))
			.Constants(new SimpleAttribute("test", 1));
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void TestReplace() throws Exception
	{
		ModelEntity entity = obj_factory.Create();

		assertEquals("1 gg", ComposeString("{test} gg", entity));

		assertEquals("!1", ComposeString("!{test}", entity));

		exception.expect(ExceptionParseError.class);
		ComposeString("{test gg", entity);
		ComposeString("test gg}", entity);
		ComposeString("{test gg}{", entity);
	}

	@Test
	public void Test1()
	{
		ModelEntity entity = obj_factory.Create();
		Response response = new Response(EEventStatus.INFO, "TAG");

		PreparedResponse prepared_response = new PreparedResponse(entity, response, 1);

		assertEquals("{\"event\":\"INFO\",\"msg_0\":\"TAG\",\"time\":1}"
			, prepared_response.GetFullString());
	}

	@Test
	public void Test2()
	{
		ModelEntity entity = obj_factory.Create();
		Response response = new Response(EEventStatus.INFO, "{test} {type}");

		PreparedResponse prepared_response = new PreparedResponse(entity, response, 1);

		assertEquals("{\"event\":\"INFO\",\"msg_0\":\"1 test\",\"time\":1}"
			, prepared_response.GetFullString());
	}

	@Test
	public void Test3()
	{
		ModelEntity entity = obj_factory.Create();
		Response response = new Response(EEventStatus.INFO, "{AID} {type} {fail}");

		PreparedResponse prepared_response = new PreparedResponse(entity, response, 1);

		assertEquals("{\"event\":\"INFO\",\"msg_0\":\"12 test <fail:not_found>\",\"time\":1}"
			, prepared_response.GetFullString());
	}

	@Test
	public void Test4()
	{
		ModelEntity entity = obj_factory.Create();
		Response response = new Response(EEventStatus.INFO, "test").Print("test", "type");

		PreparedResponse prepared_response = new PreparedResponse(entity, response, 1);

		assertEquals("{\"attributes\":{\"test\":1,\"type\":\"test\"},\"event\":\"INFO\",\"msg_0\":\"test\",\"time\":1}"
			, prepared_response.GetFullString());
	}
}