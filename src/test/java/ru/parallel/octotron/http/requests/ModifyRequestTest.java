package ru.parallel.octotron.http.requests;

import org.junit.Test;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import static org.junit.Assert.fail;

public class ModifyRequestTest extends RequestTest
{
	@Test
	public void ImportTest() throws Exception
	{
		ModifyRequestTest.factory.Sensors(new SimpleAttribute("test", 0)).Create(1);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/modify/import?path=obj(AID)&name=test&value=test");

		if(test == null)
			fail("bad response: " + test);
	}

	@Test
	public void UncheckedImportTest() throws Exception
	{
		ModifyRequestTest.factory.Sensors(new SimpleAttribute("test", 0)).Create(1);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/modify/unchecked_import?path=obj(AID)&name=test&value=test");

		if(test == null)
			fail("bad response: " + test);
	}

	@Test
	public void SetValidTest() throws Exception
	{
		ModifyRequestTest.factory.Sensors(new SimpleAttribute("test", 0)).Create(1);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/modify/set_valid?path=obj(AID)&name=test");

		if(test == null)
			fail("bad response: " + test);
	}

	@Test
	public void SetInvalidTest() throws Exception
	{
		ModifyRequestTest.factory.Sensors(new SimpleAttribute("test", 0)).Create(1);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/modify/set_invalid?path=obj(AID)&name=test");

		if(test == null)
			fail("bad response: " + test);
	}

	@Test
	public void SuppressTest() throws Exception
	{
		ModifyRequestTest.factory.Create(1);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/modify/suppress?path=obj(AID)&template_id=1&description=spam");

		if(test == null)
			fail("bad response: " + test);
	}

	@Test
	public void UnsuppressTest() throws Exception
	{
		ModifyRequestTest.factory.Create(1);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/modify/unsuppress?path=obj(AID)&template_id=1");

		if(test == null)
			fail("bad response: " + test);
	}

}

