package ru.parallel.octotron.rules;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.GeneralTest;
import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.generators.Enumerator;
import ru.parallel.octotron.generators.LinkFactory;
import ru.parallel.octotron.generators.ObjectFactory;
import ru.parallel.octotron.generators.tmpl.ConstTemplate;
import ru.parallel.octotron.generators.tmpl.SensorTemplate;
import ru.parallel.octotron.generators.tmpl.VarTemplate;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

//they do not work because invalid or wrong ctime, need to fix somehow
public class ComplexComputeTest extends GeneralTest
{
	private static ModelObject object;

	@BeforeClass
	public static void Init()
		throws ExceptionSystemError, ExceptionParseError
	{
		ObjectFactory in = new ObjectFactory()
			.Sensors(new SensorTemplate("in_d1", -1, 10.0))
			.Sensors(new SensorTemplate("in_l1", -1, 20))
			.Sensors(new SensorTemplate("in_b1", -1, true))
			.Sensors(new SensorTemplate("in_str1", -1, "yes"))
			.Sensors(new SensorTemplate("d1", -1, 10.0))
			.Sensors(new SensorTemplate("d2", -1, 11.0))
			.Sensors(new SensorTemplate("l1", -1, 20L))
			.Sensors(new SensorTemplate("l2", -1, 21L))
			.Sensors(new SensorTemplate("b1", -1, true))
			.Sensors(new SensorTemplate("b2", -1, true))
			.Sensors(new SensorTemplate("d1_inv", -1, 10.0))
			.Sensors(new SensorTemplate("d2_inv", -1, 11.0))
			.Sensors(new SensorTemplate("l1_inv", -1, 20L))
			.Sensors(new SensorTemplate("l2_inv", -1, 21L))
			.Sensors(new SensorTemplate("b1_inv", -1, true))
			.Sensors(new SensorTemplate("b2_inv", -1, true))
			.Sensors(new SensorTemplate("str1", -1, "yes"))
			.Sensors(new SensorTemplate("str2", -1, "yes"));

		ObjectFactory out = new ObjectFactory()
			.Sensors(new SensorTemplate("out_d1", -1, 20.0))
			.Sensors(new SensorTemplate("out_l1", -1, 10))
			.Sensors(new SensorTemplate("out_b1", -1, false))
			.Sensors(new SensorTemplate("out_str1", -1, "no"))
			.Sensors(new SensorTemplate("d1", -1, 20.0))
			.Sensors(new SensorTemplate("d2", -1, 21.0))
			.Sensors(new SensorTemplate("l1", -1, 10L))
			.Sensors(new SensorTemplate("l2", -1, 11L))
			.Sensors(new SensorTemplate("b1", -1, false))
			.Sensors(new SensorTemplate("b2", -1, false))
			.Sensors(new SensorTemplate("d1_inv", -1, 20.0))
			.Sensors(new SensorTemplate("d2_inv", -1, 21.0))
			.Sensors(new SensorTemplate("l1_inv", -1, 10L))
			.Sensors(new SensorTemplate("l2_inv", -1, 11L))
			.Sensors(new SensorTemplate("b1_inv", -1, false))
			.Sensors(new SensorTemplate("b2_inv", -1, false))
			.Sensors(new SensorTemplate("str1", -1, "no"))
			.Sensors(new SensorTemplate("str2", -1, "no"))
			.Sensors(new SensorTemplate("mismatch_num", -1, 333L))
			.Sensors(new SensorTemplate("match_num", -1, 444L));

		ObjectFactory self = new ObjectFactory()
			.Sensors(new SensorTemplate("mod_d1", -1, 0.0))
			.Sensors(new SensorTemplate("mod_l1", -1, 2L))
			.Sensors(new SensorTemplate("d1", -1, 0.0))
			.Sensors(new SensorTemplate("d2", -1, 1.0))
			.Sensors(new SensorTemplate("l1", -1, 2L))
			.Sensors(new SensorTemplate("l2", -1, 3L))
			.Sensors(new SensorTemplate("b1", -1, true))
			.Sensors(new SensorTemplate("b2", -1, false))
			.Sensors(new SensorTemplate("d1_inv", -1, 0.0))
			.Sensors(new SensorTemplate("d2_inv", -1, 1.0))
			.Sensors(new SensorTemplate("l1_inv", -1, 2L))
			.Sensors(new SensorTemplate("l2_inv", -1, 3L))
			.Sensors(new SensorTemplate("b1_inv", -1, true))
			.Sensors(new SensorTemplate("b2_inv", -1, false))
			.Sensors(new SensorTemplate("str1", -1, "maybe"))
			.Sensors(new SensorTemplate("str2", -1, "maybe"))
			.Sensors(new SensorTemplate("bt1", -1, true))
			.Sensors(new SensorTemplate("bt2", -1, true))
			.Sensors(new SensorTemplate("bf1", -1, false))
			.Sensors(new SensorTemplate("bf2", -1, false))
			.Sensors(new SensorTemplate("mismatch_num", -1, 222L))
			.Sensors(new SensorTemplate("match_num", -1, 444L))

			.Vars(new VarTemplate("d_sum_1", new AStrictDoubleSum("self", "d1", "d2")))
			.Vars(new VarTemplate("d_sum_2", new AStrictDoubleSum("in_n", "d1", "d2")))
			.Vars(new VarTemplate("d_sum_3", new AStrictDoubleSum("out_n", "d1", "d2")))
			.Vars(new VarTemplate("d_sum_4", new AStrictDoubleSum("all_n", "d1", "d2")))
			.Vars(new VarTemplate("d_sum_5", new AStrictDoubleSum("self", "d1", "d2", "d1_inv")))
			.Vars(new VarTemplate("l_sum_1", new AStrictLongSum("self", "l1", "l2")))
			.Vars(new VarTemplate("l_sum_2", new AStrictLongSum("in_n", "l1", "l2")))
			.Vars(new VarTemplate("l_sum_3", new AStrictLongSum("out_n", "l1", "l2")))
			.Vars(new VarTemplate("l_sum_4", new AStrictLongSum("all_n", "l1", "l2")))
			.Vars(new VarTemplate("l_sum_5", new AStrictLongSum("self", "l1", "l2", "l1_inv")))
			.Vars(new VarTemplate("match_count_1", new AStrictMatchCount(true, "self", "b1", "b2")))
			.Vars(new VarTemplate("match_count_2", new AStrictMatchCount(true, "in_n", "b1", "b2")))
			.Vars(new VarTemplate("match_count_3", new AStrictMatchCount(true, "out_n", "b1", "b2")))
			.Vars(new VarTemplate("match_count_4", new AStrictMatchCount(true, "all_n", "b1", "b2")))
			.Vars(new VarTemplate("match_count_5", new AStrictMatchCount(true, "self", "b1", "b2", "b1_inv")))
			.Vars(new VarTemplate("not_match_count_1", new AStrictNotMatchCount(true, "self", "b1", "b2")))
			.Vars(new VarTemplate("not_match_count_2", new AStrictNotMatchCount(true, "in_n", "b1", "b2")))
			.Vars(new VarTemplate("not_match_count_3", new AStrictNotMatchCount(true, "out_n", "b1", "b2")))
			.Vars(new VarTemplate("not_match_count_4", new AStrictNotMatchCount(true, "all_n", "b1", "b2")))
			.Vars(new VarTemplate("not_match_count_5", new AStrictNotMatchCount(true, "self", "b1", "b2", "b1_inv")))
			.Vars(new VarTemplate("sd_sum_1", new ASoftDoubleSum("self", "d1", "d2")))
			.Vars(new VarTemplate("sd_sum_2", new ASoftDoubleSum("in_n", "d1", "d2")))
			.Vars(new VarTemplate("sd_sum_3", new ASoftDoubleSum("out_n", "d1", "d2")))
			.Vars(new VarTemplate("sd_sum_4", new ASoftDoubleSum("all_n", "d1", "d2")))
			.Vars(new VarTemplate("sd_sum_5", new ASoftDoubleSum("self", "d1_inv", "d2")))
			.Vars(new VarTemplate("sl_sum_1", new ASoftLongSum("self", "l1", "l2")))
			.Vars(new VarTemplate("sl_sum_2", new ASoftLongSum("in_n", "l1", "l2")))
			.Vars(new VarTemplate("sl_sum_3", new ASoftLongSum("out_n", "l1", "l2")))
			.Vars(new VarTemplate("sl_sum_4", new ASoftLongSum("all_n", "l1", "l2")))
			.Vars(new VarTemplate("sl_sum_5", new ASoftLongSum("self", "l1", "l2_inv")))
			.Vars(new VarTemplate("s_match_count_1", new ASoftMatchCount(true, "self", "b1", "b2")))
			.Vars(new VarTemplate("s_match_count_2", new ASoftMatchCount(true, "in_n", "b1", "b2")))
			.Vars(new VarTemplate("s_match_count_3", new ASoftMatchCount(true, "out_n", "b1", "b2")))
			.Vars(new VarTemplate("s_match_count_4", new ASoftMatchCount(true, "all_n", "b1", "b2")))
			.Vars(new VarTemplate("s_match_count_5", new ASoftMatchCount(true, "self", "b1", "b2_inv")))
			.Vars(new VarTemplate("s_not_match_count_sum_1", new ASoftNotMatchCount(true, "self", "b1", "b2")))
			.Vars(new VarTemplate("s_not_match_count_sum_2", new ASoftNotMatchCount(true, "in_n", "b1", "b2")))
			.Vars(new VarTemplate("s_not_match_count_sum_3", new ASoftNotMatchCount(true, "out_n", "b1", "b2")))
			.Vars(new VarTemplate("s_not_match_count_sum_4", new ASoftNotMatchCount(true, "all_n", "b1", "b2")))
			.Vars(new VarTemplate("s_not_match_count_sum_5", new ASoftNotMatchCount(true, "self", "b1", "b2_inv")))
			.Vars(new VarTemplate("req_s_1", new RequireSomeValid(1, 11, "self", "l1_inv", "l1", "l2_inv", "l2")))
			.Vars(new VarTemplate("req_s_2", new RequireSomeValid(2, true, "self", "l1_inv", "l1", "l2_inv", "l2")))
			.Vars(new VarTemplate("req_s_3", new RequireSomeValid(3, "ok", "self", "l1_inv", "l1", "l2_inv", "l2")))
			.Vars(new VarTemplate("req_a_1", new RequireAllValid(11, "self", "l1", "l2")))
			.Vars(new VarTemplate("req_a_2", new RequireAllValid(true, "self", "l1_inv", "l1", "l2_inv", "l2")))
			.Vars(new VarTemplate("req_a_3", new RequireAllValid("ok", "self", "l1_inv", "l1", "l2_inv", "l2")))
			.Vars(new VarTemplate("value_s_1", new ValueIfSomeValid("b1", 1, "self", "l1_inv", "l1", "l2_inv", "l2")))
			.Vars(new VarTemplate("value_s_2", new ValueIfSomeValid("b1", 1, "self", "l1_inv", "l1", "l2_inv", "l2")))
			.Vars(new VarTemplate("value_s_3", new ValueIfSomeValid("b1", 1, "self", "l1_inv", "l1", "l2_inv", "l2")))
			.Vars(new VarTemplate("value_a_1", new ValueIfAllValid("b1", "self", "l1", "l2")))
			.Vars(new VarTemplate("value_a_2", new ValueIfAllValid("b1", "self", "l1_inv", "l1", "l2_inv", "l2")))
			.Vars(new VarTemplate("value_a_3", new ValueIfAllValid("b1", "self", "l1_inv", "l1", "l2_inv", "l2")));

		object = self.Create();

		LinkFactory links = new LinkFactory()
			.Constants(new ConstTemplate("type", "test"));

		ModelObjectList ins = in.Create(3);
		ModelObjectList outs = out.Create(4);

		Enumerator.Sequence(ins, "in_lid");
		Enumerator.Sequence(outs, "out_lid");

		links.EveryToOne(ins, object, true);

		links.OneToEvery(object, outs, true);

		ModelObjectList objects = new ModelObjectList();

		objects.add(object);
		objects = objects.append(ins);
		objects = objects.append(outs);

		for(ModelEntity entity : objects)
			for(Attribute attr : entity.GetAttributes())
				if(attr.GetInfo().GetType() == EAttributeType.SENSOR)
					attr.UpdateDependant();

		// --------------

		for(ModelEntity entity : objects)
		{
			entity.GetSensor("d1_inv").SetUserInvalid();
			entity.GetSensor("d2_inv").SetUserInvalid();
			entity.GetSensor("l1_inv").SetUserInvalid();
			entity.GetSensor("l2_inv").SetUserInvalid();
			entity.GetSensor("b1_inv").SetUserInvalid();
			entity.GetSensor("b2_inv").SetUserInvalid();
		}

		model_service.Operate();
	}

