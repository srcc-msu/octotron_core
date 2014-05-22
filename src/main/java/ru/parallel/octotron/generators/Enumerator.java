/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.utils.IEntityList;

public final class Enumerator
{
	private Enumerator(){}

	public static void Sequence(IEntityList<? extends OctoEntity> list, String att, int div)
	{
		int i = 0;

		for(OctoEntity ent : list)
		{
			ent.DeclareAttribute(att, i);

			i++;
			if(div != 0)
				i %= div;
		}
	}

	public static void Sequence(IEntityList<? extends OctoEntity> list, String att)
	{
		Enumerator.Sequence(list, att, 0);
	}
}