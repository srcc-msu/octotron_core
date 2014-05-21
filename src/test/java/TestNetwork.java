package test.java;


import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.core.IGraph;
import ru.parallel.octotron.http.HTTPRequest;
import ru.parallel.octotron.http.HTTPServer;
import ru.parallel.octotron.http.ParsedHttpRequest;
import ru.parallel.octotron.http.RequestParser;
import ru.parallel.octotron.http.RequestResult;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.utils.OctoObjectList;
import ru.parallel.utils.FileUtils;

/**
 * sometimes tests can fail.. race condition
 * messages are not guaranteed to come in fixed order
 * */
public class TestNetwork extends Assert
{
	private static final int HTTP_PORT = 4300;

	private static IGraph graph;
	private static LinkFactory links;
	private static ObjectFactory factory;
	private static HTTPServer http;
	private static GraphService graph_service;

	@BeforeClass
	public static void Init()
	{
		try
		{
			TestNetwork.http = HTTPServer.GetDummyServer(TestNetwork.HTTP_PORT);

			TestNetwork.graph = new Neo4jGraph("dbs/test_network", Neo4jGraph.Op.RECREATE);
			TestNetwork.graph_service = new GraphService(TestNetwork.graph);
			TestNetwork.graph_service.EnableObjectIndex("AID");
			TestNetwork.graph_service.EnableLinkIndex("AID");

			TestNetwork.factory = new ObjectFactory(TestNetwork.graph_service);
			TestNetwork.links = new LinkFactory(TestNetwork.graph_service)
				.Attributes(new SimpleAttribute("type", "a_link"));
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private static final long SLEEP = 100;

	@Before
	public void Clean()
	{
		try
		{
			TestNetwork.graph_service.Clean();
			TestNetwork.http.Clear();
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

/**
 * get info about request and close it
 * */
	private HTTPRequest GetHttpRequest(String str) {
		ParsedHttpRequest request = null;

		try
		{
			TestNetwork.http.Clear();

			FileUtils.ExecSilent("curl", "-u:", "-sS", "127.0.0.1:" + TestNetwork.HTTP_PORT + str);
			Thread.sleep(TestNetwork.SLEEP);

			if((request = TestNetwork.http.GetBlockingRequest()) != null)
				request.GetHttpRequest().FinishString("");
			else
				Assert.fail("did not get the message");
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}

		return request.GetHttpRequest();
	}

	/**
	 * testing curl manually, will use it in later tests via the function
	 * */
	@Test
	public void HttpMessage()
	{
		int COUNT = 3;

		try
		{
			TestNetwork.http.Clear();

			String target = "/view/p";
			String params = "path(obj==AID)";

			for(int i = 0; i < COUNT; i++)
			{
				FileUtils.ExecSilent("curl", "-u:", "-sS", "127.0.0.1:" + TestNetwork.HTTP_PORT + target + "?" + params  + i);
				Thread.sleep(TestNetwork.SLEEP);
			}

			ParsedHttpRequest request;

			for(int i = 0; i < COUNT; i++)
			{
				if((request = TestNetwork.http.GetBlockingRequest()) != null)
				{
					Assert.assertEquals("got wrong target", request.GetHttpRequest().GetPath(), target);
					Assert.assertEquals("got wrong params", request.GetHttpRequest().GetQuery(), params + i);
					request.GetHttpRequest().FinishString("");
				}
				else
					Assert.fail("did not get the message");
			}

			if(TestNetwork.http.GetRequest() != null)
				Assert.fail("unexpected message");
		}

		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	private RequestResult GetRequestResult(String str_request)
		throws ExceptionSystemError, ExceptionParseError
	{
		HTTPRequest request = GetHttpRequest(str_request);

		return RequestParser.ParseFromHttp(request).GetParsedRequest().Execute(TestNetwork.graph_service, null);
	}

	@Test
	public void HttpRequest()
	{
		try
		{
			TestNetwork.factory.Create(10);
			String test = GetRequestResult("/view/p?path=(obj==AID)").data;

			if(test == null || test.length() == 0)
				Assert.fail("empty response");
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void HttpObjRequest()
	{
		try
		{
			OctoObjectList l = TestNetwork.factory.Create(10);
			long AID = l.get(0).GetAttribute("AID").GetLong();

			String test = GetRequestResult("/view/p?path=(obj=="+AID+")").data;

			if(test == null || test.length() == 0)
				Assert.fail("empty response");
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void HttpQueryRequest()
	{
		try
		{
			OctoObjectList l = TestNetwork.factory.Create(10);
			long AID = l.get(0).GetAttribute("AID").GetLong();

			String test = GetRequestResult("/view/p?path=obj(type==AID).q(AID=="+AID+")").data;

			if(test == null || test.length() == 0)
				Assert.fail("empty response");
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void HttpUniqRequest()
	{
		try
		{
			TestNetwork.factory.Create(10);
			String test = GetRequestResult("/view/p?path=obj(type==AID).uniq()").data;

			if(test == null || test.length() == 0)
				Assert.fail("empty response");
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void HttpNeighbourRequest()
	{
		try
		{
			OctoObjectList objs = TestNetwork.factory.Create(10);

			TestNetwork.links.AllToAll(objs.range(0, 5), objs.range(0, 10));

			String test = GetRequestResult("/view/p?path=obj(type==AID).in_n()").data;
			if(test == null || test.length() == 0)
				Assert.fail("empty response in_n");

			test = GetRequestResult("/view/p?path=obj(type==AID).out_n()").data;
			if(test == null || test.length() == 0)
				Assert.fail("empty response out_n");

			test = GetRequestResult("/view/p?path=obj(type==AID).all_n()").data;
			if(test == null || test.length() == 0)
				Assert.fail("empty response all_n");
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void HttpObjLinkRequest()
	{
		try
		{
			OctoObjectList objs = TestNetwork.factory.Create(10);

			TestNetwork.links.AllToAll(objs.range(0, 5), objs.range(0, 10));

			String test = GetRequestResult("/view/p?path=obj(type==AID).in_l()").data;
			if(test == null || test.length() == 0)
				Assert.fail("empty response in_l");

			test = GetRequestResult("/view/p?path=obj(type==AID).out_l()").data;
			if(test == null || test.length() == 0)
				Assert.fail("empty response out_l");

			test = GetRequestResult("/view/p?path=obj(type==AID).all_l()").data;
			if(test == null || test.length() == 0)
				Assert.fail("empty response all_l");
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void HttpLinkRequest()
	{
		try
		{
			OctoObjectList objs = TestNetwork.factory.Create(10);

			TestNetwork.links.AllToAll(objs.range(0, 5), objs.range(0, 10));

			String test = GetRequestResult("/view/p?path=link(AID)").data;
			if(test == null || test.length() == 0)
				Assert.fail("empty response");
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void HttpLinkPropRequest()
	{
		try
		{
			OctoObjectList objs = TestNetwork.factory.Create(10);

			TestNetwork.links.AllToAll(objs.range(0, 5), objs.range(0, 10));

			String test = GetRequestResult("/view/p?path=link(AID).source()").data;
			if(test == null || test.length() == 0)
				Assert.fail("empty response source");

			test = GetRequestResult("/view/p?path=link(AID).target()").data;
			if(test == null || test.length() == 0)
				Assert.fail("empty response taregt");
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}
}
