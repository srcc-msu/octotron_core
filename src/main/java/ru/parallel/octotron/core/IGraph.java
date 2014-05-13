/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.core;
import java.util.List;

import main.java.ru.parallel.octotron.primitive.Uid;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionDBError;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.octotron.utils.ObjectList;

/**
 * interface for graph features<br>
 * access to elements must be provided using a unique identifier \\uid<br>
 * */
public interface IGraph
{
	public Uid AddObject();

	public List<Uid> GetOutLinks(Uid uid)
		throws ExceptionModelFail;

	public List<Uid> GetInLinks(Uid uid)
		throws ExceptionModelFail;

	List<Object[]> GetObjectAttributes(Uid uid)
		throws ExceptionModelFail;
	void DeleteObjectAttribute(Uid uid, String name)
		throws ExceptionModelFail;
	boolean TestObjectAttribute(Uid uid, String name)
		throws ExceptionModelFail;

	public void DeleteObject(Uid uid)
		throws ExceptionModelFail;

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	public Uid AddLink(Uid source, Uid target, String link_type)
		throws ExceptionModelFail, ExceptionDBError;

	public Uid GetLinkTarget(Uid uid)
		throws ExceptionModelFail;
	public Uid GetLinkSource(Uid uid)
		throws ExceptionModelFail;

	List<Object[]> GetLinkAttributes(Uid uid)
		throws ExceptionModelFail;
	void DeleteLinkAttribute(Uid uid, String name)
		throws ExceptionModelFail;
	boolean TestLinkAttribute(Uid uid, String name)
		throws ExceptionModelFail;

	public void DeleteLink(Uid uid)
		throws ExceptionModelFail;

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	public IIndex GetIndex();

	List<Uid> GetAllObjects();
	List<Uid> GetAllLinks();

	void SetObjectAttribute(Uid uid, String name, Object value)
		throws ExceptionModelFail, ExceptionDBError;

	Object GetObjectAttribute(Uid uid, String name)
		throws ExceptionModelFail;

	Object GetLinkAttribute(Uid uid, String name)
		throws ExceptionModelFail;

	void SetLinkAttribute(Uid uid, String name, Object value)
		throws ExceptionModelFail;

// ----------------------------------------------------------
// **********************************************************
// ----------------------------------------------------------

	String ExportDot(ObjectList objects, String... ignoreList);
}

