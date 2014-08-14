package ru.parallel.octotron.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.model.impl.ModelObjectList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import static org.junit.Assert.*;

/**
 * sometimes tests can fail if message did not came in time</br>
 * messages are not guaranteed to come in fixed order</br>
 * */
public class RequestEmulationTest
{
	private static final int HTTP_PORT = 4300;

	private static Neo4jGraph graph;

	private static LinkFactory links;
	private static ObjectFactory factory;

	private static HTTPServer http;

	@BeforeClass
	public static void Init() throws Exception
	{
		RequestEmulationTest.http = HTTPServer.GetDummyServer(RequestEmulationTest.HTTP_PORT);

		RequestEmulationTest.graph = new Neo4jGraph( "dbs/" + RequestEmulationTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphService.Init (graph);
		GraphService.Get().EnableObjectIndex("AID");
		GraphService.Get().EnableLinkIndex("AID");

		RequestEmulationTest.factory = new ObjectFactory();
		RequestEmulationTest.links = new LinkFactory()
			.Constants(new SimpleAttribute("type", "a_link"));
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		RequestEmulationTest.graph.Shutdown();
		RequestEmulationTest.graph.Delete();
	}

	private static final long SLEEP = 100;

	@Before
	public void Clean() throws Exception
	{
		GraphService.Get().Clean();
		RequestEmulationTest.http.Clear();
	}

/**
 * get info about request and close it
 * */
	private HTTPRequest GetHttpRequest(String str) throws Exception
	{
		RequestEmulationTest.http.Clear();

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://127.0.0.1:" + RequestEmulationTest.HTTP_PORT + str);

		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, (int)RequestEmulationTest.SLEEP);
		HttpConnectionParams.setSoTimeout(params, (int)RequestEmulationTest.SLEEP);

		try
		{
			client.execute(request);
		}
		catch (Exception ignore)
		{}

		client.getConnectionManager().shutdown();

		ParsedHttpRequest parsed_request = RequestEmulationTest.http.GetBlockingRequest();

		if(parsed_request == null)
			fail("did not get the message");

		parsed_request.GetHttpRequest().FinishString("");

		return parsed_request.GetHttpRequest();
	}

	private String GetRequestResult(String str_request) throws Exception
	{
		HTTPRequest request = GetHttpRequest(str_request);

		RequestResult result = RequestParser.ParseFromHttp(request)
			.GetParsedRequest().Execute(null);

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
			fail("bad response: " + test);
	}

	@Test
	public void HttpObjRequest() throws Exception
	{
		RequestEmulationTest.factory.Create(10);

		String test = GetRequestResult("/view/p?path=obj(AID)");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpQueryRequest() throws Exception
	{
		ModelObjectList l = RequestEmulationTest.factory.Create(10);
		long AID = l.get(0).GetAttribute("AID").GetLong();

		String test = GetRequestResult("/view/p?path=obj(AID).q(AID=="+AID+")");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpUniqRequest() throws Exception
	{
		RequestEmulationTest.factory.Create(10);
		String test = GetRequestResult("/view/p?path=obj(AID).uniq()");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpNeighbourRequest() throws Exception
	{
		ModelObjectList objects = RequestEmulationTest.factory.Create(10);
		RequestEmulationTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));

		String test = GetRequestResult("/view/p?path=obj(AID).in_n()");
		if(test == null || !test.contains("AID"))
			fail("bad response in_n: " + test);

		test = GetRequestResult("/view/p?path=obj(AID).out_n()");
		if(test == null || !test.contains("AID"))
			fail("bad response out_n: " + test);

		test = GetRequestResult("/view/p?path=obj(AID).all_n()");
		if(test == null || !test.contains("AID"))
			fail("bad response all_n: " + test);
	}

	@Test
	public void HttpObjLinkRequest() throws Exception
	{
		ModelObjectList objects = RequestEmulationTest.factory.Create(10);

		RequestEmulationTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));

		String test = GetRequestResult("/view/p?path=obj(AID).in_l()");
		if(test == null || !test.contains("AID"))
			fail("bad response in_l: " + test);

		test = GetRequestResult("/view/p?path=obj(AID).out_l()");
		if(test == null || !test.contains("AID"))
			fail("bad response out_l: " + test);

		test = GetRequestResult("/view/p?path=obj(AID).all_l()");
		if(test == null || !test.contains("AID"))
			fail("bad response all_l: " + test);
	}

	@Test
	public void HttpLinkRequest() throws Exception
	{
		ModelObjectList objects = RequestEmulationTest.factory.Create(10);

		RequestEmulationTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));

		String test = GetRequestResult("/view/p?path=link(AID)");
		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpLinkPropRequest() throws Exception
	{
		ModelObjectList objects = RequestEmulationTest.factory.Create(10);

		RequestEmulationTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));

		String test = GetRequestResult("/view/p?path=link(AID).source()");
		if(test == null || !test.contains("AID"))
			fail("bad response source: " + test);

		test = GetRequestResult("/view/p?path=link(AID).target()");
		if(test == null || !test.contains("AID"))
			fail("bad response target: " + test);
	}
}
