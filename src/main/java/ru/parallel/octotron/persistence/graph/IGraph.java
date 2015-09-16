/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence.graph;

import ru.parallel.octotron.core.primitive.Info;

import java.util.List;

/**
 * interface for graph features<br>
 * access to elements must be provided using a unique identifier \\id<br>
 * */
public interface IGraph
{
	Info<EGraphType> AddObject();

	List<Info<EGraphType>> GetOutLinks(Info<EGraphType> info);

	List<Info<EGraphType>> GetInLinks(Info<EGraphType> info);

	List<String> GetObjectAttributes(Info<EGraphType> info);
	void DeleteObjectAttribute(Info<EGraphType> info, String name);
	boolean TestObjectAttribute(Info<EGraphType> info, String name);

	void DeleteObject(Info<EGraphType> info);

//--------
// **********************************************************
//--------

	Info<EGraphType> AddLink(Info<EGraphType> source, Info<EGraphType> target, String link_type);

	Info<EGraphType> GetLinkTarget(Info<EGraphType> info);
	Info<EGraphType> GetLinkSource(Info<EGraphType> info);

	List<String> GetLinkAttributes(Info<EGraphType> info);
	void DeleteLinkAttribute(Info<EGraphType> info, String name);
	boolean TestLinkAttribute(Info<EGraphType> info, String name);

	void DeleteLink(Info<EGraphType> info);

//--------
// **********************************************************
//--------

	void AddNodeLabel(Info<EGraphType> info, String label);
	boolean TestNodeLabel(Info<EGraphType> info, String label);

	List<Info<EGraphType>> GetAllLabeledNodes(String label);

//--------
// **********************************************************
//--------

	IIndex GetIndex();

	List<Info<EGraphType>> GetAllObjects();
	List<Info<EGraphType>> GetAllLinks();

	void SetObjectAttribute(Info<EGraphType> info, String name, Object value);

	Object GetObjectAttribute(Info<EGraphType> info, String name);

	Object GetLinkAttribute(Info<EGraphType> info, String name);

	void SetLinkAttribute(Info<EGraphType> info, String name, Object value);

//--------
// **********************************************************
//--------

	String ExportDot(List<Info<EGraphType>> uids);
}

