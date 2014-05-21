/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang3.tuple.Pair;

import ru.parallel.octotron.http.PathOperations.CHAIN_TYPE;
import ru.parallel.octotron.http.PathOperations.PathToken;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.utils.IEntityList;

/**
 * class for parsing http-compatible query<br>
 * */
public final class PathParser
{
	private PathParser(){}

/**
 * tokens that are allowed in modify area<br>
 * this area is used for import and direct model manipulations<br>
 * */
	static final PathToken[] TOKENS =
	{
		PathOperations.obj, PathOperations.link
		, PathOperations.q
		, PathOperations.in_n, PathOperations.out_n, PathOperations.all_n
		, PathOperations.uniq
		, PathOperations.in_l, PathOperations.out_l, PathOperations.all_l
		, PathOperations.source, PathOperations.target
	};

	private static final Map<IEntityList.EQueryType, String> DELIMS
		= new HashMap<IEntityList.EQueryType, String>();

	static
	{
		PathParser.DELIMS.put(IEntityList.EQueryType.SET, "=");
		PathParser.DELIMS.put(IEntityList.EQueryType.EQ, "==");
		PathParser.DELIMS.put(IEntityList.EQueryType.NE, "!=");
		PathParser.DELIMS.put(IEntityList.EQueryType.LE, "<=");
		PathParser.DELIMS.put(IEntityList.EQueryType.GE, ">=");
		PathParser.DELIMS.put(IEntityList.EQueryType.LT, "<");
		PathParser.DELIMS.put(IEntityList.EQueryType.GT, ">");
	}

/**
 * parse BaseAttribute from string "name=value"<br>
 * */
	private static Pair<SimpleAttribute, IEntityList.EQueryType> AttrFromString(String str, IEntityList.EQueryType type)
		throws ExceptionParseError
	{
		String delim = PathParser.DELIMS.get(type);

		int idx = str.indexOf(delim);

		if(idx != -1)
		{
			String name = str.substring(0, idx);

			String val_str = str.substring(idx + delim.length(), str.length());

			Object value = SimpleAttribute.ValueFromStr(val_str);

			return Pair.of(new SimpleAttribute(name, value), type);
		}
		else
			return null;
	}

	private static Pair<SimpleAttribute, IEntityList.EQueryType> OpFromStr(String str)
		throws ExceptionParseError
	{
		Pair<SimpleAttribute, IEntityList.EQueryType> val;

// enum.values() preserve order - it is important for parsing = and ==
		for(IEntityList.EQueryType type : IEntityList.EQueryType.values())
		{
			if(type == IEntityList.EQueryType.NONE)
				continue;

			val = PathParser.AttrFromString(str, type);

			if(val != null)
				return val;
		}

		return Pair.of(new SimpleAttribute(str, null), IEntityList.EQueryType.NONE);
	}

/**
 * parse list of attributes, separated by commas<br>
 * name=val,name2,name3=val<br>
 * may be empty<br>
 * */
	private static List<Pair<SimpleAttribute, IEntityList.EQueryType>> StrToAttrList(String str)
		throws ExceptionParseError
	{
		List<Pair<SimpleAttribute, IEntityList.EQueryType>> result
			= new LinkedList<Pair<SimpleAttribute, IEntityList.EQueryType>>();

		if(str == null || str.isEmpty())
			return result;

		String[] attrs = str.split(",");

		for(String attr : attrs)
		{
			result.add(OpFromStr(attr.replaceAll("\\s","")));
		}

		return result;
	}

	private static final Pattern pattern = Pattern.compile("([a-zA-Z_-]+)\\(([^)]*)\\)\\.?");

	private static List<PathToken> ParseTokens(String path)
		throws ExceptionParseError
	{
		List<PathToken> result = new LinkedList<PathToken>();

// matches <id>(<anything but closing bracket>)<optional dot>
		Matcher matcher = pattern.matcher(path);

		while(matcher.find())
		{
			if(matcher.groupCount() != 2) // found id and part in brackets
				throw new ExceptionParseError("could not parse path");

			String name = matcher.group(1);
			String params = matcher.group(2);

			PathToken base_token = null;

			for(PathToken token : TOKENS)
				if(token.GetName().equals(name))
				{
					base_token = token;
					break;
				}

			if(base_token == null)
				throw new ExceptionParseError("unknown path token: " + name);

			result.add(new PathToken(base_token, StrToAttrList(params)));
		}

		return result;
	}

/**
 * check that each token follows after allowed token<br>
 * and start/end tokens are correct<br>
 * returns type of the last token if succeeded<br>
 * throws ExceptionParseError otherwise<br>
 * */
	private static void TypeCheck(List<PathToken> tokens)
		throws ExceptionParseError
	{
		if(tokens.isEmpty())
			throw new ExceptionParseError("empty request");

		CHAIN_TYPE type = CHAIN_TYPE.E_START;

		for(PathToken token : tokens)
		{
			if(token.GetIn() == CHAIN_TYPE.E_ANY)
			{
				type = token.GetOut();
				continue;
			}

			else if(token.GetIn() == CHAIN_TYPE.E_MATCH)
			{
				continue;
			}

			else if(token.GetIn() == type)
			{
				type = token.GetOut();
				continue;
			}

			throw new ExceptionParseError("type error: " + token.GetIn()
				+ " can not follow after " + type);
		}
	}

/**
 * parse Request request and returns CParsedRequest,<br>
 * that is ready to be executed on given input<br>
 * */
	public static ParsedPath Parse(String path)
		throws ExceptionParseError
	{
		try
		{
			List<PathToken> tokens = ParseTokens(path);

			TypeCheck(tokens);

			return new ParsedPath(tokens);
		}
		catch(ExceptionParseError e)
		{
			String res = "could not parse path: " + e + System.lineSeparator();

			for(StackTraceElement s : e.getStackTrace())
				res += System.lineSeparator() + s;

			throw new ExceptionParseError(res);
		}
	}
}
