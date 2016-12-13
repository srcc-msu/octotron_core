package ru.parallel.octotron.rules;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.attributes.EAttributeType;
import ru.parallel.octotron.exception.ExceptionSystemError;
import ru.parallel.octotron.generators.Enumerator;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.generators.tmpl.ConstTemplate;
import ru.parallel.octotron.generators.tmpl.SensorTemplate;
import ru.parallel.octotron.rules.plain.*;

import static org.junit.Assert.assertEquals;

//they do not work because invalid or wrong ctime, need to fix somehow
public class ComputeTests extends GeneralTest
{
	private static ModelObject object;

	@BeforeClass
	public static void Init()
		throws ExceptionSystemError
	{
		ObjectFactory in = new ObjectFactory()
			.Sensors(new SensorTemplate("in_d1", -1, 10.0))
			.Sensors(new SensorTemplate("in_l1", -1, 20))
			.Sensors(new SensorTemplate("in_b1", -1, true))
			.Sensors(new SensorTemplate("in_str1", -1, "yes"))
			.Sensors(new SensorTemplate("d1", -1, 10.0))
			.Sensors(new SensorTemplate("d2", -1, 11.0))
			.Sensors(new SensorTemplate("l1", -1, 20L))
			.Sensors(new SensorTemplate("l2", -1, 21L))
			.Sensors(new SensorTemplate("b1", -1, true))
			.Sensors(new SensorTemplate("b2", -1, true))
			.Sensors(new SensorTemplate("d1_inv", -1, 10.0))
			.Sensors(new SensorTemplate("d2_inv", -1, 11.0))
			.Sensors(new SensorTemplate("l1_inv", -1, 20L))
			.Sensors(new SensorTemplate("l2_inv", -1, 21L))
			.Sensors(new SensorTemplate("b1_inv", -1, true))
			.Sensors(new SensorTemplate("b2_inv", -1, true))
			.Sensors(new SensorTemplate("str1", -1, "yes"))
			.Sensors(new SensorTemplate("str2", -1, "yes"));

		ObjectFactory out = new ObjectFactory()
			.Sensors(new SensorTemplate("out_d1", -1, 20.0))
			.Sensors(new SensorTemplate("out_l1", -1, 10))
			.Sensors(new SensorTemplate("out_b1", -1, false))
			.Sensors(new SensorTemplate("out_str1", -1, "no"))
			.Sensors(new SensorTemplate("d1", -1, 20.0))
			.Sensors(new SensorTemplate("d2", -1, 21.0))
			.Sensors(new SensorTemplate("l1", -1, 10L))
			.Sensors(new SensorTemplate("l2", -1, 11L))
			.Sensors(new SensorTemplate("b1", -1, false))
			.Sensors(new SensorTemplate("b2", -1, false))
			.Sensors(new SensorTemplate("d1_inv", -1, 20.0))
			.Sensors(new SensorTemplate("d2_inv", -1, 21.0))
			.Sensors(new SensorTemplate("l1_inv", -1, 10L))
			.Sensors(new SensorTemplate("l2_inv", -1, 11L))
			.Sensors(new SensorTemplate("b1_inv", -1, false))
			.Sensors(new SensorTemplate("b2_inv", -1, false))
			.Sensors(new SensorTemplate("str1", -1, "no"))
			.Sensors(new SensorTemplate("str2", -1, "no"))
			.Sensors(new SensorTemplate("mismatch_num", -1, 333L))
			.Sensors(new SensorTemplate("match_num", -1, 444L));

		ObjectFactory self = new ObjectFactory()
			.Sensors(new SensorTemplate("mod_d1", -1, 0.0))
			.Sensors(new SensorTemplate("mod_l1", -1, 2L))
			.Sensors(new SensorTemplate("d1", -1, 0.0))
			.Sensors(new SensorTemplate("d2", -1, 1.0))
			.Sensors(new SensorTemplate("l1", -1, 2L))
			.Sensors(new SensorTemplate("l2", -1, 3L))
			.Sensors(new SensorTemplate("b1", -1, true))
			.Sensors(new SensorTemplate("b2", -1, false))
			.Sensors(new SensorTemplate("d1_inv", -1, 0.0))
			.Sensors(new SensorTemplate("d2_inv", -1, 1.0))
			.Sensors(new SensorTemplate("l1_inv", -1, 2L))
			.Sensors(new SensorTemplate("l2_inv", -1, 3L))
			.Sensors(new SensorTemplate("b1_inv", -1, true))
			.Sensors(new SensorTemplate("b2_inv", -1, false))
			.Sensors(new SensorTemplate("str1", -1, "maybe"))
			.Sensors(new SensorTemplate("str2", -1, "maybe"))
			.Sensors(new SensorTemplate("bt1", -1, true))
			.Sensors(new SensorTemplate("bt2", -1, true))
			.Sensors(new SensorTemplate("bf1", -1, false))
			.Sensors(new SensorTemplate("bf2", -1, false))
			.Sensors(new SensorTemplate("mismatch_num", -1, 222L))
			.Sensors(new SensorTemplate("match_num", -1, 444L));

		object = self.Create();

		LinkFactory links = new LinkFactory()
			.Constants(new ConstTemplate("type", "test"));

		ModelObjectList ins = in.Create(3);
		ModelObjectList outs = out.Create(4);

		Enumerator.Sequence(ins, "in_lid");
		Enumerator.Sequence(outs, "out_lid");

		links.EveryToOne(ins, object, true);

		links.OneToEvery(object, outs, true);

		ModelObjectList objects = new ModelObjectList();

		objects.add(object);
		objects = objects.append(ins);
		objects = objects.append(outs);

		for(ModelEntity entity : objects)
			for(Attribute attr : entity.GetAttributes())
				if(attr.GetInfo().GetType() == EAttributeType.SENSOR)
					attr.UpdateDependant(1);

		// --------------

		for(ModelEntity entity : objects)
		{
			entity.GetSensor("d1_inv").SetUserInvalid();
			entity.GetSensor("d2_inv").SetUserInvalid();
			entity.GetSensor("l1_inv").SetUserInvalid();
			entity.GetSensor("l2_inv").SetUserInvalid();
			entity.GetSensor("b1_inv").SetUserInvalid();
			entity.GetSensor("b2_inv").SetUserInvalid();
		}
	}

