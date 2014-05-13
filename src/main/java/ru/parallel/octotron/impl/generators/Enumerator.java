/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.impl.generators;

import main.java.ru.parallel.octotron.core.OctoEntity;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionDBError;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.octotron.utils.AbsEntityList;

public final class Enumerator
{
	private Enumerator(){}

	public static void Sequence(AbsEntityList<? extends OctoEntity> list, String att, int div)
		throws ExceptionModelFail, ExceptionDBError
	{
		int i = 0;

		for(OctoEntity ent : list)
		{
			ent.DeclareAttribute(att, i);

			i++;
			if(div != 0)
				i = i % div;
		}
	}

	public static void Sequence(AbsEntityList<? extends OctoEntity> list, String att)
		throws ExceptionModelFail, ExceptionDBError
	{
		Sequence(list, att, 0);
	}
}
