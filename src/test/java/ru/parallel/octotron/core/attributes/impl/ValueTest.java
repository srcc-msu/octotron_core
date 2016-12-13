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
		assertEquals(1L, new Value(1).GetLong().longValue());
		assertEquals(1.0, new Value(1.0).GetDouble(), Value.EPSILON);
		assertEquals("str", new Value("str").GetString());
		assertEquals(true, new Value(true).GetBoolean());
		assertEquals(false, new Value(false).GetBoolean());
	}

	@Test
	public void TestValueToString() throws Exception
	{
		assertEquals("1", new Value(1).ValueToString());
		assertEquals("2", new Value(2L).ValueToString());

		assertEquals("1.00", new Value(1.0).ValueToString());
		assertEquals("2.34", new Value(2.344).ValueToString());
		assertEquals("3.46", new Value(3.456f).ValueToString()); // round up

		assertEquals("\"str\"", new Value("str").ValueToString());

		assertEquals("true", new Value(true).ValueToString());
		assertEquals("false", new Value(false).ValueToString());
	}

	@Test
	public void TestIsDefined() throws Exception
	{
		assertEquals(true, new Value(Value.invalid).IsDefined());
		assertEquals(false, new Value(Value.undefined).IsDefined());
		assertEquals(true, new Value("a").IsDefined());
		assertEquals(true, new Value(1).IsDefined());
	}

	@Test
	public void TestIsValid() throws Exception
	{
		assertEquals(false, new Value(Value.invalid).IsValid());
		assertEquals(true, new Value(Value.undefined).IsValid());
		assertEquals(true, new Value("a").IsValid());
		assertEquals(true, new Value(1).IsValid());
	}

	@Test
	public void TestIsComputable() throws Exception
	{
		assertEquals(false, new Value(Value.invalid).IsComputable());
		assertEquals(false, new Value(Value.undefined).IsComputable());
		assertEquals(true, new Value("a").IsComputable());
		assertEquals(true, new Value(1).IsComputable());
	}

	@Test
	public void TestToDouble() throws Exception
	{
		assertEquals(1.0, new Value(1).ToDouble(), Value.EPSILON);
		assertEquals(2.0, new Value(2L).ToDouble(), Value.EPSILON);

		assertEquals(1.0, new Value(1.0).ToDouble(), Value.EPSILON);
		assertEquals(2.0, new Value(2.0f).ToDouble(), Value.EPSILON);
	}

	@Test
	public void TestOp() throws Exception
	{
		Value value_long = new Value(2L);
		Value value_double = new Value(1.0);
		Value value_string = new Value("str");
		Value value_boolean = new Value(true);

		assertTrue(value_long.eq(new Value(2)));
		assertTrue(value_long.ne(new Value(1L)));
		assertTrue(value_long.lt(new Value(3)));
		assertTrue(value_long.ge(new Value(2L)));

		assertTrue(value_double.eq(new Value(1.0)));
		assertTrue(value_double.ne(new Value(2.0)));
		assertTrue(value_double.lt(new Value(3.0)));
		assertTrue(value_double.ge(new Value(1.0)));

		assertTrue(value_string.eq(new Value("str")));
		assertTrue(value_string.ne(new Value("ts")));

		assertTrue(value_boolean.eq(new Value(true)));
		assertTrue(value_boolean.ne(new Value(false)));
	}
}