package ru.parallel.octotron.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import static org.junit.Assert.assertEquals;

public class OctoLinkTest
{
	private static Neo4jGraph graph;
	private static GraphService graph_service;
	private static ModelService model_service;

	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		OctoLinkTest.graph = new Neo4jGraph( "dbs/" + OctoLinkTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		OctoLinkTest.graph_service = new GraphService(OctoLinkTest.graph);
		OctoLinkTest.model_service = new ModelService(OctoLinkTest.graph_service);

		SimpleAttribute[] obj_att = {
			new SimpleAttribute("object", "ok")
		};

		OctoLinkTest.obj_factory = new ObjectFactory(OctoLinkTest.model_service).Constants(obj_att);

		SimpleAttribute[] link_att = {
			new SimpleAttribute("link", "ok"),
			new SimpleAttribute("type", "contain"),
		};

		OctoLinkTest.link_factory = new LinkFactory(OctoLinkTest.model_service).Constants(link_att);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		OctoLinkTest.graph.Shutdown();
		OctoLinkTest.graph.Delete();
	}

	@Test
	public void TestSource()
	{
		ModelObject object1 = obj_factory.Create();
		ModelObject object2 = obj_factory.Create();

		ModelLink link1 = link_factory.OneToOne(object1, object2);
		ModelLink link2 = link_factory.OneToOne(object2, object1);

		assertEquals(object1, link1.Source());
		assertEquals(object2, link2.Source());
	}

	@Test
	public void TestTarget()
	{
		ModelObject object1 = obj_factory.Create();
		ModelObject object2 = obj_factory.Create();

		ModelLink link1 = link_factory.OneToOne(object1, object2);
		ModelLink link2 = link_factory.OneToOne(object2, object1);

		assertEquals(object2, link1.Target());
		assertEquals(object1, link2.Target());
	}
}