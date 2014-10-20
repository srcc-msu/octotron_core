package ru.parallel.octotron.http;

import org.junit.Test;
import ru.parallel.octotron.core.collections.ModelList;

import static org.junit.Assert.assertEquals;

public class PathParserTest
{
	@Test
	public void TestTokens() throws Exception
	{
		assertEquals(1, PathParser.ParseTokens("obj").size());
		assertEquals(2, PathParser.ParseTokens("obj.uniq").size());
		assertEquals(2, PathParser.ParseTokens("obj(AID==33).uniq").size());
		assertEquals(2, PathParser.ParseTokens("obj(AID==33).uniq()").size());
		assertEquals(4, PathParser.ParseTokens("obj(AID==33).uniq().in_n().q(test==\" t \")").size());
	}

	@Test
	public void TestAttrFromString() throws Exception
	{
		PathOperations.Query result;

		result = PathParser.AttrFromString("test==\"val\"");
		assertEquals("test", result.attribute.GetName());
		assertEquals("val", result.attribute.GetValue());
		assertEquals(ModelList.EQueryType.EQ, result.type);

		result = PathParser.AttrFromString("_test!=true");
		assertEquals("_test", result.attribute.GetName());
		assertEquals(true, result.attribute.GetValue());
		assertEquals(ModelList.EQueryType.NE, result.type);

		result = PathParser.AttrFromString("test_1>=0.0");
		assertEquals("test_1", result.attribute.GetName());
		assertEquals(0.0, (Double)result.attribute.GetValue(), 0.1);
		assertEquals(ModelList.EQueryType.GE, result.type);

		result = PathParser.AttrFromString("test_2<=0");
		assertEquals("test_2", result.attribute.GetName());
		assertEquals(0L, result.attribute.GetValue());
		assertEquals(ModelList.EQueryType.LE, result.type);

		result = PathParser.AttrFromString("test-3<23.4");
		assertEquals("test-3", result.attribute.GetName());
		assertEquals(23.4, (Double)result.attribute.GetValue(), 0.1);
		assertEquals(ModelList.EQueryType.LT, result.type);

		result = PathParser.AttrFromString("test-4>2423");
		assertEquals("test-4", result.attribute.GetName());
		assertEquals(2423L, result.attribute.GetValue());
		assertEquals(ModelList.EQueryType.GT, result.type);
	}
}