	@Test
	public void TestDependencies()
	{
		assertEquals(new HashSet(object.GetVar("d_sum_1").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("d1", "d2")));
		assertEquals(new HashSet(object.GetVar("d_sum_2").GetRule().GetDependency(object))
			, new HashSet(object.GetInNeighbors().GetAttributes("d1", "d2")));
		assertEquals(new HashSet(object.GetVar("d_sum_3").GetRule().GetDependency(object))
			, new HashSet(object.GetOutNeighbors().GetAttributes("d1", "d2")));
		assertEquals(new HashSet(object.GetVar("d_sum_4").GetRule().GetDependency(object))
			, new HashSet(object.GetAllNeighbors().GetAttributes("d1", "d2")));
		assertEquals(new HashSet(object.GetVar("d_sum_5").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("d1", "d2", "d1_inv")));

		assertEquals(new HashSet(object.GetVar("l_sum_1").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("l1", "l2")));
		assertEquals(new HashSet(object.GetVar("l_sum_2").GetRule().GetDependency(object))
			, new HashSet(object.GetInNeighbors().GetAttributes("l1", "l2")));
		assertEquals(new HashSet(object.GetVar("l_sum_3").GetRule().GetDependency(object))
			, new HashSet(object.GetOutNeighbors().GetAttributes("l1", "l2")));
		assertEquals(new HashSet(object.GetVar("l_sum_4").GetRule().GetDependency(object))
			, new HashSet(object.GetAllNeighbors().GetAttributes("l1", "l2")));
		assertEquals(new HashSet(object.GetVar("l_sum_5").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("l1", "l2", "l1_inv")));

		assertEquals(new HashSet(object.GetVar("match_count_1").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("match_count_2").GetRule().GetDependency(object))
			, new HashSet(object.GetInNeighbors().GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("match_count_3").GetRule().GetDependency(object))
			, new HashSet(object.GetOutNeighbors().GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("match_count_4").GetRule().GetDependency(object))
			, new HashSet(object.GetAllNeighbors().GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("match_count_5").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("b1", "b2", "b1_inv")));

		assertEquals(new HashSet(object.GetVar("not_match_count_1").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("not_match_count_2").GetRule().GetDependency(object))
			, new HashSet(object.GetInNeighbors().GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("not_match_count_3").GetRule().GetDependency(object))
			, new HashSet(object.GetOutNeighbors().GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("not_match_count_4").GetRule().GetDependency(object))
			, new HashSet(object.GetAllNeighbors().GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("not_match_count_5").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("b1", "b2", "b1_inv")));

		assertEquals(new HashSet(object.GetVar("sd_sum_1").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("d1", "d2")));
		assertEquals(new HashSet(object.GetVar("sd_sum_2").GetRule().GetDependency(object))
			, new HashSet(object.GetInNeighbors().GetAttributes("d1", "d2")));
		assertEquals(new HashSet(object.GetVar("sd_sum_3").GetRule().GetDependency(object))
			, new HashSet(object.GetOutNeighbors().GetAttributes("d1", "d2")));
		assertEquals(new HashSet(object.GetVar("sd_sum_4").GetRule().GetDependency(object))
			, new HashSet(object.GetAllNeighbors().GetAttributes("d1", "d2")));
		assertEquals(new HashSet(object.GetVar("sd_sum_5").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("d1_inv", "d2")));

		assertEquals(new HashSet(object.GetVar("sl_sum_1").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("l1", "l2")));
		assertEquals(new HashSet(object.GetVar("sl_sum_2").GetRule().GetDependency(object))
			, new HashSet(object.GetInNeighbors().GetAttributes("l1", "l2")));
		assertEquals(new HashSet(object.GetVar("sl_sum_3").GetRule().GetDependency(object))
			, new HashSet(object.GetOutNeighbors().GetAttributes("l1", "l2")));
		assertEquals(new HashSet(object.GetVar("sl_sum_4").GetRule().GetDependency(object))
			, new HashSet(object.GetAllNeighbors().GetAttributes("l1", "l2")));
		assertEquals(new HashSet(object.GetVar("sl_sum_5").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("l1", "l2_inv")));

		assertEquals(new HashSet(object.GetVar("s_match_count_1").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("s_match_count_2").GetRule().GetDependency(object))
			, new HashSet(object.GetInNeighbors().GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("s_match_count_3").GetRule().GetDependency(object))
			, new HashSet(object.GetOutNeighbors().GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("s_match_count_4").GetRule().GetDependency(object))
			, new HashSet(object.GetAllNeighbors().GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("s_match_count_5").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("b1", "b2_inv")));

		assertEquals(new HashSet(object.GetVar("s_not_match_count_sum_1").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("s_not_match_count_sum_2").GetRule().GetDependency(object))
			, new HashSet(object.GetInNeighbors().GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("s_not_match_count_sum_3").GetRule().GetDependency(object))
			, new HashSet(object.GetOutNeighbors().GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("s_not_match_count_sum_4").GetRule().GetDependency(object))
			, new HashSet(object.GetAllNeighbors().GetAttributes("b1", "b2")));
		assertEquals(new HashSet(object.GetVar("s_not_match_count_sum_5").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("b1", "b2_inv")));

		assertEquals(new HashSet(object.GetVar("req_s_1").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("l1_inv", "l1", "l2_inv", "l2")));
		assertEquals(new HashSet(object.GetVar("req_s_2").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("l1_inv", "l1", "l2_inv", "l2")));
		assertEquals(new HashSet(object.GetVar("req_s_3").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("l1_inv", "l1", "l2_inv", "l2")));
		assertEquals(new HashSet(object.GetVar("req_a_1").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("l1", "l2")));
		assertEquals(new HashSet(object.GetVar("req_a_2").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("l1_inv", "l1", "l2_inv", "l2")));
		assertEquals(new HashSet(object.GetVar("req_a_3").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("l1_inv", "l1", "l2_inv", "l2")));

		assertEquals(new HashSet(object.GetVar("value_s_1").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("b1", "l1_inv", "l1", "l2_inv", "l2")));
		assertEquals(new HashSet(object.GetVar("value_s_2").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("b1", "l1_inv", "l1", "l2_inv", "l2")));
		assertEquals(new HashSet(object.GetVar("value_s_3").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("b1", "l1_inv", "l1", "l2_inv", "l2")));
		assertEquals(new HashSet(object.GetVar("value_a_1").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("b1", "l1", "l2")));
		assertEquals(new HashSet(object.GetVar("value_a_2").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("b1", "l1_inv", "l1", "l2_inv", "l2")));
		assertEquals(new HashSet(object.GetVar("value_a_3").GetRule().GetDependency(object))
			, new HashSet(object.GetAttributes("b1", "l1_inv", "l1", "l2_inv", "l2")));
	}

