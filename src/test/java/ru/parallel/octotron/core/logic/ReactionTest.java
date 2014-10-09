package ru.parallel.octotron.core.logic;

import com.google.common.collect.Iterables;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.logic.impl.Equals;
import ru.parallel.octotron.core.logic.impl.NotEquals;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.reactions.PreparedResponse;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReactionTest
{
	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		ModelService.Init(ModelService.EMode.CREATION);

		ReactionTest.obj_factory = new ObjectFactory();
		ReactionTest.link_factory = new LinkFactory();
	}

	@Test
	public void TestSetReactionState()
	{
		ModelEntity entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		ReactionTemplate reaction_template = new Equals("test", 1);

		entity.GetBuilder().AddReaction(reaction_template);

		Reaction reaction
			= Iterables.get(entity.GetAttribute("test").GetReactions(), 0);

		reaction.SetState(0);
		reaction.SetState(1);
	}

	@Test
	public void TestGetReactionState()
	{
		final int N = 4;

		ModelEntity entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		ReactionTemplate reaction_template1 = new Equals("test", 1);
		ReactionTemplate reaction_template2 = new Equals("test", 1);

		entity.GetBuilder().AddReaction(reaction_template1);
		entity.GetBuilder().AddReaction(reaction_template2);

		Reaction reaction1
			= Iterables.get(entity.GetAttribute("test").GetReactions(), 0);
		Reaction reaction2
			= Iterables.get(entity.GetAttribute("test").GetReactions(), 1);

		assertEquals(0, reaction1.GetState());
		assertEquals(0, reaction2.GetState());

		reaction1.SetState(1);

		assertEquals(1, reaction1.GetState());
		assertEquals(0, reaction2.GetState());

		reaction2.SetState(2);

		assertEquals(1, reaction1.GetState());
		assertEquals(2, reaction2.GetState());
	}

	@Test
	public void TestAddMarker()
	{
		ModelObject entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		ReactionTemplate reaction_template = new Equals("test", 1);

		entity.GetBuilder().AddReaction(reaction_template);

		Reaction reaction
			= Iterables.get(entity.GetAttribute("test").GetReactions(), 0);

		reaction.AddMarker("test1", true);
		reaction.AddMarker("test2", false);
	}

	@Test
	public void TestDeleteMarker()
	{
		ModelObject entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		ReactionTemplate reaction_template = new Equals("test", 1);

		entity.GetBuilder().AddReaction(reaction_template);

		Reaction reaction
			= Iterables.get(entity.GetAttribute("test").GetReactions(), 0);

		assertEquals(0, reaction.GetMarkers().size());

		long id1 = reaction.AddMarker("test1", true);
		long id2 = reaction.AddMarker("test2", false);

		assertEquals(2, reaction.GetMarkers().size());

		reaction.DeleteMarker(id1);
		assertEquals(1, reaction.GetMarkers().size());

		reaction.DeleteMarker(id2);
		assertEquals(0, reaction.GetMarkers().size());
	}

	@Test
	public void TestGetMarkers()
	{
		ModelObject entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		ReactionTemplate reaction_template = new Equals("test", 1);

		entity.GetBuilder().AddReaction(reaction_template);

		Reaction reaction
			= Iterables.get(entity.GetAttribute("test").GetReactions(), 0);

		final int N = 10;

		for(int i = 0; i < N; i++)
		{
			reaction.AddMarker("test" + i, true);

			Collection<Marker> markers = reaction.GetMarkers();

			assertEquals(i + 1, markers.size());
		}
	}

	@Test
	public void TestProcess1()
	{
		ModelObject entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		entity.GetBuilder().AddReaction(new Equals("test", 1)
			.Response(new Response(EEventStatus.INFO, "descr")));

		SensorAttribute sensor = entity.GetSensor("test");

		Reaction reaction
			= Iterables.get(sensor.GetReactions(), 0);

		reaction.Process();

		assertNull(reaction.Process());

		sensor.Update(1L);
		assertEquals("descr", reaction.Process().GetDescription());

		sensor.Update(1L);
		assertNull(reaction.Process());

		sensor.Update(2L);
		assertNull(reaction.Process());
	}


	@Test
	public void TestProcess2()
	{
		ModelObject entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		entity.GetBuilder().AddReaction(new Equals("test", 1).Repeatable()
			.Response(new Response(EEventStatus.INFO, "descr")));

		SensorAttribute sensor = entity.GetSensor("test");

		Reaction reaction
			= Iterables.get(sensor.GetReactions(), 0);

		reaction.Process();

		assertNull(reaction.Process());

		sensor.Update(1L);
		assertEquals("descr", reaction.Process().GetDescription());

		sensor.Update(1L);
		assertEquals("descr", reaction.Process().GetDescription());

		sensor.Update(2L);
		assertNull(reaction.Process());
	}

	@Test
	public void TestProcess3()
	{
		ModelObject entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		entity.GetBuilder().AddReaction(new Equals("test", 1).Repeat(2)
			.Response(new Response(EEventStatus.INFO, "descr")));

		SensorAttribute sensor = entity.GetSensor("test");

		Reaction reaction
			= Iterables.get(sensor.GetReactions(), 0);

		reaction.Process();

		assertNull(reaction.Process());

		sensor.Update(1L);
		assertNull(reaction.Process());

		sensor.Update(1L);
		assertEquals("descr", reaction.Process().GetDescription());

		sensor.Update(1L);
		assertNull(reaction.Process());

		sensor.Update(2L);
		assertNull(reaction.Process());
	}

	@Test
	public void TestProcess4()
	{
		ModelObject entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		entity.GetBuilder().AddReaction(new Equals("test", 1).Repeat(2).Repeatable()
			.Response(new Response(EEventStatus.INFO, "descr")));

		SensorAttribute sensor = entity.GetSensor("test");

		Reaction reaction
			= Iterables.get(sensor.GetReactions(), 0);

		reaction.Process();

		assertNull(reaction.Process());

		sensor.Update(1L);
		assertNull(reaction.Process());

		sensor.Update(1L);
		assertEquals("descr", reaction.Process().GetDescription());

		sensor.Update(1L);
		assertEquals("descr", reaction.Process().GetDescription());

		sensor.Update(2L);
		assertNull(reaction.Process());
	}

	@Test
	public void TestProcess5() throws InterruptedException
	{
		ModelObject entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		entity.GetBuilder().AddReaction(new Equals("test", 1).Delay(1)
			.Response(new Response(EEventStatus.INFO, "descr")));

		SensorAttribute sensor = entity.GetSensor("test");

		Reaction reaction
			= Iterables.get(sensor.GetReactions(), 0);

		reaction.Process();

		assertNull(reaction.Process());

		sensor.Update(1L);
		assertNull(reaction.Process());

		Thread.sleep(1500);
		assertEquals("descr", reaction.Process().GetDescription());

		sensor.Update(1L);
		assertNull(reaction.Process());

		sensor.Update(2L);
		assertNull(reaction.Process());
	}

	@Test
	public void TestProcess6() throws InterruptedException
	{
		ModelObject entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		entity.GetBuilder().AddReaction(new Equals("test", 1).Delay(1).Repeatable()
			.Response(new Response(EEventStatus.INFO, "descr")));

		SensorAttribute sensor = entity.GetSensor("test");

		Reaction reaction
			= Iterables.get(sensor.GetReactions(), 0);

		reaction.Process();

		assertNull(reaction.Process());

		sensor.Update(1L);
		assertNull(reaction.Process());

		Thread.sleep(1500);
		assertEquals("descr", reaction.Process().GetDescription());

		sensor.Update(1L);
		assertEquals("descr", reaction.Process().GetDescription());

		sensor.Update(2L);
		assertNull(reaction.Process());
	}

	@Test
	public void TestProcess7() throws InterruptedException
	{
		ModelObject entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		entity.GetBuilder().AddReaction(new Equals("test", 1).Delay(1).Repeat(2)
			.Response(new Response(EEventStatus.INFO, "descr")));

		SensorAttribute sensor = entity.GetSensor("test");

		Reaction reaction
			= Iterables.get(sensor.GetReactions(), 0);

		reaction.Process();

		assertNull(reaction.Process());

		sensor.Update(1L);
		assertNull(reaction.Process());

		Thread.sleep(1500);
		assertNull(reaction.Process());

		sensor.Update(1L);
		assertEquals("descr", reaction.Process().GetDescription());

		sensor.Update(1L);
		assertNull(reaction.Process());

		sensor.Update(2L);
		assertNull(reaction.Process());
	}

	@Test
	public void TestProcess8() throws InterruptedException
	{
		ModelObject entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder().DeclareSensor("test", 0);

		entity.GetBuilder().AddReaction(new Equals("test", 1).Delay(1).Repeat(2).Repeatable()
			.Response(new Response(EEventStatus.INFO, "descr")));

		SensorAttribute sensor = entity.GetSensor("test");

		Reaction reaction
			= Iterables.get(sensor.GetReactions(), 0);

		reaction.Process();

		assertNull(reaction.Process());

		sensor.Update(1L);
		assertNull(reaction.Process());

		Thread.sleep(1500);
		assertNull(reaction.Process());

		sensor.Update(1L);
		assertEquals("descr", reaction.Process().GetDescription());

		sensor.Update(1L);
		assertEquals("descr", reaction.Process().GetDescription());

		sensor.Update(2L);
		assertNull(reaction.Process());
	}

}