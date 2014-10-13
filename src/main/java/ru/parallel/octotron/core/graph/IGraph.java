/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph;

import ru.parallel.octotron.core.primitive.IUniqueID;

import java.util.List;

/**
 * interface for graph features<br>
 * access to elements must be provided using a unique identifier \\id<br>
 * */
public interface IGraph
{
	IUniqueID<EGraphType> AddObject();

	List<IUniqueID<EGraphType>> GetOutLinks(IUniqueID<EGraphType> id);

	List<IUniqueID<EGraphType>> GetInLinks(IUniqueID<EGraphType> id);

	List<String> GetObjectAttributes(IUniqueID<EGraphType> id);
	void DeleteObjectAttribute(IUniqueID<EGraphType> id, String name);
	boolean TestObjectAttribute(IUniqueID<EGraphType> id, String name);

	void DeleteObject(IUniqueID<EGraphType> id);

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	IUniqueID<EGraphType> AddLink(IUniqueID<EGraphType> source, IUniqueID<EGraphType> target, String link_type);

	IUniqueID<EGraphType> GetLinkTarget(IUniqueID<EGraphType> id);
	IUniqueID<EGraphType> GetLinkSource(IUniqueID<EGraphType> id);

	List<String> GetLinkAttributes(IUniqueID<EGraphType> id);
	void DeleteLinkAttribute(IUniqueID<EGraphType> id, String name);
	boolean TestLinkAttribute(IUniqueID<EGraphType> id, String name);

	void DeleteLink(IUniqueID<EGraphType> id);

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	void AddNodeLabel(IUniqueID<EGraphType> id, String label);
	boolean TestNodeLabel(IUniqueID<EGraphType> id, String label);

	List<IUniqueID<EGraphType>> GetAllLabeledNodes(String label);

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	IIndex GetIndex();

	List<IUniqueID<EGraphType>> GetAllObjects();
	List<IUniqueID<EGraphType>> GetAllLinks();

	void SetObjectAttribute(IUniqueID<EGraphType> id, String name, Object value);

	Object GetObjectAttribute(IUniqueID<EGraphType> id, String name);

	Object GetLinkAttribute(IUniqueID<EGraphType> id, String name);

	void SetLinkAttribute(IUniqueID<EGraphType> id, String name, Object value);

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	String ExportDot(List<IUniqueID<EGraphType>> uids);
}

