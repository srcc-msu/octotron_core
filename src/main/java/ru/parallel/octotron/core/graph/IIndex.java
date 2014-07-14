/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph;

import ru.parallel.octotron.core.primitive.Uid;

import java.util.List;

/**
 * interface for implementing index-like entity for<br>
 * querying objects from model<br>
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
	Uid GetObject(String name, Object value);
	Uid GetLink(String name, Object value);

/**
 * get all entities, that have attribute /name = /value
 * */
	List<Uid> GetObjects(String name, Object value);
	List<Uid> GetLinks(String name, Object value);

/**
 * get all entities, that have attribute /name with any value
 * */
	List<Uid> GetObjects(String name);
	List<Uid> GetLinks(String name);

/**
 * get all entities that have attribute /name and it matches the /pattern
 * */
	List<Uid> QueryObjects(String name, String pattern);
	List<Uid> QueryLinks(String name, String pattern);
}
