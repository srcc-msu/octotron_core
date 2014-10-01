package ru.parallel.octotron.core.graph.impl;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import static org.junit.Assert.assertEquals;

public class GraphAttributeTest
{
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		GraphAttributeTest.graph = new Neo4jGraph( "dbs/" + GraphAttributeTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphService.Init (graph);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		GraphAttributeTest.graph.Shutdown();
		GraphAttributeTest.graph.Delete();
	}

	@Test
	public void TestGetString() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		object.DeclareAttribute("test", "test");
		assertEquals("test", object.GetAttribute("test").GetString());
	}

	@Test
	public void TestGetLong() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		 object.DeclareAttribute("test1", 1L);
		 object.DeclareAttribute("test2", 1);
		assertEquals(Long.valueOf(1L), object.GetAttribute("test1").GetLong());
		assertEquals(Long.valueOf(1L), object.GetAttribute("test2").GetLong());
	}

	@Test
	public void TestGetDouble() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		object.DeclareAttribute("test1", 1.0);
		object.DeclareAttribute("test2", 1.0f);

		assertEquals(1.0, object.GetAttribute("test1").GetDouble(), GraphAttribute.EPSILON);
		assertEquals(1.0, object.GetAttribute("test2").GetDouble(), GraphAttribute.EPSILON);
	}

	@Test
	public void TestGetBoolean() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		object.DeclareAttribute("test", false);
		assertEquals(false, object.GetAttribute("test").GetBoolean());
	}

	@Test
	public void TestToDouble() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		object.DeclareAttribute("test1", 1);
		object.DeclareAttribute("test2", 1L);

		assertEquals(1.0, object.GetAttribute("test1").ToDouble(), GraphAttribute.EPSILON);
		assertEquals(1.0, object.GetAttribute("test2").ToDouble(), GraphAttribute.EPSILON);
	}

	@Test
	public void TestEq() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		object.DeclareAttribute("test1", 1);
		object.DeclareAttribute("test2", 1L);
		object.DeclareAttribute("test3", "1");

		assertEquals(true, object.GetAttribute("test1").eq(1L));
		assertEquals(true, object.GetAttribute("test2").eq(1L));
		assertEquals(true, object.GetAttribute("test3").eq("1"));
	}

	@Test
	public void TestAeq() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		object.DeclareAttribute("test1", 1.0f);
		object.DeclareAttribute("test2", 1.0);

		assertEquals(true, object.GetAttribute("test1").aeq(1.0, GraphAttribute.EPSILON));
		assertEquals(true, object.GetAttribute("test2").aeq(1.0, GraphAttribute.EPSILON));
	}

	@Test
	public void TestNe() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		object.DeclareAttribute("test1", 1);
		object.DeclareAttribute("test2", 1L);
		object.DeclareAttribute("test3", "1");

		assertEquals(true, object.GetAttribute("test1").ne(0L));
		assertEquals(true, object.GetAttribute("test2").ne(0L));
		assertEquals(true, object.GetAttribute("test3").ne("0"));
	}

	@Test
	public void TestGt() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		object.DeclareAttribute("test1", 1);
		object.DeclareAttribute("test2", 1L);
		object.DeclareAttribute("test3", 1.0f);
		object.DeclareAttribute("test4", 1.0);

		assertEquals(true, object.GetAttribute("test1").gt(0L));
		assertEquals(true, object.GetAttribute("test2").gt(0L));
		assertEquals(true, object.GetAttribute("test3").gt(0.0));
		assertEquals(true, object.GetAttribute("test4").gt(0.0));

		assertEquals(false, object.GetAttribute("test1").gt(2L));
		assertEquals(false, object.GetAttribute("test2").gt(2L));
		assertEquals(false, object.GetAttribute("test3").gt(2.0));
		assertEquals(false, object.GetAttribute("test4").gt(2.0));
	}

	@Test
	public void TestLt() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		object.DeclareAttribute("test1", 1);
		object.DeclareAttribute("test2", 1L);
		object.DeclareAttribute("test3", 1.0f);
		object.DeclareAttribute("test4", 1.0);

		assertEquals(true, object.GetAttribute("test1").lt(2L));
		assertEquals(true, object.GetAttribute("test2").lt(2L));
		assertEquals(true, object.GetAttribute("test3").lt(2.0));
		assertEquals(true, object.GetAttribute("test4").lt(2.0));

		assertEquals(false, object.GetAttribute("test1").lt(0L));
		assertEquals(false, object.GetAttribute("test2").lt(0L));
		assertEquals(false, object.GetAttribute("test3").lt(0.0));
		assertEquals(false, object.GetAttribute("test4").lt(0.0));
	}

	@Test
	public void TestGe() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		object.DeclareAttribute("test1", 1);
		object.DeclareAttribute("test2", 1L);
		object.DeclareAttribute("test3", 1.0f);
		object.DeclareAttribute("test4", 1.0);

		assertEquals(true, object.GetAttribute("test1").ge(0L));
		assertEquals(true, object.GetAttribute("test2").ge(0L));
		assertEquals(true, object.GetAttribute("test3").ge(0.0));
		assertEquals(true, object.GetAttribute("test4").ge(0.0));

		assertEquals(false, object.GetAttribute("test1").ge(2L));
		assertEquals(false, object.GetAttribute("test2").ge(2L));
		assertEquals(false, object.GetAttribute("test3").ge(2.0));
		assertEquals(false, object.GetAttribute("test4").ge(2.0));
	}

	@Test
	public void TestLe() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		object.DeclareAttribute("test1", 1);
		object.DeclareAttribute("test2", 1L);
		object.DeclareAttribute("test3", 1.0f);
		object.DeclareAttribute("test4", 1.0);

		assertEquals(true, object.GetAttribute("test1").le(2L));
		assertEquals(true, object.GetAttribute("test2").le(2L));
		assertEquals(true, object.GetAttribute("test3").le(2.0));
		assertEquals(true, object.GetAttribute("test4").le(2.0));

		assertEquals(false, object.GetAttribute("test1").le(0L));
		assertEquals(false, object.GetAttribute("test2").le(0L));
		assertEquals(false, object.GetAttribute("test3").le(0.0));
		assertEquals(false, object.GetAttribute("test4").le(0.0));
	}

	private void SingleTypeCheck(Object object1, Object object2, boolean must_throw)
	{
		GraphObject object = GraphService.Get().AddObject();
		object.DeclareAttribute("test", object1);

		boolean catched = false;

		try
		{
			object.UpdateAttribute("test", object2);
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}

		assertEquals(must_throw, catched); // can update to the same type
	}

	@Test
	public void TestTypes() throws Exception
	{
		SingleTypeCheck("", "", false);
		SingleTypeCheck(true, true, false);
		SingleTypeCheck(0L, 0L, false);
		SingleTypeCheck(0L, 0, false);
		SingleTypeCheck(0, 0L, false);
		SingleTypeCheck(0, 0, false);
		SingleTypeCheck(0.0, 0.0, false);
		SingleTypeCheck(0.0, 0.0f, false);
		SingleTypeCheck(0.0f, 0.0, false);
		SingleTypeCheck(0.0f, 0.0f, false);

		SingleTypeCheck("", 0, true);
		SingleTypeCheck("", 0L, true);
		SingleTypeCheck("", 0.0, true);
		SingleTypeCheck("", 0.0f, true);
		SingleTypeCheck("", false, true);

		SingleTypeCheck(true, "", true);
		SingleTypeCheck(true, 0, true);
		SingleTypeCheck(true, 0L, true);
		SingleTypeCheck(true, 0.0, true);
		SingleTypeCheck(true, 0.0f, true);

		SingleTypeCheck(0L, 0.0, true);
		SingleTypeCheck(0L, 0.0f, true);
		SingleTypeCheck(0L, "", true);
		SingleTypeCheck(0L, true, true);

		SingleTypeCheck(0, 0.0, true);
		SingleTypeCheck(0, 0.0f, true);
		SingleTypeCheck(0, "", true);
		SingleTypeCheck(0, true, true);

		SingleTypeCheck(0.0, 0, true);
		SingleTypeCheck(0.0, 0L, true);
		SingleTypeCheck(0.0, "", true);
		SingleTypeCheck(0.0, true, true);

		SingleTypeCheck(0.0f, 0, true);
		SingleTypeCheck(0.0f, 0L, true);
		SingleTypeCheck(0.0f, "", true);
		SingleTypeCheck(0.0f, true, true);
	}
}