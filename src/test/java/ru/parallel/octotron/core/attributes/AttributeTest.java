package ru.parallel.octotron.core.attributes;

import org.junit.Test;
import ru.parallel.octotron.core.attributes.impl.Const;
import ru.parallel.octotron.core.attributes.impl.Value;

import static org.junit.Assert.*;

public class AttributeTest
{
	@Test
	public void TestCTime() throws Exception
	{
		Attribute attribute = new Const(null, "const", new Value("str"));

		assertEquals(0, attribute.GetCTime());
		attribute.SetCTime(1);

		assertEquals(1, attribute.GetCTime());
	}

	@Test
	public void TestGetSpeed() throws Exception
	{
		Attribute attribute = new Const(null, "const", new Value(0));

		assertEquals(Value.invalid, attribute.GetSpeed());

		attribute.UpdateValue(Value.invalid);
		assertEquals(Value.invalid, attribute.GetSpeed());

		attribute.UpdateValue(Value.undefined);
		assertEquals(Value.invalid, attribute.GetSpeed());

		attribute.UpdateValue(new Value(1));
		assertEquals(Value.invalid, attribute.GetSpeed());

		attribute.UpdateValue(new Value(100));
		assertEquals(new Value(0.0), attribute.GetSpeed());

		Thread.sleep(1000);

		attribute.UpdateValue(new Value(200));
		assertNotEquals(new Value(0.0), attribute.GetSpeed());
	}

	@Test
	public void TestUpdateValue() throws Exception
	{
		Attribute attribute = new Const(null, "const", new Value(0));

		attribute.UpdateValue(Value.invalid);
		assertEquals(Value.invalid, attribute.GetValue());

		attribute.UpdateValue(Value.undefined);
		assertEquals(Value.undefined, attribute.GetValue());

		attribute.UpdateValue(new Value(1));
		assertEquals(new Value(1), attribute.GetValue());
	}
}