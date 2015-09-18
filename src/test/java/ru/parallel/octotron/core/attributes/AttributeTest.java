package ru.parallel.octotron.core.attributes;

import org.junit.Test;
import ru.parallel.octotron.core.attributes.impl.Const;
import ru.parallel.octotron.core.attributes.impl.Sensor;
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

	@Test
	public void testEquals() throws Exception
	{
		Attribute a1 = new Const(null, "const", new Value(0));
		Attribute a2 = new Const(null, "const", new Value(0));

		assertEquals(a1, a1);
		assertEquals(a2, a2);
		assertNotEquals(a2, a1);
		assertNotEquals(a1, a2);

		assertNotEquals(a1, "test");
	}

	@Test
	public void TestIDependOn() throws Exception
	{
		Attribute a1 = new Const(null, "", new Value(0));

		Attribute i_on1 = new Const(null, "", new Value("i_on1"));
		Attribute i_on2 = new Const(null, "", new Value("i_on2"));

		a1.AddIDependOn(i_on1);
		a1.AddIDependOn(i_on2);

		assertEquals(a1.GetIDependOn().size(), 2);
		assertEquals(a1.GetIDependOn().eq("i_on1").get(0), i_on1);
		assertEquals(a1.GetIDependOn().eq("i_on2").get(0), i_on2);
	}

	@Test
	public void TestDependOnMe() throws Exception
	{
		Attribute a1 = new Const(null, "", new Value(0));

		Attribute on_me1 = new Const(null, "", new Value("on_me1"));
		Attribute on_me2 = new Const(null, "", new Value("on_me2"));

		a1.AddDependOnMe(on_me1);
		a1.AddDependOnMe(on_me2);

		assertEquals(a1.GetDependOnMe().size(), 2);
		assertEquals(a1.GetDependOnMe().eq("on_me1").get(0), on_me1);
		assertEquals(a1.GetDependOnMe().eq("on_me2").get(0), on_me2);
	}

	@Test
	public void TestDependenciesDefined() throws Exception
	{
		Attribute a1 = new Const(null, "", new Value(0));

		Attribute i_on1 = new Sensor(null, "", 1, Value.undefined);
		Attribute i_on2 = new Sensor(null, "", 2, Value.undefined);

		a1.AddIDependOn(i_on1);
		a1.AddIDependOn(i_on2);

		assertEquals(a1.DependenciesDefined(), false);

		i_on1.UpdateValue(new Value(0));
		assertEquals(a1.DependenciesDefined(), false);

		i_on2.UpdateValue(new Value(0));
		assertEquals(a1.DependenciesDefined(), true);
	}
}