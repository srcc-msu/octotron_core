package ru.parallel.octotron.rules;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.attributes.AbstractAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.EDependencyType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.generators.Enumerator;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;

import static org.junit.Assert.assertEquals;

//they do not work because invalid or wrong ctime, need to fix somehow
public class ComputeTests
{
	private static Context context;

	@BeforeClass
	public static void InitController() throws Exception
	{
		context = Context.CreateTestContext(0);
	}

	private static ModelObject object;

	@BeforeClass
	public static void Init()
		throws ExceptionSystemError
	{
		ObjectFactory in = new ObjectFactory(context.model_service)
			.Sensors(new SimpleAttribute("in_d1", 10.0))
			.Sensors(new SimpleAttribute("in_l1", 20))
			.Sensors(new SimpleAttribute("in_b1", true))
			.Sensors(new SimpleAttribute("in_str1", "yes"))
			.Sensors(new SimpleAttribute("d1", 10.0))
			.Sensors(new SimpleAttribute("d2", 11.0))
			.Sensors(new SimpleAttribute("l1", 20L))
			.Sensors(new SimpleAttribute("l2", 21L))
			.Sensors(new SimpleAttribute("b1", true))
			.Sensors(new SimpleAttribute("b2", true))
			.Sensors(new SimpleAttribute("str1", "yes"))
			.Sensors(new SimpleAttribute("str2", "yes"));

		ObjectFactory out = new ObjectFactory(context.model_service)
			.Sensors(new SimpleAttribute("out_d1", 20.0))
			.Sensors(new SimpleAttribute("out_l1", 10))
			.Sensors(new SimpleAttribute("out_b1", false))
			.Sensors(new SimpleAttribute("out_str1", "no"))
			.Sensors(new SimpleAttribute("d1", 20.0))
			.Sensors(new SimpleAttribute("d2", 21.0))
			.Sensors(new SimpleAttribute("l1", 10L))
			.Sensors(new SimpleAttribute("l2", 11L))
			.Sensors(new SimpleAttribute("b1", false))
			.Sensors(new SimpleAttribute("b2", false))
			.Sensors(new SimpleAttribute("str1", "no"))
			.Sensors(new SimpleAttribute("str2", "no"))
			.Sensors(new SimpleAttribute("mismatch_num", 333L))
			.Sensors(new SimpleAttribute("match_num", 444L));

		ObjectFactory self = new ObjectFactory(context.model_service)
			.Sensors(new SimpleAttribute("d1", 0.0))
			.Sensors(new SimpleAttribute("d2", 1.0))
			.Sensors(new SimpleAttribute("l1", 2L))
			.Sensors(new SimpleAttribute("l2", 3L))
			.Sensors(new SimpleAttribute("b1", true))
			.Sensors(new SimpleAttribute("b2", false))
			.Sensors(new SimpleAttribute("str1", "maybe"))
			.Sensors(new SimpleAttribute("str2", "maybe"))
			.Sensors(new SimpleAttribute("bt1", true))
			.Sensors(new SimpleAttribute("bt2", true))
			.Sensors(new SimpleAttribute("bf1", false))
			.Sensors(new SimpleAttribute("bf2", false))
			.Sensors(new SimpleAttribute("mismatch_num", 222L))
			.Sensors(new SimpleAttribute("match_num", 444L));

		object = self.Create();

		LinkFactory links = new LinkFactory(context.model_service)
			.Constants(new SimpleAttribute("type", "test"));

		ModelObjectList ins = in.Create(3);
		ModelObjectList outs = out.Create(4);

		Enumerator.Sequence(context.model_service, ins, "in_lid");
		Enumerator.Sequence(context.model_service, outs, "out_lid");

		links.EveryToOne(ins, object);

		links.OneToEvery(object, outs);

		ModelObjectList objects = new ModelObjectList();

		objects.add(object);
		objects = objects.append(ins);
		objects = objects.append(outs);

		for(ModelEntity entity : objects)
			for(IModelAttribute attr : entity.GetAttributes())
				if(attr.GetType() == EAttributeType.SENSOR)
					((SensorAttribute)attr).Update(attr.GetValue());
	}

	@Test
	public void TestAggregateDoubleSum() throws Exception
	{
		AggregateDoubleSum self_rule
			= new AggregateDoubleSum(EDependencyType.SELF, "d1", "d2");
		AggregateDoubleSum in_rule
			= new AggregateDoubleSum(EDependencyType.IN, "d1", "d2");
		AggregateDoubleSum out_rule
			= new AggregateDoubleSum(EDependencyType.OUT, "d1", "d2");
		AggregateDoubleSum all_rule
			= new AggregateDoubleSum(EDependencyType.ALL, "d1", "d2");

		assertEquals(  1.0, (Double)self_rule.Compute(object), AbstractAttribute.EPSILON);
		assertEquals( 63.0, (Double)  in_rule.Compute(object), AbstractAttribute.EPSILON);
		assertEquals(164.0, (Double) out_rule.Compute(object), AbstractAttribute.EPSILON);
		assertEquals(228.0, (Double) all_rule.Compute(object), AbstractAttribute.EPSILON);
	}

