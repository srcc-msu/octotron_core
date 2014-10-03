/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph;

import ru.parallel.octotron.core.primitive.UniqueID;

import java.util.List;

/**
 * interface for graph features<br>
 * access to elements must be provided using a unique identifier \\id<br>
 * */
public interface IGraph
{
	UniqueID<EGraphType> AddObject();

	List<UniqueID<EGraphType>> GetOutLinks(UniqueID<EGraphType> id);

	List<UniqueID<EGraphType>> GetInLinks(UniqueID<EGraphType> id);

	List<String> GetObjectAttributes(UniqueID<EGraphType> id);
	void DeleteObjectAttribute(UniqueID<EGraphType> id, String name);
	boolean TestObjectAttribute(UniqueID<EGraphType> id, String name);

	void DeleteObject(UniqueID<EGraphType> id);

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	UniqueID<EGraphType> AddLink(UniqueID<EGraphType> source, UniqueID<EGraphType> target, String link_type);

	UniqueID<EGraphType> GetLinkTarget(UniqueID<EGraphType> id);
	UniqueID<EGraphType> GetLinkSource(UniqueID<EGraphType> id);

	List<String> GetLinkAttributes(UniqueID<EGraphType> id);
	void DeleteLinkAttribute(UniqueID<EGraphType> id, String name);
	boolean TestLinkAttribute(UniqueID<EGraphType> id, String name);

	void DeleteLink(UniqueID<EGraphType> id);

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	void AddNodeLabel(UniqueID<EGraphType> id, String label);
	boolean TestNodeLabel(UniqueID<EGraphType> id, String label);

	List<UniqueID<EGraphType>> GetAllLabeledNodes(String label);

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	IIndex GetIndex();

	List<UniqueID<EGraphType>> GetAllObjects();
	List<UniqueID<EGraphType>> GetAllLinks();

	void SetObjectAttribute(UniqueID<EGraphType> id, String name, Object value);

	Object GetObjectAttribute(UniqueID<EGraphType> id, String name);

	Object GetLinkAttribute(UniqueID<EGraphType> id, String name);

	void SetLinkAttribute(UniqueID<EGraphType> id, String name, Object value);

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	String ExportDot(List<UniqueID<EGraphType>> uids);
}

