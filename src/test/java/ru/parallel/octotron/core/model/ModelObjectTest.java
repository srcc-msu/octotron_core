package ru.parallel.octotron.core.model;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.ReactionTemplate;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.logic.impl.Equals;
import ru.parallel.octotron.core.primitive.EDependencyType;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModelObjectTest
{
	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		ModelService.Init(ModelService.EMode.CREATION);

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
		ModelService.Finish();
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
		ModelObject entity = ModelService.Get().AddObject();
		entity.GetBuilder().DeclareSensor("test", "");

		boolean catched = false;

		try
		{
			entity.GetBuilder().DeclareSensor("test", "");
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

		entity.GetBuilder().DeclareSensor("test", 0);
		ReactionTemplate reaction = new Equals("test", 1)
			.Response(new Response(EEventStatus.INFO, "test"));

		entity.GetBuilder().AddReaction(reaction);
	}

	@Test
	public void TestAddReactions()
	{
		ModelObject entity = ModelObjectTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		List<ReactionTemplate> reactions = new LinkedList<>();

		reactions.add(new Equals("test", 1)
			.Response(new Response(EEventStatus.INFO, "test")));
		reactions.add(new Equals("test", 2)
			.Response(new Response(EEventStatus.INFO, "test")));

		entity.GetBuilder().AddReaction(reactions);
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
		public AttributeList<IModelAttribute> GetDependency(ModelEntity entity)
		{
			return new AttributeList<>();
		}
	}

	@Test
	public void TestAddRule()
	{
		ModelObject entity = ModelObjectTest.obj_factory.Create();

		entity.GetBuilder().DeclareVar(new DummyRule("test1", EDependencyType.ALL));
	}

	@Test
	public void TestAddRules()
	{
		ModelObject entity = ModelObjectTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		List<ru.parallel.octotron.core.logic.Rule> rules = new LinkedList<>();

		rules.add(new DummyRule("test1", EDependencyType.ALL));
		rules.add(new DummyRule("test2", EDependencyType.ALL));

		entity.GetBuilder().DeclareVar(rules);
	}

	@Test
	public void TestAddMarker()
	{
		ModelObject entity = ModelObjectTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		ReactionTemplate reaction = new Equals("test", 1)
			.Response(new Response(EEventStatus.INFO, "test"));

		entity.GetBuilder().AddReaction(reaction);

//		reaction.AddMarker("test1", true);
//		reaction.AddMarker("test2", false);
	}

	@Test
	public void TestDeleteMarker()
	{
		ModelObject entity = ModelObjectTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);
		ReactionTemplate reaction = new Equals("test", 1)
			.Response(new Response(EEventStatus.INFO, "test"));

		entity.GetBuilder().AddReaction(reaction);

/*		assertEquals(0, reaction.GetMarkers().size());

		long id1 = reaction.AddMarker("test1", true);
		long id2 = reaction.AddMarker("test2", false);

		assertEquals(2, reaction.GetMarkers().size());

		reaction.DeleteMarker(id1);
		assertEquals(1, reaction.GetMarkers().size());

		reaction.DeleteMarker(id2);
		assertEquals(0, reaction.GetMarkers().size());*/
	}

	@Test
	public void TestGetMarkers()
	{
		ModelObject entity = ModelObjectTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);
		ReactionTemplate reaction = new Equals("test", 1)
			.Response(new Response(EEventStatus.INFO, "test"));

		entity.GetBuilder().AddReaction(reaction);

		final int N = 10;

	/*	for(int i = 0; i < N; i++)
		{
			reaction.AddMarker("test" + i, true);

			Collection<Marker> markers = reaction.GetMarkers();

			assertEquals(i + 1, markers.size());

			int j = 0;

			for(Marker marker : markers)
			{
				assertEquals("test" + j, marker.GetDescription());
				j++;
			}

			assertEquals(i+1, j);
		}*/
	}
}
