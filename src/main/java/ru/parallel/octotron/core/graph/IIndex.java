/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph;

import ru.parallel.octotron.core.primitive.UniqueID;

import java.util.List;

/**
 * interface for implementing index-like entity for<br>
 * querying objects from model_old<br>
 * */
public interface IIndex
{
/**
 * enable indexing for attributes with name \name
 * */
	void EnableObjectIndex(String name);
	void EnableLinkIndex(String name);

/**
 * disable indexing for attributes with name \name
 * */
	void DisableObjectIndex(String name);
	void DisableLinkIndex(String name);

/**
 * get one entity, that has attribute /name = /value
 * if there are more than one such entities - throws exception
 * */
	UniqueID<EGraphType> GetObject(String name, Object value);
	UniqueID<EGraphType> GetLink(String name, Object value);

/**
 * get all entities, that have attribute /name = /value
 * */
	List<UniqueID<EGraphType>> GetObjects(String name, Object value);
	List<UniqueID<EGraphType>> GetLinks(String name, Object value);

/**
 * get all entities, that have attribute /name with any value
 * */
	List<UniqueID<EGraphType>> GetObjects(String name);
	List<UniqueID<EGraphType>> GetLinks(String name);

/**
 * get all entities that have attribute /name and it matches the /pattern
 * */
	List<UniqueID<EGraphType>> QueryObjects(String name, String pattern);
	List<UniqueID<EGraphType>> QueryLinks(String name, String pattern);
}
