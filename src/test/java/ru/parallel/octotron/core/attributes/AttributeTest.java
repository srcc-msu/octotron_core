package ru.parallel.octotron.core.attributes;

import org.junit.Test;
import ru.parallel.octotron.core.attributes.impl.*;
import ru.parallel.octotron.generators.tmpl.ReactionAction;
import ru.parallel.octotron.rules.plain.Manual;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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

		attribute.Update(Value.invalid, 1);
		assertEquals(Value.invalid, attribute.GetSpeed());

		attribute.Update(Value.undefined, 1);
		assertEquals(Value.invalid, attribute.GetSpeed());

		attribute.Update(new Value(1), 1);
		assertEquals(Value.invalid, attribute.GetSpeed());

		attribute.Update(new Value(100), 1);
		assertEquals(new Value(0.0), attribute.GetSpeed());

		attribute.Update(new Value(200), 2);
		assertNotEquals(new Value(0.0), attribute.GetSpeed());
	}

	@Test
	public void TestUpdateValue() throws Exception
	{
		Attribute attribute = new Const(null, "const", new Value(0));

		attribute.Update(Value.invalid, 1);
		assertEquals(Value.invalid, attribute.GetValue());

		attribute.Update(Value.undefined, 1);
		assertEquals(Value.undefined, attribute.GetValue());

		attribute.Update(new Value(1), 1);
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

		Attribute i_on1 = new Sensor(null, "", 1, Value.undefined, 0);
		Attribute i_on2 = new Sensor(null, "", 2, Value.undefined, 0);

		a1.AddIDependOn(i_on1);
		a1.AddIDependOn(i_on2);

		assertEquals(a1.DependenciesDefined(), false);

		i_on1.Update(new Value(0), 1);
		assertEquals(a1.DependenciesDefined(), false);

		i_on2.Update(new Value(0), 1);
		assertEquals(a1.DependenciesDefined(), true);
	}

	@Test
	public void TestConstRepresentation() throws Exception
	{
		Const a_const = new Const(null, "", new Value(0));

		Map<String, Object> short_representation = a_const.GetShortRepresentation();
		Map<String, Object> long_representation = a_const.GetLongRepresentation();

		assertNotEquals(short_representation, long_representation);
	}

	@Test
	public void TestSensorRepresentation() throws Exception
	{
		Sensor a_const = new Sensor(null, "", 0, new Value(0), 0);

		Map<String, Object> short_representation = a_const.GetShortRepresentation();
		Map<String, Object> long_representation = a_const.GetLongRepresentation();

		assertNotEquals(short_representation, long_representation);
	}

	@Test
	public void TestVarRepresentation() throws Exception
	{
		Var a_var = new Var(null, "", new Manual());

		Map<String, Object> short_representation = a_var.GetShortRepresentation();
		Map<String, Object> long_representation = a_var.GetLongRepresentation();

		assertNotEquals(short_representation, long_representation);
	}

	@Test
	public void TestTriggerRepresentation() throws Exception
	{
		Trigger a_trigger = new Trigger(null, "", new Manual());

		Map<String, Object> short_representation = a_trigger.GetShortRepresentation();
		Map<String, Object> long_representation = a_trigger.GetLongRepresentation();

		assertNotEquals(short_representation, long_representation);
	}

	@Test
	public void TestReactionRepresentation() throws Exception
	{
		Reaction a_reaction = new Reaction(null, "", new ReactionAction());

		Map<String, Object> short_representation = a_reaction.GetShortRepresentation();
		Map<String, Object> long_representation = a_reaction.GetLongRepresentation();

		assertNotEquals(short_representation, long_representation);
	}
}