	@Test
	public void TestAStrictDoubleSum() throws Exception
	{
		assertEquals(  1.0, (Double)object.GetVar("d_sum_1").GetRule().Compute(object, object.GetVar("d_sum_1")), Value.EPSILON);
		assertEquals( 63.0, (Double)object.GetVar("d_sum_2").GetRule().Compute(object, object.GetVar("d_sum_2")), Value.EPSILON);
		assertEquals(164.0, (Double)object.GetVar("d_sum_3").GetRule().Compute(object, object.GetVar("d_sum_3")), Value.EPSILON);
		assertEquals(227.0, (Double)object.GetVar("d_sum_4").GetRule().Compute(object, object.GetVar("d_sum_4")), Value.EPSILON);
		assertEquals(Value.invalid,  object.GetVar("d_sum_5").GetRule().Compute(object, object.GetVar("d_sum_5")));
	}

	@Test
	public void TestAStrictLongSum() throws Exception
	{
		assertEquals(  5L, object.GetVar("l_sum_1").GetRule().Compute(object, object.GetVar("l_sum_1")));
		assertEquals(123L, object.GetVar("l_sum_2").GetRule().Compute(object, object.GetVar("l_sum_2")));
		assertEquals( 84L, object.GetVar("l_sum_3").GetRule().Compute(object, object.GetVar("l_sum_3")));
		assertEquals(207L, object.GetVar("l_sum_4").GetRule().Compute(object, object.GetVar("l_sum_4")));
		assertEquals(Value.invalid, object.GetVar("l_sum_5").GetRule().Compute(object, object.GetVar("l_sum_5")));
	}

