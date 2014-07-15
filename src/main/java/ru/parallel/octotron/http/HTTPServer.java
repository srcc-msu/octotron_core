/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import com.sun.net.httpserver.*;
import org.apache.commons.lang3.tuple.Pair;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.GlobalSettings;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTP server for requests processing<br>
 * */
public class HTTPServer
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private final Queue<ParsedHttpRequest> requests;
	private final Queue<ParsedHttpRequest> blocking_requests;

	private HttpServer server;
	private final ExecutorService executor;

/**
 * parse request to tokens and add parsed_request to message queue
 * id the parsed_request is not blocking - close the underlying request
 * */
	private class StandardHandler implements HttpHandler
	{
		@Override
		public void handle(HttpExchange http_exchange)
			throws IOException
		{
			HTTPRequest http_request = new HTTPRequest(http_exchange);

			ParsedHttpRequest request;

			try
			{
				request = RequestParser.ParseFromHttp(http_request);
			}
			catch (ExceptionParseError e)
			{
				http_request.FinishError(e.getMessage());

				LOGGER.log(Level.WARNING, "request failed: "
					+ http_request.GetPath() + http_request.GetQuery(), e);

				return;
			}

			if(!request.GetParsedRequest().IsBlocking())
			{
				http_request.FinishString("request queued");
				requests.add(request);
			}
			else
				blocking_requests.add(request);
		}
	}

/**
 * parse request to tokens and add parsed_request to message queue
 * id the parsed_request is not blocking - close the underlying request
 * */
	private class DefaultHandler implements HttpHandler
	{
		@Override
		public void handle(HttpExchange http_exchange)
			throws IOException
		{
			HTTPRequest http_request = new HTTPRequest(http_exchange);

			http_request.FinishString("URI is not in format /request_type/operation");
		}
	}

	private static BasicAuthenticator GetAuth(String area, Pair<String, String> auth)
	{
		final String user_ref = auth.getLeft();
		final String password_ref = auth.getRight();

		return new BasicAuthenticator(area)
		{
			@Override
			public boolean checkCredentials(String user, String password)
			{
				return user.equals(user_ref) && password.equals(password_ref);
			}
		};
	}

/**
 * create and start the server, listening on /port<br>
 * messages are not guaranteed to come in fixed order<br>
 * */
	public HTTPServer(GlobalSettings settings)
		throws ExceptionSystemError
	{
		// why is it turned off by default >.<
		if(!Boolean.getBoolean("sun.net.httpserver.nodelay"))
			LOGGER.log(Level.CONFIG, "nodelay is not set to true, import will be slow. Add '-Dsun.net.httpserver.nodelay=true' as argument to java command.");

		requests = new ConcurrentLinkedQueue<>();
		blocking_requests = new ConcurrentLinkedQueue<>();

		executor = Executors.newCachedThreadPool();

		try
		{
			server = HttpServer.create(new InetSocketAddress(settings.GetPort()), 0);
		}
		catch (IOException e)
		{
			throw new ExceptionSystemError(e);
		}

		server.setExecutor(executor);

		HttpContext request = server.createContext("/view", new StandardHandler());
		HttpContext modify  = server.createContext("/modify", new StandardHandler());
		HttpContext control = server.createContext("/control", new StandardHandler());

		server.createContext("/", new DefaultHandler());

		request.setAuthenticator(HTTPServer.GetAuth("view", settings.GetViewCredentials()));
		modify.setAuthenticator(HTTPServer.GetAuth("modify", settings.GetModifyCredentials()));
		control.setAuthenticator(HTTPServer.GetAuth("control", settings.GetControlCredentials()));

		server.start();

		LOGGER.log(Level.INFO, "request server listening on port: " + settings.GetPort());
	}

/**
 * dummy constructor for testing
 * */
	private HTTPServer(int port)
		throws ExceptionSystemError
	{
		requests = new ConcurrentLinkedQueue<>();
		blocking_requests = new ConcurrentLinkedQueue<>();

		executor = Executors.newCachedThreadPool();

		try
		{
			server = HttpServer.create(new InetSocketAddress(port), 0);
		}
		catch (IOException e)
		{
			throw new ExceptionSystemError(e);
		}

		server.setExecutor(executor);

		server.createContext("/view", new StandardHandler());
		server.createContext("/modify", new StandardHandler());
		server.createContext("/control", new StandardHandler());

		server.createContext("/", new DefaultHandler());

		server.start();
	}

	public static HTTPServer GetDummyServer(int port)
		throws ExceptionSystemError
	{
		return new HTTPServer(port);
	}

/**
 * returns a single request, if got something. other way returns \null<br>
 * */
	public ParsedHttpRequest GetRequest()
	{
		return requests.poll();
	}

/**
 * returns a single request, if got something. other way returns \null<br>
 * */
	public ParsedHttpRequest GetBlockingRequest()
	{
		return blocking_requests.poll();
	}

/**
 * stop the server<br>
 * */
	public void Finish()
	{
		server.stop(0);
		executor.shutdown();
	}

/**
 * clean all unclaimed messages from the requests queue<br>
 * */
	public void Clear()
	{
		requests.clear();
		blocking_requests.clear();
	}
}
