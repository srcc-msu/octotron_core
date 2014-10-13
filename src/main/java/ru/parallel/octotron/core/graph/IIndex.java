/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph;

import ru.parallel.octotron.core.primitive.IUniqueID;

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
	IUniqueID<EGraphType> GetObject(String name, Object value);
	IUniqueID<EGraphType> GetLink(String name, Object value);

/**
 * get all entities, that have attribute /name = /value
 * */
	List<IUniqueID<EGraphType>> GetObjects(String name, Object value);
	List<IUniqueID<EGraphType>> GetLinks(String name, Object value);

/**
 * get all entities, that have attribute /name with any value
 * */
	List<IUniqueID<EGraphType>> GetObjects(String name);
	List<IUniqueID<EGraphType>> GetLinks(String name);

/**
 * get all entities that have attribute /name and it matches the /pattern
 * */
	List<IUniqueID<EGraphType>> QueryObjects(String name, String pattern);
	List<IUniqueID<EGraphType>> QueryLinks(String name, String pattern);
}
