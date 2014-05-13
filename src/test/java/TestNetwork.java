package test.java;

import java.io.IOException;


import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.core.IGraph;
import main.java.ru.parallel.octotron.http.HTTPRequest;
import main.java.ru.parallel.octotron.http.HTTPServer;
import main.java.ru.parallel.octotron.http.ParsedHttpRequest;
import main.java.ru.parallel.octotron.http.RequestParser;
import main.java.ru.parallel.octotron.http.RequestResult;
import main.java.ru.parallel.octotron.impl.generators.LinkFactory;
import main.java.ru.parallel.octotron.impl.generators.ObjectFactory;
import main.java.ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import main.java.ru.parallel.octotron.primitive.SimpleAttribute;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionParseError;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import main.java.ru.parallel.octotron.utils.ObjectList;
import main.java.ru.parallel.utils.FileUtils;

/**
 * sometimes tests can fail.. race condition
 * messages are not guaranteed to come in fixed order
 * */
public class TestNetwork extends Assert
{
	private static int HTTP_PORT = 4300;

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
			http = HTTPServer.GetDummyServer(HTTP_PORT);

			graph = new Neo4jGraph("dbs/test_network", Neo4jGraph.Op.RECREATE);
			graph_service = new GraphService(graph);
			graph_service.EnableObjectIndex("AID");
			graph_service.EnableLinkIndex("AID");

			factory = new ObjectFactory(graph_service);
			links = new LinkFactory(graph_service)
				.Attributes(new SimpleAttribute("type", "a_link"));
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private static long SLEEP = 100;

	@Before
	public void Clean()
	{
		try
		{
			graph_service.Clean();
			http.Clear();
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
	private HTTPRequest GetHttpRequest(String str)
		throws ExceptionSystemError
	{
		ParsedHttpRequest request = null;

		try
		{
			http.Clear();

			FileUtils.ExecSilent("curl", "-u:", "-sS", "127.0.0.1:" + HTTP_PORT + str);
			Thread.sleep(SLEEP);

			if((request = http.GetBlockingRequest()) != null)
				request.GetHttpRequest().FinishString("");
			else
				fail("did not get the message");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
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
			http.Clear();

			String target = "/view/p";
			String params = "path(obj==AID)";

			for(int i = 0; i < COUNT; i++)
			{
				FileUtils.ExecSilent("curl", "-u:", "-sS", "127.0.0.1:" + HTTP_PORT + target + "?" + params  + i);
				Thread.sleep(SLEEP);
			}

			ParsedHttpRequest request;

			for(int i = 0; i < COUNT; i++)
			{
				if((request = http.GetBlockingRequest()) != null)
				{
					assertEquals("got wrong target", request.GetHttpRequest().GetPath(), target);
					assertEquals("got wrong params", request.GetHttpRequest().GetQuery(), params + i);
					request.GetHttpRequest().FinishString("");
				}
				else
					fail("did not get the message");
			}

			if(http.GetRequest() != null)
				fail("unexpected message");
		}

		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	private RequestResult GetRequestResult(String str_request)
		throws IOException, ExceptionSystemError, ExceptionParseError
	{
		HTTPRequest request = GetHttpRequest(str_request);

		return RequestParser.ParseFromHttp(request).GetParsedRequest().Execute(graph_service, null);
	}

	@Test
	public void HttpRequest()
	{
		try
		{
			factory.Create(10);
			String test = GetRequestResult("/view/p?path=(obj==AID)").data;

			if(test == null || test.length() == 0)
				fail("empty response");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void HttpObjRequest()
	{
		try
		{
			ObjectList l = factory.Create(10);
			long AID = l.get(0).GetAttribute("AID").GetLong();

			String test = GetRequestResult("/view/p?path=(obj=="+AID+")").data;

			if(test == null || test.length() == 0)
				fail("empty response");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void HttpQueryRequest()
	{
		try
		{
			ObjectList l = factory.Create(10);
			long AID = l.get(0).GetAttribute("AID").GetLong();

			String test = GetRequestResult("/view/p?path=obj(type==AID).q(AID=="+AID+")").data;

			if(test == null || test.length() == 0)
				fail("empty response");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void HttpUniqRequest()
	{
		try
		{
			factory.Create(10);
			String test = GetRequestResult("/view/p?path=obj(type==AID).uniq()").data;

			if(test == null || test.length() == 0)
				fail("empty response");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void HttpNeighbourRequest()
	{
		try
		{
			ObjectList objs = factory.Create(10);

			links.AllToAll(objs.range(0, 5), objs.range(0, 10));

			String test = GetRequestResult("/view/p?path=obj(type==AID).in_n()").data;
			if(test == null || test.length() == 0)
				fail("empty response in_n");

			test = GetRequestResult("/view/p?path=obj(type==AID).out_n()").data;
			if(test == null || test.length() == 0)
				fail("empty response out_n");

			test = GetRequestResult("/view/p?path=obj(type==AID).all_n()").data;
			if(test == null || test.length() == 0)
				fail("empty response all_n");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void HttpObjLinkRequest()
	{
		try
		{
			ObjectList objs = factory.Create(10);

			links.AllToAll(objs.range(0, 5), objs.range(0, 10));

			String test = GetRequestResult("/view/p?path=obj(type==AID).in_l()").data;
			if(test == null || test.length() == 0)
				fail("empty response in_l");

			test = GetRequestResult("/view/p?path=obj(type==AID).out_l()").data;
			if(test == null || test.length() == 0)
				fail("empty response out_l");

			test = GetRequestResult("/view/p?path=obj(type==AID).all_l()").data;
			if(test == null || test.length() == 0)
				fail("empty response all_l");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void HttpLinkRequest()
	{
		try
		{
			ObjectList objs = factory.Create(10);

			links.AllToAll(objs.range(0, 5), objs.range(0, 10));

			String test = GetRequestResult("/view/p?path=link(AID)").data;
			if(test == null || test.length() == 0)
				fail("empty response");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void HttpLinkPropRequest()
	{
		try
		{
			ObjectList objs = factory.Create(10);

			links.AllToAll(objs.range(0, 5), objs.range(0, 10));

			String test = GetRequestResult("/view/p?path=link(AID).source()").data;
			if(test == null || test.length() == 0)
				fail("empty response source");

			test = GetRequestResult("/view/p?path=link(AID).target()").data;
			if(test == null || test.length() == 0)
				fail("empty response taregt");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
