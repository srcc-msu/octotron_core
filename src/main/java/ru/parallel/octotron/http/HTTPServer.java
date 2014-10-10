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
import ru.parallel.octotron.exec.ExecutionController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTP server for requests processing<br>
 * */
public class HTTPServer
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private HttpServer server;
	private final ExecutionController controller;

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
			controller.HttpRequestInform();
			HttpExchangeWrapper http_exchange_wrapper = new HttpExchangeWrapper(http_exchange);

			ParsedModelRequest request;

			try
			{
				request = HttpRequestParser.ParseFromExchange(http_exchange_wrapper);
			}
			catch (ExceptionParseError e)
			{
				http_exchange_wrapper.FinishError(e.getMessage());

				LOGGER.log(Level.WARNING, "request failed: "
					+ http_exchange_wrapper.GetPath() + http_exchange_wrapper.GetQuery(), e);

				return;
			}

			if(!request.IsBlocking())
			{
				http_exchange_wrapper.FinishString("request queued");
				controller.AddRequest(request);
			}
			else
				controller.AddBlockingRequest(request, http_exchange_wrapper);
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

	private static BasicAuthenticator GetAuth(String area, GlobalSettings.Credential credential)
	{
		final String user_ref = credential.user;
		final String password_ref = credential.password;

		return new BasicAuthenticator(area)
		{
			@Override
			public boolean checkCredentials(String user, String password)
			{
				boolean result = user.equals(user_ref) && password.equals(password_ref);

				if(!result)
					LOGGER.log(Level.WARNING, "[FAIL] authentication attempt, username: '" + user + "', password: '" + password + "'");

				return result;
			}
		};
	}

/**
 * create and start the server, listening on /port<br>
 * messages are not guaranteed to come in fixed order<br>
 * */
	public HTTPServer(ExecutionController controller, ExecutorService executor)
		throws ExceptionSystemError
	{
		this.controller = controller;

		// why is it turned off by default >.<
		if(!Boolean.getBoolean("sun.net.httpserver.nodelay"))
			LOGGER.log(Level.CONFIG, "nodelay is not set to true, import will be slow. Add '-Dsun.net.httpserver.nodelay=true' as argument to java command.");

		try
		{
			server = HttpServer.create(new InetSocketAddress(controller.GetContext().settings.GetPort()), 0);
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

		request.setAuthenticator(HTTPServer.GetAuth("view", controller.GetContext().settings.GetViewCredentials()));
		modify.setAuthenticator(HTTPServer.GetAuth("modify", controller.GetContext().settings.GetModifyCredentials()));
		control.setAuthenticator(HTTPServer.GetAuth("control", controller.GetContext().settings.GetControlCredentials()));

		server.start();

		LOGGER.log(Level.INFO, "request server listening on port: " + controller.GetContext().settings.GetPort());
	}

	/**
	 * stop the server, closes all exchanges<br>
	 * */
	public void Finish()
	{
		server.stop(0);
	}
}
