package ru.parallel.octotron.reactions;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;

import static org.junit.Assert.*;
import static ru.parallel.octotron.reactions.PreparedResponse.ComposeString;
import static ru.parallel.octotron.reactions.PreparedResponse.ReplaceOne;

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
	public ExpectedException exception = ExpectedException.none();

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

		PreparedResponse prepared_response = new PreparedResponse(response, entity, 1, null);

		assertEquals("{\"event\":\"INFO\",\"msg0\":\"TAG\",\"time\":\"1\"}"
			, prepared_response.GetFullString());
	}

	@Test
	public void Test2()
	{
		ModelEntity entity = obj_factory.Create();
		Response response = new Response(EEventStatus.INFO, "{test} {type}");

		PreparedResponse prepared_response = new PreparedResponse(response, entity, 1, null);

		assertEquals("{\"event\":\"INFO\",\"msg0\":\"1 test\",\"time\":\"1\"}"
			, prepared_response.GetFullString());
	}

	@Test
	public void Test3()
	{
		ModelEntity entity = obj_factory.Create();
		Response response = new Response(EEventStatus.INFO, "{AID} {type} {fail}");

		PreparedResponse prepared_response = new PreparedResponse(response, entity, 1, null);

		assertEquals("{\"event\":\"INFO\",\"msg0\":\"12 test <not_found>\",\"time\":\"1\"}"
			, prepared_response.GetFullString());
	}

	@Test
	public void Test4()
	{
		ModelEntity entity = obj_factory.Create();
		Response response = new Response(EEventStatus.INFO, "test").Print("test", "type");

		PreparedResponse prepared_response = new PreparedResponse(response, entity, 1, null);

		assertEquals("{\"attributes\":{\"test\":1,\"type\":\"test\"},\"event\":\"INFO\",\"msg0\":\"test\",\"time\":\"1\"}"
			, prepared_response.GetFullString());
	}
}