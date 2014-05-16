/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.core;
import java.util.List;

import main.java.ru.parallel.octotron.primitive.Uid;
import main.java.ru.parallel.octotron.utils.ObjectList;

/**
 * interface for graph features<br>
 * access to elements must be provided using a unique identifier \\uid<br>
 * */
public interface IGraph
{
	Uid AddObject();

	List<Uid> GetOutLinks(Uid uid);

	List<Uid> GetInLinks(Uid uid);

	List<Object[]> GetObjectAttributes(Uid uid);
	void DeleteObjectAttribute(Uid uid, String name);
	boolean TestObjectAttribute(Uid uid, String name);

	void DeleteObject(Uid uid);

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	Uid AddLink(Uid source, Uid target, String link_type);

	Uid GetLinkTarget(Uid uid);
	Uid GetLinkSource(Uid uid);

	List<Object[]> GetLinkAttributes(Uid uid);
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

	String ExportDot(ObjectList objects);
}

