package ru.parallel.octotron.core.model.impl.attribute;

import org.junit.*;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.impl.Equals;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
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
	public void TestGetLastValue() throws Exception
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
	public void TestGetCTime() throws Exception
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
	public void TestGetATime() throws Exception
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
	public void TestGetSpeed() throws Exception
	{
		SensorAttribute attribute = object.DeclareSensor("test", 0.0);

		assertEquals(0.0, attribute.GetSpeed(), GraphAttribute.EPSILON);

		attribute.Update(0.0, true);
		assertEquals(0.0, attribute.GetSpeed(), GraphAttribute.EPSILON);

		Thread.sleep(SLEEP);
		attribute.Update(10.0, true);
		assertEquals(10.0 / (SLEEP / 1000), attribute.GetSpeed(), 1.0);

		Thread.sleep(SLEEP);
		attribute.Update(10.0, false);
		assertEquals(10.0 / (SLEEP / 1000), attribute.GetSpeed(), 1.0);

		Thread.sleep(SLEEP);
		attribute.Update(10.0, true);
		assertEquals(0.0, attribute.GetSpeed(), GraphAttribute.EPSILON);
	}

	@Test
	public void TestUpdate() throws Exception
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
	public void TestValid() throws Exception
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
	public void TestReactions() throws Exception
	{
		SensorAttribute attribute = object.DeclareSensor("test", 0);

		assertEquals(0, attribute.GetReactions().size());

		attribute.AddReaction(new Equals("test1", 0));
		assertEquals(1, attribute.GetReactions().size());

		attribute.AddReaction(new Equals("test2", 0));
		assertEquals(2, attribute.GetReactions().size());
	}

	@Test
	public void TestMarkers() throws Exception
	{
		SensorAttribute attribute = object.DeclareSensor("test", 0);

		assertEquals(0, attribute.GetMarkers().size());

		long id1 = attribute.AddMarker(new Equals("test", 0), "a", true);
		assertEquals(1, attribute.GetMarkers().size());

		long id2 = attribute.AddMarker(new Equals("test", 0), "a", true);
		assertEquals(2, attribute.GetMarkers().size());

		attribute.DeleteMarker(id1);
		assertEquals(1, attribute.GetMarkers().size());

		attribute.DeleteMarker(id2);
		assertEquals(0, attribute.GetMarkers().size());
	}

	@Test
	public void TestGetReadyReactions() throws Exception
	{
		SensorAttribute attribute = object.DeclareSensor("test", 0);

		Reaction r1 = new Equals("test", 1);
		Reaction r2 = new Equals("test", 2);

		Reaction r3 = new Equals("test", 3).Delay(2).Repeat(0);
		Reaction r4 = new Equals("test", 4).Delay(0).Repeat(3);

		Reaction r5 = new Equals("test", 5).Delay(2).Repeat(4);

		attribute.AddReaction(r1);
		attribute.AddReaction(r2);
		attribute.AddReaction(r3);
		attribute.AddReaction(r4);
		attribute.AddReaction(r5);

		assertEquals(0, attribute.GetReadyReactions().size());

		attribute.Update(1);
		assertEquals(1, attribute.GetReadyReactions().size());

		attribute.Update(2);
		assertEquals(1, attribute.GetReadyReactions().size());

		attribute.Update(3);
		assertEquals(0, attribute.GetReadyReactions().size());
		Thread.sleep(3000);
		assertEquals(1, attribute.GetReadyReactions().size());

		attribute.Update(4);
		assertEquals(0, attribute.GetReadyReactions().size());
		attribute.Update(4);
		assertEquals(0, attribute.GetReadyReactions().size());
		attribute.Update(4);
		assertEquals(1, attribute.GetReadyReactions().size());


		attribute.Update(5);
		assertEquals(0, attribute.GetReadyReactions().size());
		Thread.sleep(1000);
		attribute.Update(5);
		assertEquals(0, attribute.GetReadyReactions().size());
		attribute.Update(5);
		assertEquals(0, attribute.GetReadyReactions().size());
		Thread.sleep(2000);
		attribute.Update(5);
		assertEquals(1, attribute.GetReadyReactions().size());
		assertEquals(0, attribute.GetReadyReactions().size());
	}
}