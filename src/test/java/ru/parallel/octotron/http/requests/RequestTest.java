package ru.parallel.octotron.http.requests;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.junit.Before;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.http.DummyHTTPServer;
import ru.parallel.utils.format.ErrorString;
import ru.parallel.utils.format.TypedString;

import static org.junit.Assert.fail;

public class RequestTest
{
	private static final long SLEEP = 100;
	protected static Context context;
	protected static LinkFactory links;
	protected static ObjectFactory factory;
	private static DummyHTTPServer http;
	private ExecutionController controller;

	@Before
	public void InitController() throws Exception
	{
		context = Context.CreateTestContext(0);
		controller = new ExecutionController(context);
	}

	@Before
	public void Init() throws Exception
	{
		RequestTest.http = new DummyHTTPServer(0);

		RequestTest.factory = new ObjectFactory(context.model_service);
		RequestTest.links = new LinkFactory(context.model_service)
			.Constants(new SimpleAttribute("type", "a_link"));
	}

/**
 * get info about request and close it
 * */
	private HttpExchangeWrapper GetHttpRequest(String str) throws Exception
	{
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://127.0.0.1:" + http.GetPort() + str);

		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, (int) RequestTest.SLEEP);
		HttpConnectionParams.setSoTimeout(params, (int) RequestTest.SLEEP);

		try
		{
			client.execute(request);
		}
		catch (Exception ignore)
		{}

		client.getConnectionManager().shutdown();

		Thread.sleep(RequestTest.SLEEP);

		HttpExchangeWrapper exchange = http.GetExchange();

		if(exchange == null)
			fail("did not get the message");

		return exchange;
	}

	protected String GetRequestResult(String str_request) throws Exception
	{
		HttpExchangeWrapper request = GetHttpRequest(str_request);

		ParsedModelRequest parsed_request = HttpRequestParser.ParseFromExchange(request);

		TypedString result = new ModelRequestExecutor(controller, parsed_request).GetResult();

		if(result instanceof ErrorString)
			throw new ExceptionParseError(result.string);

		return result.string;
	}
}
