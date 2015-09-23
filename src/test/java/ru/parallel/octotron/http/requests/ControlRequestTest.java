package ru.parallel.octotron.http.requests;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ControlRequestTest extends RequestTest
{
	@Test
	public void StatTest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String result = GetRequestResult("/control/stat");

		assertTrue(result.contains("[]"));
	}

	@Test
	public void ModeTest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String result = GetRequestResult("/control/mode?silent=true");

		assertTrue(result.contains("activated"));
	}

	@Test
	public void SnapshotTest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String result = GetRequestResult("/control/snapshot");

		assertTrue(result.contains("[]"));
	}

	@Test
	public void ModTimeTest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String result = GetRequestResult("/control/timeout");

		assertTrue(result.contains("[]"));
	}

	@Test
	public void SelftestTest() throws Exception
	{
		model_service.EnableObjectIndex("AID");

		model_service.Operate();

		String result = GetRequestResult("/control/selftest");

		assertTrue(result.contains("graph_test"));
	}

	@Test
	public void QuitTest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String result = GetRequestResult("/control/quit");

		assertTrue(result.contains("quit"));
	}
}

