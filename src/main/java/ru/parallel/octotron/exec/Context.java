package ru.parallel.octotron.exec;

import ru.parallel.octotron.core.model.ModelData;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;

public final class Context
{
	public final GlobalSettings settings;
	public final ModelData model_data;
	public final ModelService model_service;

	private Context(GlobalSettings settings, ModelData model_data, ModelService model_service)
	{
		this.settings = settings;
		this.model_data = model_data;
		this.model_service = model_service;
	}

	public static Context CreateFromConfig(String json_config)
		throws ExceptionSystemError
	{
		GlobalSettings settings = new GlobalSettings(json_config);
		ModelData model_data = new ModelData();
		ModelService model_service = new ModelService(model_data, ModelService.EMode.CREATION
			, settings.GetDbPath() + "/" + settings.GetModelName());

		return new Context(settings, model_data, model_service);
	}

	public static Context CreateTestContext(int port)
		throws ExceptionSystemError
	{
		GlobalSettings settings = new GlobalSettings(port);
		ModelData model_data = new ModelData();
		ModelService model_service = new ModelService(model_data, ModelService.EMode.CREATION
			, settings.GetDbPath() + "/" + settings.GetModelName());

		return new Context(settings, model_data, model_service);
	}
}
