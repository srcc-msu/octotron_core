package ru.parallel.octotron.http;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import ru.parallel.octotron.core.collections.EntityList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import static org.junit.Assert.assertEquals;

public class PathParserTest
{
	@Test
	public void TestAttrFromString() throws Exception
	{
		Pair<SimpleAttribute, EntityList.EQueryType> result;

		result = PathParser.AttrFromString("test==\"val\"");
		assertEquals("test", result.getLeft().GetName());
		assertEquals("val", result.getLeft().GetValue());
		assertEquals(EntityList.EQueryType.EQ, result.getRight());

		result = PathParser.AttrFromString("_test!=true");
		assertEquals("_test", result.getLeft().GetName());
		assertEquals(true, result.getLeft().GetValue());
		assertEquals(EntityList.EQueryType.NE, result.getRight());

		result = PathParser.AttrFromString("test_1>=0.0");
		assertEquals("test_1", result.getLeft().GetName());
		assertEquals(0.0, (Double)result.getLeft().GetValue(), 0.1);
		assertEquals(EntityList.EQueryType.GE, result.getRight());

		result = PathParser.AttrFromString("test_2<=0");
		assertEquals("test_2", result.getLeft().GetName());
		assertEquals(0L, result.getLeft().GetValue());
		assertEquals(EntityList.EQueryType.LE, result.getRight());

		result = PathParser.AttrFromString("test-3<23.4");
		assertEquals("test-3", result.getLeft().GetName());
		assertEquals(23.4, (Double)result.getLeft().GetValue(), 0.1);
		assertEquals(EntityList.EQueryType.LT, result.getRight());

		result = PathParser.AttrFromString("test-4>2423");
		assertEquals("test-4", result.getLeft().GetName());
		assertEquals(2423L, result.getLeft().GetValue());
		assertEquals(EntityList.EQueryType.GT, result.getRight());
	}
}