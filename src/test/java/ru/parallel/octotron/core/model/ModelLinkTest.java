package ru.parallel.octotron.core.model;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import static org.junit.Assert.assertEquals;

public class ModelLinkTest
{
	private static Neo4jGraph graph;

	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		ModelLinkTest.graph = new Neo4jGraph( "dbs/" + ModelLinkTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphService.Init (graph);

		SimpleAttribute[] obj_att = {
			new SimpleAttribute("object", "ok")
		};

		ModelLinkTest.obj_factory = new ObjectFactory().Constants(obj_att);

		SimpleAttribute[] link_att = {
			new SimpleAttribute("link", "ok"),
			new SimpleAttribute("type", "contain"),
		};

		ModelLinkTest.link_factory = new LinkFactory().Constants(link_att);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		ModelLinkTest.graph.Shutdown();
		ModelLinkTest.graph.Delete();
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