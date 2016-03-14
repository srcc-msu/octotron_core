package ru.parallel.octotron.persistence.impl;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.exception.ExceptionModelFail;
import ru.parallel.octotron.exception.ExceptionSystemError;
import ru.parallel.octotron.generators.tmpl.ReactionAction;
import ru.parallel.octotron.persistence.neo4j.Neo4jGraph;
import ru.parallel.octotron.rules.plain.Match;
import ru.parallel.octotron.rules.plain.MatchArg;

public class GraphOperationManagerTest extends GeneralTest
{
	private static ModelObject o1;
	private static ModelObject o2;

	private static ModelLink u_link;
	private static ModelLink d_link;
	private static GraphOperationManager manager;

	private static Neo4jGraph graph;

	@BeforeClass
	public static void StaticInit() throws ExceptionSystemError
	{
		graph = new Neo4jGraph("test", Neo4jGraph.Op.CREATE);

		graph.GetIndex().EnableLinkIndex("AID");
		graph.GetIndex().EnableObjectIndex("AID");

		GraphCreationManager creation_manager = new GraphCreationManager(model_service, graph);

		o1 = model_service.AddObject();
		o2 = model_service.AddObject();

		u_link = model_service.AddLink(o1, o2, false);
		d_link = model_service.AddLink(o1, o2, true);

		o1.GetBuilder().DeclareConst("const", "");
		o1.GetBuilder().DeclareSensor("sensor", 0, "");
		o1.GetBuilder().DeclareVar("var", new MatchArg("sensor", "const"));
		o1.GetBuilder().DeclareTrigger("trigger", new Match("var", false));
		o1.GetBuilder().DeclareReaction("reaction", new ReactionAction().On("trigger"));

		creation_manager.RegisterObject(o1);
		creation_manager.RegisterObject(o2);
		creation_manager.RegisterLink(u_link);
		creation_manager.RegisterLink(d_link);

		creation_manager.RegisterConst(o1.GetConst("const"));
		creation_manager.RegisterSensor(o1.GetSensor("sensor"));
		creation_manager.RegisterVar(o1.GetVar("var"));
		creation_manager.RegisterReaction(o1.GetReaction("reaction"));
		creation_manager.RegisterTrigger(o1.GetTrigger("trigger"));

		creation_manager.MakeRuleDependency(o1);

		manager = new GraphOperationManager(model_service, graph);
	}

	@AfterClass
	public static void StaticClean() throws ExceptionSystemError
	{
		graph.Shutdown();
		graph.Delete();
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void TestRegisterObject() throws Exception
	{
		exception.expect(ExceptionModelFail.class);

		manager.RegisterObject(o1);
		manager.RegisterObject(o2);
	}

	@Test
	public void TestRegisterLink() throws Exception
	{
		exception.expect(ExceptionModelFail.class);

		manager.RegisterLink(u_link);
		manager.RegisterLink(d_link);
	}

	@Test
	public void TestRegisterConst() throws Exception
	{
		exception.expect(ExceptionModelFail.class);

		manager.RegisterConst(o1.GetConst("const"));
	}

	@Test
	public void TestRegisterSensor() throws Exception
	{
		manager.RegisterSensor(o1.GetSensor("sensor"));
	}

	@Test
	public void TestRegisterVar() throws Exception
	{
		manager.RegisterVar(o1.GetVar("var"));
	}

	@Test
	public void TestRegisterTrigger() throws Exception
	{
		manager.RegisterTrigger(o1.GetTrigger("trigger"));
	}

	@Test
	public void TestRegisterReaction() throws Exception
	{
		manager.RegisterReaction(o1.GetReaction("reaction"));
	}

	@Test
	public void TestMakeRuleDependency() throws Exception
	{
		exception.expect(ExceptionModelFail.class);

		manager.MakeRuleDependency(o1);
	}
}
