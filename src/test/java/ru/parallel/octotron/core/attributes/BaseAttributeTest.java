package ru.parallel.octotron.core.attributes;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.parallel.octotron.core.attributes.impl.Const;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import static org.junit.Assert.*;

public class BaseAttributeTest
{
	@Test
	public void TestGetName() throws Exception
	{
		BaseAttribute attribute = new Const(null, "const", new Value("str"));

		assertEquals("const", attribute.GetName());
	}

	@Test
	public void TestGetValue() throws Exception
	{
		BaseAttribute attribute = new Const(null, "const", new Value("str"));

		assertEquals(new Value("str"), attribute.GetValue());
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void TestUpdateValue() throws Exception
	{
		BaseAttribute attribute = new Const(null, "const", new Value("str"));

		assertEquals(new Value("str"), attribute.GetValue());

		attribute.UpdateValue(new Value("trs"));
		assertEquals(new Value("trs"), attribute.GetValue());

		exception.expect(ExceptionModelFail.class);

		attribute.UpdateValue(new Value(0));
		assertEquals(new Value(0), attribute.GetValue());

	}

	/*
	// TODO
	@Test
	public void TestUpdateValue2() throws Exception
	{
		BaseAttribute attribute = new Const(null, "const", new Value("str"));

		assertEquals(new Value("str"), attribute.GetValue());

		attribute.UpdateValue(new Value("trs"));
		assertEquals(new Value("trs"), attribute.GetValue());

		attribute.UpdateValue(Value.invalid);
		assertEquals(Value.invalid, attribute.GetValue());

		exception.expect(ExceptionModelFail.class);

		attribute.UpdateValue(new Value(0));
		assertEquals(new Value(0), attribute.GetValue());
	}

	@Test
	public void TestUpdateValue3() throws Exception
	{
		BaseAttribute attribute = new Const(null, "const", new Value("str"));

		assertEquals(new Value("str"), attribute.GetValue());

		attribute.UpdateValue(new Value("trs"));
		assertEquals(new Value("trs"), attribute.GetValue());

		attribute.UpdateValue(Value.undefined);
		assertEquals(Value.undefined, attribute.GetValue());

		exception.expect(ExceptionModelFail.class);

		attribute.UpdateValue(new Value(0));
		assertEquals(new Value(0), attribute.GetValue());
	}*/

	@Test
	public void TestGetLongRepresentation() throws Exception
	{
		BaseAttribute attribute = new Const(null, "const", new Value("str"));

		assertEquals(attribute.GetID(), attribute.GetLongRepresentation().get("attribute AID"));
		assertEquals("const", attribute.GetLongRepresentation().get("name"));
		assertEquals(new Value("str"), attribute.GetLongRepresentation().get("value"));
	}

	@Test
	public void TestGetShortRepresentation() throws Exception
	{
		BaseAttribute attribute = new Const(null, "const", new Value("str"));

		assertEquals(attribute.GetID(), attribute.GetShortRepresentation().get("attribute AID"));
		assertEquals(new Value("str"), attribute.GetShortRepresentation().get("const"));
	}

	@Test
	public void TestIsDefined() throws Exception
	{
		BaseAttribute attribute = new Const(null, "const", new Value("str"));
		BaseAttribute attribute_i = new Const(null, "const", Value.invalid);
		BaseAttribute attribute_u = new Const(null, "const", Value.undefined);

		assertTrue(attribute.IsDefined());
		assertTrue(attribute_i.IsDefined());
		assertFalse(attribute_u.IsDefined());
	}

	@Test
	public void TestIsValid() throws Exception
	{
		BaseAttribute attribute = new Const(null, "const", new Value("str"));
		BaseAttribute attribute_i = new Const(null, "const", Value.invalid);
		BaseAttribute attribute_u = new Const(null, "const", Value.undefined);

		assertTrue(attribute.IsValid());
		assertFalse(attribute_i.IsValid());
		assertTrue(attribute_u.IsValid());
	}

	@Test
	public void TestIsComputable() throws Exception
	{
		BaseAttribute attribute = new Const(null, "const", new Value("str"));
		BaseAttribute attribute_i = new Const(null, "const", Value.invalid);
		BaseAttribute attribute_u = new Const(null, "const", Value.undefined);

		assertTrue(attribute.IsComputable());
		assertFalse(attribute_i.IsComputable());
		assertFalse(attribute_u.IsComputable());
	}

	@Test
	public void TestOp() throws Exception
	{
		BaseAttribute value_long = new Const(null, "", new Value(2L));
		BaseAttribute value_double = new Const(null, "", new Value(1.0));
		BaseAttribute value_string = new Const(null, "", new Value("str"));
		BaseAttribute value_boolean = new Const(null, "", new Value(true));

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