	@Test
	public void TestAggregateLongSum() throws Exception
	{
		AggregateLongSum self_rule
			= new AggregateLongSum(EDependencyType.SELF, "l1", "l2");
		AggregateLongSum in_rule
			= new AggregateLongSum(EDependencyType.IN, "l1", "l2");
		AggregateLongSum out_rule
			= new AggregateLongSum(EDependencyType.OUT, "l1", "l2");
		AggregateLongSum all_rule
			= new AggregateLongSum(EDependencyType.ALL, "l1", "l2");

		assertEquals(  5L, self_rule.Compute(object));
		assertEquals(123L,   in_rule.Compute(object));
		assertEquals( 84L,  out_rule.Compute(object));
		assertEquals(212L, all_rule.Compute(object));
	}

	@Test
	public void TestAggregateMatchCount()
	{
		AggregateMatchCount self_rule
			= new AggregateMatchCount(true, EDependencyType.SELF, "b1", "b2");
		AggregateMatchCount in_rule
			= new AggregateMatchCount(true, EDependencyType.IN, "b1", "b2");
		AggregateMatchCount out_rule
			= new AggregateMatchCount(true, EDependencyType.OUT, "b1", "b2");
		AggregateMatchCount all_rule
			= new AggregateMatchCount(true, EDependencyType.ALL, "b1", "b2");

		assertEquals(1L, self_rule.Compute(object));
		assertEquals(6L,   in_rule.Compute(object));
		assertEquals(0L,  out_rule.Compute(object));
		assertEquals(7L,  all_rule.Compute(object));
	}

	@Test
	public void TestAggregateNotMatchCount()
	{
		AggregateNotMatchCount self_rule
			= new AggregateNotMatchCount(true, EDependencyType.SELF, "b1", "b2");
		AggregateNotMatchCount in_rule
			= new AggregateNotMatchCount(true, EDependencyType.IN, "b1", "b2");
		AggregateNotMatchCount out_rule
			= new AggregateNotMatchCount(true, EDependencyType.OUT, "b1", "b2");
		AggregateNotMatchCount all_rule
			= new AggregateNotMatchCount(true, EDependencyType.ALL, "b1", "b2");

		assertEquals(1L, self_rule.Compute(object));
		assertEquals(0L,   in_rule.Compute(object));
		assertEquals(8L,  out_rule.Compute(object));
		assertEquals(9L,  all_rule.Compute(object));
	}

	@Test
	public void TestArgMatchAprx()
	{
		ArgMatchAprx rule1 = new ArgMatchAprx("d1", "d2", AbstractAttribute.EPSILON);
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
		LogicalAnd rule1 = new LogicalAnd("bt1", "bt2");
		LogicalAnd rule2 = new LogicalAnd("bt1", "bt2", "bf1");
		LogicalAnd rule3 = new LogicalAnd("bf2");
		LogicalAnd rule4 = new LogicalAnd("bf1", "bf2");

		assertEquals(true , rule1.Compute(object));
		assertEquals(false, rule2.Compute(object));
		assertEquals(false, rule3.Compute(object));
		assertEquals(false, rule4.Compute(object));
	}

	@Test
	public void TestLogicalOr()
	{
		LogicalOr rule1 = new LogicalOr("bt1", "bt2");
		LogicalOr rule2 = new LogicalOr("bt1", "bt2", "bf1");
		LogicalOr rule3 = new LogicalOr("bt1");
		LogicalOr rule4 = new LogicalOr("bf1", "bf2");

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
		VarArgMatchAprx rule1 = new VarArgMatchAprx("d1", "d2", AbstractAttribute.EPSILON);
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

		assertEquals(66, rule1.Compute(object));
		assertEquals(50, rule2.Compute(object));
	}

	@Test
	public void TestCalcSpeed() throws Exception
	{
		CalcSpeed rule_l = new CalcSpeed("l1");
		CalcSpeed rule_d = new CalcSpeed("d1");

		Object l1 = object.GetAttribute("l1").GetValue();
		Object d1 = object.GetAttribute("d1").GetValue();

		object.GetSensor("l1").Update(10L);
		object.GetSensor("d1").Update(10.0);

		assertEquals(0.0, (Double)rule_l.Compute(object), AbstractAttribute.EPSILON);
		assertEquals(0.0, (Double)rule_d.Compute(object), AbstractAttribute.EPSILON);

		Thread.sleep(2000);

		object.GetSensor("l1").Update(20L);
		object.GetSensor("d1").Update(20.0);

		assertEquals(5.0, (double)rule_l.Compute(object), AbstractAttribute.EPSILON);
		assertEquals(5.0, (double)rule_d.Compute(object), AbstractAttribute.EPSILON);

		object.GetSensor("l1").Update(20L);
		object.GetSensor("d1").Update(20.0);

		assertEquals(0.0, (Double)rule_l.Compute(object), AbstractAttribute.EPSILON);
		assertEquals(0.0, (Double)rule_d.Compute(object), AbstractAttribute.EPSILON);

		object.GetSensor("l1").Update(l1);
		object.GetSensor("d1").Update(d1);

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
}
