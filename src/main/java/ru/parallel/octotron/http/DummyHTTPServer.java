/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.http.requests.HttpExchangeWrapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * HTTP server for requests processing<br>
 * */
public class DummyHTTPServer
{
	private HttpServer server;
	private int port;

	private final List<HttpExchangeWrapper> requests = new LinkedList<>();

	public HttpExchangeWrapper GetExchange()
	{
		return requests.remove(0);
	}


	public int GetPort()
	{
		return port;
	}

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
			HttpExchangeWrapper http_exchange_wrapper = new HttpExchangeWrapper(http_exchange);

			requests.add(http_exchange_wrapper);
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
			HttpExchangeWrapper http_request = new HttpExchangeWrapper(http_exchange);

			http_request.FinishString("URI is not in format /request_type/operation");
		}
	}

/**
 * create and start the server, listening on /port<br>
 * messages are not guaranteed to come in fixed order<br>
 * */
	public DummyHTTPServer(int port)
		throws ExceptionSystemError
	{
		try
		{
			server = HttpServer.create(new InetSocketAddress(port), 0);
		}
		catch(IOException e)
		{
			throw new ExceptionSystemError(e);
		}

		server.setExecutor(null);

		server.createContext("/view", new StandardHandler());
		server.createContext("/modify", new StandardHandler());
		server.createContext("/control", new StandardHandler());

		server.createContext("/", new DefaultHandler());

		server.start();

		this.port = server.getAddress().getPort();
	}

/**
 * stop the server<br>
 * */
	public void Finish()
	{
		server.stop(0);
	}
}
