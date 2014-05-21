package ru.parallel.octotron.core;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;

public class OctoAttributeTest
{
	private static GraphService graph_service;
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init()
	{
		try
		{
			OctoAttributeTest.graph = new Neo4jGraph("dbs/test_neo4j", Neo4jGraph.Op.RECREATE);
			OctoAttributeTest.graph_service = new GraphService(OctoAttributeTest.graph);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@AfterClass
	public static void Delete()
	{
		OctoAttributeTest.graph.Shutdown();
		try
		{
			OctoAttributeTest.graph.Delete();
		}
		catch (ExceptionSystemError e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void TestGetCTime() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute1 = object.DeclareAttribute("test1", "");
		OctoAttribute attribute2 = object.DeclareAttribute("test2", "");

		Assert.assertEquals(0, attribute1.GetCTime());
		Assert.assertEquals(0, attribute2.GetCTime());

		graph_service.SetMeta(object, "test1", "ctime", 1L);

// reread from the graph
		Assert.assertEquals(1, object.GetAttribute("test1").GetCTime());
		Assert.assertEquals(0, object.GetAttribute("test2").GetCTime());
	}

	@Test
	public void TestGetATime() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute1 = object.DeclareAttribute("test1", "");
		OctoAttribute attribute2 = object.DeclareAttribute("test2", "");

		Assert.assertEquals(0, attribute1.GetATime());
		Assert.assertEquals(0, attribute2.GetATime());

		graph_service.SetMeta(object, "test1", "atime", 1L);

// reread from the graph
		Assert.assertEquals(1L, object.GetAttribute("test1").GetATime());
		Assert.assertEquals(0, object.GetAttribute("test2").GetATime());
	}

	@Test
	public void TestGetSpeed() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute_l = object.DeclareAttribute("test1", 1L);
		OctoAttribute attribute_d = object.DeclareAttribute("test2", 1.0);

		Assert.assertEquals(0.0, attribute_l.GetSpeed(), OctoAttribute.DELTA);
		Assert.assertEquals(0.0, attribute_d.GetSpeed(), OctoAttribute.DELTA);

		attribute_l.Update(2L, true);
		attribute_d.Update(2.0, true);

// speed not available now - requires 2 updates
		Assert.assertEquals(0.0, attribute_l.GetSpeed(), OctoAttribute.DELTA);
		Assert.assertEquals(0.0, attribute_d.GetSpeed(), OctoAttribute.DELTA);

		Thread.sleep(2000); // 2sec

		attribute_l.Update(4L, true);
		attribute_d.Update(4.0, true);

// speed must be positive
		Assert.assertTrue(attribute_l.GetSpeed() > 0.0);
		Assert.assertTrue(attribute_d.GetSpeed() > 0.0);

		attribute_l.Update(4L, true);
		attribute_d.Update(4.0, true);

// must be 0
		Assert.assertEquals(0.0, attribute_l.GetSpeed(), OctoAttribute.DELTA);
		Assert.assertEquals(0.0, attribute_d.GetSpeed(), OctoAttribute.DELTA);
	}

	@Test
	public void TestUpdate() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute1 = object.DeclareAttribute("test1", "");
		OctoAttribute attribute2 = object.DeclareAttribute("test2", "");
		OctoAttribute attribute3 = object.DeclareAttribute("test3", "");
		OctoAttribute attribute4 = object.DeclareAttribute("test4", "");

		Assert.assertEquals(0, attribute1.GetCTime());
		Assert.assertEquals(0, attribute2.GetCTime());
		Assert.assertEquals(0, attribute3.GetCTime());
		Assert.assertEquals(0, attribute4.GetCTime());

		attribute1.Update("", false);
		// ctime changed - first update
		Assert.assertNotEquals(0, attribute1.GetCTime());
		// atime always changes
		Assert.assertNotEquals(0, attribute1.GetATime());

		attribute2.Update("", true);
		// ctime changed - first update
		Assert.assertNotEquals(0, attribute2.GetCTime());
		// atime always changes
		Assert.assertNotEquals(0, attribute2.GetATime());

		attribute3.Update("a", false);
		// ctime changed with a new value
		Assert.assertNotEquals(0, attribute3.GetCTime());
		// atime always changes
		Assert.assertNotEquals(0, attribute3.GetATime());

		attribute4.Update("a", true);
		// ctime changed with a new value
		Assert.assertNotEquals(0, attribute4.GetCTime());
		// atime always changes
		Assert.assertNotEquals(0, attribute4.GetATime());

		long attribute1_ctime = attribute1.GetCTime();
		long attribute2_ctime = attribute2.GetCTime();
		long attribute3_ctime = attribute3.GetCTime();
		long attribute4_ctime = attribute4.GetCTime();

		Thread.sleep(2000); // 2 secs

		attribute1.Update("", false);
		// ctime not changed
		Assert.assertEquals(attribute1_ctime, attribute1.GetCTime());

		attribute2.Update("", true);
		// ctime changed with allow_overwrite
		Assert.assertNotEquals(attribute2_ctime, attribute2.GetCTime());

		attribute3.Update("a", false);
		// ctime not changed
		Assert.assertEquals(attribute3_ctime, attribute3.GetCTime());

		attribute4.Update("a", true);
		// ctime changed with allow_overwrite
		Assert.assertNotEquals(attribute4_ctime, attribute4.GetCTime());
	}