	@Test
	public void TestAStrictMatchCount()
		throws ExceptionParseError
	{
		assertEquals(1L, object.GetVar("match_count_1").GetRule().Compute(object, object.GetVar("match_count_1")));
		assertEquals(6L, object.GetVar("match_count_2").GetRule().Compute(object, object.GetVar("match_count_2")));
		assertEquals(0L, object.GetVar("match_count_3").GetRule().Compute(object, object.GetVar("match_count_3")));
		assertEquals(6L, object.GetVar("match_count_4").GetRule().Compute(object, object.GetVar("match_count_4")));
		assertEquals(Value.invalid, object.GetVar("match_count_5").GetRule().Compute(object, object.GetVar("match_count_5")));
	}

	@Test
	public void TestAStrictNotMatchCount()
		throws ExceptionParseError
	{
		assertEquals(1L, object.GetVar("not_match_count_1").GetRule().Compute(object, object.GetVar("not_match_count_1")));
		assertEquals(0L, object.GetVar("not_match_count_2").GetRule().Compute(object, object.GetVar("not_match_count_2")));
		assertEquals(8L, object.GetVar("not_match_count_3").GetRule().Compute(object, object.GetVar("not_match_count_3")));
		assertEquals(8L, object.GetVar("not_match_count_4").GetRule().Compute(object, object.GetVar("not_match_count_4")));
		assertEquals(Value.invalid,  object.GetVar("not_match_count_5").GetRule().Compute(object, object.GetVar("not_match_count_5")));
	}