	@Test
	public void TestArgMatchAprx()
	{
		MatchArgAprx rule1 = new MatchArgAprx("d1", "d2", Value.EPSILON);
		MatchArgAprx rule2 = new MatchArgAprx("d1", "d2", 2.0);

		assertEquals(rule1.GetDependency(object), object.GetAttributes("d1", "d2"));

		assertEquals(false, rule1.Compute(object, null));
		assertEquals(true , rule2.Compute(object, null));
	}

	@Test
	public void TestArgMatch()
	{
		MatchArg rule1 = new MatchArg("l1", "l2");
		MatchArg rule2 = new MatchArg("str1", "str2");

		assertEquals(rule1.GetDependency(object), object.GetAttributes("l1", "l2"));

		assertEquals(false, rule1.Compute(object, null));
		assertEquals(true , rule2.Compute(object, null));
	}

	@Test
	public void TestContainsString()
	{
		ContainsString rule1 = new ContainsString("str1", "may");
		ContainsString rule2 = new ContainsString("str2", "bee");

		assertEquals(rule1.GetDependency(object), object.GetAttributes("str1"));

		assertEquals(true , rule1.Compute(object, null));
		assertEquals(false, rule2.Compute(object, null));
	}

	@Test
	public void TestSLogicalAnd()
	{
		SoftLogicalAnd rule1 = new SoftLogicalAnd("bt1", "bt2");
		SoftLogicalAnd rule2 = new SoftLogicalAnd("bt1", "bt2", "bf1");
		SoftLogicalAnd rule3 = new SoftLogicalAnd("bf2");
		SoftLogicalAnd rule4 = new SoftLogicalAnd("bf1", "bf2");

		assertEquals(rule1.GetDependency(object), object.GetAttributes("bt1", "bt2"));

		assertEquals(true , rule1.Compute(object, null));
		assertEquals(false, rule2.Compute(object, null));
		assertEquals(false, rule3.Compute(object, null));
		assertEquals(false, rule4.Compute(object, null));
	}

