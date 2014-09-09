package ru.parallel.octotron.generators;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.logic.impl.Equals;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.rules.Match;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BaseFactoryTest
{
	private static Neo4jGraph graph;

	private static ObjectFactory object_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		BaseFactoryTest.graph = new Neo4jGraph( "dbs/"
			+ BaseFactoryTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE, true);
		GraphService.Init(BaseFactoryTest.graph);

		object_factory = new ObjectFactory();
		link_factory = new LinkFactory();
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

		ObjectFactory f1 = object_factory
			.Constants(attributes).Constants(attr2, attr3);
		LinkFactory f2 = link_factory
			.Constants(attr2, attr3).Constants(attributes);

		ModelObject obj = f1.Create();
		ModelLink link = f2.Constants(new SimpleAttribute("type", "1"))
			.OneToOne(f1.Create(), f1.Create());

		assertTrue(obj.TestAttribute("test1"));
		assertTrue(obj.TestAttribute("test2"));
		assertTrue(obj.TestAttribute("test3"));

		assertTrue(link.TestAttribute("test1"));
		assertTrue(link.TestAttribute("test2"));
		assertTrue(link.TestAttribute("test3"));
	}

	@Test
	public void TestVaryings()
	{
		Rule[] rules = { new Match("test1", "", "") };
		Rule rule2 = new Match("test2", "", "");
		Rule rule3 = new Match("test3", "", "");

		ObjectFactory f1 = object_factory
			.Varyings(rules).Varyings(rule2, rule3);
		LinkFactory f2 = link_factory
			.Varyings(rule2, rule3).Varyings(rules);

		ModelObject obj = f1.Create();
		ModelLink link = f2.Constants(new SimpleAttribute("type", "1"))
			.OneToOne(f1.Create(), f1.Create());

		assertTrue(obj.TestAttribute("test1"));
		assertTrue(obj.TestAttribute("test2"));
		assertTrue(obj.TestAttribute("test3"));

		assertTrue(link.TestAttribute("test1"));
		assertTrue(link.TestAttribute("test2"));
		assertTrue(link.TestAttribute("test3"));
	}

	@Test
	public void TestReactions()
	{
		Reaction[] reactions = { new Equals("test2", 0).Response(new Response(EEventStatus.INFO, "")) };
		Reaction reaction2 = new Equals("test3", 0).Response(new Response(EEventStatus.INFO, ""));
		Reaction reaction3 = new Equals("test3", 0).Response(new Response(EEventStatus.INFO, ""));

		ObjectFactory f1 = object_factory
			.Sensors(new SimpleAttribute("test1", 0))
			.Sensors(new SimpleAttribute("test2", 0))
			.Sensors(new SimpleAttribute("test3", 0))
			.Reactions(reactions).Reactions(reaction2, reaction3);

		LinkFactory f2 = link_factory
			.Sensors(new SimpleAttribute("test1", 0))
			.Sensors(new SimpleAttribute("test2", 0))
			.Sensors(new SimpleAttribute("test3", 0))
			.Reactions(reaction2, reaction3).Reactions(reactions);

		ModelObject obj = f1.Create();

		ModelLink link = f2.Constants(new SimpleAttribute("type", "test"))
			.OneToOne(f1.Create(), f1.Create());

		assertEquals(0, obj.GetMetaAttribute("test1").GetReactions().size());
		assertEquals(1, obj.GetMetaAttribute("test2").GetReactions().size());
		assertEquals(2, obj.GetMetaAttribute("test3").GetReactions().size());

		assertEquals(0, link.GetMetaAttribute("test1").GetReactions().size());
		assertEquals(1, link.GetMetaAttribute("test2").GetReactions().size());
		assertEquals(2, link.GetMetaAttribute("test3").GetReactions().size());
	}
}
