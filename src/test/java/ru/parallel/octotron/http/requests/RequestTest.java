package ru.parallel.octotron.http.requests;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.junit.Before;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.services.ServiceLocator;
import ru.parallel.octotron.services.impl.ModelService;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.generators.tmpl.ConstTemplate;
import ru.parallel.octotron.http.DummyHTTPServer;
import ru.parallel.utils.format.ErrorString;
import ru.parallel.utils.format.TypedString;

import static org.junit.Assert.fail;

public class RequestTest
{
	private static final long SLEEP = 100;

	protected Context context;
	protected ModelService model_service;

	private DummyHTTPServer http;

	protected LinkFactory link_factory;
	protected ObjectFactory object_factory;

	@Before
	public void InitCommon() throws Exception
	{
		context = Context.CreateTestContext(0);
		ServiceLocator.INSTANCE = new ServiceLocator(context);

		model_service = ServiceLocator.INSTANCE.GetModelService();

		http = new DummyHTTPServer(0);

		object_factory = new ObjectFactory();
		link_factory = new LinkFactory()
			.Constants(new ConstTemplate("type", "a_link"));
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

		TypedString result = new ModelRequestExecutor(parsed_request).GetResult();

		if(result instanceof ErrorString)
			throw new ExceptionParseError(result.string);

		return result.string;
	}
}
