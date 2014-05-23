package ru.parallel.octotron.core;

import org.junit.*;
import static org.junit.Assert.*;

import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.logic.TimerProcessor;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.EDependencyType;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.utils.OctoAttributeList;

public class OctoEntityTest
{
	private static Neo4jGraph graph;
	private static GraphService graph_service;

	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		OctoEntityTest.graph = new Neo4jGraph("dbs/test_node", Neo4jGraph.Op.RECREATE);
		OctoEntityTest.graph_service = new GraphService(OctoEntityTest.graph);

		SimpleAttribute[] obj_att = {
			new SimpleAttribute("object", "ok")
		};

		OctoEntityTest.obj_factory = new ObjectFactory(OctoEntityTest.graph_service).Attributes(obj_att);

		SimpleAttribute[] link_att = {
			new SimpleAttribute("link", "ok"),
			new SimpleAttribute("type", "contain"),
		};

		OctoEntityTest.link_factory = new LinkFactory(OctoEntityTest.graph_service).Attributes(link_att);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		OctoEntityTest.graph.Shutdown();
		OctoEntityTest.graph.Delete();
	}

	@Test
	public void TestDeclareAttribute() throws Exception
	{
		OctoEntity entity = graph_service.AddObject();
		entity.DeclareAttribute("test", "");

		boolean catched = false;

		try
		{
			entity.DeclareAttribute("test", "");
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}
		Assert.assertTrue(catched);
	}


	/**
	 * set attribute, remove it and ensure it does not exists
	 * */
	@Test
	public void TestRemoveAttribute()
	{
		OctoEntity entity = OctoEntityTest.obj_factory.Create();

		entity.DeclareAttribute("test_test", 1);
		entity.RemoveAttribute("test_test");

		Assert.assertEquals("attribute presents - wrong"
			, entity.TestAttribute("test_test"), false);
	}

	@Test
	public void TestSetTimer() throws Exception
	{
		OctoEntity entity = OctoEntityTest.obj_factory.Create();

		entity.SetTimer("timer1", 1);

		entity.SetTimer("timer2", 3);

		assertEquals(0, TimerProcessor.Process().size());

		Thread.sleep(2000); // 2 secs, 1st time must timeout

		OctoAttributeList list1 = TimerProcessor.Process();
		assertEquals(1, list1.size());
		assertEquals(entity, list1.get(0).GetParent());

		Thread.sleep(2000); // 4 secs, 2nd time must timeout

		OctoAttributeList list2 = TimerProcessor.Process();
		assertEquals(1, list2.size());
		assertEquals(entity, list2.get(0).GetParent());

		Thread.sleep(1000);
		assertEquals(0, TimerProcessor.Process().size());
	}

	private class DummySelfRule extends OctoRule
	{
		private EDependencyType dep_type;
		private long n = 0l;

		DummySelfRule(String name, EDependencyType dep_type)
		{
			super(name);
			this.dep_type = dep_type;
		}

		public long GetN()
		{
			return n;
		}

		@Override
		public Object Compute(OctoEntity entity)
		{
			return n++;
		}

		@Override
		public Object GetDefaultValue()
		{
			return 0l;
		}

		@Override
		public EDependencyType GetDeps()
		{
			return dep_type;
		}
	}

	@Test
	public void TestUpdate() throws Exception
	{
		OctoEntity entity = OctoEntityTest.obj_factory.Create();

		assertEquals(0, entity.Update(EDependencyType.SELF));
		assertEquals(0, entity.Update(EDependencyType.IN));
		assertEquals(0, entity.Update(EDependencyType.OUT));

		DummySelfRule rule1 = new DummySelfRule("test2", EDependencyType.ALL);
		DummySelfRule rule2 = new DummySelfRule("test1", EDependencyType.SELF);
		DummySelfRule rule3 = new DummySelfRule("test3", EDependencyType.IN);
		DummySelfRule rule4 = new DummySelfRule("test4", EDependencyType.OUT);

		entity.AddRule(rule1);
		entity.AddRule(rule2);
		entity.AddRule(rule3);
		entity.AddRule(rule4);

		assertEquals(2, entity.Update(EDependencyType.SELF));
		assertEquals(2, entity.Update(EDependencyType.IN));
		assertEquals(2, entity.Update(EDependencyType.OUT));

		assertEquals(3, rule1.GetN());
		assertEquals(1, rule2.GetN());
		assertEquals(1, rule3.GetN());
		assertEquals(1, rule4.GetN());
	}
}