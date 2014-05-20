/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.primitive;

/**
 * shows if the rule depends only on owner attributes or on any neighbors<br>
 * */
public enum EDependencyType
{
	SELF, IN, OUT, ALL
}
