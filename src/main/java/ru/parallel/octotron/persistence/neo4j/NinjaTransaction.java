/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 * not threadsafe
 * */
public class NinjaTransaction
{
	private final GraphDatabaseService db;

	private Transaction transaction;

	private int count = 0;
	private boolean deleted = false;

	private final int count_threshold;

/**
 * /alive_threshold - how many seconds transaction can be opened
 * if it is negative - transaction will be written every time
 * */
	public NinjaTransaction(GraphDatabaseService db
		, int count_threshold)
	{
		this.db = db;

		this.count_threshold = count_threshold;

		Rewind();
	}

	private void Rewind()
	{
		if(transaction != null)
		{
			transaction.success();
			transaction.close();
		}

		transaction = db.beginTx();

		count = 0;
	}

/**
 * do not use it
 * */
	public void Close()
	{
		if(transaction != null)
		{
			if(count > 0) // do not commit if nothing was written
				transaction.success();

			transaction.close();
		}

		transaction = null;
	}

/**
 * commit and create new transaction
 * */
	public void ForceWrite()
	{
		if(transaction != null)
			transaction.success();

		Rewind();
	}

/**
 * create new transaction, if it is not active
 * if it is active and too old - commit it and create new
 * */
	public void Write()
	{
		if(transaction == null)
			Rewind();

		if(deleted)
		{
			transaction.success();
			Rewind();
		}

		count++;

		if(count > count_threshold)
		{
			transaction.success();
			Rewind();
		}
	}

/**
 * if transaction modified too many times - close and commit it<br>
 * */
	public void Read()
	{
		if(deleted)
		{
			transaction.success();
			Rewind();
		}

		if(count > count_threshold)
		{
			transaction.success();
			Rewind();
		}
	}

/**
 * fail and commit transaction
 * */
	public void Fail()
	{
		if(transaction != null)
			transaction.failure();

		Rewind();
	}

	public void Delete()
	{
		deleted = true;
	}
}
