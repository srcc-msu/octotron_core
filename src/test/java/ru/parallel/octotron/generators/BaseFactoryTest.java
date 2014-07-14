package ru.parallel.octotron.generators;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.*;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.rules.Match;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * test some common cases - attributes, factories
 * */
public class BaseFactoryTest
{
	private static Neo4jGraph graph;
	private static GraphService graph_service;

	private static final int N = 10; // some testing param

	@BeforeClass
	public static void Init() throws Exception
	{
		BaseFactoryTest.graph = new Neo4jGraph( "dbs/"
			+ BaseFactoryTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		BaseFactoryTest.graph_service = new GraphService(BaseFactoryTest.graph);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		BaseFactoryTest.graph.Shutdown();
		BaseFactoryTest.graph.Delete();
	}

/**
 * check that object factory creates required amount of objects
 * with given property
 * */
	@Test
	public void TestAttributes()
	{
		SimpleAttribute[] attributes = { new SimpleAttribute("test1", 0) };
		SimpleAttribute attr2 = new SimpleAttribute("test2", 1);
		SimpleAttribute attr3 = new SimpleAttribute("test3", 2);

		ObjectFactory f1 = new ObjectFactory(BaseFactoryTest.graph_service)
			.Attributes(attributes).Attributes(attr2, attr3);
		LinkFactory f2 = new LinkFactory(BaseFactoryTest.graph_service)
			.Attributes(attr2, attr3).Attributes(attributes);

		GraphObject obj = f1.Create();
		GraphLink link = f2.Attributes(new SimpleAttribute("type", "1"))
			.OneToOne(f1.Create(), f1.Create());

		assertTrue(obj.TestAttribute("test1"));
		assertTrue(obj.TestAttribute("test2"));
		assertTrue(obj.TestAttribute("test3"));

		assertTrue("missing link attribute", link.TestAttribute("test1"));
		assertTrue("missing link attribute", link.TestAttribute("test2"));
		assertTrue("missing link attribute", link.TestAttribute("test3"));
	}

	@Test
	public void TestRules()
	{
		OctoRule[] rules = { new Match("test1", "", "") };
		OctoRule rule2 = new Match("test2", "", "");
		OctoRule rule3 = new Match("test3", "", "");

		ObjectFactory f1 = new ObjectFactory(BaseFactoryTest.graph_service)
			.Rules(rules).Rules(rule2, rule3);
		LinkFactory f2 = new LinkFactory(BaseFactoryTest.graph_service)
			.Rules(rule2, rule3).Rules(rules);

		GraphObject obj = f1.Create();
		GraphLink link = f2.Attributes(new SimpleAttribute("type", "1"))
			.OneToOne(f1.Create(), f1.Create());

		assertEquals(3, obj.GetRules().size());
		assertEquals("test1", obj.GetRules().get(0).GetAttribute());
		assertEquals("test2", obj.GetRules().get(1).GetAttribute());
		assertEquals("test3", obj.GetRules().get(2).GetAttribute());

		assertEquals(3, link.GetRules().size());
		assertEquals("test2", link.GetRules().get(0).GetAttribute());
		assertEquals("test3", link.GetRules().get(1).GetAttribute());
		assertEquals("test1", link.GetRules().get(2).GetAttribute());
	}

	@Test
	public void TestReactions()
	{
		OctoReaction[] reactions = { new OctoReaction("test1", 0, new OctoResponse(EEventStatus.INFO, "")) };
		OctoReaction reaction2 = new OctoReaction("test2", 0, new OctoResponse(EEventStatus.INFO, ""));
		OctoReaction reaction3 = new OctoReaction("test3", 0, new OctoResponse(EEventStatus.INFO, ""));

		ObjectFactory f1 = new ObjectFactory(BaseFactoryTest.graph_service)
			.Reactions(reactions).Reactions(reaction2, reaction3);
		LinkFactory f2 = new LinkFactory(BaseFactoryTest.graph_service)
			.Reactions(reaction2, reaction3).Reactions(reactions);

		GraphObject obj = f1.Create();
		GraphLink link = f2.Attributes(new SimpleAttribute("type", "1"))
			.OneToOne(f1.Create(), f1.Create());

		assertEquals(3, obj.GetReactions().size());
		assertEquals("test1", obj.GetReactions().get(0).GetCheckName());
		assertEquals("test2", obj.GetReactions().get(1).GetCheckName());
		assertEquals("test3", obj.GetReactions().get(2).GetCheckName());

		assertEquals(3, link.GetReactions().size());
		assertEquals("test2", link.GetReactions().get(0).GetCheckName());
		assertEquals("test3", link.GetReactions().get(1).GetCheckName());
		assertEquals("test1", link.GetReactions().get(2).GetCheckName());
	}

}