	@Test
	public void TestASoftDoubleSum() throws Exception
	{
		assertEquals(  1.0, object.GetVar("sd_sum_1").GetRule().Compute(object, object.GetVar("sd_sum_1")));
		assertEquals( 63.0, object.GetVar("sd_sum_2").GetRule().Compute(object, object.GetVar("sd_sum_2")));
		assertEquals(164.0, object.GetVar("sd_sum_3").GetRule().Compute(object, object.GetVar("sd_sum_3")));
		assertEquals(227.0, object.GetVar("sd_sum_4").GetRule().Compute(object, object.GetVar("sd_sum_4")));
		assertEquals(  1.0, object.GetVar("sd_sum_5").GetRule().Compute(object, object.GetVar("sd_sum_5")));
	}

	@Test
	public void TestASoftLongSum() throws Exception
	{
		assertEquals(  5L, object.GetVar("sl_sum_1").GetRule().Compute(object, object.GetVar("sl_sum_1")));
		assertEquals(123L, object.GetVar("sl_sum_2").GetRule().Compute(object, object.GetVar("sl_sum_2")));
		assertEquals( 84L, object.GetVar("sl_sum_3").GetRule().Compute(object, object.GetVar("sl_sum_3")));
		assertEquals(207L, object.GetVar("sl_sum_4").GetRule().Compute(object, object.GetVar("sl_sum_4")));
		assertEquals(  2L, object.GetVar("sl_sum_5").GetRule().Compute(object, object.GetVar("sl_sum_5")));
	}