	@Test
	public void TestSLogicalOr()
	{
		SoftLogicalOr rule1 = new SoftLogicalOr("bt1", "bt2");
		SoftLogicalOr rule2 = new SoftLogicalOr("bt1", "bt2", "bf1");
		SoftLogicalOr rule3 = new SoftLogicalOr("bt1");
		SoftLogicalOr rule4 = new SoftLogicalOr("bf1", "bf2");

		assertEquals(rule1.GetDependency(object), object.GetAttributes("bt1", "bt2"));

		assertEquals(true , rule1.Compute(object, null));
		assertEquals(true , rule2.Compute(object, null));
		assertEquals(true , rule3.Compute(object, null));
		assertEquals(false, rule4.Compute(object, null));
	}

	@Test
	public void TestLogicalAnd()
	{
		StrictLogicalAnd rule1 = new StrictLogicalAnd("bt1", "bt2");
		StrictLogicalAnd rule2 = new StrictLogicalAnd("bt1", "bt2", "bf1");
		StrictLogicalAnd rule3 = new StrictLogicalAnd("bf2");
		StrictLogicalAnd rule4 = new StrictLogicalAnd("bf1", "bf2");

		assertEquals(rule1.GetDependency(object), object.GetAttributes("bt1", "bt2"));

		assertEquals(true , rule1.Compute(object, null));
		assertEquals(false, rule2.Compute(object, null));
		assertEquals(false, rule3.Compute(object, null));
		assertEquals(false, rule4.Compute(object, null));
	}

	@Test
	public void TestLogicalOr()
	{
		StrictLogicalOr rule1 = new StrictLogicalOr("bt1", "bt2");
		StrictLogicalOr rule2 = new StrictLogicalOr("bt1", "bt2", "bf1");
		StrictLogicalOr rule3 = new StrictLogicalOr("bt1");
		StrictLogicalOr rule4 = new StrictLogicalOr("bf1", "bf2");

		assertEquals(rule1.GetDependency(object), object.GetAttributes("bt1", "bt2"));

		assertEquals(true , rule1.Compute(object, null));
		assertEquals(true , rule2.Compute(object, null));
		assertEquals(true , rule3.Compute(object, null));
		assertEquals(false, rule4.Compute(object, null));
	}

	@Test
	public void TestLowerArgThreshold()
	{
		GTArg rule1 = new GTArg("l1", "l2");
		GTArg rule2 = new GTArg("l2", "l1");
		GTArg rule3 = new GTArg("d1", "d2");
		GTArg rule4 = new GTArg("d2", "d1");

		assertEquals(rule1.GetDependency(object), object.GetAttributes("l1", "l2"));

		assertEquals(false, rule1.Compute(object, null));
		assertEquals(true , rule2.Compute(object, null));
		assertEquals(false, rule3.Compute(object, null));
		assertEquals(true , rule4.Compute(object, null));
	}

	@Test
	public void TestLowerThreshold()
	{
		GT rule1 = new GT("l1", 10);
		GT rule2 = new GT("l2", 0);
		GT rule3 = new GT("d1", 10.0);
		GT rule4 = new GT("d2", 0.0);

		assertEquals(rule1.GetDependency(object), object.GetAttributes("l1"));

		assertEquals(false, rule1.Compute(object, null));
		assertEquals(true , rule2.Compute(object, null));
		assertEquals(false, rule3.Compute(object, null));
		assertEquals(true , rule4.Compute(object, null));
	}

