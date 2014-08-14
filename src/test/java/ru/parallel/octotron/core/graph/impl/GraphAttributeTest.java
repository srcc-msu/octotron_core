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
		GraphAttribute attribute = object.DeclareAttribute("test", "test");
		assertEquals("test", attribute.GetString());
	}

	@Test
	public void TestGetLong() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		GraphAttribute attribute1 = object.DeclareAttribute("test1", 1L);
		GraphAttribute attribute2 = object.DeclareAttribute("test2", 1);
		assertEquals(Long.valueOf(1L), attribute1.GetLong());
		assertEquals(Long.valueOf(1L), attribute2.GetLong());
	}

	@Test
	public void TestGetDouble() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		GraphAttribute attribute1 = object.DeclareAttribute("test1", 1.0);
		GraphAttribute attribute2 = object.DeclareAttribute("test2", 1.0f);

		assertEquals(1.0, attribute1.GetDouble(), GraphAttribute.EPSILON);
		assertEquals(1.0, attribute2.GetDouble(), GraphAttribute.EPSILON);
	}

	@Test
	public void TestGetBoolean() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		GraphAttribute attribute = object.DeclareAttribute("test", false);
		assertEquals(false, attribute.GetBoolean());
	}

	@Test
	public void TestToDouble() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		GraphAttribute attribute1 = object.DeclareAttribute("test1", 1);
		GraphAttribute attribute2 = object.DeclareAttribute("test2", 1L);

		assertEquals(1.0, attribute1.ToDouble(), GraphAttribute.EPSILON);
		assertEquals(1.0, attribute2.ToDouble(), GraphAttribute.EPSILON);
	}

	@Test
	public void TestEq() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		GraphAttribute attribute_i = object.DeclareAttribute("test1", 1);
		GraphAttribute attribute_l = object.DeclareAttribute("test2", 1L);
		GraphAttribute attribute_s = object.DeclareAttribute("test3", "1");

		assertEquals(true, attribute_i.eq(1L));
		assertEquals(true, attribute_l.eq(1L));
		assertEquals(true, attribute_s.eq("1"));
	}

	@Test
	public void TestAeq() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		GraphAttribute attribute_f = object.DeclareAttribute("test1", 1.0f);
		GraphAttribute attribute_d = object.DeclareAttribute("test2", 1.0);

		assertEquals(true, attribute_f.aeq(1.0, GraphAttribute.EPSILON));
		assertEquals(true, attribute_d.aeq(1.0, GraphAttribute.EPSILON));
	}

	@Test
	public void TestNe() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		GraphAttribute attribute_i = object.DeclareAttribute("test1", 1);
		GraphAttribute attribute_l = object.DeclareAttribute("test2", 1L);
		GraphAttribute attribute_s = object.DeclareAttribute("test3", "1");

		assertEquals(true, attribute_i.ne(0L));
		assertEquals(true, attribute_l.ne(0L));
		assertEquals(true, attribute_s.ne("0"));
	}

	@Test
	public void TestGt() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		GraphAttribute attribute_i = object.DeclareAttribute("test1", 1);
		GraphAttribute attribute_l = object.DeclareAttribute("test2", 1L);
		GraphAttribute attribute_f = object.DeclareAttribute("test3", 1.0f);
		GraphAttribute attribute_d = object.DeclareAttribute("test4", 1.0);

		assertEquals(true, attribute_i.gt(0L));
		assertEquals(true, attribute_l.gt(0L));
		assertEquals(true, attribute_f.gt(0.0));
		assertEquals(true, attribute_d.gt(0.0));

		assertEquals(false, attribute_i.gt(2L));
		assertEquals(false, attribute_l.gt(2L));
		assertEquals(false, attribute_f.gt(2.0));
		assertEquals(false, attribute_d.gt(2.0));
	}

	@Test
	public void TestLt() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		GraphAttribute attribute_i = object.DeclareAttribute("test1", 1);
		GraphAttribute attribute_l = object.DeclareAttribute("test2", 1L);
		GraphAttribute attribute_f = object.DeclareAttribute("test3", 1.0f);
		GraphAttribute attribute_d = object.DeclareAttribute("test4", 1.0);

		assertEquals(true, attribute_i.lt(2L));
		assertEquals(true, attribute_l.lt(2L));
		assertEquals(true, attribute_f.lt(2.0));
		assertEquals(true, attribute_d.lt(2.0));

		assertEquals(false, attribute_i.lt(0L));
		assertEquals(false, attribute_l.lt(0L));
		assertEquals(false, attribute_f.lt(0.0));
		assertEquals(false, attribute_d.lt(0.0));
	}

	@Test
	public void TestGe() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		GraphAttribute attribute_i = object.DeclareAttribute("test1", 1);
		GraphAttribute attribute_l = object.DeclareAttribute("test2", 1L);
		GraphAttribute attribute_f = object.DeclareAttribute("test3", 1.0f);
		GraphAttribute attribute_d = object.DeclareAttribute("test4", 1.0);

		assertEquals(true, attribute_i.ge(0L));
		assertEquals(true, attribute_l.ge(0L));
		assertEquals(true, attribute_f.ge(0.0));
		assertEquals(true, attribute_d.ge(0.0));

		assertEquals(false, attribute_i.ge(2L));
		assertEquals(false, attribute_l.ge(2L));
		assertEquals(false, attribute_f.ge(2.0));
		assertEquals(false, attribute_d.ge(2.0));
	}

	@Test
	public void TestLe() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();
		GraphAttribute attribute_i = object.DeclareAttribute("test1", 1);
		GraphAttribute attribute_l = object.DeclareAttribute("test2", 1L);
		GraphAttribute attribute_f = object.DeclareAttribute("test3", 1.0f);
		GraphAttribute attribute_d = object.DeclareAttribute("test4", 1.0);

		assertEquals(true, attribute_i.le(2L));
		assertEquals(true, attribute_l.le(2L));
		assertEquals(true, attribute_f.le(2.0));
		assertEquals(true, attribute_d.le(2.0));

		assertEquals(false, attribute_i.le(0L));
		assertEquals(false, attribute_l.le(0L));
		assertEquals(false, attribute_f.le(0.0));
		assertEquals(false, attribute_d.le(0.0));
	}

	private void SingleTypeCheck(Object object1, Object object2, boolean must_throw)
	{
		GraphAttribute attribute = GraphService.Get().AddObject()
			.DeclareAttribute("test", object1);

		boolean catched = false;

		try
		{
			attribute.SetValue(object2);
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