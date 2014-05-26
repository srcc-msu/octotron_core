/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.parallel.octotron.primitive.exception.ExceptionModelFail;

public class PersistentContainer<KEY_T, VALUE_T> implements Serializable
{
	private static final long serialVersionUID = 3738367887222285089L;

	private final Map<KEY_T, VALUE_T> objects;

	public PersistentContainer()
	{
		this.objects = new HashMap<>();
	}

	public KEY_T Add(KEY_T key, VALUE_T object)
	{
		if(objects.containsKey(key))
			throw new ExceptionModelFail("object with such key alreay presents: " + key);

		objects.put(key, object);

		return key;
	}

	public VALUE_T Get(KEY_T key)
	{
		VALUE_T object = objects.get(key);

		if(object == null)
			throw new ExceptionModelFail("no object with key: " + key);

		return object;
	}

	public VALUE_T Try(KEY_T key)
	{
		VALUE_T rule = objects.get(key);

		if(rule == null)
			return null;

		return rule;
	}

	public void Delete(KEY_T key)
	{
		VALUE_T object = objects.get(key);

		if(object == null)
			throw new ExceptionModelFail("object with key: " + key + " does not exist");

		objects.remove(key);
	}

	public List<VALUE_T> GetAll()
	{
		List<VALUE_T> res = new LinkedList<>();
		res.addAll(objects.values());
		return res;
	}
}
