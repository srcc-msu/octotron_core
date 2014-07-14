/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph;

import ru.parallel.octotron.core.primitive.Uid;

import java.util.List;

/**
 * interface for graph features<br>
 * access to elements must be provided using a unique identifier \\uid<br>
 * */
public interface IGraph
{
	Uid AddObject();

	List<Uid> GetOutLinks(Uid uid);

	List<Uid> GetInLinks(Uid uid);

	List<String> GetObjectAttributes(Uid uid);
	void DeleteObjectAttribute(Uid uid, String name);
	boolean TestObjectAttribute(Uid uid, String name);

	void DeleteObject(Uid uid);

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	Uid AddLink(Uid source, Uid target, String link_type);

	Uid GetLinkTarget(Uid uid);
	Uid GetLinkSource(Uid uid);

	List<String> GetLinkAttributes(Uid uid);
	void DeleteLinkAttribute(Uid uid, String name);
	boolean TestLinkAttribute(Uid uid, String name);

	void DeleteLink(Uid uid);

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	IIndex GetIndex();

	List<Uid> GetAllObjects();
	List<Uid> GetAllLinks();

	void SetObjectAttribute(Uid uid, String name, Object value);

	Object GetObjectAttribute(Uid uid, String name);

	Object GetLinkAttribute(Uid uid, String name);

	void SetLinkAttribute(Uid uid, String name, Object value);

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	String ExportDot(List<Uid> uids);
}

