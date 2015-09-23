package ru.parallel.octotron.core.model;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.generators.tmpl.*;
import ru.parallel.octotron.rules.plain.Manual;

import java.util.Map;

import static org.junit.Assert.*;

public class ModelEntityTest extends GeneralTest
{
	private static ObjectFactory obj_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		obj_factory = new ObjectFactory()
			// AID constant will be added by system
			.Constants(new ConstTemplate("const1", 1))
			.Constants(new ConstTemplate("const2", 2))
			.Sensors(new SensorTemplate("sensor3", 0, 3))
			.Sensors(new SensorTemplate("sensor4", 0, 4))
			.Sensors(new SensorTemplate("sensor5", 0, 5))
			.Vars(new VarTemplate("var6", new Manual()))
			.Vars(new VarTemplate("var7", new Manual()))
			.Vars(new VarTemplate("var8", new Manual()))
			.Vars(new VarTemplate("var9", new Manual()))
			.Triggers(new TriggerTemplate("trigger10", new Manual()))
			.Triggers(new TriggerTemplate("trigger11", new Manual()))
			.Triggers(new TriggerTemplate("trigger12", new Manual()))
			.Triggers(new TriggerTemplate("trigger13", new Manual()))
			.Triggers(new TriggerTemplate("trigger14", new Manual()))
			.Reactions(new ReactionTemplate("reaction15", new ReactionAction()))
			.Reactions(new ReactionTemplate("reaction16", new ReactionAction()))
			.Reactions(new ReactionTemplate("reaction17", new ReactionAction()))
			.Reactions(new ReactionTemplate("reaction18", new ReactionAction()))
			.Reactions(new ReactionTemplate("reaction19", new ReactionAction()))
			.Reactions(new ReactionTemplate("reaction20", new ReactionAction()));
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void TestGetAttribute1() throws Exception
	{
		ModelObject o = obj_factory.Create();

		o.GetAttribute("const1");
		o.GetAttribute("sensor3");
		o.GetAttribute("var6");
		o.GetAttribute("trigger10");
		o.GetAttribute("reaction15");
	}

	@Test
	public void TestGetAttribute2() throws Exception
	{
		ModelObject o = obj_factory.Create();

		exception.expect(ExceptionModelFail.class);

		new ObjectFactory().Create().GetAttribute("b");
	}

	@Test
	public void TestGetAttribute3() throws Exception
	{
		ModelObject o = obj_factory.Create();

		exception.expect(ExceptionModelFail.class);

		o.GetAttribute("");
	}

	@Test
	public void TestGetAttributes() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertEquals(o.GetAttributes().size(), 21);
	}

	@Test
	public void TestGetAttributesByType() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertEquals(o.GetAttributes(EAttributeType.CONST).size(), 3);
		assertEquals(o.GetAttributes(EAttributeType.SENSOR).size(), 3);
		assertEquals(o.GetAttributes(EAttributeType.VAR).size(), 4);
		assertEquals(o.GetAttributes(EAttributeType.TRIGGER).size(), 5);
		assertEquals(o.GetAttributes(EAttributeType.REACTION).size(), 6);
	}

	@Test
	public void TestGetAttributesValues() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertEquals(o.GetAttributesValues().size(), 21);
	}

	@Test
	public void TestTestAttribute() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertTrue(o.TestAttribute("const1"));
		assertTrue(o.TestAttribute("sensor3"));
		assertTrue(o.TestAttribute("var6"));
		assertTrue(o.TestAttribute("trigger10"));
		assertTrue(o.TestAttribute("reaction15"));

		assertFalse(o.TestAttribute(""));
		assertFalse(o.TestAttribute("a"));

		assertFalse(new ObjectFactory().Create().TestAttribute("b"));
	}

	@Test
	public void TestGetConst1() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertNotNull(o.GetConst("const1"));

		exception.expect(ExceptionModelFail.class);

		o.GetConst("a");
	}

	@Test
	public void TestGetConst2() throws Exception
	{
		exception.expect(ExceptionModelFail.class);

		new ObjectFactory().Create().GetConst("a");
	}

	@Test
	public void TestGetConstOrNull() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertNotNull(o.GetConstOrNull("const1"));
		assertNull(o.GetConstOrNull("a"));
		assertNull(new ObjectFactory().Create().GetConstOrNull("b"));
	}

	@Test
	public void TestGetConst() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertEquals(o.GetConst().size(), 3);
	}

