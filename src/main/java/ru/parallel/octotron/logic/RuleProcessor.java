/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.primitive.EDependencyType;
import ru.parallel.octotron.utils.OctoEntityList;

/**
 * Processes changed static attributes<br>
 * check dependencies and recalculate compute attributes<br>
 * */
public class RuleProcessor
{

	/**
	 * get all nearest neighbors for changed attributes<br>
	 * get computational rules for all neighbors<br>
	 * check dependencies, call rules, that must be recalculated<br>
	 * return new changed attributes<br>
	 * probably requires few calculation steps for attribute chains<br>
	 * */
	public OctoEntityList Process(OctoEntityList changed)
	{
		OctoEntityList comp_changed = new OctoEntityList();

		for(OctoEntity entity : changed)
		{
			if(entity.Update(EDependencyType.SELF) > 0)
				comp_changed.add(entity);
		}

		for(OctoEntity entity : changed.GetSurround())
		{
			if(entity.Update(EDependencyType.SELF) > 0)
				comp_changed.add(entity);
		}

		return comp_changed.Uniq();
	}
}