	@Test
	public void TestMatchAprx()
	{
		MatchAprx rule1 = new MatchAprx("d1", 0.0, 1.0);
		MatchAprx rule2 = new MatchAprx("d2", 2.5, 2.0);

		MatchAprx rule3 = new MatchAprx("l1", 0L, 1L);
		MatchAprx rule4 = new MatchAprx("l2", -1L, 5L);

		assertEquals(rule1.GetDependency(object), object.GetAttributes("d1"));

		assertEquals(true, rule1.Compute(object, null));
		assertEquals(true , rule2.Compute(object, null));
		assertEquals(false, rule3.Compute(object, null));
		assertEquals(true , rule4.Compute(object, null));
	}

	@Test
	public void TestMatch()
	{
		Match rule1 = new Match("d1", 0.0);
		Match rule2 = new Match("d2", 2.5);

		Match rule3 = new Match("l1", 2);
		Match rule4 = new Match("l2", -1);

		assertEquals(rule1.GetDependency(object), object.GetAttributes("d1"));

		assertEquals(true , rule1.Compute(object, null));
		assertEquals(false, rule2.Compute(object, null));
		assertEquals(true , rule3.Compute(object, null));
		assertEquals(false, rule4.Compute(object, null));
	}

	@Test
	public void TestNotMatch()
	{
		NotMatch rule1 = new NotMatch("d1", 0.0);
		NotMatch rule2 = new NotMatch("d2", 2.5);

		NotMatch rule3 = new NotMatch("l1", 2);
		NotMatch rule4 = new NotMatch("l2", -1);

		assertEquals(rule1.GetDependency(object), object.GetAttributes("d1"));

		assertEquals(false, rule1.Compute(object, null));
		assertEquals(true , rule2.Compute(object, null));
		assertEquals(false, rule3.Compute(object, null));
		assertEquals(true , rule4.Compute(object, null));
	}

	@Test
	public void TestNotMatchArg()
	{
		NotMatchArg rule1 = new NotMatchArg("l1", "l2");
		NotMatchArg rule2 = new NotMatchArg("str1", "str2");

		assertEquals(rule1.GetDependency(object), object.GetAttributes("l1", "l2"));

		assertEquals(true, rule1.Compute(object, null));
		assertEquals(false, rule2.Compute(object, null));
	}

	@Test
	public void TestUpperArgThreshold()
	{
		LTArg rule1 = new LTArg("l1", "l2");
		LTArg rule2 = new LTArg("l2", "l1");
		LTArg rule3 = new LTArg("d1", "d2");
		LTArg rule4 = new LTArg("d2", "d1");

		assertEquals(rule1.GetDependency(object), object.GetAttributes("l1", "l2"));

		assertEquals(true, rule1.Compute(object, null));
		assertEquals(false, rule2.Compute(object, null));
		assertEquals(true, rule3.Compute(object, null));
		assertEquals(false, rule4.Compute(object, null));
	}

	@Test
	public void TestUpperThreshold()
	{
		LT rule1 = new LT("l1", 10);
		LT rule2 = new LT("l2", 0);
		LT rule3 = new LT("d1", 10.0);
		LT rule4 = new LT("d2", 0.0);

		assertEquals(rule1.GetDependency(object), object.GetAttributes("l1"));

		assertEquals(true, rule1.Compute(object, null));
		assertEquals(false, rule2.Compute(object, null));
		assertEquals(true, rule3.Compute(object, null));
		assertEquals(false, rule4.Compute(object, null));
	}

	@Test
	public void TestMatchArgAprx()
	{
		MatchArgAprx rule1 = new MatchArgAprx("d1", "d2", Value.EPSILON);
		MatchArgAprx rule2 = new MatchArgAprx("d1", "d2", 2.0);

		assertEquals(rule1.GetDependency(object), object.GetAttributes("d1", "d2"));

		assertEquals(false, rule1.Compute(object, null));
		assertEquals(true, rule2.Compute(object, null));
	}

	@Test
	public void TestMatchArg()
	{
		MatchArg rule1 = new MatchArg("l1", "l2");
		MatchArg rule2 = new MatchArg("str1", "str2");

		assertEquals(rule1.GetDependency(object), object.GetAttributes("l1", "l2"));

		assertEquals(false, rule1.Compute(object, null));
		assertEquals(true, rule2.Compute(object, null));
	}