	@Test
	public void TestGetString() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute = object.DeclareAttribute("test", "test");
		Assert.assertEquals("test", attribute.GetString());
	}

	@Test
	public void TestGetLong() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute1 = object.DeclareAttribute("test1", 1L);
		OctoAttribute attribute2 = object.DeclareAttribute("test2", 1);
		Assert.assertEquals(Long.valueOf(1L), attribute1.GetLong());
		Assert.assertEquals(Long.valueOf(1L), attribute2.GetLong());
	}

	@Test
	public void TestGetDouble() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute1 = object.DeclareAttribute("test1", 1.0);
		OctoAttribute attribute2 = object.DeclareAttribute("test2", 1.0f);

		Assert.assertEquals(1.0, attribute1.GetDouble(), OctoAttribute.DELTA);
		Assert.assertEquals(1.0, attribute2.GetDouble(), OctoAttribute.DELTA);
	}

	@Test
	public void TestGetBoolean() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute = object.DeclareAttribute("test", false);
		Assert.assertEquals(false, attribute.GetBoolean());
	}

	@Test
	public void TestToDouble() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute1 = object.DeclareAttribute("test1", 1);
		OctoAttribute attribute2 = object.DeclareAttribute("test2", 1L);

		Assert.assertEquals(1.0, attribute1.ToDouble(), OctoAttribute.DELTA);
		Assert.assertEquals(1.0, attribute2.ToDouble(), OctoAttribute.DELTA);
	}

	@Test
	public void TestEq() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute_i = object.DeclareAttribute("test1", 1);
		OctoAttribute attribute_l = object.DeclareAttribute("test2", 1L);
		OctoAttribute attribute_s = object.DeclareAttribute("test3", "1");

		Assert.assertEquals(true, attribute_i.eq(1L));
		Assert.assertEquals(true, attribute_l.eq(1L));
		Assert.assertEquals(true, attribute_s.eq("1"));
	}

	@Test
	public void TestAeq() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute_f = object.DeclareAttribute("test1", 1.0f);
		OctoAttribute attribute_d = object.DeclareAttribute("test2", 1.0);

		Assert.assertEquals(true, attribute_f.aeq(1.0, OctoAttribute.DELTA));
		Assert.assertEquals(true, attribute_d.aeq(1.0, OctoAttribute.DELTA));
	}

	@Test
	public void TestNe() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute_i = object.DeclareAttribute("test1", 1);
		OctoAttribute attribute_l = object.DeclareAttribute("test2", 1L);
		OctoAttribute attribute_s = object.DeclareAttribute("test3", "1");

		Assert.assertEquals(true, attribute_i.ne(0L));
		Assert.assertEquals(true, attribute_l.ne(0L));
		Assert.assertEquals(true, attribute_s.ne("0"));
	}

	@Test
	public void TestGt() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute_i = object.DeclareAttribute("test1", 1);
		OctoAttribute attribute_l = object.DeclareAttribute("test2", 1L);
		OctoAttribute attribute_f = object.DeclareAttribute("test3", 1.0f);
		OctoAttribute attribute_d = object.DeclareAttribute("test4", 1.0);

		Assert.assertEquals(true, attribute_i.gt(0L));
		Assert.assertEquals(true, attribute_l.gt(0L));
		Assert.assertEquals(true, attribute_f.gt(0.0));
		Assert.assertEquals(true, attribute_d.gt(0.0));

		Assert.assertEquals(false, attribute_i.gt(2L));
		Assert.assertEquals(false, attribute_l.gt(2L));
		Assert.assertEquals(false, attribute_f.gt(2.0));
		Assert.assertEquals(false, attribute_d.gt(2.0));
	}

	@Test
	public void TestLt() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute_i = object.DeclareAttribute("test1", 1);
		OctoAttribute attribute_l = object.DeclareAttribute("test2", 1L);
		OctoAttribute attribute_f = object.DeclareAttribute("test3", 1.0f);
		OctoAttribute attribute_d = object.DeclareAttribute("test4", 1.0);

		Assert.assertEquals(true, attribute_i.lt(2L));
		Assert.assertEquals(true, attribute_l.lt(2L));
		Assert.assertEquals(true, attribute_f.lt(2.0));
		Assert.assertEquals(true, attribute_d.lt(2.0));

		Assert.assertEquals(false, attribute_i.lt(0L));
		Assert.assertEquals(false, attribute_l.lt(0L));
		Assert.assertEquals(false, attribute_f.lt(0.0));
		Assert.assertEquals(false, attribute_d.lt(0.0));
	}

	@Test
	public void TestGe() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute_i = object.DeclareAttribute("test1", 1);
		OctoAttribute attribute_l = object.DeclareAttribute("test2", 1L);
		OctoAttribute attribute_f = object.DeclareAttribute("test3", 1.0f);
		OctoAttribute attribute_d = object.DeclareAttribute("test4", 1.0);

		Assert.assertEquals(true, attribute_i.ge(0L));
		Assert.assertEquals(true, attribute_l.ge(0L));
		Assert.assertEquals(true, attribute_f.ge(0.0));
		Assert.assertEquals(true, attribute_d.ge(0.0));

		Assert.assertEquals(false, attribute_i.ge(2L));
		Assert.assertEquals(false, attribute_l.ge(2L));
		Assert.assertEquals(false, attribute_f.ge(2.0));
		Assert.assertEquals(false, attribute_d.ge(2.0));
	}

	@Test
	public void TestLe() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute_i = object.DeclareAttribute("test1", 1);
		OctoAttribute attribute_l = object.DeclareAttribute("test2", 1L);
		OctoAttribute attribute_f = object.DeclareAttribute("test3", 1.0f);
		OctoAttribute attribute_d = object.DeclareAttribute("test4", 1.0);

		Assert.assertEquals(true, attribute_i.le(2L));
		Assert.assertEquals(true, attribute_l.le(2L));
		Assert.assertEquals(true, attribute_f.le(2.0));
		Assert.assertEquals(true, attribute_d.le(2.0));

		Assert.assertEquals(false, attribute_i.le(0L));
		Assert.assertEquals(false, attribute_l.le(0L));
		Assert.assertEquals(false, attribute_f.le(0.0));
		Assert.assertEquals(false, attribute_d.le(0.0));
	}

	@Test
	public void TestValid() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		OctoAttribute attribute = object.DeclareAttribute("test", 0);

		Assert.assertTrue(attribute.IsValid());

		attribute.SetInvalid();
		Assert.assertFalse(attribute.IsValid());

		attribute.SetValid();
		Assert.assertTrue(attribute.IsValid());

		attribute.SetInvalid();
		Assert.assertFalse(attribute.IsValid());

		attribute.SetValid();
		Assert.assertTrue(attribute.IsValid());
	}

	private void SingleTypeCheck(Object object1, Object object2, boolean must_throw)
	{
		OctoAttribute attribute = graph_service.AddObject()
			.DeclareAttribute("test", object1);

		boolean catched = false;

		try
		{
			attribute.Update(object2, true);
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}

		Assert.assertEquals(must_throw, catched); // can update to the same type
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