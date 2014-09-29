package ru.parallel.octotron.core.primitive;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleAttributeTest
{
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void TestValueFromStr() throws Exception
	{
		assertEquals(1L, SimpleAttribute.ValueFromStr("1"));
		assertEquals(1.0, (Double)SimpleAttribute.ValueFromStr("1.0"), GraphAttribute.EPSILON);
		assertEquals("str", SimpleAttribute.ValueFromStr("str"));
		assertEquals(true, SimpleAttribute.ValueFromStr("true"));
		assertEquals(false, SimpleAttribute.ValueFromStr("false"));
	}

	@Test
	public void TestConformType() throws Exception
	{
		assertTrue(SimpleAttribute.ConformType(1) instanceof Long);
		assertTrue(SimpleAttribute.ConformType(2L) instanceof Long);

		assertTrue(SimpleAttribute.ConformType(1.0) instanceof Double);
		assertTrue(SimpleAttribute.ConformType(2.0f) instanceof Double);

		assertTrue(SimpleAttribute.ConformType("str") instanceof String);
		assertTrue(SimpleAttribute.ConformType(true) instanceof Boolean);
	}

	@Test
	public void TestValueToStr() throws Exception
	{
		assertEquals("1", SimpleAttribute.ValueToStr(1));
		assertEquals("2", SimpleAttribute.ValueToStr(2L));

		assertEquals("1.00", SimpleAttribute.ValueToStr(1.0));
		assertEquals("2.34", SimpleAttribute.ValueToStr(2.345));
		assertEquals("3.46", SimpleAttribute.ValueToStr(3.456f)); // round up

		assertEquals("\"str\"", SimpleAttribute.ValueToStr("str"));

		assertEquals("true", SimpleAttribute.ValueToStr(true));
		assertEquals("false", SimpleAttribute.ValueToStr(false));
	}
}