	@Test
	public void TestManual()
	{
		Manual rule = new Manual();

		assertEquals(rule.GetDependency(object), new AttributeList<>());

		assertEquals(false, rule.Compute(object, null));
	}

	@Test
	public void TestLinkedMatch()
	{
		LinkedMatch rule1 = new LinkedMatch("b1");
		LinkedMatch rule2 = new LinkedMatch("b2");
		LinkedMatch rule3 = new LinkedMatch("match_num");
		LinkedMatch rule4 = new LinkedMatch("mismatch_num");

		assertEquals(rule1.GetDependency(object.GetOutLinks().get(0))
			, AttributeList.From(
			object.GetAttribute("b1")
			, object.GetOutLinks().get(0).Other(object).GetAttribute("b1")));

		assertEquals(false, rule1.Compute(object.GetOutLinks().get(0)));
		assertEquals(true , rule2.Compute(object.GetOutLinks().get(0)));

		assertEquals(true , rule3.Compute(object.GetOutLinks().get(0)));
		assertEquals(false, rule4.Compute(object.GetOutLinks().get(0)));
	}

	@Test
	public void TestLinkedNotMatch()
	{
		LinkedNotMatch rule1 = new LinkedNotMatch("b1");
		LinkedNotMatch rule2 = new LinkedNotMatch("b2");
		LinkedNotMatch rule3 = new LinkedNotMatch("match_num");
		LinkedNotMatch rule4 = new LinkedNotMatch("mismatch_num");

		assertEquals(rule1.GetDependency(object.GetOutLinks().get(0))
			, AttributeList.From(
			object.GetAttribute("b1")
			, object.GetOutLinks().get(0).Other(object).GetAttribute("b1")));

		assertEquals(true, rule1.Compute(object.GetOutLinks().get(0)));
		assertEquals(false , rule2.Compute(object.GetOutLinks().get(0)));

		assertEquals(false , rule3.Compute(object.GetOutLinks().get(0)));
		assertEquals(true, rule4.Compute(object.GetOutLinks().get(0)));
	}

	@Test
	public void TestToPct()
	{
		ToArgPct rule1 = new ToArgPct("l1", "l2");
		ToPct rule2 = new ToPct("l2", 6);

		assertEquals(rule1.GetDependency(object), object.GetAttributes("l1", "l2"));
		assertEquals(rule2.GetDependency(object), object.GetAttributes("l2"));

		assertEquals(66L, rule1.Compute(object, null));
		assertEquals(50L, rule2.Compute(object, null));
	}

	@Test
	public void TestCalcSpeed() throws Exception
	{
		Speed rule_l = new Speed("mod_l1");
		Speed rule_d = new Speed("mod_d1");

		assertEquals(rule_l.GetDependency(object), object.GetAttributes("mod_l1"));
		assertEquals(rule_d.GetDependency(object), object.GetAttributes("mod_d1"));

		object.GetSensor("mod_l1").Update(10L, 1);
		object.GetSensor("mod_d1").Update(10.0, 1);

		assertEquals(0.0, Value.Construct(rule_l.Compute(object, null)).GetDouble(), Value.EPSILON);
		assertEquals(0.0, Value.Construct(rule_d.Compute(object, null)).GetDouble(), Value.EPSILON);

		object.GetSensor("mod_l1").Update(20L, 3);
		object.GetSensor("mod_d1").Update(20.0, 3);

		assertEquals(5.0, Value.Construct(rule_l.Compute(object, null)).GetDouble(), Value.EPSILON);
		assertEquals(5.0, Value.Construct(rule_d.Compute(object, null)).GetDouble(), Value.EPSILON);

		object.GetSensor("mod_l1").Update(20L, 3);
		object.GetSensor("mod_d1").Update(20.0, 3);

		assertEquals(0.0, Value.Construct(rule_l.Compute(object, null)).GetDouble(), Value.EPSILON);
		assertEquals(0.0, Value.Construct(rule_d.Compute(object, null)).GetDouble(), Value.EPSILON);
	}

