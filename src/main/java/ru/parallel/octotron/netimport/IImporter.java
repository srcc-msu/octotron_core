/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.netimport;

import org.apache.commons.lang3.tuple.Pair;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.util.List;

public interface IImporter
{
	/**
	 * returns a chunk of data
	 * if the size is more than max_count, return only max_count entities
	 * data over max_count will be returned in the next call
	 * */
	List<Pair<ModelEntity, SimpleAttribute>> Get(int max_count);

	int GetSize();
}
