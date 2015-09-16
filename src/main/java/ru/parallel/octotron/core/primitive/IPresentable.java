/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.primitive;

import java.util.Map;

/**
 * interface for any presentable entity, which is able
 * to provide short and detailed information about itself
 * in form of dictionary
 * */
public interface IPresentable
{
	Map<String, Object> GetLongRepresentation();
	Map<String, Object> GetShortRepresentation();

	Map<String, Object> GetRepresentation(boolean verbose);
}
