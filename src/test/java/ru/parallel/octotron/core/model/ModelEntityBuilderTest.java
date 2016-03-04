package ru.parallel.octotron.core.model;

import org.junit.Before;
import org.junit.Test;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.generators.tmpl.ReactionAction;
import ru.parallel.octotron.rules.plain.StrictLogicalAnd;
import ru.parallel.octotron.rules.plain.Manual;
import ru.parallel.octotron.rules.plain.Match;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ModelEntityBuilderTest extends GeneralTest
{
	private ModelObject object;
	private ModelObjectBuilder builder;

	@Before
	public void Init() throws Exception
	{
		object = new ModelObject();
		builder = new ModelObjectBuilder(object);
	}

	@Test
	public void TestMakeDependencies() throws Exception
	{
		builder.DeclareConst("const", "");
		builder.DeclareSensor("sensor", 0, 0);

		builder.DeclareVar("var_1", new Match("const", ""));
		builder.DeclareVar("var_2", new Match("sensor", 0));
		builder.DeclareVar("var_3", new Manual());

		builder.DeclareTrigger("trigger_1", new Match("var_1", true));
		builder.DeclareTrigger("trigger_2", new Match("var_2", true));
		builder.DeclareTrigger("trigger_3", new StrictLogicalAnd("trigger_1", "trigger_2"));

		builder.DeclareReaction("reaction_1", new ReactionAction().On("trigger_1"));
		builder.DeclareReaction("reaction_2", new ReactionAction().On("trigger_2").On("trigger_3"));

		builder.MakeDependencies();

		assertEquals(object.GetConst("const").GetIDependOn().size(), 0);
		assertEquals(object.GetConst("const").GetDependOnMe().get(0)
			, object.GetVar("var_1"));

		assertEquals(object.GetSensor("sensor").GetIDependOn().size(), 0);
		assertEquals(object.GetSensor("sensor").GetDependOnMe().get(0)
			, object.GetVar("var_2"));

		assertEquals(object.GetVar("var_1").GetIDependOn().get(0)
			, object.GetConst("const"));
		assertEquals(object.GetVar("var_1").GetDependOnMe().get(0)
			, object.GetTrigger("trigger_1"));

		assertEquals(object.GetVar("var_2").GetIDependOn().get(0)
			, object.GetSensor("sensor"));
		assertEquals(object.GetVar("var_2").GetDependOnMe().get(0)
			, object.GetTrigger("trigger_2"));

		assertEquals(object.GetVar("var_3").GetIDependOn().size(), 0);
		assertEquals(object.GetVar("var_3").GetDependOnMe().size(), 0);

		assertEquals(object.GetTrigger("trigger_1").GetIDependOn().get(0)
			, object.GetVar("var_1"));
		assertEquals(object.GetTrigger("trigger_1").GetDependOnMe().get(0)
			, object.GetReaction("reaction_1"));

// trigger 2
		assertEquals(object.GetTrigger("trigger_2").GetIDependOn().get(0)
			, object.GetVar("var_2"));

		assertEquals(
			object.GetTrigger("trigger_2").GetDependOnMe()
			, AttributeList.From(
				object.GetReaction("reaction_2")
				, object.GetTrigger("trigger_3")));

// trigger 3
		assertEquals(
			object.GetTrigger("trigger_3").GetIDependOn()
			, AttributeList.From(
				object.GetTrigger("trigger_1")
				, object.GetTrigger("trigger_2")));

		assertEquals(object.GetTrigger("trigger_3").GetDependOnMe().get(0)
			, object.GetReaction("reaction_2"));

// reaction 1
		assertEquals(object.GetReaction("reaction_1").GetIDependOn().get(0)
			, object.GetTrigger("trigger_1"));
		assertEquals(object.GetReaction("reaction_1").GetDependOnMe().size(), 0);

// reaction 2

		assertEquals(
			object.GetReaction("reaction_2").GetIDependOn()
			, AttributeList.From(
				object.GetTrigger("trigger_2")
				, object.GetTrigger("trigger_3")));

		assertEquals(object.GetReaction("reaction_2").GetDependOnMe().size(), 0);
	}

	@Test
	public void TestDeclareConst() throws Exception
	{
		assertEquals(object.GetConst().size(), 0);

		builder.DeclareConst("const", "");

		assertNotNull(object.GetConst("const"));
	}

	@Test
	public void TestDeclareSensor() throws Exception
	{
		assertEquals(object.GetSensor().size(), 0);

		builder.DeclareSensor("sensor", 0, "");

		assertNotNull(object.GetSensor("sensor"));
	}

	@Test
	public void TestDeclareVar() throws Exception
	{
		assertEquals(object.GetVar().size(), 0);

		builder.DeclareVar("var", new Manual());

		assertNotNull(object.GetVar("var"));
	}

	@Test
	public void TestDeclareTrigger() throws Exception
	{
		assertEquals(object.GetTrigger().size(), 0);

		builder.DeclareTrigger("trigger", new Manual());

		assertNotNull(object.GetTrigger("trigger"));
	}

	@Test
	public void TestAddReaction() throws Exception
	{
		assertEquals(object.GetReaction().size(), 0);

		builder.DeclareReaction("const", new ReactionAction());

		assertNotNull(object.GetReaction("const"));
	}
}
