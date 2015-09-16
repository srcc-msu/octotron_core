/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence.graph;

import ru.parallel.octotron.core.primitive.Info;

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
	Info<EGraphType> GetObject(String name, Object value);
	Info<EGraphType> GetLink(String name, Object value);

/**
 * get all entities, that have attribute /name = /value
 * */
	List<Info<EGraphType>> GetObjects(String name, Object value);
	List<Info<EGraphType>> GetLinks(String name, Object value);

/**
 * get all entities, that have attribute /name with any value
 * */
	List<Info<EGraphType>> GetObjects(String name);
	List<Info<EGraphType>> GetLinks(String name);

/**
 * get all entities that have attribute /name and it matches the /pattern
 * */
	List<Info<EGraphType>> QueryObjects(String name, String pattern);
	List<Info<EGraphType>> QueryLinks(String name, String pattern);
}
