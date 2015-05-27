package ru.parallel.octotron.generators;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.generators.tmpl.ConstTemplate;
import ru.parallel.octotron.generators.tmpl.ReactionAction;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;
import ru.parallel.octotron.generators.tmpl.VarTemplate;
import ru.parallel.octotron.rules.plain.Match;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BaseFactoryTest extends GeneralTest
{
	private static ObjectFactory object_factory;
	private static LinkFactory link_factory;

	@BeforeClass
	public static void Init() throws Exception
	{
		object_factory = new ObjectFactory();
		link_factory = new LinkFactory();
	}

/**
 * check that object factory creates required amount of objects
 * with given property
 * */
	@Test
	public void TestAttributes()
	{
		ConstTemplate[] attributes = { new ConstTemplate("test1", 0) };
		ConstTemplate attr2 = new ConstTemplate("test2", 1);
		ConstTemplate attr3 = new ConstTemplate("test3", 2);

		ObjectFactory f1 = object_factory
			.Constants(attributes).Constants(attr2, attr3);
		LinkFactory f2 = link_factory
			.Constants(attr2, attr3).Constants(attributes);

		ModelObject obj = f1.Create();
		ModelLink link = f2.Constants(new ConstTemplate("type", "1"))
			.OneToOne(f1.Create(), f1.Create(), true);

		assertTrue(obj.TestAttribute("test1"));
		assertTrue(obj.TestAttribute("test2"));
		assertTrue(obj.TestAttribute("test3"));

		assertTrue(link.TestAttribute("test1"));
		assertTrue(link.TestAttribute("test2"));
		assertTrue(link.TestAttribute("test3"));
	}

	@Test
	public void TestVaryings()
	{
		VarTemplate[] rules = { new VarTemplate("test1", new Match("", "")) };
		Rule rule2 = new Match("", "");
		Rule rule3 = new Match("", "");

		ObjectFactory f1 = object_factory
			.Vars(rules)
			.Vars(new VarTemplate("test2", rule2), new VarTemplate("test3", rule3));

		ModelObject obj = f1.Create();

		assertTrue(obj.TestAttribute("test1"));
		assertTrue(obj.TestAttribute("test2"));
		assertTrue(obj.TestAttribute("test3"));
	}

	@Test
	public void TestReactions() throws Exception
	{
		ReactionTemplate[] reactions = { new ReactionTemplate("r1", new ReactionAction().Begin(new Response(EEventStatus.INFO))) };
		ReactionTemplate reaction2 = new ReactionTemplate("r2", new ReactionAction().Begin(new Response(EEventStatus.INFO)));
		ReactionTemplate reaction3 = new ReactionTemplate("r3", new ReactionAction().Begin(new Response(EEventStatus.INFO)));

		ObjectFactory f1 = object_factory
			.Reactions(reactions)
			.Reactions(reaction2, reaction3);

		ModelObject obj = f1.Create();

		assertEquals(3, obj.GetReactions().size());
	}
}
