package ru.parallel.octotron.rules;

import org.junit.*;
import static org.junit.Assert.*;

import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.generators.Enumerator;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.core.primitive.EDependencyType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.core.graph.collections.ObjectList;

public class ComputeTests
{
	private static GraphService graph_service;
	private static Neo4jGraph graph;
	private static GraphObject obj;

	@BeforeClass
	public static void Init()
		throws ExceptionSystemError
	{
		ComputeTests.graph = new Neo4jGraph( "dbs/" + ComputeTests.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		ComputeTests.graph_service = new GraphService(ComputeTests.graph);

		ObjectFactory in = new ObjectFactory(graph_service)
			.Attributes(new SimpleAttribute("in_d1", 10.0))
			.Attributes(new SimpleAttribute("in_l1", 20))
			.Attributes(new SimpleAttribute("in_b1", true))
			.Attributes(new SimpleAttribute("in_str1", "yes"))
			.Attributes(new SimpleAttribute("d1", 10.0))
			.Attributes(new SimpleAttribute("d2", 11.0))
			.Attributes(new SimpleAttribute("l1", 20))
			.Attributes(new SimpleAttribute("l2", 21))
			.Attributes(new SimpleAttribute("b1", true))
			.Attributes(new SimpleAttribute("b2", true))
			.Attributes(new SimpleAttribute("str1", "yes"))
			.Attributes(new SimpleAttribute("str2", "yes"));

		ObjectFactory out = new ObjectFactory(graph_service)
			.Attributes(new SimpleAttribute("out_d1", 20.0))
			.Attributes(new SimpleAttribute("out_l1", 10))
			.Attributes(new SimpleAttribute("out_b1", false))
			.Attributes(new SimpleAttribute("out_str1", "no"))
			.Attributes(new SimpleAttribute("d1", 20.0))
			.Attributes(new SimpleAttribute("d2", 21.0))
			.Attributes(new SimpleAttribute("l1", 10))
			.Attributes(new SimpleAttribute("l2", 11))
			.Attributes(new SimpleAttribute("b1", false))
			.Attributes(new SimpleAttribute("b2", false))
			.Attributes(new SimpleAttribute("str1", "no"))
			.Attributes(new SimpleAttribute("str2", "no"))
			.Attributes(new SimpleAttribute("mismatch_num", 333))
			.Attributes(new SimpleAttribute("match_num", 444));

		ObjectFactory self = new ObjectFactory(graph_service)
			.Attributes(new SimpleAttribute("d1", 0.0))
			.Attributes(new SimpleAttribute("d2", 1.0))
			.Attributes(new SimpleAttribute("l1", 2))
			.Attributes(new SimpleAttribute("l2", 3))
			.Attributes(new SimpleAttribute("b1", true))
			.Attributes(new SimpleAttribute("b2", false))
			.Attributes(new SimpleAttribute("str1", "maybe"))
			.Attributes(new SimpleAttribute("str2", "maybe"))
			.Attributes(new SimpleAttribute("bt1", true))
			.Attributes(new SimpleAttribute("bt2", true))
			.Attributes(new SimpleAttribute("bf1", false))
			.Attributes(new SimpleAttribute("bf2", false))
			.Attributes(new SimpleAttribute("mismatch_num", 222))
			.Attributes(new SimpleAttribute("match_num", 444));

		obj = self.Create();

		LinkFactory links = new LinkFactory(graph_service)
			.Attributes(new SimpleAttribute("type", "test"));

		ObjectList ins = in.Create(3);
		ObjectList outs = out.Create(4);

		Enumerator.Sequence(ins, "in_lid");
		Enumerator.Sequence(outs, "out_lid");

		links.EveryToOne(ins, obj);

		links.OneToEvery(obj, outs);

// hack to avoid problems with ctime check
		for(GraphObject obj : graph_service.GetAllObjects())
			for(GraphAttribute attribute : obj.GetAttributes())
				graph_service.SetMeta(obj, attribute.GetName(), "ctime", 1L);
	}

	@AfterClass
	public static void Delete()
		throws ExceptionSystemError
	{
		ComputeTests.graph.Shutdown();
		ComputeTests.graph.Delete();
	}

	@Test
	public void TestAggregateDoubleSum() throws Exception
	{
		AggregateDoubleSum self_rule
			= new AggregateDoubleSum("res", EDependencyType.SELF, "d1", "d2");
		AggregateDoubleSum in_rule
			= new AggregateDoubleSum("res", EDependencyType.IN, "d1", "d2");
		AggregateDoubleSum out_rule
			= new AggregateDoubleSum("res", EDependencyType.OUT, "d1", "d2");
		AggregateDoubleSum all_rule
			= new AggregateDoubleSum("res", EDependencyType.ALL, "d1", "d2");

		assertEquals(  1.0, (Double)self_rule.Compute(obj), GraphAttribute.EPSILON);
		assertEquals( 63.0, (Double)  in_rule.Compute(obj), GraphAttribute.EPSILON);
		assertEquals(164.0, (Double) out_rule.Compute(obj), GraphAttribute.EPSILON);
		assertEquals(228.0, (Double) all_rule.Compute(obj), GraphAttribute.EPSILON);
	}

	@Test
	public void TestAggregateLongSum() throws Exception
	{
		AggregateLongSum self_rule
			= new AggregateLongSum("res", EDependencyType.SELF, "l1", "l2");
		AggregateLongSum in_rule
			= new AggregateLongSum("res", EDependencyType.IN, "l1", "l2");
		AggregateLongSum out_rule
			= new AggregateLongSum("res", EDependencyType.OUT, "l1", "l2");
		AggregateLongSum all_rule
			= new AggregateLongSum("res", EDependencyType.ALL, "l1", "l2");

		assertEquals(  5L, self_rule.Compute(obj));
		assertEquals(123L,   in_rule.Compute(obj));
		assertEquals( 84L,  out_rule.Compute(obj));
		assertEquals(212L,  all_rule.Compute(obj));
	}

	@Test
	public void TestAggregateMatchCount()
	{
		AggregateMatchCount self_rule
			= new AggregateMatchCount("res", true, EDependencyType.SELF, "b1", "b2");
		AggregateMatchCount in_rule
			= new AggregateMatchCount("res", true, EDependencyType.IN, "b1", "b2");
		AggregateMatchCount out_rule
			= new AggregateMatchCount("res", true, EDependencyType.OUT, "b1", "b2");
		AggregateMatchCount all_rule
			= new AggregateMatchCount("res", true, EDependencyType.ALL, "b1", "b2");

		assertEquals(1L, self_rule.Compute(obj));
		assertEquals(6L,   in_rule.Compute(obj));
		assertEquals(0L,  out_rule.Compute(obj));
		assertEquals(7L,  all_rule.Compute(obj));
	}

	@Test
	public void TestAggregateNotMatchCount()
	{
		AggregateNotMatchCount self_rule
			= new AggregateNotMatchCount("res", true, EDependencyType.SELF, "b1", "b2");
		AggregateNotMatchCount in_rule
			= new AggregateNotMatchCount("res", true, EDependencyType.IN, "b1", "b2");
		AggregateNotMatchCount out_rule
			= new AggregateNotMatchCount("res", true, EDependencyType.OUT, "b1", "b2");
		AggregateNotMatchCount all_rule
			= new AggregateNotMatchCount("res", true, EDependencyType.ALL, "b1", "b2");

		assertEquals(1L, self_rule.Compute(obj));
		assertEquals(0L,   in_rule.Compute(obj));
		assertEquals(8L,  out_rule.Compute(obj));
		assertEquals(9L,  all_rule.Compute(obj));
	}

	@Test
	public void TestArgMatchAprx()
	{
		ArgMatchAprx rule1 = new ArgMatchAprx("test", "d1", "d2", GraphAttribute.EPSILON);
		ArgMatchAprx rule2 = new ArgMatchAprx("test", "d1", "d2", 2.0);

		assertEquals(false, rule1.Compute(obj));
		assertEquals(true , rule2.Compute(obj));
	}

	@Test
	public void TestArgMatch()
	{
		ArgMatch rule1 = new ArgMatch("test", "l1", "l2");
		ArgMatch rule2 = new ArgMatch("test", "str1", "str2");

		assertEquals(false, rule1.Compute(obj));
		assertEquals(true , rule2.Compute(obj));
	}

	@Test
	public void TestContainsString()
	{
		ContainsString rule1 = new ContainsString("test", "str1", "may");
		ContainsString rule2 = new ContainsString("test", "str2", "bee");

		assertEquals(true , rule1.Compute(obj));
		assertEquals(false, rule2.Compute(obj));
	}

	@Test
	public void TestLogicalAnd()
	{
		LogicalAnd rule1 = new LogicalAnd("test", "bt1", "bt2");
		LogicalAnd rule2 = new LogicalAnd("test", "bt1", "bt2", "bf1");
		LogicalAnd rule3 = new LogicalAnd("test", "bf2");
		LogicalAnd rule4 = new LogicalAnd("test", "bf1", "bf2");

		assertEquals(true , rule1.Compute(obj));
		assertEquals(false, rule2.Compute(obj));
		assertEquals(false, rule3.Compute(obj));
		assertEquals(false, rule4.Compute(obj));
	}

	@Test
	public void TestLogicalOr()
	{
		LogicalOr rule1 = new LogicalOr("test", "bt1", "bt2");
		LogicalOr rule2 = new LogicalOr("test", "bt1", "bt2", "bf1");
		LogicalOr rule3 = new LogicalOr("test", "bt1");
		LogicalOr rule4 = new LogicalOr("test", "bf1", "bf2");

		assertEquals(true , rule1.Compute(obj));
		assertEquals(true , rule2.Compute(obj));
		assertEquals(true , rule3.Compute(obj));
		assertEquals(false, rule4.Compute(obj));
	}

	@Test
	public void TestLowerArgThreshold()
	{
		LowerArgThreshold rule1 = new LowerArgThreshold("test", "l1", "l2");
		LowerArgThreshold rule2 = new LowerArgThreshold("test", "l2", "l1");
		LowerArgThreshold rule3 = new LowerArgThreshold("test", "d1", "d2");
		LowerArgThreshold rule4 = new LowerArgThreshold("test", "d2", "d1");

		assertEquals(false, rule1.Compute(obj));
		assertEquals(true , rule2.Compute(obj));
		assertEquals(false, rule3.Compute(obj));
		assertEquals(true , rule4.Compute(obj));
	}

	@Test
	public void TestLowerThreshold()
	{
		LowerThreshold rule1 = new LowerThreshold("test", "l1", 10);
		LowerThreshold rule2 = new LowerThreshold("test", "l2", 0);
		LowerThreshold rule3 = new LowerThreshold("test", "d1", 10.0);
		LowerThreshold rule4 = new LowerThreshold("test", "d2", 0.0);

		assertEquals(false, rule1.Compute(obj));
		assertEquals(true , rule2.Compute(obj));
		assertEquals(false, rule3.Compute(obj));
		assertEquals(true , rule4.Compute(obj));
	}

	@Test
	public void TestMatchAprx()
	{
		MatchAprx rule1 = new MatchAprx("test", "d1", 0.0, 1.0);
		MatchAprx rule2 = new MatchAprx("test", "d2", 2.5, 2.0);

		MatchAprx rule3 = new MatchAprx("test", "l1", 0L, 1L);
		MatchAprx rule4 = new MatchAprx("test", "l2", -1L, 5L);

		assertEquals(true, rule1.Compute(obj));
		assertEquals(true , rule2.Compute(obj));
		assertEquals(false, rule3.Compute(obj));
		assertEquals(true , rule4.Compute(obj));
	}

	@Test
	public void TestMatch()
	{
		Match rule1 = new Match("test", "d1", 0.0);
		Match rule2 = new Match("test", "d2", 2.5);

		Match rule3 = new Match("test", "l1", 2);
		Match rule4 = new Match("test", "l2", -1);

		assertEquals(true , rule1.Compute(obj));
		assertEquals(false, rule2.Compute(obj));
		assertEquals(true , rule3.Compute(obj));
		assertEquals(false, rule4.Compute(obj));
	}

	@Test
	public void TestMirrorBoolean()
	{
		MirrorBoolean rule1 = new MirrorBoolean("in_b1", "in_lid", 0);
		MirrorBoolean rule2 = new MirrorBoolean("out_b1", "out_lid", 0);

		assertEquals(true, rule1.Compute(obj));
		assertEquals(false, rule2.Compute(obj));
	}

	@Test
	public void TestMirrorDouble()
	{
		MirrorDouble rule1 = new MirrorDouble("in_d1", "in_lid", 1);
		MirrorDouble rule2 = new MirrorDouble("out_d1", "out_lid", 1);

		assertEquals(10.0, rule1.Compute(obj));
		assertEquals(20.0, rule2.Compute(obj));
	}

	@Test
	public void TestMirrorLong()
	{
		MirrorDouble rule1 = new MirrorDouble("in_l1", "in_lid", 2);
		MirrorDouble rule2 = new MirrorDouble("out_l1", "out_lid", 2);

		assertEquals(20L, rule1.Compute(obj));
		assertEquals(10L, rule2.Compute(obj));
	}

	@Test
	public void TestMirrorString()
	{
		MirrorDouble rule1 = new MirrorDouble("in_str1", "in_lid", 2);
		MirrorDouble rule2 = new MirrorDouble("out_str1", "out_lid", 2);

		assertEquals("yes", rule1.Compute(obj));
		assertEquals("no", rule2.Compute(obj));
	}

	@Test
	public void TestNotMatch()
	{
		Match rule1 = new Match("test", "bt1", false);
		Match rule2 = new Match("test", "bf1", false);

		Match rule3 = new Match("test", "l1", -1);
		Match rule4 = new Match("test", "l2", 3);

		assertEquals(false, rule1.Compute(obj));
		assertEquals(true , rule2.Compute(obj));
		assertEquals(false, rule3.Compute(obj));
		assertEquals(true , rule4.Compute(obj));
	}

	@Test
	public void TestUpdatedRecently() throws Exception
	{
		IAttribute attr = obj.DeclareAttribute("test_update", 0);

		UpdatedRecently rule = new UpdatedRecently("test", "test_update", 1);

		assertEquals(false, rule.Compute(obj));

		attr.Update(0, true);
		assertEquals(true, rule.Compute(obj));

		Thread.sleep(2000);
		assertEquals(false, rule.Compute(obj));
		attr.Update(0, true);
		assertEquals(true, rule.Compute(obj));

		Thread.sleep(3000);
		assertEquals(false, rule.Compute(obj));
		attr.Update(0, true);
		assertEquals(true, rule.Compute(obj));
	}

	@Test
	public void TestUpperArgThreshold()
	{
		UpperArgThreshold rule1 = new UpperArgThreshold("test", "l1", "l2");
		UpperArgThreshold rule2 = new UpperArgThreshold("test", "l2", "l1");
		UpperArgThreshold rule3 = new UpperArgThreshold("test", "d1", "d2");
		UpperArgThreshold rule4 = new UpperArgThreshold("test", "d2", "d1");

		assertEquals(true, rule1.Compute(obj));
		assertEquals(false, rule2.Compute(obj));
		assertEquals(true, rule3.Compute(obj));
		assertEquals(false, rule4.Compute(obj));
	}

	@Test
	public void TestUpperThreshold()
	{
		UpperThreshold rule1 = new UpperThreshold("test", "l1", 10);
		UpperThreshold rule2 = new UpperThreshold("test", "l2", 0);
		UpperThreshold rule3 = new UpperThreshold("test", "d1", 10.0);
		UpperThreshold rule4 = new UpperThreshold("test", "d2", 0.0);

		assertEquals(true, rule1.Compute(obj));
		assertEquals(false, rule2.Compute(obj));
		assertEquals(true, rule3.Compute(obj));
		assertEquals(false, rule4.Compute(obj));
	}

	@Test
	public void TestVarArgMatchAprx()
	{
		VarArgMatchAprx rule1 = new VarArgMatchAprx("test", "d1", "d2", GraphAttribute.EPSILON);
		VarArgMatchAprx rule2 = new VarArgMatchAprx("test", "d1", "d2", 2.0);

		assertEquals(false, rule1.Compute(obj));
		assertEquals(true, rule2.Compute(obj));
	}

	@Test
	public void TestVarArgMatch()
	{
		VarArgMatch rule1 = new VarArgMatch("test", "l1", "l2");
		VarArgMatch rule2 = new VarArgMatch("test", "str1", "str2");

		assertEquals(false, rule1.Compute(obj));
		assertEquals(true, rule2.Compute(obj));
	}

	@Test
	public void TestLinkedVarArgMatch()
	{
		LinkedVarArgMatch rule1 = new LinkedVarArgMatch("test", "b1");
		LinkedVarArgMatch rule2 = new LinkedVarArgMatch("test", "b2");
		LinkedVarArgMatch rule3 = new LinkedVarArgMatch("test", "match_num");
		LinkedVarArgMatch rule4 = new LinkedVarArgMatch("test", "mismatch_num");

		assertEquals(false, rule1.Compute(obj.GetOutLinks().get(0)));
		assertEquals(true , rule2.Compute(obj.GetOutLinks().get(0)));

		assertEquals(true , rule3.Compute(obj.GetOutLinks().get(0)));
		assertEquals(false, rule4.Compute(obj.GetOutLinks().get(0)));
	}

	@Test
	public void TestToPct()
	{
		ToArgPct rule1 = new ToArgPct("test", "l1", "l2");
		ToPct rule2 = new ToPct("test", "l2", 6);

		assertEquals(66, rule1.Compute(obj));
		assertEquals(50, rule2.Compute(obj));
	}
}
