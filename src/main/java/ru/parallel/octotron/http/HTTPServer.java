/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.apache.commons.lang3.tuple.Pair;

import main.java.ru.parallel.octotron.exec.GlobalSettings;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionParseError;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * HTTP server for requests processing<br>
 * */
public class HTTPServer
{
	private Queue<ParsedHttpRequest> requests;
	private Queue<ParsedHttpRequest> blocking_requests;

	private HttpServer server;
	private ExecutorService executor;

/**
 * parse request to tokens and add parsed_request to message queue
 * id the parsed_request is not blocking - close the underlying request
 * */
	private class StandartHandler implements HttpHandler
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

				System.err.println("request failed:" + http_request.GetQuery());
				System.err.println(e.getMessage());

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
		this(settings, false);
	}

	public HTTPServer(GlobalSettings settings, boolean suppress_check)
		throws ExceptionSystemError
	{
		if(!Boolean.getBoolean("sun.net.httpserver.nodelay"))
		{
			if(suppress_check)
				System.err.println("nodelay is not set to true, import will be slow. Add '-Dsun.net.httpserver.nodelay=true' as argument to java command.");
			else
				throw new RuntimeException("nodelay is not set to true. Add '-Dsun.net.httpserver.nodelay=true' as argument to java command.");
		}
		// why is it turned off by default >.<

		requests = new ConcurrentLinkedQueue<ParsedHttpRequest>();
		blocking_requests = new ConcurrentLinkedQueue<ParsedHttpRequest>();

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

		HttpContext request = server.createContext("/view", new StandartHandler());
		HttpContext modify  = server.createContext("/modify", new StandartHandler());
		HttpContext control = server.createContext("/control", new StandartHandler());

		server.createContext("/", new DefaultHandler());

		request.setAuthenticator(GetAuth("view", settings.GetViewCredentials()));
		modify.setAuthenticator(GetAuth("modify", settings.GetModifyCredentials()));
		control.setAuthenticator(GetAuth("control", settings.GetControlCredentials()));

		server.start();

		System.out.println("request server listetning on port: " + settings.GetPort());
	}

/**
 * dummy constructor for testing
 * */
	private HTTPServer(int port)
		throws ExceptionSystemError
	{
		requests = new ConcurrentLinkedQueue<ParsedHttpRequest>();
		blocking_requests = new ConcurrentLinkedQueue<ParsedHttpRequest>();

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

		server.createContext("/view", new StandartHandler());
		server.createContext("/modify", new StandartHandler());
		server.createContext("/control", new StandartHandler());

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
