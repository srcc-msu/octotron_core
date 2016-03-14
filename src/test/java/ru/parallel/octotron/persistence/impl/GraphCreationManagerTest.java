package ru.parallel.octotron.persistence.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.exception.ExceptionSystemError;
import ru.parallel.octotron.generators.tmpl.ReactionAction;
import ru.parallel.octotron.persistence.neo4j.Neo4jGraph;
import ru.parallel.octotron.rules.plain.Match;
import ru.parallel.octotron.rules.plain.MatchArg;

public class GraphCreationManagerTest extends GeneralTest
{
	private Neo4jGraph graph;
	private GraphCreationManager manager;

	private ModelObject o1;
	private ModelObject o2;

	private ModelLink u_link;
	private ModelLink d_link;

	@Before
	public void Init() throws ExceptionSystemError
	{
		graph = new Neo4jGraph("test", Neo4jGraph.Op.CREATE);

		graph.GetIndex().EnableLinkIndex("AID");
		graph.GetIndex().EnableObjectIndex("AID");

		manager = new GraphCreationManager(model_service, graph);

		o1 = model_service.AddObject();
		o2 = model_service.AddObject();

		u_link = model_service.AddLink(o1, o2, false);
		d_link = model_service.AddLink(o1, o2, true);

		o1.GetBuilder().DeclareConst("const", "");
		o1.GetBuilder().DeclareSensor("sensor", 0, "");
		o1.GetBuilder().DeclareVar("var", new MatchArg("sensor", "const"));
		o1.GetBuilder().DeclareTrigger("trigger", new Match("var", false));
		o1.GetBuilder().DeclareReaction("reaction", new ReactionAction().On("trigger"));
	}

	@After
	public void Clean() throws ExceptionSystemError
	{
		graph.Shutdown();
		graph.Delete();
	}

	@Test
	public void TestRegisterObject() throws Exception
	{
		manager.RegisterObject(o1);
		manager.RegisterObject(o2);
	}

	@Test
	public void TestRegisterLink() throws Exception
	{
		manager.RegisterObject(o1);
		manager.RegisterObject(o2);

		manager.RegisterLink(u_link);
		manager.RegisterLink(d_link);
	}

	@Test
	public void TestRegisterConst() throws Exception
	{
		manager.RegisterObject(o1);

		manager.RegisterConst(o1.GetConst("const"));
	}

	@Test
	public void TestRegisterSensor() throws Exception
	{
		manager.RegisterObject(o1);

		manager.RegisterSensor(o1.GetSensor("sensor"));
	}

	@Test
	public void TestRegisterVar() throws Exception
	{
		manager.RegisterObject(o1);

		manager.RegisterVar(o1.GetVar("var"));
	}

	@Test
	public void TestRegisterTrigger() throws Exception
	{
		manager.RegisterObject(o1);

		manager.RegisterTrigger(o1.GetTrigger("trigger"));
	}

	@Test
	public void TestRegisterReaction() throws Exception
	{
		manager.RegisterObject(o1);

		manager.RegisterReaction(o1.GetReaction("reaction"));
	}

	@Test
	public void TestMakeRuleDependency() throws Exception
	{
		manager.RegisterObject(o1);
		manager.RegisterObject(o2);
		manager.RegisterLink(u_link);
		manager.RegisterLink(d_link);

		manager.RegisterConst(o1.GetConst("const"));
		manager.RegisterSensor(o1.GetSensor("sensor"));
		manager.RegisterVar(o1.GetVar("var"));
		manager.RegisterReaction(o1.GetReaction("reaction"));
		manager.RegisterTrigger(o1.GetTrigger("trigger"));

		manager.MakeRuleDependency(o1);
	}
}
