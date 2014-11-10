/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.primitive;

import java.util.Map;

public interface IPresentable
{
	Map<String, Object> GetLongRepresentation();
	Map<String, Object> GetShortRepresentation();

	Map<String, Object> GetRepresentation(boolean verbose);
}
