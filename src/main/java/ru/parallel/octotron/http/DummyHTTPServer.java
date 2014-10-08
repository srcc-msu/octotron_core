/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import com.sun.net.httpserver.*;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.GlobalSettings;
import ru.parallel.octotron.logic.ExecutionController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTP server for requests processing<br>
 * */
public class DummyHTTPServer
{
	private HttpServer server;

	public HttpExchangeWrapper GetExchange()
	{
		return requests.remove(0);
	}

	private List<HttpExchangeWrapper> requests = new LinkedList<>();

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
		catch (IOException e)
		{
			throw new ExceptionSystemError(e);
		}

		server.setExecutor(null);

		HttpContext request = server.createContext("/view", new StandardHandler());
		HttpContext modify  = server.createContext("/modify", new StandardHandler());
		HttpContext control = server.createContext("/control", new StandardHandler());

		server.createContext("/", new DefaultHandler());

		server.start();
	}

/**
 * stop the server<br>
 * */
	public void Finish()
	{
		server.stop(0);
	}
}
