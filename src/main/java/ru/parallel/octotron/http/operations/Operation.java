package ru.parallel.octotron.http.operations;

import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.utils.format.TypedString;

import java.util.Map;

public abstract class Operation
{
	private final String name;
	private final boolean is_blocking;

	public Operation(String name, boolean is_blocking)
	{
		this.name = name;
		this.is_blocking = is_blocking;
	}

	public String GetName()
	{
		return name;
	}

	public boolean IsBlocking()
	{
		return is_blocking;
	}

	public abstract TypedString ExecuteOperation(ExecutionController controller
		, Map<String, String> params) throws ExceptionParseError;
}
