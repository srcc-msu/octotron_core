package ru.parallel.octotron.core.model;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.parallel.octotron.GeneralTest;

import static org.junit.Assert.assertEquals;

public class ModelObjectBuilderTest extends GeneralTest
{
	private ModelObject object;
	private ModelObjectBuilder builder;

	@Before
	public void Init() throws Exception
	{
		object = new ModelObject();
		builder = new ModelObjectBuilder(object);
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void TestAddOutLink()
	{
		assertEquals(object.GetOutLinks().size(), 0);
		assertEquals(object.GetInLinks().size(), 0);
		assertEquals(object.GetUndirectedLinks().size(), 0);
		assertEquals(object.GetAllLinks().size(), 0);

		builder.AddOutLink(new ModelLink(object, object, true));

		assertEquals(object.GetOutLinks().size(), 1);
		assertEquals(object.GetInLinks().size(), 0);
		assertEquals(object.GetUndirectedLinks().size(), 0);
		assertEquals(object.GetAllLinks().size(), 1);
	}

	@Test
	public void TestAddInLink()
	{
		assertEquals(object.GetOutLinks().size(), 0);
		assertEquals(object.GetInLinks().size(), 0);
		assertEquals(object.GetUndirectedLinks().size(), 0);
		assertEquals(object.GetAllLinks().size(), 0);

		builder.AddInLink(new ModelLink(object, object, true));

		assertEquals(object.GetOutLinks().size(), 0);
		assertEquals(object.GetInLinks().size(), 1);
		assertEquals(object.GetUndirectedLinks().size(), 0);
		assertEquals(object.GetAllLinks().size(), 1);
	}

	@Test
	public void TestAddUndirectedLink()
	{
		assertEquals(object.GetOutLinks().size(), 0);
		assertEquals(object.GetInLinks().size(), 0);
		assertEquals(object.GetUndirectedLinks().size(), 0);
		assertEquals(object.GetAllLinks().size(), 0);

		builder.AddUndirectedLink(new ModelLink(object, object, false));

		assertEquals(object.GetOutLinks().size(), 0);
		assertEquals(object.GetInLinks().size(), 0);
		assertEquals(object.GetUndirectedLinks().size(), 1);
		assertEquals(object.GetAllLinks().size(), 1);
	}
}
