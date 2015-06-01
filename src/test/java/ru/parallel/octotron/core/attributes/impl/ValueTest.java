package ru.parallel.octotron.core.attributes.impl;

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
	public void TestValueFromString() throws Exception
	{
		assertEquals(1L, Value.ValueFromString("1").GetLong().longValue());
		assertEquals(1.0, Value.ValueFromString("1.0").GetDouble(), Value.EPSILON);
		assertEquals("str", Value.ValueFromString("str").GetString());
		assertEquals(true, Value.ValueFromString("true").GetBoolean());
		assertEquals(false, Value.ValueFromString("false").GetBoolean());
		assertEquals(true, Value.ValueFromString("True").GetBoolean());
		assertEquals(false, Value.ValueFromString("False").GetBoolean());
	}

	@Test
	public void TestConstruct() throws Exception
	{
		assertEquals(1L, Value.Construct(1).GetRaw());
		assertEquals(2L, Value.Construct(2L).GetRaw());

		assertEquals(1.0, Value.Construct(1.0).GetRaw());
		assertEquals(2.0, Value.Construct(2.0f).GetRaw());

		assertEquals("str", Value.Construct("str").GetRaw());
		assertEquals(true, Value.Construct(true).GetRaw());
	}

	@Test
	public void TestGet() throws Exception
	{
		assertEquals(1L, Value.Construct(1).GetLong().longValue());
		assertEquals(1.0, Value.Construct(1.0).GetDouble(), Value.EPSILON);
		assertEquals("str", Value.Construct("str").GetString());
		assertEquals(true, Value.Construct(true).GetBoolean());
		assertEquals(false, Value.Construct(false).GetBoolean());
	}

	@Test
	public void TestValueToString() throws Exception
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

	@Test
	public void TestIsDefined() throws Exception
	{
		assertEquals(true, Value.Construct(Value.invalid).IsDefined());
		assertEquals(false, Value.Construct(Value.undefined).IsDefined());
		assertEquals(true, Value.Construct("a").IsDefined());
		assertEquals(true, Value.Construct(1).IsDefined());
	}

	@Test
	public void TestIsValid() throws Exception
	{
		assertEquals(false, Value.Construct(Value.invalid).IsValid());
		assertEquals(true, Value.Construct(Value.undefined).IsValid());
		assertEquals(true, Value.Construct("a").IsValid());
		assertEquals(true, Value.Construct(1).IsValid());
	}

	@Test
	public void TestIsComputable() throws Exception
	{
		assertEquals(false, Value.Construct(Value.invalid).IsComputable());
		assertEquals(false, Value.Construct(Value.undefined).IsComputable());
		assertEquals(true, Value.Construct("a").IsComputable());
		assertEquals(true, Value.Construct(1).IsComputable());
	}

	@Test
	public void TestToDouble() throws Exception
	{
		assertEquals(1.0, Value.Construct(1).ToDouble(), Value.EPSILON);
		assertEquals(2.0, Value.Construct(2L).ToDouble(), Value.EPSILON);

		assertEquals(1.0, Value.Construct(1.0).ToDouble(), Value.EPSILON);
		assertEquals(2.0, Value.Construct(2.0f).ToDouble(), Value.EPSILON);
	}

	@Test
	public void TestOp() throws Exception
	{
		Value value_long = Value.Construct(2L);
		Value value_double = Value.Construct(1.0);
		Value value_string = Value.Construct("str");
		Value value_boolean = Value.Construct(true);

		assertTrue(value_long.eq(Value.Construct(2)));
		assertTrue(value_long.ne(Value.Construct(1L)));
		assertTrue(value_long.lt(Value.Construct(3)));
		assertTrue(value_long.ge(Value.Construct(2L)));

		assertTrue(value_double.eq(Value.Construct(1.0)));
		assertTrue(value_double.ne(Value.Construct(2.0)));
		assertTrue(value_double.lt(Value.Construct(3.0)));
		assertTrue(value_double.ge(Value.Construct(1.0)));

		assertTrue(value_string.eq(Value.Construct("str")));
		assertTrue(value_string.ne(Value.Construct("ts")));

		assertTrue(value_boolean.eq(Value.Construct(true)));
		assertTrue(value_boolean.ne(Value.Construct(false)));
	}
}