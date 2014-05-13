/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.netimport;

import java.util.List;

public interface IImporter
{
	/**
	 * returns a chunk of data
	 * if the size is more than max_count, return only max_count entities
	 * data over max_count will be returned in the next call
	 * */
	List<? extends ISensorData> Get(int max_count);

	int GetSize();
}
