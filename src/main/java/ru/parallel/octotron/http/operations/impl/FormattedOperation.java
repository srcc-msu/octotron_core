package ru.parallel.octotron.http.operations.impl;

import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.http.operations.Operation;
import ru.parallel.utils.format.TypedString;

import java.util.Map;

import static ru.parallel.utils.AutoFormat.ToJsonp;

public abstract class FormattedOperation extends Operation
{
	public FormattedOperation(String name, boolean is_blocking)
	{
		super(name, is_blocking);
	}

	@Override
	public final TypedString ExecuteOperation(ExecutionController controller, Map<String, String> params)
		throws ExceptionParseError
	{
		String callback = params.get("callback");
		params.remove("callback");

		TypedString result = Execute(controller, params);

		if(callback != null)
			return ToJsonp(result, callback);

		return result;
	}

	protected abstract TypedString Execute(ExecutionController controller, Map<String, String> params)
		throws ExceptionParseError;
}
