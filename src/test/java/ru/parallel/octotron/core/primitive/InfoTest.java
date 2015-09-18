package ru.parallel.octotron.core.primitive;

import org.junit.Test;

import static org.junit.Assert.*;

public class InfoTest
{
	@Test
	public void TestStructure() throws Exception
	{
		Info<String> info = new Info(10, "type");

		assertEquals(info.GetID(), 10);
		assertEquals(info.GetType(), "type");
	}

	@Test
	public void testEquals() throws Exception
	{
		Info<String> info1 = new Info(10, "type");
		Info<String> info2 = new Info(10, "type");
		Info<String> info3 = new Info(11, "type2");

		assertEquals(info1, info2);
		assertEquals(info2, info1);
		assertNotEquals(info1, info3);
		assertNotEquals(info3, info2);

		assertNotEquals(info3, "test");
	}
}