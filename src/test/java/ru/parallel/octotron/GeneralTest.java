package ru.parallel.octotron;

import org.junit.BeforeClass;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.bg_services.ServiceLocator;
import ru.parallel.octotron.services.ModelService;

public class GeneralTest
{
	protected static Context context;
	protected static ModelService model_service;

	@BeforeClass
	public static void InitCommon() throws Exception
	{
		context = Context.CreateTestContext(0);
		ServiceLocator.INSTANCE = new ServiceLocator(context);

		model_service = ServiceLocator.INSTANCE.GetModelService();
	}
}
