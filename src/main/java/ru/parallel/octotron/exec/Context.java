/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.services.ModelData;
import ru.parallel.octotron.logic.Statistics;

public final class Context
{
	public final GlobalSettings settings;
	public final ModelData model_data;

	public final Statistics stat = new Statistics();


	private Context(GlobalSettings settings, ModelData model_data)
	{
		this.settings = settings;
		this.model_data = model_data;
	}

	public static Context CreateFromConfig(String json_config)
		throws ExceptionSystemError
	{
		GlobalSettings settings = new GlobalSettings(json_config);
		ModelData model_data = new ModelData();


		return new Context(settings, model_data);
	}

	public static Context CreateTestContext(int port)
		throws ExceptionSystemError
	{
		GlobalSettings settings = new GlobalSettings(port);
		ModelData model_data = new ModelData();

		return new Context(settings, model_data);
	}
}
