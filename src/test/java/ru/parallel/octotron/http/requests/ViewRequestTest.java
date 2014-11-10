package ru.parallel.octotron.http.requests;

import org.junit.Test;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.impl.Equals;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.rules.CalcSpeed;

import static org.junit.Assert.fail;

public class ViewRequestTest extends RequestTest
{
	@Test
	public void CountTest() throws Exception
	{
		ViewRequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/count?path=obj(AID)");

		if(test == null || !test.contains("10"))
			fail("bad response: " + test);
	}

	@Test
	public void AttributeTest() throws Exception
	{
		ViewRequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/attribute?path=obj(AID)&names=AID");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);

		test = GetRequestResult("/view/attribute?path=obj(AID)&names=AID&v");
		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void EntityTest() throws Exception
	{
		ViewRequestTest.factory.Sensors(new SimpleAttribute("sensor", 1))
			.Varyings(new SimpleAttribute("rule", new CalcSpeed("sensor"))).Create(10);

		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/entity?path=obj(AID)&v");
		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);

		test = GetRequestResult("/view/entity?path=obj(AID)");
		if(test == null || !test.contains("AID") || !test.contains("sensor") || !test.contains("rule"))
			fail("bad response: " + test);

		test = GetRequestResult("/view/entity?path=obj(AID)&type=const");
		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);

		test = GetRequestResult("/view/entity?path=obj(AID)&type=var");
		if(test == null || !test.contains("rule"))
			fail("bad response: " + test);
	}


	@Test
	public void ReactionTest() throws Exception
	{
		ViewRequestTest.factory.Sensors(new SimpleAttribute("test", 0))
			.Reactions(new Equals("test", 1)).Create(1);

		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/reaction?path=obj(AID)&name=test");
		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);

		test = GetRequestResult("/view/reaction?path=obj(AID)&name=test&v");
		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void SuppressedTest() throws Exception
	{
		ModelObjectList obj = ViewRequestTest.factory.Sensors(new SimpleAttribute("test", 0))
			.Reactions(new Equals("test", 1)).Create(1);
		context.model_service.EnableObjectIndex("AID");


		Reaction reaction = obj.Only().GetAttribute("test").GetReactions().iterator().next();

		String test = GetRequestResult("/view/suppressed");
		if(test == null || test.contains("AID"))
			fail("bad response: " + test);

		reaction.SetSuppress(true);

		test = GetRequestResult("/view/suppressed");
		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void AllResponseTest() throws Exception
	{
		ViewRequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/all_response");

		if(test == null)
			fail("bad response: " + test);
	}

	@Test
	public void VersionTest() throws Exception
	{
		ViewRequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/version");

		if(test == null || !test.contains("version"))
			fail("bad response: " + test);
	}
}

