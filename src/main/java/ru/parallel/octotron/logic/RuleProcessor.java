/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.primitive.EDependencyType;
import ru.parallel.octotron.utils.ObjectList;

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
	public ObjectList Process(ObjectList changed)
	{
		ObjectList in_n = changed.GetInNeighbors().Uniq();
		ObjectList out_n = changed.GetOutNeighbors().Uniq();

		ObjectList comp_changed = new ObjectList();

// parents, self depends
		for(OctoObject obj : changed)
		{
			if(obj.Update(EDependencyType.SELF))
				comp_changed.add(obj);
		}

// in_n, for them we are 'out'
		for(OctoObject obj : in_n)
		{
			if(obj.Update(EDependencyType.OUT))
				comp_changed.add(obj);
		}

// out_n, for them we are 'in'
		for(OctoObject obj : out_n)
		{
			if(obj.Update(EDependencyType.IN))
				comp_changed.add(obj);
		}

		return comp_changed;
	}
}
