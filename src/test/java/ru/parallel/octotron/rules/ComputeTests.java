package ru.parallel.octotron.rules;

import org.junit.*;
import static org.junit.Assert.*;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.core.OctoAttribute;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.EDependencyType;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;

public class ComputeTests
{
	private static GraphService graph_service;
	private static Neo4jGraph graph;
	private static OctoObject obj;

	@BeforeClass
	public static void Init()
		throws ExceptionSystemError
	{
		ComputeTests.graph = new Neo4jGraph( "dbs/" + ComputeTests.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		ComputeTests.graph_service = new GraphService(ComputeTests.graph);

		ObjectFactory in = new ObjectFactory(graph_service)
			.Attributes(new SimpleAttribute("d1", 10.0))
			.Attributes(new SimpleAttribute("d2", 11.0))
			.Attributes(new SimpleAttribute("l1", 20))
			.Attributes(new SimpleAttribute("l2", 21))
			.Attributes(new SimpleAttribute("b1", true))
			.Attributes(new SimpleAttribute("b2", true))
			.Attributes(new SimpleAttribute("str1", "yes"))
			.Attributes(new SimpleAttribute("str2", "yes"));

		ObjectFactory out = new ObjectFactory(graph_service)
			.Attributes(new SimpleAttribute("d1", 20.0))
			.Attributes(new SimpleAttribute("d2", 21.0))
			.Attributes(new SimpleAttribute("l1", 10))
			.Attributes(new SimpleAttribute("l2", 11))
			.Attributes(new SimpleAttribute("b1", false))
			.Attributes(new SimpleAttribute("b2", false))
			.Attributes(new SimpleAttribute("str1", "no"))
			.Attributes(new SimpleAttribute("str2", "no"));

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
			.Attributes(new SimpleAttribute("bf2", false));

		obj = self.Create();

		LinkFactory links = new LinkFactory(graph_service)
			.Attributes(new SimpleAttribute("type", "test"));

		links.EveryToOne(in.Create(3), obj);

		links.OneToEvery(obj, out.Create(4));

// hack to avoid problems with ctime check
		for(OctoObject obj : graph_service.GetAllObjects())
			for(OctoAttribute attribute : obj.GetAttributes())
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

		assertEquals(  1.0, (Double)self_rule.Compute(obj), OctoAttribute.EPSILON);
		assertEquals( 63.0, (Double)  in_rule.Compute(obj), OctoAttribute.EPSILON);
		assertEquals(164.0, (Double) out_rule.Compute(obj), OctoAttribute.EPSILON);
		assertEquals(228.0, (Double) all_rule.Compute(obj), OctoAttribute.EPSILON);
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
		ArgMatchAprx rule1 = new ArgMatchAprx("test", "d1", "d2", OctoAttribute.EPSILON);
		ArgMatchAprx rule2 = new ArgMatchAprx("test", "d1", "d2", 2.0);

		assertEquals(false, rule1.Compute(obj));
		assertEquals(true, rule2.Compute(obj));
	}

	@Test
	public void TestArgMatch()
	{
		ArgMatch rule1 = new ArgMatch("test", "l1", "l2");
		ArgMatch rule2 = new ArgMatch("test", "str1", "str2");

		assertEquals(false, rule1.Compute(obj));
		assertEquals(true, rule2.Compute(obj));
	}

	@Test
	public void TestContainsString()
	{
		ContainsString rule1 = new ContainsString("test", "str1", "may");
		ContainsString rule2 = new ContainsString("test", "str2", "bee");

		assertEquals(true, rule1.Compute(obj));
		assertEquals(false, rule2.Compute(obj));
	}

	@Test
	public void TestLogicalAnd()
	{
		LogicalAnd rule1 = new LogicalAnd("test", "bt1", "bt2");
		LogicalAnd rule2 = new LogicalAnd("test", "bt1", "bt2", "bf1");
		LogicalAnd rule3 = new LogicalAnd("test", "bf2");
		LogicalAnd rule4 = new LogicalAnd("test", "bf1", "bf2");

		assertEquals(true, rule1.Compute(obj));
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

		assertEquals(true, rule1.Compute(obj));
		assertEquals(true, rule2.Compute(obj));
		assertEquals(true, rule3.Compute(obj));
		assertEquals(false, rule4.Compute(obj));
	}

	@Test
	public void TestLowerArgThreshold()
	{
		LowerArgThreshold rule1 = new LowerArgThreshold("test", "l1", "l2");
		LowerArgThreshold rule2 = new LowerArgThreshold("test", "l2", "l1");
		LowerArgThreshold rule3 = new LowerArgThreshold("test", "d1", "d2");
		LowerArgThreshold rule4 = new LowerArgThreshold("test", "d2", "d1");
	}

	@Test
	public void TestLowerThreshold()
	{
	}

	@Test
	public void TestMatchAprx()
	{
	}

	@Test
	public void TestMatch()
	{
	}

	@Test
	public void TestMirrorBoolean()
	{
	}

	@Test
	public void TestMirrorDouble()
	{
	}

	@Test
	public void TestMirror()
	{
	}

	@Test
	public void TestMirrorLong()
	{
	}

	@Test
	public void TestMirrorString()
	{
	}

	@Test
	public void TestNotMatch()
	{
	}

	@Test
	public void TestUpdatedRecently()
	{
	}

	@Test
	public void TestUpperArgThreshold()
	{
	}

	@Test
	public void TestUpperThreshold()
	{
	}

	@Test
	public void TestVarArgMatchAprx()
	{
	}

	@Test
	public void TestVarArgMatch()
	{
	}
}