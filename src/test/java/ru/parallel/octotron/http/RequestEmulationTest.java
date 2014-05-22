package ru.parallel.octotron.http;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.core.IGraph;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.utils.OctoObjectList;
import ru.parallel.utils.FileUtils;
import ru.parallel.utils.JavaUtils;

/**
 * sometimes tests can fail.. race condition
 * messages are not guaranteed to come in fixed order
 * */
public class RequestEmulationTest extends Assert
{
	private static final int HTTP_PORT = 4300;

	private static IGraph graph;
	private static GraphService graph_service;

	private static LinkFactory links;
	private static ObjectFactory factory;

	private static HTTPServer http;

	@BeforeClass
	public static void Init() throws Exception
	{
		RequestEmulationTest.http = HTTPServer.GetDummyServer(RequestEmulationTest.HTTP_PORT);

		RequestEmulationTest.graph = new Neo4jGraph("dbs/test_network", Neo4jGraph.Op.RECREATE);
		RequestEmulationTest.graph_service = new GraphService(RequestEmulationTest.graph);
		RequestEmulationTest.graph_service.EnableObjectIndex("AID");
		RequestEmulationTest.graph_service.EnableLinkIndex("AID");

		RequestEmulationTest.factory = new ObjectFactory(RequestEmulationTest.graph_service);
		RequestEmulationTest.links = new LinkFactory(RequestEmulationTest.graph_service)
			.Attributes(new SimpleAttribute("type", "a_link"));
	}

	private static final long SLEEP = 100;

	@Before
	public void Clean() throws Exception
	{
		RequestEmulationTest.graph_service.Clean();
		RequestEmulationTest.http.Clear();
	}

/**
 * get info about request and close it
 * */
	private HTTPRequest GetHttpRequest(String str) throws Exception
	{
		ParsedHttpRequest request = null;

		RequestEmulationTest.http.Clear();

		FileUtils.ExecSilent("curl", "-u:", "-sS", "127.0.0.1:" + RequestEmulationTest.HTTP_PORT + str);
		Thread.sleep(RequestEmulationTest.SLEEP);

		if((request = RequestEmulationTest.http.GetBlockingRequest()) != null)
			request.GetHttpRequest().FinishString("");
		else
			Assert.fail("did not get the message");

		return request.GetHttpRequest();
	}

	/**
	 * testing curl manually, will use it in later tests via the function
	 * */
	@Test
	public void HttpMessage() throws Exception
	{
		int COUNT = 3;

		RequestEmulationTest.http.Clear();

		String target = "/view/p";

		for(int i = 0; i < COUNT; i++)
		{
			String params = "path=obj(AID==" + i + ")";

			FileUtils.ExecSilent("curl", "-u:", "-sS", "127.0.0.1:" + RequestEmulationTest.HTTP_PORT + target + "?" + params  + i);
			Thread.sleep(RequestEmulationTest.SLEEP);
		}

		ParsedHttpRequest request;

		for(int i = 0; i < COUNT; i++)
		{
			if((request = RequestEmulationTest.http.GetBlockingRequest()) != null)
			{
				String params = "path=obj(AID==" + i + ")";

				Assert.assertEquals("got wrong target", request.GetHttpRequest().GetPath(), target);
				Assert.assertEquals("got wrong params", request.GetHttpRequest().GetQuery(), params + i);
				request.GetHttpRequest().FinishString("");
			}
			else
				Assert.fail("did not get the message");
		}

		if(RequestEmulationTest.http.GetRequest() != null)
			Assert.fail("unexpected message");
	}

	private String GetRequestResult(String str_request) throws Exception
	{
		HTTPRequest request = GetHttpRequest(str_request);

		RequestResult result = RequestParser.ParseFromHttp(request)
			.GetParsedRequest()
			.Execute(RequestEmulationTest.graph_service, null);

		if(result.type.equals(RequestResult.E_RESULT_TYPE.ERROR))
			throw new ExceptionParseError(result.data);

		return result.data;
	}

