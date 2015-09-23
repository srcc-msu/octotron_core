package ru.parallel.octotron.http.requests;

import org.junit.Test;
import ru.parallel.octotron.core.collections.ModelObjectList;

import static org.junit.Assert.assertTrue;

/**
 * sometimes tests can fail if message did not came in time</br>
 * messages are not guaranteed to come in fixed order</br>
 * */
public class PathTest extends RequestTest
{

	@Test
	public void HttpRequest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String result = GetRequestResult("/view/entity?path=obj(AID)");

		assertTrue(result.contains("AID"));
	}

	@Test
	public void HttpObjRequest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String result = GetRequestResult("/view/entity?path=obj(AID)");

		assertTrue(result.contains("AID"));
	}

	@Test
	public void HttpQueryRequest() throws Exception
	{
		ModelObjectList l = object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		long AID = l.get(0).GetAttribute("AID").GetLong();

		String result = GetRequestResult("/view/entity?path=obj(AID).q(AID=="+AID+")");

		assertTrue(result.contains("AID"));
	}

	@Test
	public void HttpUniqRequest() throws Exception
	{
		object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		String result = GetRequestResult("/view/entity?path=obj(AID).uniq()");

		assertTrue(result.contains("AID"));
	}

	@Test
	public void HttpNeighbourRequest() throws Exception
	{
		ModelObjectList objects = object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		link_factory.AllToAll(objects.range(0, 5), objects.range(0, 10), true);
		model_service.EnableLinkIndex("AID");

		String result = GetRequestResult("/view/entity?path=obj(AID).in_n()");

		assertTrue(result.contains("AID"));

		result = GetRequestResult("/view/entity?path=obj(AID).out_n()");

		assertTrue(result.contains("AID"));

		result = GetRequestResult("/view/entity?path=obj(AID).all_n()");

		assertTrue(result.contains("AID"));
	}

	@Test
	public void HttpObjLinkRequest() throws Exception
	{
		ModelObjectList objects = object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		link_factory.AllToAll(objects.range(0, 5), objects.range(0, 10), true);
		model_service.EnableLinkIndex("AID");

		String result = GetRequestResult("/view/entity?path=obj(AID).in_l()");

		assertTrue(result.contains("AID"));

		result = GetRequestResult("/view/entity?path=obj(AID).out_l()");

		assertTrue(result.contains("AID"));

		result = GetRequestResult("/view/entity?path=obj(AID).all_l()");

		assertTrue(result.contains("AID"));
	}

	@Test
	public void HttpObjUndirectedLinkRequest() throws Exception
	{
		ModelObjectList objects = object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		link_factory.AllToAll(objects.range(0, 5), objects.range(0, 10), false);
		model_service.EnableLinkIndex("AID");

		String result = GetRequestResult("/view/entity?path=obj(AID).u_l()");

		assertTrue(result.contains("AID"));
	}

	@Test
	public void HttpLinkRequest() throws Exception
	{
		ModelObjectList objects = object_factory.Create(10);
		model_service.EnableObjectIndex("AID");

		link_factory.AllToAll(objects.range(0, 5), objects.range(0, 10), true);
		model_service.EnableLinkIndex("AID");

		String result = GetRequestResult("/view/entity?path=link(AID)");

		assertTrue(result.contains("AID"));
	}

	@Test
	public void HttpLinkPropRequest() throws Exception
	{
		ModelObjectList objects = object_factory.Create(10);

		link_factory.AllToAll(objects.range(0, 5), objects.range(0, 10), true);
		model_service.EnableLinkIndex("AID");

		String result = GetRequestResult("/view/entity?path=link(AID).source()");

		assertTrue(result.contains("AID"));

		result = GetRequestResult("/view/entity?path=link(AID).target()");

		assertTrue(result.contains("AID"));
	}
}
