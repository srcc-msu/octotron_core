package ru.parallel.octotron.exec;

import ru.parallel.octotron.core.model.ModelData;
import ru.parallel.octotron.core.model.ModelService;

public final class Context
{
	public final ModelData model_data;
	public final ModelService model_service;
	public final GlobalSettings settings;

	private Context(ModelData model_data, ModelService model_service, GlobalSettings settings)
	{
		this.model_data = model_data;
		this.model_service = model_service;
		this.settings = settings;
	}

	public static Context CreateFromConfig(String json_config)
	{
		ModelData model_data = new ModelData();
		ModelService model_service = new ModelService(model_data, ModelService.EMode.CREATION);
		GlobalSettings settings = new GlobalSettings(json_config);

		return new Context(model_data, model_service, settings);
	}

	public static Context CreateTestContext(int port)
	{
		ModelData model_data = new ModelData();
		ModelService model_service = new ModelService(model_data, ModelService.EMode.CREATION);
		GlobalSettings settings = new GlobalSettings(port);

		return new Context(model_data, model_service, settings);
	}
}