	@Test
	public void HttpRequest() throws Exception
	{
		RequestEmulationTest.factory.Create(10);
		String test = GetRequestResult("/view/p?path=obj(AID)");

		if(test == null || !test.contains("AID"))
			Assert.fail("bad response: " + test);
	}

	@Test
	public void HttpObjRequest() throws Exception
	{
		OctoObjectList l = RequestEmulationTest.factory.Create(10);
		long AID = l.get(0).GetAttribute("AID").GetLong();

		String test = GetRequestResult("/view/p?path=obj(AID)");

		if(test == null || !test.contains("AID"))
			Assert.fail("bad response: " + test);
	}

	@Test
	public void HttpQueryRequest() throws Exception
	{
		OctoObjectList l = RequestEmulationTest.factory.Create(10);
		long AID = l.get(0).GetAttribute("AID").GetLong();

		String test = GetRequestResult("/view/p?path=obj(AID).q(AID=="+AID+")");

		if(test == null || !test.contains("AID"))
			Assert.fail("bad response: " + test);
	}

	@Test
	public void HttpUniqRequest() throws Exception
	{
		RequestEmulationTest.factory.Create(10);
		String test = GetRequestResult("/view/p?path=obj(AID).uniq()");

		if(test == null || !test.contains("AID"))
			Assert.fail("bad response: " + test);
	}

	@Test
	public void HttpNeighbourRequest() throws Exception
	{
		OctoObjectList objs = RequestEmulationTest.factory.Create(10);
		RequestEmulationTest.links.AllToAll(objs.range(0, 5), objs.range(0, 10));

		String test = GetRequestResult("/view/p?path=obj(AID).in_n()");
		if(test == null || !test.contains("AID"))
			Assert.fail("bad response in_n: " + test);

		test = GetRequestResult("/view/p?path=obj(AID).out_n()");
		if(test == null || !test.contains("AID"))
			Assert.fail("bad response out_n: " + test);

		test = GetRequestResult("/view/p?path=obj(AID).all_n()");
		if(test == null || !test.contains("AID"))
			Assert.fail("bad response all_n: " + test);
	}

	@Test
	public void HttpObjLinkRequest() throws Exception
	{
		OctoObjectList objs = RequestEmulationTest.factory.Create(10);

		RequestEmulationTest.links.AllToAll(objs.range(0, 5), objs.range(0, 10));

		String test = GetRequestResult("/view/p?path=obj(AID).in_l()");
		if(test == null || !test.contains("AID"))
			Assert.fail("bad response in_l: " + test);

		test = GetRequestResult("/view/p?path=obj(AID).out_l()");
		if(test == null || !test.contains("AID"))
			Assert.fail("bad response out_l: " + test);

		test = GetRequestResult("/view/p?path=obj(AID).all_l()");
		if(test == null || !test.contains("AID"))
			Assert.fail("bad response all_l: " + test);
	}

	@Test
	public void HttpLinkRequest() throws Exception
	{
		OctoObjectList objs = RequestEmulationTest.factory.Create(10);

		RequestEmulationTest.links.AllToAll(objs.range(0, 5), objs.range(0, 10));

		String test = GetRequestResult("/view/p?path=link(AID)");
		if(test == null || !test.contains("AID"))
			Assert.fail("bad response: " + test);
	}

	@Test
	public void HttpLinkPropRequest() throws Exception
	{
		OctoObjectList objs = RequestEmulationTest.factory.Create(10);

		RequestEmulationTest.links.AllToAll(objs.range(0, 5), objs.range(0, 10));

		String test = GetRequestResult("/view/p?path=link(AID).source()");
		if(test == null || !test.contains("AID"))
			Assert.fail("bad response source: " + test);

		test = GetRequestResult("/view/p?path=link(AID).target()");
		if(test == null || !test.contains("AID"))
			Assert.fail("bad response target: " + test);
	}
}
