/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;


import org.apache.commons.io.IOUtils;

import main.java.ru.parallel.octotron.http.RequestResult.E_RESULT_TYPE;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

/**
 * stores all request information and<br>
 * allows to finish them with certain results<br>
 * */
public class HTTPRequest
{
	private HttpExchange http_request;
	private OutputStream http_request_writer;

	private boolean finished = false;

	private final String query;
	private final String path;

	public HTTPRequest(HttpExchange http_request)
	{
		this.http_request = http_request;
		http_request_writer = http_request.getResponseBody();

		query = http_request.getRequestURI().getQuery();
		path = http_request.getRequestURI().getPath();
	}

/**
 * finish the current request, fill content_type and<br>
 * return code with data from result<br>
 * */
	public void Finish(RequestResult result)
		throws IOException
	{
		String content_type;
		int return_code;

		if(result.type == E_RESULT_TYPE.JSON)
			content_type = "application/json";
		else if(result.type == E_RESULT_TYPE.JSONP)
			content_type = "application/javascript";
		else if(result.type == E_RESULT_TYPE.CSV)
			content_type = "text/csv";
		else
			content_type = "text/plain";

		if(result.type == E_RESULT_TYPE.ERROR)
			return_code = HttpURLConnection.HTTP_BAD_REQUEST;
		else
			return_code = HttpURLConnection.HTTP_OK;

		Finish(result.data, content_type, return_code);
	}

/**
 * successfully finish request with specified response<br>
 * */
	public void FinishString(String response)
		throws IOException
	{
		Finish(response, "text/plain", HttpURLConnection.HTTP_OK);
	}

/**
 * finish the current request with error and show the error description<br>
 * */
	public void FinishError(String description)
		throws IOException
	{
		Finish(description, "text/plain", HttpURLConnection.HTTP_BAD_REQUEST);
	}

/**
 * finish the current request with specified content type and return code<br>
 * */
	private void Finish(String data, String content_type, int return_code)
		throws IOException
	{
		if(finished)
			throw new IOException("attempt to finish a finished request");

		finished = true;

		InputStream http_request_reader = http_request.getRequestBody();

// read all body if the request has send something useless
		try
		{
			IOUtils.readLines(http_request_reader);
		}
		catch (IOException ignore)
		{
			System.err.println("could not read from a request");
		}
		finally
		{
			try
			{
				http_request_reader.close();
			}
			catch (IOException ignore)
			{
				System.err.println("could not close a request");
			}
		}

		Headers header = http_request.getResponseHeaders();
		header.set("Content-Type", content_type + "; charset=us-ascii");

// send headers and data
		try
		{
			http_request.sendResponseHeaders(return_code, data.length());
			http_request_writer.write(data.getBytes());
		}
		catch (IOException ignore) // most likely a client closed without reading
		{
			System.err.println("could not write all data");
		}
		finally
		{
			try
			{
				http_request_writer.close();
			}
			catch(IOException ignore) // may fail to close because did not write enough bytes
			{
				System.err.println("could not close a writer");
			}
		}

		http_request.close();
	}

	public String GetQuery()
	{
		return query;
	}

	public String GetPath()
	{
		return path;
	}
}
