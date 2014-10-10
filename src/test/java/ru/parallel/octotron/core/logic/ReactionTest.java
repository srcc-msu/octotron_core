package ru.parallel.octotron.core.logic;

import com.google.common.collect.Iterables;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.logic.impl.Equals;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReactionTest
{
	private static Context context;

	@BeforeClass
	public static void InitController() throws Exception
	{
		context = Context.CreateTestContext(0);
	}

	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		ReactionTest.obj_factory = new ObjectFactory(context.model_service);
		ReactionTest.link_factory = new LinkFactory(context.model_service);
	}

	@Test
	public void TestSetReactionState()
	{
		ModelEntity entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder(context.model_service).DeclareSensor("test", 0);

		ReactionTemplate reaction_template = new Equals("test", 1);

		entity.GetBuilder(context.model_service).AddReaction(reaction_template);

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

		entity.GetBuilder(context.model_service).DeclareSensor("test", 0);

		ReactionTemplate reaction_template1 = new Equals("test", 1);
		ReactionTemplate reaction_template2 = new Equals("test", 1);

		entity.GetBuilder(context.model_service).AddReaction(reaction_template1);
		entity.GetBuilder(context.model_service).AddReaction(reaction_template2);

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
	public void TestProcess1()
	{
		ModelObject entity = ReactionTest.obj_factory.Create();

		entity.GetBuilder(context.model_service).DeclareSensor("test", 0);

		entity.GetBuilder(context.model_service).AddReaction(new Equals("test", 1)
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

		entity.GetBuilder(context.model_service).DeclareSensor("test", 0);

		entity.GetBuilder(context.model_service).AddReaction(new Equals("test", 1).Repeatable()
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

		entity.GetBuilder(context.model_service).DeclareSensor("test", 0);

		entity.GetBuilder(context.model_service).AddReaction(new Equals("test", 1).Repeat(2)
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

		entity.GetBuilder(context.model_service).DeclareSensor("test", 0);

		entity.GetBuilder(context.model_service).AddReaction(new Equals("test", 1).Repeat(2).Repeatable()
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

		entity.GetBuilder(context.model_service).DeclareSensor("test", 0);

		entity.GetBuilder(context.model_service).AddReaction(new Equals("test", 1).Delay(1)
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

		entity.GetBuilder(context.model_service).DeclareSensor("test", 0);

		entity.GetBuilder(context.model_service).AddReaction(new Equals("test", 1).Delay(1).Repeatable()
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

		entity.GetBuilder(context.model_service).DeclareSensor("test", 0);

		entity.GetBuilder(context.model_service).AddReaction(new Equals("test", 1).Delay(1).Repeat(2)
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

		entity.GetBuilder(context.model_service).DeclareSensor("test", 0);

		entity.GetBuilder(context.model_service).AddReaction(new Equals("test", 1).Delay(1).Repeat(2).Repeatable()
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