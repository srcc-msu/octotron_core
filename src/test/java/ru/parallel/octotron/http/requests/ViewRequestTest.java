package ru.parallel.octotron.http.requests;

import org.junit.Test;
import ru.parallel.octotron.core.attributes.impl.Reaction;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.generators.tmpl.ReactionAction;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;
import ru.parallel.octotron.generators.tmpl.SensorTemplate;
import ru.parallel.octotron.generators.tmpl.VarTemplate;
import ru.parallel.octotron.rules.Speed;

import static junit.framework.TestCase.assertTrue;

public class ViewRequestTest extends RequestTest
{
	@Test
	public void CountTest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String result = GetRequestResult("/view/count?path=obj(AID)");

		assertTrue(result.contains("10"));
	}

	@Test
	public void AttributesTest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String result = GetRequestResult("/view/attributes?path=obj(AID)&names=AID");

		assertTrue(result.contains("AID"));

		result = GetRequestResult("/view/attributes?path=obj(AID)&names=AID&v");
		assertTrue(result.contains("AID"));
	}

	@Test
	public void EntityTest() throws Exception
	{
		object_factory.Sensors(new SensorTemplate("sensor", -1, 1))
			.Vars(new VarTemplate("rule", new Speed("sensor"))).Create(10);

		model_service.EnableObjectIndex("AID");

		String result = GetRequestResult("/view/entity?path=obj(AID)&v");
		assertTrue(result.contains("AID"));

		result = GetRequestResult("/view/entity?path=obj(AID)");
		assertTrue(result.contains("AID"));
		assertTrue(result.contains("rule"));

		result = GetRequestResult("/view/entity?path=obj(AID)&type=const");
		assertTrue(result.contains("AID"));

		result = GetRequestResult("/view/entity?path=obj(AID)&type=var");
		assertTrue(result.contains("rule"));
	}

	@Test
	public void SuppressedTest() throws Exception
	{
		ModelObject obj = object_factory
			.Reactions(new ReactionTemplate("test_reaction", new ReactionAction())).Create();
		model_service.EnableObjectIndex("AID");

		Reaction reaction = obj.GetReaction().iterator().next();

		String result = GetRequestResult("/view/suppressed?v");
		assertTrue(result.contains("[]"));

		reaction.SetSuppressed(true);

		result = GetRequestResult("/view/suppressed?v");
		assertTrue(result.contains("suppressed\": true"));
	}

	@Test
	public void AllResponseTest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String result = GetRequestResult("/view/all_response");
		assertTrue(result.contains("[]"));
	}

	@Test
	public void VersionTest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String result = GetRequestResult("/view/version");
		assertTrue(result.contains("version"));
	}
}
