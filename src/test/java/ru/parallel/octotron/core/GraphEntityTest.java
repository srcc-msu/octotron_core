package ru.parallel.octotron.core;

import org.junit.*;
import static org.junit.Assert.*;

import org.junit.rules.ExpectedException;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.logic.TimerProcessor;
import ru.parallel.octotron.neo4j.impl.Marker;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.core.primitive.EDependencyType;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.graph.collections.EntityList;

import java.util.LinkedList;
import java.util.List;

public class GraphEntityTest
{
	private static Neo4jGraph graph;
	private static GraphService graph_service;

	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		GraphEntityTest.graph = new Neo4jGraph( "dbs/" + GraphEntityTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphEntityTest.graph_service = new GraphService(GraphEntityTest.graph);

		SimpleAttribute[] obj_att = {
			new SimpleAttribute("object", "ok")
		};

		GraphEntityTest.obj_factory = new ObjectFactory(GraphEntityTest.graph_service).Attributes(obj_att);

		SimpleAttribute[] link_att = {
			new SimpleAttribute("link", "ok"),
			new SimpleAttribute("type", "contain"),
		};

		GraphEntityTest.link_factory = new LinkFactory(GraphEntityTest.graph_service).Attributes(link_att);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		GraphEntityTest.graph.Shutdown();
		GraphEntityTest.graph.Delete();
	}

	@Rule
	public ExpectedException exception = ExpectedException.none();


	@Test
	public void TestDeclareAttribute() throws Exception
	{
		GraphEntity entity = graph_service.AddObject();
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
		assertTrue(catched);
	}


	/**
	 * set attribute, remove it and ensure it does not exists
	 * */
	@Test
	public void TestRemoveAttribute()
	{
		GraphEntity entity = GraphEntityTest.obj_factory.Create();

		entity.DeclareAttribute("test_test", 1);
		entity.RemoveAttribute("test_test");

		assertEquals("attribute presents - wrong"
			, entity.TestAttribute("test_test"), false);
	}

	@Test
	public void TestSetTimer() throws Exception
	{
		GraphEntity entity = GraphEntityTest.obj_factory.Create();

		entity.SetTimer("timer1", 1);

		entity.SetTimer("timer2", 3);

		assertEquals(0, TimerProcessor.Process().size());

		Thread.sleep(2000); // 2 secs, 1st time must timeout

		EntityList list1 = TimerProcessor.Process();
		assertEquals(1, list1.size());
		assertEquals(entity, list1.get(0));

		Thread.sleep(2000); // 4 secs, 2nd time must timeout

		EntityList list2 = TimerProcessor.Process();
		assertEquals(1, list2.size());
		assertEquals(entity, list2.get(0));

		Thread.sleep(1000);
		assertEquals(0, TimerProcessor.Process().size());

		entity.SetTimer("timer1", 1);

		Thread.sleep(2000); // 2 secs, 1st time must timeout

		EntityList list3 = TimerProcessor.Process();
		assertEquals(1, list3.size());
		assertEquals(entity, list3.get(0));
	}

	private class DummyRule extends OctoRule
	{
		private static final long serialVersionUID = -6085542113382606406L;
		private EDependencyType dep_type;
		private long n = 0L;

		DummyRule(String name, EDependencyType dep_type)
		{
			super(name);
			this.dep_type = dep_type;
		}

		public long GetN()
		{
			return n;
		}

		@Override
		public Object Compute(GraphEntity entity)
		{
			return n++;
		}

		@Override
		public Object GetDefaultValue()
		{
			return 0L;
		}

		@Override
		public EDependencyType GetDependency()
		{
			return dep_type;
		}
	}

