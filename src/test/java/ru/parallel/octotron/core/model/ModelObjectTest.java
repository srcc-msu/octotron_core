package ru.parallel.octotron.core.model;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.logic.Marker;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.logic.impl.Equals;
import ru.parallel.octotron.core.model.impl.meta.ReactionObject;
import ru.parallel.octotron.core.primitive.EDependencyType;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModelObjectTest
{
	private static Neo4jGraph graph;

	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		ModelObjectTest.graph = new Neo4jGraph( "dbs/" + ModelObjectTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphService.Init(ModelObjectTest.graph);

		SimpleAttribute[] obj_att = {
			new SimpleAttribute("object", "ok")
		};

		ModelObjectTest.obj_factory = new ObjectFactory().Constants(obj_att);

		SimpleAttribute[] link_att = {
			new SimpleAttribute("link", "ok"),
			new SimpleAttribute("type", "contain"),
		};

		ModelObjectTest.link_factory = new LinkFactory().Constants(link_att);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		ModelObjectTest.graph.Shutdown();
		ModelObjectTest.graph.Delete();
	}

	/**
	 * check in links
	 * currently checks only if count matches, it is not correct
	 * */
	@Test
	public void TestGetInLinks()
	{
		int N = 10;

		ModelObject node = ModelObjectTest.obj_factory.Create();

		ModelObjectTest.link_factory.EveryToOne(ModelObjectTest.obj_factory.Create(N), node);

		assertEquals("in links not match, any type"
			, node.GetInLinks().size(), N);

		assertEquals("in links not match, exact type"
			, node.GetInLinks().Filter("link").size(), N);

		assertEquals("in links not match, exact type and value"
			, node.GetInLinks().Filter("link", "ok").size(), N);

		assertEquals("in links not match, exact type, wrong value"
			, node.GetInLinks().Filter("link", "fail").size(), 0);

		assertEquals("in links not match, wrong type, wrong value"
			, node.GetInLinks().Filter("fail", "fail").size(), 0);
	}

	@Test
	public void TestGetInNeighborsParam()
	{
		int N = 10;

		ModelObject node = ModelObjectTest.obj_factory.Create();

		ModelObjectTest.link_factory.EveryToOne(ModelObjectTest.obj_factory.Create(N), node);

		assertEquals("in links not match, any type"
			, node.GetInNeighbors().size(), N);

		assertEquals("in links not match, exact type"
			, node.GetInNeighbors("link").size(), N);

		assertEquals("in links not match, exact type and value"
			, node.GetInNeighbors("link", "ok").size(), N);

		assertEquals("in links not match, exact type, wrong value"
			, node.GetInNeighbors("link", "fail").size(), 0);

		assertEquals("in links not match, wrong type, wrong value"
			, node.GetInNeighbors("fail", "fail").size(), 0);
	}

	@Test
	public void TestGetOutNeighborsParam()
	{
		int N = 10;

		ModelObject node = ModelObjectTest.obj_factory.Create();

		ModelObjectTest.link_factory.OneToEvery(node, ModelObjectTest.obj_factory.Create(N));

		assertEquals("in links not match, any type"
			, node.GetOutNeighbors().size(), N);

		assertEquals("in links not match, exact type"
			, node.GetOutNeighbors("link").size(), N);

		assertEquals("in links not match, exact type and value"
			, node.GetOutNeighbors("link", "ok").size(), N);

		assertEquals("in links not match, exact type, wrong value"
			, node.GetOutNeighbors("link", "fail").size(), 0);

		assertEquals("in links not match, wrong type, wrong value"
			, node.GetOutNeighbors("fail", "fail").size(), 0);
	}

	@Test
	public void TestGetOutLinks()
	{
		int N = 10;

		ModelObject node = ModelObjectTest.obj_factory.Create();

		ModelObjectTest.link_factory.OneToEvery(node, ModelObjectTest.obj_factory.Create(N));

		assertEquals("out links not match, any type"
			, node.GetOutLinks().size(), N);

		assertEquals("out links not match, exact type"
			, node.GetOutLinks().Filter("link").size(), N);

		assertEquals("out links not match, exact type and value"
			, node.GetOutLinks().Filter("link", "ok").size(), N);

		assertEquals("out links not match, exact type, wrong value"
			, node.GetOutLinks().Filter("link", "fail").size(), 0);

		assertEquals("out links not match, wrong type, wrong value"
			, node.GetOutLinks().Filter("fail", "fail").size(), 0);
	}

	/**
	 * check different neighbors
	 * currently checks only if count matches, it is not correct
	 * do not check links
	 * */
	@Test
	public void TestGetInNeighbors()
	{
		int N = 10;

		ModelObject node = ModelObjectTest.obj_factory.Create();

		ModelObjectTest.link_factory.EveryToOne(ModelObjectTest.obj_factory.Create(N), node);

		assertEquals(N
			, node.GetInNeighbors().size(), N);

		assertEquals(N
			, node.GetInNeighbors().Filter("object").size(), N);

		assertEquals(N
			, node.GetInNeighbors().Filter("object", "ok").size());

		assertEquals(0
			, node.GetInNeighbors().Filter("object", "fail").size());

		assertEquals(0
			, node.GetInNeighbors().Filter("fail", "fail").size());
	}

	@Test
	public void TestGetOutNeighbors()
	{
		int N = 10;

		ModelObject node = ModelObjectTest.obj_factory.Create();

		ModelObjectTest.link_factory.OneToEvery(node, ModelObjectTest.obj_factory.Create(N));

		assertEquals("in neighbors not match, any type"
			, node.GetOutNeighbors()
				.size(), N);

		assertEquals("in neighbors not match, exact type"
			, node.GetOutNeighbors()
				.Filter("object").size(), N);

		assertEquals("in neighbors not match, exact type and value"
			, node.GetOutNeighbors()
				.Filter("object", "ok").size(), N);

		assertEquals("in neighbors not match, exact type, wrong value"
			, node.GetOutNeighbors()
				.Filter("object", "fail").size(), 0);

		assertEquals("in neighbors not match, wrong type, wrong value"
			, node.GetOutNeighbors()
				.Filter("fail", "fail").size(), 0);
	}


	@Test
	public void TestDeclareAttribute() throws Exception
	{
		ModelObject entity = ModelService.AddObject();
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

	@Test
	public void TestAddReaction()
	{
		ModelObject entity = ModelObjectTest.obj_factory.Create();

		entity.DeclareSensor("test", 0);
		Reaction reaction = new Equals("test", 1)
			.Response(new Response(EEventStatus.INFO, "test"));

		entity.AddReaction(reaction);
	}

	@Test
	public void TestAddReactions()
	{
		ModelObject entity = ModelObjectTest.obj_factory.Create();

		entity.DeclareSensor("test", 0);

		List<Reaction> reactions = new LinkedList<>();

		reactions.add(new Equals("test", 1)
			.Response(new Response(EEventStatus.INFO, "test")));
		reactions.add(new Equals("test", 2)
			.Response(new Response(EEventStatus.INFO, "test")));

		entity.AddReactions(reactions);
	}

	private class DummyRule extends ru.parallel.octotron.core.logic.Rule
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
		public AttributeList<IMetaAttribute> GetDependency(ModelEntity entity)
		{
			return new AttributeList<>();
		}
	}

	@Test
	public void TestAddRule()
	{
		ModelObject entity = ModelObjectTest.obj_factory.Create();

		entity.DeclareVarying(new DummyRule("test1", EDependencyType.ALL));
	}

	@Test
	public void TestAddRules()
	{
		ModelObject entity = ModelObjectTest.obj_factory.Create();

		entity.DeclareSensor("test", 0);

		List<ru.parallel.octotron.core.logic.Rule> rules = new LinkedList<>();

		rules.add(new DummyRule("test1", EDependencyType.ALL));
		rules.add(new DummyRule("test2", EDependencyType.ALL));

		entity.DeclareVaryings(rules);
	}

	@Test
	public void TestAddMarker()
	{
		ModelObject entity = ModelObjectTest.obj_factory.Create();

		entity.DeclareSensor("test", 0);
		Reaction reaction = new Equals("test", 1)
			.Response(new Response(EEventStatus.INFO, "test"));

		entity.AddReaction(reaction);

		entity.AddMarker(reaction, "test1", true);
		entity.AddMarker(reaction, "test2", false);
	}

	@Test
	public void TestDeleteMarker()
	{
		ModelObject entity = ModelObjectTest.obj_factory.Create();

		entity.DeclareSensor("test", 0);
		Reaction reaction = new Equals("test", 1)
			.Response(new Response(EEventStatus.INFO, "test"));

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
		ModelObject entity = ModelObjectTest.obj_factory.Create();

		final int N = 10;

		for(int i = 0; i < N; i++)
			entity.DeclareSensor("test" + i, 0);

		for(int i = 0; i < N; i++)
		{
			Reaction reaction = new Equals("test" + i, 1)
				.Response(new Response(EEventStatus.INFO, "test"));

			entity.AddReaction(reaction);

			List<ReactionObject> reactions = entity.GetReactions();

			assertEquals(i + 1, reactions.size());
		}
	}


	@Test
	public void TestGetMarkers()
	{
		ModelObject entity = ModelObjectTest.obj_factory.Create();

		entity.DeclareSensor("test", 0);
		Reaction reaction = new Equals("test", 1)
			.Response(new Response(EEventStatus.INFO, "test"));

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
}