	@Test
	public void TestASoftMatchCount()
		throws ExceptionParseError
	{
		assertEquals(1L, object.GetVar("s_match_count_1").GetRule().Compute(object, object.GetVar("s_match_count_1")));
		assertEquals(6L, object.GetVar("s_match_count_2").GetRule().Compute(object, object.GetVar("s_match_count_2")));
		assertEquals(0L, object.GetVar("s_match_count_3").GetRule().Compute(object, object.GetVar("s_match_count_3")));
		assertEquals(6L, object.GetVar("s_match_count_4").GetRule().Compute(object, object.GetVar("s_match_count_4")));
		assertEquals(1L, object.GetVar("s_match_count_5").GetRule().Compute(object, object.GetVar("s_match_count_5")));
	}

	@Test
	public void TestASoftNotMatchCount()
		throws ExceptionParseError
	{
		assertEquals(1L, object.GetVar("s_not_match_count_sum_1").GetRule().Compute(object, object.GetVar("s_not_match_count_sum_1")));
		assertEquals(0L, object.GetVar("s_not_match_count_sum_2").GetRule().Compute(object, object.GetVar("s_not_match_count_sum_2")));
		assertEquals(8L, object.GetVar("s_not_match_count_sum_3").GetRule().Compute(object, object.GetVar("s_not_match_count_sum_3")));
		assertEquals(8L, object.GetVar("s_not_match_count_sum_4").GetRule().Compute(object, object.GetVar("s_not_match_count_sum_4")));
		assertEquals(0L, object.GetVar("s_not_match_count_sum_5").GetRule().Compute(object, object.GetVar("s_not_match_count_sum_5")));
	}

