/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import com.sun.net.httpserver.*;
import ru.parallel.octotron.exception.ExceptionParseError;
import ru.parallel.octotron.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.exec.GlobalSettings;
import ru.parallel.octotron.http.requests.HttpExchangeWrapper;
import ru.parallel.octotron.http.requests.HttpRequestParser;
import ru.parallel.octotron.http.requests.ParsedModelRequest;
import ru.parallel.octotron.bg_services.ServiceLocator;
import ru.parallel.octotron.bg_services.side.HttpService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTP server for requests processing<br>
 * */
public class HTTPServer
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private final HttpServer server;

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

			ParsedModelRequest request;

			try
			{
				request = HttpRequestParser.ParseFromExchange(http_exchange_wrapper);
			}
			catch(ExceptionParseError e)
			{
				http_exchange_wrapper.FinishError(e.getMessage());

				LOGGER.log(Level.WARNING, "request failed: "
					+ http_exchange_wrapper.GetPath() + http_exchange_wrapper.GetQuery(), e);

				return;
			}

			if(!request.IsBlocking())
			{
				http_exchange_wrapper.FinishString("request queued");
				ServiceLocator.INSTANCE.GetRequestService().AddRequest(request);
			}
			else
				ServiceLocator.INSTANCE.GetBlockingRequestService().AddRequest(request, http_exchange_wrapper);
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

	private static void SetAuth(HttpContext context, String area, GlobalSettings.Credential credential)
	{
		final String user_ref = credential.user;
		final String password_ref = credential.password;

		if(user_ref.isEmpty() && password_ref.isEmpty())
			return;

		context.setAuthenticator(new BasicAuthenticator(area)
		{
			@Override
			public boolean checkCredentials(String user, String password)
			{
				boolean result = user.equals(user_ref) && password.equals(password_ref);

				if(!result)
					LOGGER.log(Level.WARNING, "[FAIL] authentication attempt, username: '" + user + "', password: '" + password + "'");

				return result;
			}
		});
	}

/**
 * create and start the server, listening on /port<br>
 * messages are not guaranteed to come in fixed order<br>
 * */
	public HTTPServer(Context context, HttpService http_service)
		throws ExceptionSystemError
	{
		// why is it turned off by default >.<
		if(!Boolean.getBoolean("sun.net.httpserver.nodelay"))
			LOGGER.log(Level.CONFIG, "nodelay is not set to true, import will be slow. Add '-Dsun.net.httpserver.nodelay=true' as argument to java command.");

		try
		{
			server = HttpServer.create(new InetSocketAddress(context.settings.GetPort()), 0);
		}
		catch(IOException e)
		{
			throw new ExceptionSystemError(e);
		}

		server.setExecutor(http_service.GetExecutor());

		HttpContext view = server.createContext("/view", new StandardHandler());
		HttpContext modify  = server.createContext("/modify", new StandardHandler());
		HttpContext control = server.createContext("/control", new StandardHandler());

		server.createContext("/", new DefaultHandler());

		HTTPServer.SetAuth(view, "view", context.settings.GetViewCredentials());
		HTTPServer.SetAuth(modify, "modify", context.settings.GetModifyCredentials());
		HTTPServer.SetAuth(control, "control", context.settings.GetControlCredentials());

		server.start();

		LOGGER.log(Level.INFO, "request server listening on port: " + context.settings.GetPort());
	}

	/**
	 * stop the server, closes all exchanges<br>
	 * */
	public void Finish()
	{
		server.stop(0);
	}
}
