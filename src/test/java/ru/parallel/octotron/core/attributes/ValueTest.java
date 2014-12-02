package ru.parallel.octotron.core.attributes;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ValueTest
{
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void TestValueFromStr() throws Exception
	{
		assertEquals(1L, Value.ValueFromStr("1").GetLong().longValue());
		assertEquals(1.0, Value.ValueFromStr("1.0").GetDouble(), Value.EPSILON);
		assertEquals("str", Value.ValueFromStr("str").GetString());
		assertEquals(true, Value.ValueFromStr("true").GetBoolean());
		assertEquals(false, Value.ValueFromStr("false").GetBoolean());
		assertEquals(true, Value.ValueFromStr("True").GetBoolean());
		assertEquals(false, Value.ValueFromStr("False").GetBoolean());
	}

	@Test
	public void TestConformType() throws Exception
	{
		assertTrue(Value.Construct(1).GetRaw() instanceof Long);
		assertTrue(Value.Construct(2L).GetRaw() instanceof Long);

		assertTrue(Value.Construct(1.0).GetRaw() instanceof Double);
		assertTrue(Value.Construct(2.0f).GetRaw() instanceof Double);

		assertTrue(Value.Construct("str").GetRaw() instanceof String);
		assertTrue(Value.Construct(true).GetRaw() instanceof Boolean);
	}

	@Test
	public void TestValueToStr() throws Exception
	{
		assertEquals("1", Value.Construct(1).ValueToString());
		assertEquals("2", Value.Construct(2L).ValueToString());

		assertEquals("1.00", Value.Construct(1.0).ValueToString());
		assertEquals("2.34", Value.Construct(2.345).ValueToString());
		assertEquals("3.46", Value.Construct(3.456f).ValueToString()); // round up

		assertEquals("\"str\"", Value.Construct("str").ValueToString());

		assertEquals("true", Value.Construct(true).ValueToString());
		assertEquals("false", Value.Construct(false).ValueToString());
	}
}