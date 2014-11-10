package ru.parallel.octotron.http.requests;

import org.junit.Test;
import ru.parallel.octotron.core.collections.ModelObjectList;

import static org.junit.Assert.fail;

/**
 * sometimes tests can fail if message did not came in time</br>
 * messages are not guaranteed to come in fixed order</br>
 * */
public class PathTest extends RequestTest
{

	@Test
	public void HttpRequest() throws Exception
	{
		RequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/entity?path=obj(AID)");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpObjRequest() throws Exception
	{
		RequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/entity?path=obj(AID)");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpQueryRequest() throws Exception
	{
		ModelObjectList l = RequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		long AID = l.get(0).GetAttribute("AID").GetLong();

		String test = GetRequestResult("/view/entity?path=obj(AID).q(AID=="+AID+")");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpUniqRequest() throws Exception
	{
		RequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/entity?path=obj(AID).uniq()");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpNeighbourRequest() throws Exception
	{
		ModelObjectList objects = RequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		RequestTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));
		context.model_service.EnableLinkIndex("AID");

		String test = GetRequestResult("/view/entity?path=obj(AID).in_n()");
		if(test == null || !test.contains("AID"))
			fail("bad response in_n: " + test);

		test = GetRequestResult("/view/entity?path=obj(AID).out_n()");
		if(test == null || !test.contains("AID"))
			fail("bad response out_n: " + test);

		test = GetRequestResult("/view/entity?path=obj(AID).all_n()");
		if(test == null || !test.contains("AID"))
			fail("bad response all_n: " + test);
	}

	@Test
	public void HttpObjLinkRequest() throws Exception
	{
		ModelObjectList objects = RequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		RequestTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));
		context.model_service.EnableLinkIndex("AID");

		String test = GetRequestResult("/view/entity?path=obj(AID).in_l()");
		if(test == null || !test.contains("AID"))
			fail("bad response in_l: " + test);

		test = GetRequestResult("/view/entity?path=obj(AID).out_l()");
		if(test == null || !test.contains("AID"))
			fail("bad response out_l: " + test);

		test = GetRequestResult("/view/entity?path=obj(AID).all_l()");
		if(test == null || !test.contains("AID"))
			fail("bad response all_l: " + test);
	}

	@Test
	public void HttpLinkRequest() throws Exception
	{
		ModelObjectList objects = RequestTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		RequestTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));
		context.model_service.EnableLinkIndex("AID");

		String test = GetRequestResult("/view/entity?path=link(AID)");
		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpLinkPropRequest() throws Exception
	{
		ModelObjectList objects = RequestTest.factory.Create(10);

		RequestTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));
		context.model_service.EnableLinkIndex("AID");

		String test = GetRequestResult("/view/entity?path=link(AID).source()");
		if(test == null || !test.contains("AID"))
			fail("bad response source: " + test);

		test = GetRequestResult("/view/entity?path=link(AID).target()");
		if(test == null || !test.contains("AID"))
			fail("bad response target: " + test);
	}
}
