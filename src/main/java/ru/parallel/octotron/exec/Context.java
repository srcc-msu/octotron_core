/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

public final class Context
{
	public final GlobalSettings settings;

	private Context(GlobalSettings settings)
	{
		this.settings = settings;
	}

	public static Context CreateFromConfig(String json_config)
	{
		GlobalSettings settings = new GlobalSettings(json_config);

		return new Context(settings);
	}

	public static Context CreateTestContext(int port)
	{
		GlobalSettings settings = new GlobalSettings(port);
		ModelData model_data = new ModelData();

		return new Context(settings);
	}
}
