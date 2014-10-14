package ru.parallel.octotron.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.junit.Before;
import org.junit.Test;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;

import static org.junit.Assert.fail;

/**
 * sometimes tests can fail if message did not came in time</br>
 * messages are not guaranteed to come in fixed order</br>
 * */
public class RequestEmulationTest
{
	private static Context context;
	private ExecutionController controller;

	@Before
	public void InitController() throws Exception
	{
		context = Context.CreateTestContext(0);
		controller = new ExecutionController(context);
	}

	private static LinkFactory links;
	private static ObjectFactory factory;

	private static DummyHTTPServer http;

	@Before
	public void Init() throws Exception
	{
		RequestEmulationTest.http = new DummyHTTPServer(0);

		RequestEmulationTest.factory = new ObjectFactory(context.model_service);
		RequestEmulationTest.links = new LinkFactory(context.model_service)
			.Constants(new SimpleAttribute("type", "a_link"));
	}

	private static final long SLEEP = 100;

/**
 * get info about request and close it
 * */
	private HttpExchangeWrapper GetHttpRequest(String str) throws Exception
	{
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://127.0.0.1:" + http.GetPort() + str);

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

		ParsedModelRequest parsed_request = HttpRequestParser.ParseFromExchange(request);

		RequestResult result = new ModelRequestExecutor(controller, parsed_request).GetResult();

		if(result.type.equals(RequestResult.E_RESULT_TYPE.ERROR))
			throw new ExceptionParseError(result.data);

		return result.data;
	}

	@Test
	public void HttpRequest() throws Exception
	{
		RequestEmulationTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/p?path=obj(AID)");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpObjRequest() throws Exception
	{
		RequestEmulationTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/p?path=obj(AID)");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpQueryRequest() throws Exception
	{
		ModelObjectList l = RequestEmulationTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		long AID = l.get(0).GetAttribute("AID").GetLong();

		String test = GetRequestResult("/view/p?path=obj(AID).q(AID=="+AID+")");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpUniqRequest() throws Exception
	{
		RequestEmulationTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		String test = GetRequestResult("/view/p?path=obj(AID).uniq()");

		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpNeighbourRequest() throws Exception
	{
		ModelObjectList objects = RequestEmulationTest.factory.Create(10);
		context.model_service.EnableObjectIndex("AID");

		RequestEmulationTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));
		context.model_service.EnableLinkIndex("AID");

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
		context.model_service.EnableObjectIndex("AID");

		RequestEmulationTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));
		context.model_service.EnableLinkIndex("AID");

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
		context.model_service.EnableObjectIndex("AID");

		RequestEmulationTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));
		context.model_service.EnableLinkIndex("AID");

		String test = GetRequestResult("/view/p?path=link(AID)");
		if(test == null || !test.contains("AID"))
			fail("bad response: " + test);
	}

	@Test
	public void HttpLinkPropRequest() throws Exception
	{
		ModelObjectList objects = RequestEmulationTest.factory.Create(10);

		RequestEmulationTest.links.AllToAll(objects.range(0, 5), objects.range(0, 10));
		context.model_service.EnableLinkIndex("AID");

		String test = GetRequestResult("/view/p?path=link(AID).source()");
		if(test == null || !test.contains("AID"))
			fail("bad response source: " + test);

		test = GetRequestResult("/view/p?path=link(AID).target()");
		if(test == null || !test.contains("AID"))
			fail("bad response target: " + test);
	}
}
