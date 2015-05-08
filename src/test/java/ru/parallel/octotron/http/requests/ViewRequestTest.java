package ru.parallel.octotron.http.requests;

import org.junit.Test;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;
import ru.parallel.octotron.core.attributes.impl.Reaction;
import ru.parallel.octotron.triggers.Equals;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.generators.tmpl.SensorTemplate;
import ru.parallel.octotron.generators.tmpl.TriggerTemplate;
import ru.parallel.octotron.generators.tmpl.VarTemplate;
import ru.parallel.octotron.rules.Speed;

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
	public void AttributeTest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

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
	public void ReactionTest() throws Exception
	{
		object_factory.Sensors(new SensorTemplate("test_sensor", -1, 0))
			.Triggers(new TriggerTemplate("test_trigger", new Equals("test_sensor", 1)))
			.Reactions(new ReactionTemplate("test_reaction").On("test_trigger")).Create(1);

		model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/reaction?path=obj(AID)&name=test_reaction");
		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);

		test = GetRequestResult("/view/reaction?path=obj(AID)&name=test_reaction&v");
		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void SuppressedTest() throws Exception
	{
		ModelObject obj = object_factory
			.Reactions(new ReactionTemplate("test_reaction")).Create();
		model_service.EnableObjectIndex("AID");

		Reaction reaction = obj.GetReactions().iterator().next();

		String test = GetRequestResult("/view/suppressed");
		if(test == null || test.contains("AID"))
			fail("bad response: " + test);

		reaction.SetSuppress(true);

		test = GetRequestResult("/view/suppressed");
		if(test == null || !test.contains("suppress"))
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

