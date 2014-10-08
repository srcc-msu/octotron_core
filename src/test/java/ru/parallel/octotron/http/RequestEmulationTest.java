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
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;

import static org.junit.Assert.fail;

/**
 * sometimes tests can fail if message did not came in time</br>
 * messages are not guaranteed to come in fixed order</br>
 * */
public class RequestEmulationTest
{
	private static final int HTTP_PORT = 4300;

	private static LinkFactory links;
	private static ObjectFactory factory;

	private static DummyHTTPServer http;

	@BeforeClass
	public static void Init() throws Exception
	{
		RequestEmulationTest.http = new DummyHTTPServer(RequestEmulationTest.HTTP_PORT);

		ModelService.Init(ModelService.EMode.CREATION);

		RequestEmulationTest.factory = new ObjectFactory();
		RequestEmulationTest.links = new LinkFactory()
			.Constants(new SimpleAttribute("type", "a_link"));
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		ModelService.Finish();
	}

	private static final long SLEEP = 100;

	@Before
	public void Clean() throws Exception
	{
		ModelService.Get().Clean();
	}

/**
 * get info about request and close it
 * */
	private HttpExchangeWrapper GetHttpRequest(String str) throws Exception
	{
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

		Thread.sleep(RequestEmulationTest.SLEEP);

		HttpExchangeWrapper exchange = http.GetExchange();

		if(exchange == null)
			fail("did not get the message");

		return exchange;
	}

	private String GetRequestResult(String str_request) throws Exception
	{
		HttpExchangeWrapper request = GetHttpRequest(str_request);

		ParsedModelRequest r = HttpRequestParser.ParseFromExchange(request);

		RequestResult result = new ModelRequestExecutor(r, request).GetResult();

		if(result.type.equals(RequestResult.E_RESULT_TYPE.ERROR))
			throw new ExceptionParseError(result.data);

		return result.data;
	}

	@Test
	public void HttpRequest() throws Exception
	{
		RequestEmulationTest.factory.Create(10);
		ModelService.Get().EnableObjectIndex("AID");

		String test = GetRequestResult("/view/p?path=obj(AID)");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpObjRequest() throws Exception
	{
		RequestEmulationTest.factory.Create(10);
		ModelService.Get().EnableObjectIndex("AID");

		String test = GetRequestResult("/view/p?path=obj(AID)");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpQueryRequest() throws Exception
	{
		ModelObjectList l = RequestEmulationTest.factory.Create(10);
		ModelService.Get().EnableObjectIndex("AID");

		long AID = l.get(0).GetAttribute("AID").GetLong();

		String test = GetRequestResult("/view/p?path=obj(AID).q(AID=="+AID+")");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpUniqRequest() throws Exception
	{
		RequestEmulationTest.factory.Create(10);
		ModelService.Get().EnableObjectIndex("AID");

		String test = GetRequestResult("/view/p?path=obj(AID).uniq()");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpNeighbourRequest() throws Exception
	{
		ModelObjectList objects = RequestEmulationTest.factory.Create(10);
		ModelService.Get().EnableObjectIndex("AID");

		RequestEmulationTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));
		ModelService.Get().EnableLinkIndex("AID");

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
		ModelService.Get().EnableObjectIndex("AID");

		RequestEmulationTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));
		ModelService.Get().EnableLinkIndex("AID");

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
		ModelService.Get().EnableObjectIndex("AID");

		RequestEmulationTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));
		ModelService.Get().EnableLinkIndex("AID");

		String test = GetRequestResult("/view/p?path=link(AID)");
		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpLinkPropRequest() throws Exception
	{
		ModelObjectList objects = RequestEmulationTest.factory.Create(10);

		RequestEmulationTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));
		ModelService.Get().EnableLinkIndex("AID");

		String test = GetRequestResult("/view/p?path=link(AID).source()");
		if(test == null || !test.contains("AID"))
			fail("bad response source: " + test);

		test = GetRequestResult("/view/p?path=link(AID).target()");
		if(test == null || !test.contains("AID"))
			fail("bad response target: " + test);
	}
}