// ------------------

	@Test
	public void TestGetSensor1() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertNotNull(o.GetSensor("sensor4"));

		exception.expect(ExceptionModelFail.class);

		o.GetSensor("a");
	}

	@Test
	public void TestGetSensor2() throws Exception
	{
		exception.expect(ExceptionModelFail.class);

		new ObjectFactory().Create().GetSensor("a");
	}

	@Test
	public void TestGetSensorOrNull() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertNotNull(o.GetSensorOrNull("sensor4"));
		assertNull(o.GetSensorOrNull("a"));
		assertNull(new ObjectFactory().Create().GetSensorOrNull("b"));
	}

	@Test
	public void TestGetSensor() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertEquals(o.GetConst().size(), 3);
	}

// ------------------

	@Test
	public void TestGetVar1() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertNotNull(o.GetVar("var7"));

		exception.expect(ExceptionModelFail.class);

		o.GetVar("a");
	}

	@Test
	public void TestGetVar2() throws Exception
	{
		exception.expect(ExceptionModelFail.class);

		new ObjectFactory().Create().GetVar("a");
	}

	@Test
	public void TestGetVarOrNull() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertNotNull(o.GetVarOrNull("var7"));
		assertNull(o.GetVarOrNull("a"));
		assertNull(new ObjectFactory().Create().GetVarOrNull("b"));
	}

	@Test
	public void TestGetVar() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertEquals(o.GetVar().size(), 4);
	}

// ------------------

	@Test
	public void TestGetTrigger1() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertNotNull(o.GetTrigger("trigger11"));

		exception.expect(ExceptionModelFail.class);

		o.GetTrigger("a");
	}

	@Test
	public void TestGetTrigger2() throws Exception
	{
		exception.expect(ExceptionModelFail.class);

		new ObjectFactory().Create().GetTrigger("a");
	}

	@Test
	public void TestGetTriggerOrNull() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertNotNull(o.GetTriggerOrNull("trigger11"));
		assertNull(o.GetTriggerOrNull("a"));
		assertNull(new ObjectFactory().Create().GetTriggerOrNull("b"));
	}

	@Test
	public void TestGetTrigger() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertEquals(o.GetTrigger().size(), 5);
	}

// ------------------

	@Test
	public void TestGetReaction1() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertNotNull(o.GetReaction("reaction17"));

		exception.expect(ExceptionModelFail.class);

		o.GetReaction("a");
	}

	@Test
	public void TestGetReaction2() throws Exception
	{
		exception.expect(ExceptionModelFail.class);

		new ObjectFactory().Create().GetReaction("a");
	}

	@Test
	public void TestGetReactionOrNull() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertNotNull(o.GetReactionOrNull("reaction17"));
		assertNull(o.GetReactionOrNull("a"));
		assertNull(new ObjectFactory().Create().GetReactionOrNull("b"));
	}

	@Test
	public void TestGetReaction() throws Exception
	{
		ModelObject o = obj_factory.Create();

		assertEquals(o.GetTrigger().size(), 5);
	}

// ------------------

	@Test
	public void TestEquals() throws Exception
	{
		ModelObject o1 = obj_factory.Create();
		ModelObject o2 = obj_factory.Create();

		assertEquals(o1, o1);
		assertNotEquals(o1, o2);
	}

	@Test
	public void TestRepresentation() throws Exception
	{
		ModelObject o = obj_factory.Create();

		Map<String, Object> short_representation = o.GetShortRepresentation();
		Map<String, Object> long_representation = o.GetLongRepresentation();

		assertEquals(short_representation.keySet(), long_representation.keySet());
		assertNotEquals(short_representation, long_representation);
	}
}