/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.http.Operations.Operation;
import ru.parallel.octotron.http.RequestResult.E_RESULT_TYPE;
import ru.parallel.octotron.logic.ExecutionController;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * parsed request with correct tokens list<br>
 * */
public class ParsedModelRequest
{
	final Operation operation;
	final Map<String, String> params;

	public ParsedModelRequest(Operation operation, Map<String, String> params)
	{
		this.operation = operation;
		this.params = params;
	}

	public boolean IsBlocking()
	{
		return operation.IsBlocking();
	}
}
