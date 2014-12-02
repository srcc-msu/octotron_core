package ru.parallel.octotron.http.requests;

import org.junit.Test;

import static org.junit.Assert.fail;

public class ControlRequestTest extends RequestTest
{
	@Test
	public void StatTest() throws Exception
	{
		ControlRequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/control/stat");

		if(test == null)
			fail("bad response: result is null");
	}

	@Test
	public void ModeTest() throws Exception
	{
		ControlRequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/control/mode?silent=true");

		if(test == null)
			fail("bad response: result is null");
	}

	@Test
	public void SnapshotTest() throws Exception
	{
		ControlRequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/control/snapshot");

		if(test == null)
			fail("bad response: result is null");
	}

	@Test
	public void ModTimeTest() throws Exception
	{
		ControlRequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/control/mod_time?interval=1");

		if(test == null)
			fail("bad response: result is null");
	}

	@Test
	public void SelftestTest() throws Exception
	{
		ControlRequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/control/selftest");

		if(test == null)
			fail("bad response: result is null");
	}

	@Test
	public void QuitTest() throws Exception
	{
		ControlRequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/control/quit");

		if(test == null)
			fail("bad response: result is null");
	}
}

