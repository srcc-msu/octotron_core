package ru.parallel.octotron.core.model;

import org.junit.*;
import org.junit.rules.ExpectedException;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.EDependencyType;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.core.OctoRule;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.neo4j.impl.Marker;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class ModelEntityTest
{
	private static Neo4jGraph graph;

	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		ModelEntityTest.graph = new Neo4jGraph( "dbs/" + ModelEntityTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphService.Init (graph);

		SimpleAttribute[] obj_att = {
			new SimpleAttribute("object", "ok")
		};

		ModelEntityTest.obj_factory = new ObjectFactory().Constants(obj_att);

		SimpleAttribute[] link_att = {
			new SimpleAttribute("link", "ok"),
			new SimpleAttribute("type", "contain"),
		};

		ModelEntityTest.link_factory = new LinkFactory().Constants(link_att);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		ModelEntityTest.graph.Shutdown();
		ModelEntityTest.graph.Delete();
	}

	@After
	public void Clean()
	{
		GraphService.Get().Clean();
	}

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void TestDeclareAttribute() throws Exception
	{
		ModelEntity entity = ModelService.AddObject();
		entity.DeclareSensor("test", "");

		boolean catched = false;

		try
		{
			entity.DeclareSensor("test", "");
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}
		assertTrue(catched);
	}

/*	@Test
	public void TestSetTimer() throws Exception
	{
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

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
	}*/

	private class DummyRule extends OctoRule
	{
		private static final long serialVersionUID = -6085542113382606406L;
		private final EDependencyType dep_type;
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
		public Object Compute(ModelEntity entity)
		{
			return n++;
		}

		@Override
		public Object GetDefaultValue()
		{
			return 0L;
		}

		@Override
		public AttributeList<ModelAttribute> GetDependency(ModelEntity entity)
		{
			return new AttributeList<>();
		}
	}

	/*@Test
	public void TestUpdate() throws Exception
	{
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		assertEquals(0, entity.Update(EDependencyType.SELF));
		assertEquals(0, entity.Update(EDependencyType.IN));
		assertEquals(0, entity.Update(EDependencyType.OUT));

		DummyRule rule1 = new DummyRule("test2", EDependencyType.ALL);
		DummyRule rule2 = new DummyRule("test1", EDependencyType.SELF);
		DummyRule rule3 = new DummyRule("test3", EDependencyType.IN);
		DummyRule rule4 = new DummyRule("test4", EDependencyType.OUT);

		entity.DeclareVariable(rule1);
		entity.DeclareVariable(rule2);
		entity.DeclareVariable(rule3);
		entity.DeclareVariable(rule4);

		assertEquals(2, entity.Update(EDependencyType.SELF));
		assertEquals(2, entity.Update(EDependencyType.IN));
		assertEquals(2, entity.Update(EDependencyType.OUT));

		assertEquals(3, rule1.GetN());
		assertEquals(1, rule2.GetN());
		assertEquals(1, rule3.GetN());
		assertEquals(1, rule4.GetN());
	}*/

	@Test
	public void TestAddReaction()
	{
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		entity.DeclareSensor("test", 0);
		OctoReaction reaction = new OctoReaction("test", 1
			, new OctoResponse(EEventStatus.INFO, "test"));

		entity.AddReaction(reaction);
	}

	@Test
	public void TestAddReactions()
	{
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		entity.DeclareSensor("test", 0);

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
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		entity.DeclareVariable(new DummyRule("test1", EDependencyType.ALL));
	}

	@Test
	public void TestAddRules()
	{
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		entity.DeclareSensor("test", 0);

		List<OctoRule> rules = new LinkedList<>();

		rules.add(new DummyRule("test1", EDependencyType.ALL));
		rules.add(new DummyRule("test2", EDependencyType.ALL));

		entity.DeclareVariables(rules);
	}

	@Test
	public void TestAddMarker()
	{
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		entity.DeclareSensor("test", 0);
		OctoReaction reaction = new OctoReaction("test", 1
			, new OctoResponse(EEventStatus.INFO, "test"));

		entity.AddReaction(reaction);

		entity.AddMarker(reaction, "test1", true);
		entity.AddMarker(reaction, "test2", false);
	}

	@Test
	public void TestDeleteMarker()
	{
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		entity.DeclareSensor("test", 0);
		OctoReaction reaction = new OctoReaction("test", 1
			, new OctoResponse(EEventStatus.INFO, "test"));

		entity.AddReaction(reaction);

		assertEquals(0, entity.GetMarkers().size());

		long id1 = entity.AddMarker(reaction, "test1", true);
		long id2 = entity.AddMarker(reaction, "test2", false);

		assertEquals(2, entity.GetMarkers().size());

		entity.DeleteMarker("test", id1);
		assertEquals(1, entity.GetMarkers().size());

		entity.DeleteMarker("test", id2);
		assertEquals(0, entity.GetMarkers().size());
	}

	@Test
	public void TestGetReactions()
	{
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		final int N = 10;

		for(int i = 0; i < N; i++)
			entity.DeclareSensor("test" + i, 0);

		for(int i = 0; i < N; i++)
		{
			OctoReaction reaction = new OctoReaction("test" + i, 1
				, new OctoResponse(EEventStatus.INFO, "test"));

			entity.AddReaction(reaction);

			List<OctoReaction> reactions = entity.GetReactions();

			assertEquals(i + 1, reactions.size());
		}
	}


	@Test
	public void TestGetMarkers()
	{
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		entity.DeclareSensor("test", 0);
		OctoReaction reaction = new OctoReaction("test", 1
			, new OctoResponse(EEventStatus.INFO, "test"));

		entity.AddReaction(reaction);

		final int N = 10;

		for(int i = 0; i < N; i++)
		{
			entity.AddMarker(reaction, "test" + i, true);

			List<Marker> markers = entity.GetMarkers();

			assertEquals(i + 1, markers.size());

			for(int j = 0; j < i + 1; j++)
				assertEquals("test" + j, markers.get(j).GetDescription());
		}
	}

	@Test
	public void TestEquals()
	{
		ModelEntity entity1 = ModelEntityTest.obj_factory.Create();
		ModelEntity entity2 = ModelEntityTest.obj_factory.Create();

		ModelEntity entity3 = ModelEntityTest.link_factory.OneToOne(ModelEntityTest.obj_factory.Create(), ModelEntityTest.obj_factory.Create());
		ModelEntity entity4 = ModelEntityTest.link_factory.OneToOne(ModelEntityTest.obj_factory.Create(), ModelEntityTest.obj_factory.Create());

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

/*	@Test
	public void TestSetReactionState()
	{
		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		entity.DeclareConstant("test", 0);

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

		ModelEntity entity = ModelEntityTest.obj_factory.Create();

		entity.DeclareConstant("test", 0);

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
	}*/
}