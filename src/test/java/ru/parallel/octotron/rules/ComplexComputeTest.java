package ru.parallel.octotron.rules;

import org.junit.BeforeClass;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EDependencyType;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.services.impl.PersistenceService;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.generators.tmpl.SensorTemplate;
import ru.parallel.octotron.generators.tmpl.VarTemplate;

//they do not work because invalid or wrong ctime, need to fix somehow
public class ComplexComputeTest extends GeneralTest
{
	private static ModelObject object;
	private final PersistenceService persistence_service;

	public ComplexComputeTest()
	{
		persistence_service = new PersistenceService("test_persistence", context);
		persistence_service.InitDummy();
	}

	@BeforeClass
	public static void Init()
		throws ExceptionSystemError
	{
		ObjectFactory self = new ObjectFactory(model_service)
			.Sensors(new SensorTemplate("val1", 10))
			.Sensors(new SensorTemplate("val2", 10))
			.Sensors(new SensorTemplate("val3", 10))
			.Sensors(new SensorTemplate("val4", 10))
			.Vars(new VarTemplate("check", new ASoftMatchCount(true, EDependencyType.SELF, "val1", "val2")));

		object = self.Create();
		model_service.Operate();
	}
}
