package ru.parallel.octotron.core.model;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

	@org.junit.Rule
	public ExpectedException exception = ExpectedException.none();

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