	@Test
	public void TestInterval()
	{
		Interval rule_l1 = new Interval("l1", 4);
		Interval rule_l2 = new Interval("l1", 4, 5);
		Interval rule_l3 = new Interval("l1", 2);
		Interval rule_l4 = new Interval("l1", 2, 4);
		Interval rule_l5 = new Interval("l1", 1, 2, 4);
		Interval rule_l6 = new Interval("l1", 1, 2);

		Interval rule_d1 = new Interval("d1", 0.5, 2.0);
		Interval rule_d2 = new Interval("d1", -1.5);
		Interval rule_d3 = new Interval("d1", -0.5, 2.0);
		Interval rule_d4 = new Interval("d1", 0.0, 2.0);
		Interval rule_d5 = new Interval("d1", -0.5, 0.0, 2.0);
		Interval rule_d6 = new Interval("d1", -1.5, -0.5, 2.0);

		assertEquals(rule_l1.GetDependency(object), object.GetAttributes("l1"));
		assertEquals(rule_d1.GetDependency(object), object.GetAttributes("d1"));

		// d1 = 0.0
		// l1 = 2

		assertEquals(0L, rule_l1.Compute(object, null));
		assertEquals(0L, rule_l2.Compute(object, null));
		assertEquals(1L, rule_l3.Compute(object, null));
		assertEquals(1L, rule_l4.Compute(object, null));
		assertEquals(2L, rule_l5.Compute(object, null));
		assertEquals(2L, rule_l6.Compute(object, null));

		assertEquals(0L, rule_d1.Compute(object, null));
		assertEquals(1L, rule_d2.Compute(object, null));
		assertEquals(1L, rule_d3.Compute(object, null));
		assertEquals(1L, rule_d4.Compute(object, null));
		assertEquals(2L, rule_d5.Compute(object, null));
		assertEquals(2L, rule_d6.Compute(object, null));
	}

	@Test
	public void TestCheckedInterval()
	{
		CheckedInterval rule_l1 = new CheckedInterval("l1", 4);
		CheckedInterval rule_l2 = new CheckedInterval("l1", 4, 5);
		CheckedInterval rule_l3 = new CheckedInterval("l1", 2);
		CheckedInterval rule_l4 = new CheckedInterval("l1", 2, 4);
		CheckedInterval rule_l5 = new CheckedInterval("l1", 1, 2, 4);
		CheckedInterval rule_l6 = new CheckedInterval("l1", 1, 2);

		CheckedInterval rule_d1 = new CheckedInterval("d1", 0.5, 2.0);
		CheckedInterval rule_d2 = new CheckedInterval("d1", -1.5);
		CheckedInterval rule_d3 = new CheckedInterval("d1", -0.5, 2.0);
		CheckedInterval rule_d4 = new CheckedInterval("d1", 0.0, 2.0);
		CheckedInterval rule_d5 = new CheckedInterval("d1", -0.5, 0.0, 2.0);
		CheckedInterval rule_d6 = new CheckedInterval("d1", -1.5, -0.5, 2.0);

		assertEquals(rule_l1.GetDependency(object), object.GetAttributes("l1"));
		assertEquals(rule_d1.GetDependency(object), object.GetAttributes("d1"));

		// d1 = 0.0
		// l1 = 2

		assertEquals(Value.invalid, rule_d1.Compute(object, null));
		assertEquals(Value.invalid, rule_d2.Compute(object, null));
		assertEquals(1L, rule_d3.Compute(object, null));
		assertEquals(1L, rule_d4.Compute(object, null));
		assertEquals(2L, rule_d5.Compute(object, null));
		assertEquals(2L, rule_d6.Compute(object, null));

		assertEquals(Value.invalid, rule_l1.Compute(object, null));
		assertEquals(Value.invalid, rule_l2.Compute(object, null));
		assertEquals(Value.invalid, rule_l3.Compute(object, null));
		assertEquals(1L, rule_l4.Compute(object, null));
		assertEquals(2L, rule_l5.Compute(object, null));
		assertEquals(Value.invalid, rule_l6.Compute(object, null));
	}
}
