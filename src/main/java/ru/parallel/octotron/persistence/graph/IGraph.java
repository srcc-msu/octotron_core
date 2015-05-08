/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence.graph;

import ru.parallel.octotron.core.primitive.ID;

import java.util.List;

/**
 * interface for graph features<br>
 * access to elements must be provided using a unique identifier \\id<br>
 * */
public interface IGraph
{
	ID<EGraphType> AddObject();

	List<ID<EGraphType>> GetOutLinks(ID<EGraphType> id);

	List<ID<EGraphType>> GetInLinks(ID<EGraphType> id);

	List<String> GetObjectAttributes(ID<EGraphType> id);
	void DeleteObjectAttribute(ID<EGraphType> id, String name);
	boolean TestObjectAttribute(ID<EGraphType> id, String name);

	void DeleteObject(ID<EGraphType> id);

//--------
// **********************************************************
//--------

	ID<EGraphType> AddLink(ID<EGraphType> source, ID<EGraphType> target, String link_type);

	ID<EGraphType> GetLinkTarget(ID<EGraphType> id);
	ID<EGraphType> GetLinkSource(ID<EGraphType> id);

	List<String> GetLinkAttributes(ID<EGraphType> id);
	void DeleteLinkAttribute(ID<EGraphType> id, String name);
	boolean TestLinkAttribute(ID<EGraphType> id, String name);

	void DeleteLink(ID<EGraphType> id);

//--------
// **********************************************************
//--------

	void AddNodeLabel(ID<EGraphType> id, String label);
	boolean TestNodeLabel(ID<EGraphType> id, String label);

	List<ID<EGraphType>> GetAllLabeledNodes(String label);

//--------
// **********************************************************
//--------

	IIndex GetIndex();

	List<ID<EGraphType>> GetAllObjects();
	List<ID<EGraphType>> GetAllLinks();

	void SetObjectAttribute(ID<EGraphType> id, String name, Object value);

	Object GetObjectAttribute(ID<EGraphType> id, String name);

	Object GetLinkAttribute(ID<EGraphType> id, String name);

	void SetLinkAttribute(ID<EGraphType> id, String name, Object value);

//--------
// **********************************************************
//--------

	String ExportDot(List<ID<EGraphType>> uids);
}

