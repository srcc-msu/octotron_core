/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;

public class PersistentMap<T> implements Serializable
{
	private static final long serialVersionUID = 3738367887222285089L;

	private PersistentContainer<Long, T> objects
		= new PersistentContainer<Long, T>();

	private long cur_id = 0;

	public long Add(T object)
		throws ExceptionModelFail
	{
		return objects.Add(cur_id++, object);
	}

	public T Get(long id)
		throws ExceptionModelFail
	{
		return objects.Get(id);
	}

	public List<T> GetAll()
		throws ExceptionModelFail
	{
		return objects.GetAll();
	}

	public T Try(long id)
		throws ExceptionModelFail
	{
		return objects.Try(id);
	}

	public void Save(String fname)
		throws IOException
	{
		FileOutputStream fout = new FileOutputStream(fname);
		ObjectOutputStream out = new ObjectOutputStream(fout);

		out.writeObject(objects);
		out.writeObject(cur_id);

		out.close();
	}

	@SuppressWarnings("unchecked")
	public void Load(String fname)
		throws IOException, ClassNotFoundException
	{
		FileInputStream fin = new FileInputStream(fname);
		ObjectInputStream in = new ObjectInputStream(fin);

		objects = (PersistentContainer<Long, T>) in.readObject();
		cur_id = (long)in.readObject();

		in.close();
	}

	public void Delete(long id)
		throws ExceptionModelFail
	{
		objects.Delete(id);
	}
}
