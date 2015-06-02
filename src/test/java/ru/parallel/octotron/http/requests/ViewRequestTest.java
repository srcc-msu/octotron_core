package ru.parallel.octotron.http.requests;

import org.junit.Test;
import ru.parallel.octotron.generators.tmpl.*;
import ru.parallel.octotron.core.attributes.impl.Reaction;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.rules.Speed;
import ru.parallel.octotron.rules.plain.Match;

import static org.junit.Assert.fail;

public class ViewRequestTest extends RequestTest
{
	@Test
	public void CountTest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/count?path=obj(AID)");

		if(test == null || !test.contains("10"))
			fail("bad response: " + test);
	}

	@Test
	public void AttributesTest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/attributes?path=obj(AID)&names=AID");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);

		test = GetRequestResult("/view/attributes?path=obj(AID)&names=AID&v");
		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void EntityTest() throws Exception
	{
		object_factory.Sensors(new SensorTemplate("sensor", -1, 1))
			.Vars(new VarTemplate("rule", new Speed("sensor"))).Create(10);

		model_service.EnableObjectIndex("AID");

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
	public void SuppressedTest() throws Exception
	{
		ModelObject obj = object_factory
			.Reactions(new ReactionTemplate("test_reaction", new ReactionAction())).Create();
		model_service.EnableObjectIndex("AID");

		Reaction reaction = obj.GetReaction().iterator().next();

		String test = GetRequestResult("/view/suppressed?v");
		if(test == null || test.contains("suppressed\": false"))
			fail("bad response: " + test);

		reaction.SetSuppressed(true);

		test = GetRequestResult("/view/suppressed?v");
		if(test == null || !test.contains("suppressed\": true"))
			fail("bad response: " + test);
	}

	@Test
	public void AllResponseTest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/all_response");

		if(test == null)
			fail("bad response: result is null");
	}

	@Test
	public void VersionTest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/version");

		if(test == null || !test.contains("version"))
			fail("bad response: " + test);
	}
}

