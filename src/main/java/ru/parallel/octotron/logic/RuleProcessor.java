/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.logic;

import main.java.ru.parallel.octotron.core.*;
import main.java.ru.parallel.octotron.primitive.EDependencyType;
import main.java.ru.parallel.octotron.utils.ObjectList;

/**
 * Processes changed static attributes<br>
 * check dependencies and recalculate compute attributes<br>
 * */
public class RuleProcessor
{
	public RuleProcessor() {}

	/**
	 * get all nearest neighbors for changed attributes<br>
	 * get computational rules for all neighbors<br>
	 * check dependencies, call rules, that must be recalculated<br>
	 * return new changed attributes<br>
	 * probably requires few calculation steps for attribute chains<br>
	 * */
	public ObjectList Process(ObjectList changed)
	{
		ObjectList in_n = changed.GetInNeighbors();
		ObjectList out_n = changed.GetOutNeighbors();

		ObjectList comp_changed = new ObjectList();

// parents, self depends
		for(OctoObject obj : changed)
		{
			if(obj.Update(EDependencyType.SELF))
				comp_changed.add(obj);
		}

// in_n, in depends
		for(OctoObject obj : in_n)
		{
			if(obj.Update(EDependencyType.IN))
				comp_changed.add(obj);
		}

// out_n, out depends
		for(OctoObject obj : out_n)
		{
			if(obj.Update(EDependencyType.OUT))
				comp_changed.add(obj);
		}

		return comp_changed;
	}
}
