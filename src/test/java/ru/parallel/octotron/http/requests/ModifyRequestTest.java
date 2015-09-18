package ru.parallel.octotron.http.requests;

import org.junit.Test;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.generators.tmpl.ReactionAction;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;
import ru.parallel.octotron.generators.tmpl.SensorTemplate;
import ru.parallel.octotron.generators.tmpl.TriggerTemplate;
import ru.parallel.octotron.rules.plain.Manual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ModifyRequestTest extends RequestTest
{
	@Test
	public void ImportTest() throws Exception
	{
		ModelObject object = object_factory
			.Sensors(new SensorTemplate("test", 0, -1))
			.Create();
		model_service.EnableObjectIndex("AID");

		String test1 = GetRequestResult(
			String.format("/modify/import?path=obj(AID==%d)&name=test&value=2"
				, object.GetInfo().GetID()));

		assertTrue(test1.contains("added"));

		String test2 = GetRequestResult(
			String.format("/modify/import?path=obj(AID==%d)&name=a&value=true"
				, object.GetInfo().GetID()));

		assertTrue(test2.contains("not found"));
	}

	@Test
	public void UncheckedImportTest() throws Exception
	{
		ModelObject object = object_factory
			.Sensors(new SensorTemplate("test", 0, -1))
			.Create();
		model_service.EnableObjectIndex("AID");

		String test1 = GetRequestResult(
			String.format("/modify/unchecked_import?path=obj(AID==%d)&name=test&value=2"
				, object.GetInfo().GetID()));

		assertTrue(test1.contains("added"));

		String test2 = GetRequestResult(
			String.format("/modify/unchecked_import?path=obj(AID==%d)&name=a&value=true"
				, object.GetInfo().GetID()));

		assertTrue(test2.contains("registered"));
	}

	@Test
	public void SetValidTest() throws Exception
	{
		ModelObject object = object_factory
			.Sensors(new SensorTemplate("test", 0, -1))
			.Create();
		model_service.EnableObjectIndex("AID");

		String test1 = GetRequestResult(
			String.format("/modify/set_valid?path=obj(AID==%d)&name=test"
				, object.GetInfo().GetID()));

		assertTrue(test1.contains("set"));

		String test2 = GetRequestResult(
			String.format("/modify/set_valid?path=obj(AID==%d)&name=a"
				, object.GetInfo().GetID()));

		assertTrue(test2.contains("not found"));
	}

	@Test
	public void SetInvalidTest() throws Exception
	{
		ModelObject object = object_factory
			.Sensors(new SensorTemplate("test", 0, -1))
			.Create();
		model_service.EnableObjectIndex("AID");

		String test1 = GetRequestResult(
			String.format("/modify/set_invalid?path=obj(AID==%d)&name=test"
				, object.GetInfo().GetID()));

		assertTrue(test1.contains("set"));

		String test2 = GetRequestResult(
			String.format("/modify/set_invalid?path=obj(AID==%d)&name=a"
				, object.GetInfo().GetID()));

		assertTrue(test2.contains("not found"));
	}

	@Test
	public void SuppressTest() throws Exception
	{
		ModelObject object = object_factory
			.Reactions(new ReactionTemplate("reaction", new ReactionAction()))
			.Create();
		model_service.EnableObjectIndex("AID");

		String test1 = GetRequestResult(
			String.format("/modify/suppress?path=obj(AID==%d)&name=reaction"
				, object.GetInfo().GetID()));

		assertTrue(test1.contains("suppressed"));

		String test2 = GetRequestResult(
			String.format("/modify/suppress?path=obj(AID==%d)&name=a"
				, object.GetInfo().GetID()));

		assertTrue(test2.contains("not found"));
	}

	@Test
	public void UnsuppressTest() throws Exception
	{
		ModelObject object = object_factory
			.Reactions(new ReactionTemplate("reaction", new ReactionAction()))
			.Create();
		model_service.EnableObjectIndex("AID");

		String test1 = GetRequestResult(
			String.format("/modify/unsuppress?path=obj(AID==%d)&name=reaction"
				, object.GetInfo().GetID()));

		assertTrue(test1.contains("unsuppressed"));

		String test2 = GetRequestResult(
			String.format("/modify/unsuppress?path=obj(AID==%d)&name=a"
				, object.GetInfo().GetID()));

		assertTrue(test2.contains("not found"));
	}

	@Test
	public void ActivateTest() throws Exception
	{
		ModelObject object = object_factory
			.Triggers(new TriggerTemplate("trigger1", new Manual()))
			.Create();

		model_service.EnableObjectIndex("AID");

		String test1 = GetRequestResult(
			String.format("/modify/activate?path=obj(AID==%s)&name=trigger1"
			, object.GetInfo().GetID()));
		assertTrue(test1.contains("added"));

		String test2 = GetRequestResult("/modify/activate?path=obj(AID)&name=trigger2");
		assertTrue(test2.contains("not found"));
	}

}

