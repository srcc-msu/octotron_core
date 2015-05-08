package ru.parallel.octotron.rules;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Sensor;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.EDependencyType;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.generators.Enumerator;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.generators.tmpl.ConstTemplate;
import ru.parallel.octotron.generators.tmpl.SensorTemplate;

import static org.junit.Assert.assertEquals;

//they do not work because invalid or wrong ctime, need to fix somehow
public class ComputeTests extends GeneralTest
{
	private static ModelObject object;

	@BeforeClass
	public static void Init()
		throws ExceptionSystemError
	{
		ObjectFactory in = new ObjectFactory(model_service)
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

		ObjectFactory out = new ObjectFactory(model_service)
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

		ObjectFactory self = new ObjectFactory(model_service)
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

		LinkFactory links = new LinkFactory(model_service)
			.Constants(new ConstTemplate("type", "test"));

		ModelObjectList ins = in.Create(3);
		ModelObjectList outs = out.Create(4);

		Enumerator.Sequence(model_service, ins, "in_lid");
		Enumerator.Sequence(model_service, outs, "out_lid");

		links.EveryToOne(ins, object, true);

		links.OneToEvery(object, outs, true);

		ModelObjectList objects = new ModelObjectList();

		objects.add(object);
		objects = objects.append(ins);
		objects = objects.append(outs);

		for(ModelEntity entity : objects)
			for(Attribute attr : entity.GetAttributes())
				if(attr.GetType() == EAttributeType.SENSOR)
					attr.UpdateDependant();

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
	public void TestAStrictDoubleSum() throws Exception
	{
		AStrictDoubleSum self_rule
			= new AStrictDoubleSum(EDependencyType.SELF, "d1", "d2");
		AStrictDoubleSum in_rule
			= new AStrictDoubleSum(EDependencyType.IN, "d1", "d2");
		AStrictDoubleSum out_rule
			= new AStrictDoubleSum(EDependencyType.OUT, "d1", "d2");
		AStrictDoubleSum all_rule
			= new AStrictDoubleSum(EDependencyType.ALL, "d1", "d2");
		AStrictDoubleSum self_wrong_rule
			= new AStrictDoubleSum(EDependencyType.SELF, "d1", "d2", "d1_inv");

		assertEquals(  1.0, (Double)self_rule.Compute(object), Value.EPSILON);
		assertEquals( 63.0, (Double)  in_rule.Compute(object), Value.EPSILON);
		assertEquals(164.0, (Double) out_rule.Compute(object), Value.EPSILON);
		assertEquals(228.0, (Double) all_rule.Compute(object), Value.EPSILON);
		assertEquals(Value.invalid,  self_wrong_rule.Compute(object));
	}

	@Test
	public void TestAStrictLongSum() throws Exception
	{
		AStrictLongSum self_rule
			= new AStrictLongSum(EDependencyType.SELF, "l1", "l2");
		AStrictLongSum in_rule
			= new AStrictLongSum(EDependencyType.IN, "l1", "l2");
		AStrictLongSum out_rule
			= new AStrictLongSum(EDependencyType.OUT, "l1", "l2");
		AStrictLongSum all_rule
			= new AStrictLongSum(EDependencyType.ALL, "l1", "l2");
		AStrictLongSum self_wrong_rule
			= new AStrictLongSum(EDependencyType.SELF, "l1", "l2", "l1_inv");

		assertEquals(  5L, self_rule.Compute(object));
		assertEquals(123L,   in_rule.Compute(object));
		assertEquals( 84L,  out_rule.Compute(object));
		assertEquals(212L, all_rule.Compute(object));
		assertEquals(Value.invalid,  self_wrong_rule.Compute(object));
	}

	@Test
	public void TestAStrictMatchCount()
	{
		AStrictMatchCount self_rule
			= new AStrictMatchCount(true, EDependencyType.SELF, "b1", "b2");
		AStrictMatchCount in_rule
			= new AStrictMatchCount(true, EDependencyType.IN, "b1", "b2");
		AStrictMatchCount out_rule
			= new AStrictMatchCount(true, EDependencyType.OUT, "b1", "b2");
		AStrictMatchCount all_rule
			= new AStrictMatchCount(true, EDependencyType.ALL, "b1", "b2");
		AStrictMatchCount self_wrong_rule
			= new AStrictMatchCount(true, EDependencyType.SELF, "b1", "b2", "b1_inv");

		assertEquals(1L, self_rule.Compute(object));
		assertEquals(6L,   in_rule.Compute(object));
		assertEquals(0L,  out_rule.Compute(object));
		assertEquals(7L,  all_rule.Compute(object));
		assertEquals(Value.invalid,  self_wrong_rule.Compute(object));
	}

	@Test
	public void TestAStrictNotMatchCount()
	{
		AStrictNotMatchCount self_rule
			= new AStrictNotMatchCount(true, EDependencyType.SELF, "b1", "b2");
		AStrictNotMatchCount in_rule
			= new AStrictNotMatchCount(true, EDependencyType.IN, "b1", "b2");
		AStrictNotMatchCount out_rule
			= new AStrictNotMatchCount(true, EDependencyType.OUT, "b1", "b2");
		AStrictNotMatchCount all_rule
			= new AStrictNotMatchCount(true, EDependencyType.ALL, "b1", "b2");
		AStrictNotMatchCount self_wrong_rule
			= new AStrictNotMatchCount(true, EDependencyType.SELF, "b1", "b2", "b1_inv");

		assertEquals(1L, self_rule.Compute(object));
		assertEquals(0L,   in_rule.Compute(object));
		assertEquals(8L,  out_rule.Compute(object));
		assertEquals(9L,  all_rule.Compute(object));
		assertEquals(Value.invalid,  self_wrong_rule.Compute(object));
	}

	@Test
	public void TestASoftDoubleSum() throws Exception
	{
		ASoftDoubleSum self_rule
			= new ASoftDoubleSum(EDependencyType.SELF, "d1", "d2");
		ASoftDoubleSum in_rule
			= new ASoftDoubleSum(EDependencyType.IN, "d1", "d2");
		ASoftDoubleSum out_rule
			= new ASoftDoubleSum(EDependencyType.OUT, "d1", "d2");
		ASoftDoubleSum all_rule
			= new ASoftDoubleSum(EDependencyType.ALL, "d1", "d2");
		ASoftDoubleSum self_wrong_rule
			= new ASoftDoubleSum(EDependencyType.SELF, "d1_inv", "d2");

		assertEquals(  1.0, (Double)self_rule.Compute(object), Value.EPSILON);
		assertEquals( 63.0, (Double)  in_rule.Compute(object), Value.EPSILON);
		assertEquals(164.0, (Double) out_rule.Compute(object), Value.EPSILON);
		assertEquals(228.0, (Double) all_rule.Compute(object), Value.EPSILON);
		assertEquals(  1.0,  self_wrong_rule.Compute(object));
	}

	@Test
	public void TestASoftLongSum() throws Exception
	{
		ASoftLongSum self_rule
			= new ASoftLongSum(EDependencyType.SELF, "l1", "l2");
		ASoftLongSum in_rule
			= new ASoftLongSum(EDependencyType.IN, "l1", "l2");
		ASoftLongSum out_rule
			= new ASoftLongSum(EDependencyType.OUT, "l1", "l2");
		ASoftLongSum all_rule
			= new ASoftLongSum(EDependencyType.ALL, "l1", "l2");
		ASoftLongSum self_wrong_rule
			= new ASoftLongSum(EDependencyType.SELF, "l1", "l2_inv");

		assertEquals(  5L, self_rule.Compute(object));
		assertEquals(123L,   in_rule.Compute(object));
		assertEquals( 84L,  out_rule.Compute(object));
		assertEquals(212L, all_rule.Compute(object));
		assertEquals(  2L,  self_wrong_rule.Compute(object));
	}

	@Test
	public void TestASoftMatchCount()
	{
		ASoftMatchCount self_rule
			= new ASoftMatchCount(true, EDependencyType.SELF, "b1", "b2");
		ASoftMatchCount in_rule
			= new ASoftMatchCount(true, EDependencyType.IN, "b1", "b2");
		ASoftMatchCount out_rule
			= new ASoftMatchCount(true, EDependencyType.OUT, "b1", "b2");
		ASoftMatchCount all_rule
			= new ASoftMatchCount(true, EDependencyType.ALL, "b1", "b2");
		ASoftMatchCount self_wrong_rule
			= new ASoftMatchCount(true, EDependencyType.SELF, "b1", "b2_inv");

		assertEquals(1L, self_rule.Compute(object));
		assertEquals(6L,   in_rule.Compute(object));
		assertEquals(0L,  out_rule.Compute(object));
		assertEquals(7L,  all_rule.Compute(object));
		assertEquals(1L,  self_wrong_rule.Compute(object));
	}

	@Test
	public void TestASoftNotMatchCount()
	{
		ASoftNotMatchCount self_rule
			= new ASoftNotMatchCount(true, EDependencyType.SELF, "b1", "b2");
		ASoftNotMatchCount in_rule
			= new ASoftNotMatchCount(true, EDependencyType.IN, "b1", "b2");
		ASoftNotMatchCount out_rule
			= new ASoftNotMatchCount(true, EDependencyType.OUT, "b1", "b2");
		ASoftNotMatchCount all_rule
			= new ASoftNotMatchCount(true, EDependencyType.ALL, "b1", "b2");
		ASoftNotMatchCount self_wrong_rule
			= new ASoftNotMatchCount(true, EDependencyType.SELF, "b1", "b2_inv");

		assertEquals(1L, self_rule.Compute(object));
		assertEquals(0L,   in_rule.Compute(object));
		assertEquals(8L,  out_rule.Compute(object));
		assertEquals(9L,  all_rule.Compute(object));
		assertEquals(0L,  self_wrong_rule.Compute(object));
	}

	@Test
	public void TestRequireSomeValid()
	{
		RequireSomeValid rule1 = new RequireSomeValid(1, 11, EDependencyType.SELF, "l1_inv", "l1", "l2_inv", "l2");
		RequireSomeValid rule2 = new RequireSomeValid(2, true, EDependencyType.SELF, "l1_inv", "l1", "l2_inv", "l2");
		RequireSomeValid rule3 = new RequireSomeValid(3, "ok", EDependencyType.SELF, "l1_inv", "l1", "l2_inv", "l2");

		assertEquals(11, rule1.Compute(object));
		assertEquals(true, rule2.Compute(object));
		assertEquals(Value.invalid, rule3.Compute(object));
	}

	@Test
	public void TestArgMatchAprx()
	{
		ArgMatchAprx rule1 = new ArgMatchAprx("d1", "d2", Value.EPSILON);
		ArgMatchAprx rule2 = new ArgMatchAprx("d1", "d2", 2.0);

		assertEquals(false, rule1.Compute(object));
		assertEquals(true , rule2.Compute(object));
	}

	@Test
	public void TestArgMatch()
	{
		ArgMatch rule1 = new ArgMatch("l1", "l2");
		ArgMatch rule2 = new ArgMatch("str1", "str2");

		assertEquals(false, rule1.Compute(object));
		assertEquals(true , rule2.Compute(object));
	}

	@Test
	public void TestContainsString()
	{
		ContainsString rule1 = new ContainsString("str1", "may");
		ContainsString rule2 = new ContainsString("str2", "bee");

		assertEquals(true , rule1.Compute(object));
		assertEquals(false, rule2.Compute(object));
	}

	@Test
	public void TestLogicalAnd()
	{
		SoftLogicalAnd rule1 = new SoftLogicalAnd("bt1", "bt2");
		SoftLogicalAnd rule2 = new SoftLogicalAnd("bt1", "bt2", "bf1");
		SoftLogicalAnd rule3 = new SoftLogicalAnd("bf2");
		SoftLogicalAnd rule4 = new SoftLogicalAnd("bf1", "bf2");

		assertEquals(true , rule1.Compute(object));
		assertEquals(false, rule2.Compute(object));
		assertEquals(false, rule3.Compute(object));
		assertEquals(false, rule4.Compute(object));
	}

	@Test
	public void TestLogicalOr()
	{
		SoftLogicalOr rule1 = new SoftLogicalOr("bt1", "bt2");
		SoftLogicalOr rule2 = new SoftLogicalOr("bt1", "bt2", "bf1");
		SoftLogicalOr rule3 = new SoftLogicalOr("bt1");
		SoftLogicalOr rule4 = new SoftLogicalOr("bf1", "bf2");

		assertEquals(true , rule1.Compute(object));
		assertEquals(true , rule2.Compute(object));
		assertEquals(true , rule3.Compute(object));
		assertEquals(false, rule4.Compute(object));
	}

	@Test
	public void TestLowerArgThreshold()
	{
		LowerArgThreshold rule1 = new LowerArgThreshold("l1", "l2");
		LowerArgThreshold rule2 = new LowerArgThreshold("l2", "l1");
		LowerArgThreshold rule3 = new LowerArgThreshold("d1", "d2");
		LowerArgThreshold rule4 = new LowerArgThreshold("d2", "d1");

		assertEquals(false, rule1.Compute(object));
		assertEquals(true , rule2.Compute(object));
		assertEquals(false, rule3.Compute(object));
		assertEquals(true , rule4.Compute(object));
	}

	@Test
	public void TestLowerThreshold()
	{
		LowerThreshold rule1 = new LowerThreshold("l1", 10);
		LowerThreshold rule2 = new LowerThreshold("l2", 0);
		LowerThreshold rule3 = new LowerThreshold("d1", 10.0);
		LowerThreshold rule4 = new LowerThreshold("d2", 0.0);

		assertEquals(false, rule1.Compute(object));
		assertEquals(true , rule2.Compute(object));
		assertEquals(false, rule3.Compute(object));
		assertEquals(true , rule4.Compute(object));
	}

	@Test
	public void TestMatchAprx()
	{
		MatchAprx rule1 = new MatchAprx("d1", 0.0, 1.0);
		MatchAprx rule2 = new MatchAprx("d2", 2.5, 2.0);

		MatchAprx rule3 = new MatchAprx("l1", 0L, 1L);
		MatchAprx rule4 = new MatchAprx("l2", -1L, 5L);

		assertEquals(true, rule1.Compute(object));
		assertEquals(true , rule2.Compute(object));
		assertEquals(false, rule3.Compute(object));
		assertEquals(true , rule4.Compute(object));
	}

	@Test
	public void TestMatch()
	{
		Match rule1 = new Match("d1", 0.0);
		Match rule2 = new Match("d2", 2.5);

		Match rule3 = new Match("l1", 2);
		Match rule4 = new Match("l2", -1);

		assertEquals(true , rule1.Compute(object));
		assertEquals(false, rule2.Compute(object));
		assertEquals(true , rule3.Compute(object));
		assertEquals(false, rule4.Compute(object));
	}

	@Test
	public void TestNotMatch()
	{
		Match rule1 = new Match("bt1", false);
		Match rule2 = new Match("bf1", false);

		Match rule3 = new Match("l1", -1);
		Match rule4 = new Match("l2", 3);

		assertEquals(false, rule1.Compute(object));
		assertEquals(true , rule2.Compute(object));
		assertEquals(false, rule3.Compute(object));
		assertEquals(true , rule4.Compute(object));
	}

	@Test
	public void TestUpperArgThreshold()
	{
		UpperArgThreshold rule1 = new UpperArgThreshold("l1", "l2");
		UpperArgThreshold rule2 = new UpperArgThreshold("l2", "l1");
		UpperArgThreshold rule3 = new UpperArgThreshold("d1", "d2");
		UpperArgThreshold rule4 = new UpperArgThreshold("d2", "d1");

		assertEquals(true, rule1.Compute(object));
		assertEquals(false, rule2.Compute(object));
		assertEquals(true, rule3.Compute(object));
		assertEquals(false, rule4.Compute(object));
	}

	@Test
	public void TestUpperThreshold()
	{
		UpperThreshold rule1 = new UpperThreshold("l1", 10);
		UpperThreshold rule2 = new UpperThreshold("l2", 0);
		UpperThreshold rule3 = new UpperThreshold("d1", 10.0);
		UpperThreshold rule4 = new UpperThreshold("d2", 0.0);

		assertEquals(true, rule1.Compute(object));
		assertEquals(false, rule2.Compute(object));
		assertEquals(true, rule3.Compute(object));
		assertEquals(false, rule4.Compute(object));
	}

	@Test
	public void TestVarArgMatchAprx()
	{
		VarArgMatchAprx rule1 = new VarArgMatchAprx("d1", "d2", Value.EPSILON);
		VarArgMatchAprx rule2 = new VarArgMatchAprx("d1", "d2", 2.0);

		assertEquals(false, rule1.Compute(object));
		assertEquals(true, rule2.Compute(object));
	}

	@Test
	public void TestVarArgMatch()
	{
		VarArgMatch rule1 = new VarArgMatch("l1", "l2");
		VarArgMatch rule2 = new VarArgMatch("str1", "str2");

		assertEquals(false, rule1.Compute(object));
		assertEquals(true, rule2.Compute(object));
	}

	@Test
	public void TestLinkedVarArgMatch()
	{
		LinkedVarArgMatch rule1 = new LinkedVarArgMatch("b1");
		LinkedVarArgMatch rule2 = new LinkedVarArgMatch("b2");
		LinkedVarArgMatch rule3 = new LinkedVarArgMatch("match_num");
		LinkedVarArgMatch rule4 = new LinkedVarArgMatch("mismatch_num");

		assertEquals(false, rule1.Compute(object.GetOutLinks().get(0)));
		assertEquals(true , rule2.Compute(object.GetOutLinks().get(0)));

		assertEquals(true , rule3.Compute(object.GetOutLinks().get(0)));
		assertEquals(false, rule4.Compute(object.GetOutLinks().get(0)));
	}

	@Test
	public void TestToPct()
	{
		ToArgPct rule1 = new ToArgPct("l1", "l2");
		ToPct rule2 = new ToPct("l2", 6);

		assertEquals(66L, rule1.Compute(object));
		assertEquals(50L, rule2.Compute(object));
	}

	@Test
	public void TestCalcSpeed() throws Exception
	{
		Speed rule_l = new Speed("mod_l1");
		Speed rule_d = new Speed("mod_d1");

		object.GetSensor("mod_l1").Import(10L);
		object.GetSensor("mod_d1").Import(10.0);

		assertEquals(0.0, Value.Construct(rule_l.Compute(object)).GetDouble(), Value.EPSILON);
		assertEquals(0.0, Value.Construct(rule_d.Compute(object)).GetDouble(), Value.EPSILON);

		Thread.sleep(2000);

		object.GetSensor("mod_l1").Import(20L);
		object.GetSensor("mod_d1").Import(20.0);

		assertEquals(5.0, Value.Construct(rule_l.Compute(object)).GetDouble(), Value.EPSILON);
		assertEquals(5.0, Value.Construct(rule_d.Compute(object)).GetDouble(), Value.EPSILON);

		object.GetSensor("mod_l1").Import(20L);
		object.GetSensor("mod_d1").Import(20.0);

		assertEquals(0.0, Value.Construct(rule_l.Compute(object)).GetDouble(), Value.EPSILON);
		assertEquals(0.0, Value.Construct(rule_d.Compute(object)).GetDouble(), Value.EPSILON);
	}

	@Test
	public void TestInterval()
	{
		// d1 = 0.0
		// l1 = 2

		assertEquals(0L, new Interval("d1", 0.5, 2.0).Compute(object));
		assertEquals(1L, new Interval("d1", -1.5).Compute(object));
		assertEquals(1L, new Interval("d1", -0.5, 2.0).Compute(object));
		assertEquals(1L, new Interval("d1", 0.0, 2.0).Compute(object));
		assertEquals(2L, new Interval("d1", -0.5, 0.0, 2.0).Compute(object));
		assertEquals(2L, new Interval("d1", -1.5, -0.5, 2.0).Compute(object));

		assertEquals(0L, new Interval("l1", 4).Compute(object));
		assertEquals(0L, new Interval("l1", 4, 5).Compute(object));
		assertEquals(1L, new Interval("l1", 2).Compute(object));
		assertEquals(1L, new Interval("l1", 2, 4).Compute(object));
		assertEquals(2L, new Interval("l1", 1, 2, 4).Compute(object));
		assertEquals(2L, new Interval("l1", 1, 2).Compute(object));
	}

	@Test
	public void TestCheckedInterval()
	{
		// d1 = 0.0
		// l1 = 2

		assertEquals(Value.invalid, new CheckedInterval("d1", 0.5, 2.0).Compute(object));
		assertEquals(Value.invalid, new CheckedInterval("d1", -1.5).Compute(object));
		assertEquals(1L, new CheckedInterval("d1", -0.5, 2.0).Compute(object));
		assertEquals(1L, new CheckedInterval("d1", 0.0, 2.0).Compute(object));
		assertEquals(2L, new CheckedInterval("d1", -0.5, 0.0, 2.0).Compute(object));
		assertEquals(2L, new CheckedInterval("d1", -1.5, -0.5, 2.0).Compute(object));

		assertEquals(Value.invalid, new CheckedInterval("l1", 4).Compute(object));
		assertEquals(Value.invalid, new CheckedInterval("l1", 4, 5).Compute(object));
		assertEquals(Value.invalid, new CheckedInterval("l1", 2).Compute(object));
		assertEquals(1L, new CheckedInterval("l1", 2, 4).Compute(object));
		assertEquals(2L, new CheckedInterval("l1", 1, 2, 4).Compute(object));
		assertEquals(Value.invalid, new CheckedInterval("l1", 1, 2).Compute(object));
	}
}
