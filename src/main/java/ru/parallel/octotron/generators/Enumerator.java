/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.model.ModelEntity;

public final class Enumerator
{
	private Enumerator(){}

	public static void Sequence(Iterable<? extends ModelEntity> list, String att, int div)
	{
		int i = 0;

		for(ModelEntity entity : list)
		{
			entity.GetBuilder().DeclareConst(att, i);

			i++;
			if(div != 0)
				i %= div;
		}
	}

	public static void Sequence(Iterable<? extends ModelEntity> list, String att)
	{
		Enumerator.Sequence(list, att, 0);
	}
}
