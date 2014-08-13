package ru.parallel.octotron.core.model.attribute;

import org.junit.*;
import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.utils.JavaUtils;

import static org.junit.Assert.*;

public class AbstractVaryingAttributeTest
{
	private static Neo4jGraph graph;
	private static ModelObject object;
	private static ModelLink link;

	private static final long SLEEP = 4000;
	private static final double DELTA = 2000;

	@BeforeClass
	public static void Init() throws Exception
	{
		AbstractVaryingAttributeTest.graph = new Neo4jGraph("dbs/" + AbstractVaryingAttributeTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphService.Init(AbstractVaryingAttributeTest.graph);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		AbstractVaryingAttributeTest.graph.Shutdown();
		AbstractVaryingAttributeTest.graph.Delete();
	}

	@Before
	public void Create()
	{
		object = ModelService.AddObject();
		link = ModelService.AddLink(ModelService.AddObject(), ModelService.AddObject(), "test");
	}

	@After
	public void Clean()
	{
		object.GetBaseEntity().Delete();
		link.GetBaseEntity().Delete();
	}

	@Test
	public void testGetLastValue() throws Exception
	{
		SensorAttribute attribute = object.DeclareSensor("test", 0);

		assertNull(attribute.GetLastValue());

		attribute.Update(1, true);
		assertEquals(0, (long) attribute.GetLastValue().GetValue().GetLong());
		assertEquals(0, attribute.GetLastValue().GetCTime());

		Thread.sleep(SLEEP);

		attribute.Update(2, true);
		assertEquals(1, (long) attribute.GetLastValue().GetValue().GetLong());
		assertEquals(JavaUtils.GetTimestamp() - 2, attribute.GetLastValue().GetCTime(), DELTA);
	}


	/**
	 * uses assert for floats for delta param
	 */
	@Test
	public void testGetCTime() throws Exception
	{
		SensorAttribute attribute = object.DeclareSensor("test", 0);

		assertEquals(0, attribute.GetCTime());

		attribute.Update(1, true);
		assertEquals(JavaUtils.GetTimestamp(), attribute.GetCTime(), DELTA);

		Thread.sleep(SLEEP);
		attribute.Update(2, true);
		assertEquals(JavaUtils.GetTimestamp(), attribute.GetCTime(), DELTA);

		Thread.sleep(SLEEP);
		attribute.Update(2, false);
		assertEquals(JavaUtils.GetTimestamp() - 2, attribute.GetCTime(), DELTA);

		Thread.sleep(SLEEP);
		attribute.Update(2, true);
		assertEquals(JavaUtils.GetTimestamp(), attribute.GetCTime(), DELTA);
	}

	@Test
	public void testGetATime() throws Exception
	{
		SensorAttribute attribute = object.DeclareSensor("test", 0);

		assertEquals(0, attribute.GetATime());

		attribute.Update(1, true);
		assertEquals(JavaUtils.GetTimestamp(), attribute.GetATime(), DELTA);

		Thread.sleep(SLEEP);
		attribute.Update(2, true);
		assertEquals(JavaUtils.GetTimestamp(), attribute.GetATime(), DELTA);

		Thread.sleep(SLEEP);
		attribute.Update(2, false);
		assertEquals(JavaUtils.GetTimestamp(), attribute.GetATime(), DELTA);

		Thread.sleep(SLEEP);
		attribute.Update(2, true);
		assertEquals(JavaUtils.GetTimestamp(), attribute.GetATime(), DELTA);
	}

	@Test
	public void testGetSpeed() throws Exception
	{
		SensorAttribute attribute = object.DeclareSensor("test", 0.0);

		assertEquals(0.0, attribute.GetSpeed(), GraphAttribute.EPSILON);

		attribute.Update(0.0, true);
		assertEquals(0.0, attribute.GetSpeed(), GraphAttribute.EPSILON);

		Thread.sleep(SLEEP);
		attribute.Update(10.0, true);
		assertEquals(10.0 / (SLEEP / 1000), attribute.GetSpeed(), GraphAttribute.EPSILON);

		Thread.sleep(SLEEP);
		attribute.Update(10.0, false);
		assertEquals(10.0 / (SLEEP / 1000), attribute.GetSpeed(), GraphAttribute.EPSILON);

		Thread.sleep(SLEEP);
		attribute.Update(10.0, true);
		assertEquals(0.0, attribute.GetSpeed(), GraphAttribute.EPSILON);
	}

	@Test
	public void testUpdate() throws Exception
	{
		SensorAttribute attribute = object.DeclareSensor("test", 0);

		assertEquals(0L, attribute.GetValue());

		attribute.Update(1, true);
		assertEquals(1L, attribute.GetValue());

		attribute.Update(1, false);
		assertEquals(1L, attribute.GetValue());

		attribute.Update(2, false);
		assertEquals(2L, attribute.GetValue());

		attribute.Update(3, false);
		assertEquals(3L, attribute.GetValue());
	}

	@Test
	public void testValid() throws Exception
	{
		SensorAttribute attribute = object.DeclareSensor("test", 0);

		attribute.Update(1, true);

		assertTrue(attribute.IsValid());

		attribute.SetInvalid();
		assertFalse(attribute.IsValid());

		attribute.SetValid();
		assertTrue(attribute.IsValid());

		attribute.SetInvalid();
		assertFalse(attribute.IsValid());

		attribute.SetValid();
		assertTrue(attribute.IsValid());
	}

	@Test
	public void testReactions() throws Exception
	{
		SensorAttribute attribute = object.DeclareSensor("test", 0);

		assertEquals(0, attribute.GetReactions().size());

		attribute.AddReaction(new OctoReaction("test1", 0, null));
		assertEquals(1, attribute.GetReactions().size());

		attribute.AddReaction(new OctoReaction("test2", 0, null));
		assertEquals(2, attribute.GetReactions().size());
	}

	@Test
	public void testMarkers() throws Exception
	{
		SensorAttribute attribute = object.DeclareSensor("test", 0);

		assertEquals(0, attribute.GetMarkers().size());

		long id1 = attribute.AddMarker(new OctoReaction("test", 0, null), "a", true);
		assertEquals(1, attribute.GetMarkers().size());

		long id2 = attribute.AddMarker(new OctoReaction("test", 0, null), "a", true);
		assertEquals(2, attribute.GetMarkers().size());

		attribute.DeleteMarker(id1);
		assertEquals(1, attribute.GetMarkers().size());

		attribute.DeleteMarker(id2);
		assertEquals(0, attribute.GetMarkers().size());
	}
}