	@Test
	public void TestUpdate() throws Exception
	{
		GraphEntity entity = GraphEntityTest.obj_factory.Create();

		assertEquals(0, entity.Update(EDependencyType.SELF));
		assertEquals(0, entity.Update(EDependencyType.IN));
		assertEquals(0, entity.Update(EDependencyType.OUT));

		DummyRule rule1 = new DummyRule("test2", EDependencyType.ALL);
		DummyRule rule2 = new DummyRule("test1", EDependencyType.SELF);
		DummyRule rule3 = new DummyRule("test3", EDependencyType.IN);
		DummyRule rule4 = new DummyRule("test4", EDependencyType.OUT);

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

	@Test
	public void TestAddReaction()
	{
		GraphEntity entity = GraphEntityTest.obj_factory.Create();

		entity.DeclareAttribute("test", 0);
		OctoReaction reaction = new OctoReaction("test", 1
			, new OctoResponse(EEventStatus.INFO, "test"));

		entity.AddReaction(reaction);
	}

	@Test
	public void TestAddReactions()
	{
		GraphEntity entity = GraphEntityTest.obj_factory.Create();

		entity.DeclareAttribute("test", 0);

		List<OctoReaction> reactions = new LinkedList<>();

		reactions.add(new OctoReaction("test", 1
			, new OctoResponse(EEventStatus.INFO, "test")));
		reactions.add(new OctoReaction("test", 2
			, new OctoResponse(EEventStatus.INFO, "test")));

		entity.AddReactions(reactions);
	}

	@Test
	public void TestAddRule()
	{
		GraphEntity entity = GraphEntityTest.obj_factory.Create();

		entity.AddRule(new DummyRule("test1", EDependencyType.ALL));
	}

	@Test
	public void TestAddRules()
	{
		GraphEntity entity = GraphEntityTest.obj_factory.Create();

		entity.DeclareAttribute("test", 0);

		List<OctoRule> rules = new LinkedList<>();

		rules.add(new DummyRule("test1", EDependencyType.ALL));
		rules.add(new DummyRule("test2", EDependencyType.ALL));

		entity.AddRules(rules);
	}

	@Test
	public void TestAddMarker()
	{
		GraphEntity entity = GraphEntityTest.obj_factory.Create();

		entity.DeclareAttribute("test", 0);
		OctoReaction reaction = new OctoReaction("test", 1
			, new OctoResponse(EEventStatus.INFO, "test"));

		entity.AddReaction(reaction);

		entity.AddMarker(reaction.GetID(), "test1", true);
		entity.AddMarker(reaction.GetID(), "test2", false);
	}

	@Test
	public void TestDeleteMarker()
	{
		GraphEntity entity = GraphEntityTest.obj_factory.Create();

		entity.DeclareAttribute("test", 0);
		OctoReaction reaction = new OctoReaction("test", 1
			, new OctoResponse(EEventStatus.INFO, "test"));

		entity.AddReaction(reaction);

		assertEquals(0, entity.GetMarkers().size());

		long id1 = entity.AddMarker(reaction.GetID(), "test1", true);
		long id2 = entity.AddMarker(reaction.GetID(), "test2", false);

		assertEquals(2, entity.GetMarkers().size());

		entity.DeleteMarker(id1);
		assertEquals(1, entity.GetMarkers().size());

		entity.DeleteMarker(id2);
		assertEquals(0, entity.GetMarkers().size());
	}


	@Test
	public void TestGetRule()
	{
		GraphEntity entity = GraphEntityTest.obj_factory.Create();

		final int N = 10;

		for(int i = 0; i < N; i++)
		{
			entity.AddRule(new DummyRule("test" + i, EDependencyType.ALL));

			List<OctoRule> rules = entity.GetRules();

			assertEquals(i + 1, rules.size());

			for(int j = 0; j < i + 1; j++)
				assertEquals("test" + j, rules.get(j).GetAttribute());
		}
	}

	@Test
	public void TestGetReactions()
	{
		GraphEntity entity = GraphEntityTest.obj_factory.Create();
		entity.DeclareAttribute("test", 0);

		final int N = 10;

		for(int i = 0; i < N; i++)
		{
			OctoReaction reaction = new OctoReaction("test" + i, 1
				, new OctoResponse(EEventStatus.INFO, "test"));

			entity.AddReaction(reaction);

			List<OctoReaction> reactions = entity.GetReactions();

			assertEquals(i + 1, reactions.size());

			for(int j = 0; j < i + 1; j++)
				assertEquals("test" + j, reactions.get(j).GetCheckName());
		}
	}


	@Test
	public void TestGetMarkers()
	{
		GraphEntity entity = GraphEntityTest.obj_factory.Create();

		entity.DeclareAttribute("test", 0);
		OctoReaction reaction = new OctoReaction("test", 1
			, new OctoResponse(EEventStatus.INFO, "test"));

		entity.AddReaction(reaction);

		final int N = 10;

		for(int i = 0; i < N; i++)
		{
			entity.AddMarker(reaction.GetID(), "test" + i, true);

			List<Marker> markers = entity.GetMarkers();

			assertEquals(i + 1, markers.size());

			for(int j = 0; j < i + 1; j++)
				assertEquals("test" + j, markers.get(j).GetDescription());
		}
	}

	@Test
	public void TestEquals()
	{
		GraphEntity entity1 = GraphEntityTest.obj_factory.Create();
		GraphEntity entity2 = GraphEntityTest.obj_factory.Create();

		GraphEntity entity3 = GraphEntityTest.link_factory.OneToOne(GraphEntityTest.obj_factory.Create(), GraphEntityTest.obj_factory.Create());
		GraphEntity entity4 = GraphEntityTest.link_factory.OneToOne(GraphEntityTest.obj_factory.Create(), GraphEntityTest.obj_factory.Create());

		assertTrue (entity1.equals(entity1));
		assertFalse(entity1.equals(entity2));
		assertFalse(entity1.equals(entity3));
		assertFalse(entity1.equals(entity4));

		assertFalse(entity2.equals(entity1));
		assertTrue(entity2.equals(entity2));
		assertFalse(entity2.equals(entity3));
		assertFalse(entity2.equals(entity4));

		assertFalse(entity3.equals(entity1));
		assertFalse(entity3.equals(entity2));
		assertTrue(entity3.equals(entity3));
		assertFalse(entity3.equals(entity4));

		assertFalse(entity4.equals(entity1));
		assertFalse(entity4.equals(entity2));
		assertFalse(entity4.equals(entity3));
		assertTrue(entity4.equals(entity4));
	}

	@Test
	public void TestSetReactionState()
	{
		GraphEntity entity = GraphEntityTest.obj_factory.Create();

		entity.DeclareAttribute("test", 0);

		OctoReaction reaction = new OctoReaction("test", 1
			, new OctoResponse(EEventStatus.INFO, "test"));

		entity.AddReaction(reaction);

		entity.SetReactionState(reaction.GetID(), 0);
		entity.SetReactionState(reaction.GetID(), 1);
	}

	@Test
	public void TestGetReactionState()
	{
		final int N = 4;

		GraphEntity entity = GraphEntityTest.obj_factory.Create();

		entity.DeclareAttribute("test", 0);

		OctoReaction reaction1 = new OctoReaction("test", 1
			, new OctoResponse(EEventStatus.INFO, "test"));

		OctoReaction reaction2 = new OctoReaction("test", 1
			, new OctoResponse(EEventStatus.INFO, "test"));

		entity.AddReaction(reaction1);
		entity.AddReaction(reaction2);

		for(int i = 0; i < N; i++)
		{
			entity.SetReactionState(reaction1.GetID(), i);
			entity.SetReactionState(reaction2.GetID(), N-i);

			assertEquals(i, entity.GetReactionState(reaction1.GetID()));
			assertEquals(N-i, entity.GetReactionState(reaction2.GetID()));
		}
	}
}