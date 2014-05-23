/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.primitive.EDependencyType;
import ru.parallel.octotron.utils.OctoObjectList;

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
	public OctoObjectList Process(OctoObjectList changed)
	{
		OctoObjectList in_n = changed.GetInNeighbors().Uniq();
		OctoObjectList out_n = changed.GetOutNeighbors().Uniq();

		OctoObjectList comp_changed = new OctoObjectList();

// parents, self depends
		for(OctoObject obj : changed)
		{
			if(obj.Update(EDependencyType.SELF) > 0)
				comp_changed.add(obj);
		}

// in_n, for them we are 'out'
		for(OctoObject obj : in_n)
		{
			if(obj.Update(EDependencyType.OUT) > 0)
				comp_changed.add(obj);
		}

// out_n, for them we are 'in'
		for(OctoObject obj : out_n)
		{
			if(obj.Update(EDependencyType.IN) > 0)
				comp_changed.add(obj);
		}

		return comp_changed;
	}
}