	@Test
	public void TestRequireSomeValid()
		throws ExceptionParseError
	{
		assertEquals(11, object.GetVar("req_s_1").GetRule().Compute(object, object.GetVar("req_s_1")));
		assertEquals(true, object.GetVar("req_s_2").GetRule().Compute(object, object.GetVar("req_s_2")));
		assertEquals(Value.invalid, object.GetVar("req_s_3").GetRule().Compute(object, object.GetVar("req_s_3")));
	}

	@Test
	public void TestRequireAllValid()
		throws ExceptionParseError
	{
		assertEquals(11, object.GetVar("req_a_1").GetRule().Compute(object, object.GetVar("req_a_1")));
		assertEquals(Value.invalid, object.GetVar("req_a_2").GetRule().Compute(object, object.GetVar("req_a_2")));
		assertEquals(Value.invalid, object.GetVar("req_a_3").GetRule().Compute(object, object.GetVar("req_a_3")));
	}

	@Test
	public void TestValueIfSomeValid()
		throws ExceptionParseError
	{
		assertEquals(true, object.GetVar("value_s_1").GetRule().Compute(object, object.GetVar("value_s_1")));
		assertEquals(true, object.GetVar("value_s_2").GetRule().Compute(object, object.GetVar("value_s_2")));
		assertEquals(true, object.GetVar("value_s_3").GetRule().Compute(object, object.GetVar("value_s_3")));
	}

	@Test
	public void TestValueIfAllValid()
		throws ExceptionParseError
	{
		assertEquals(true, object.GetVar("value_a_1").GetRule().Compute(object, object.GetVar("value_a_1")));
		assertEquals(Value.invalid, object.GetVar("value_a_2").GetRule().Compute(object, object.GetVar("value_a_2")));
		assertEquals(Value.invalid, object.GetVar("value_a_3").GetRule().Compute(object, object.GetVar("req_a_3")));
	}
}
