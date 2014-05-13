/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.rules;

import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.core.OctoRule;
import main.java.ru.parallel.octotron.primitive.EDependencyType;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionDBError;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;

public class CalcSpeed extends OctoRule
{
	private static final long serialVersionUID = -5796823312858284235L;
	private String measured_attr;

	public CalcSpeed(String speed_attr, String measured_attr)
	{
		super(speed_attr);
		this.measured_attr = measured_attr;
	}

	@Override
	public EDependencyType GetDeps()
	{
		return EDependencyType.SELF;
	}

	@Override
	public Object Compute(OctoObject object)
		throws ExceptionModelFail, ExceptionDBError
	{
		if(!object.GetAttribute(measured_attr).IsValid() || object.GetAttribute(measured_attr).GetTime() == 0)
			return GetDefaultValue();

		if(!object.TestAttribute("_last_" + measured_attr))
			object.SetAttribute("_last_" + measured_attr, object.GetAttribute(measured_attr).GetValue());

		long time_dif = object.GetAttribute(measured_attr).GetTime()
			- object.GetAttribute("_last_" + measured_attr).GetTime();

		double val_dif = (object.GetAttribute(measured_attr).ToDouble()
			- object.GetAttribute("_last_" + measured_attr).ToDouble());

		if(time_dif <= 0)
			return GetDefaultValue();

		object.SetAttribute("_last_" + measured_attr, object.GetAttribute(measured_attr).GetValue());

		return val_dif / time_dif;
	}

	@Override
	public Object GetDefaultValue()
		throws ExceptionModelFail
	{
		return 0.0;
	}
}
