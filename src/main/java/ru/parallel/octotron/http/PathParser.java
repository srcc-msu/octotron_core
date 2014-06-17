/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import org.apache.commons.lang3.tuple.Pair;
import ru.parallel.octotron.http.PathOperations.CHAIN_TYPE;
import ru.parallel.octotron.http.PathOperations.PathToken;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.utils.IEntityList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static final Map<String, IEntityList.EQueryType> DELIMS
		= new HashMap<>();

	static
	{
		PathParser.DELIMS.put("==", IEntityList.EQueryType.EQ);
		PathParser.DELIMS.put("!=", IEntityList.EQueryType.NE);
		PathParser.DELIMS.put("<=", IEntityList.EQueryType.LE);
		PathParser.DELIMS.put(">=", IEntityList.EQueryType.GE);
		PathParser.DELIMS.put("<", IEntityList.EQueryType.LT);
		PathParser.DELIMS.put(">", IEntityList.EQueryType.GT);
	}


// matched <id><any chars !=<>.><value>
	private static final Pattern op_pattern_op = Pattern.compile("^([a-zA-Z_\\-0-9\\.]+)([!=<>]+)(.+)$");
	private static final Pattern op_pattern_word = Pattern.compile("^([a-zA-Z_\\-0-9\\.]+)$");

/**
 * parse SimpleAttribute from string "<name><op><value>"<br>
 * */
	public static Pair<SimpleAttribute, IEntityList.EQueryType> AttrFromString(String str)
		throws ExceptionParseError
	{
		if(str.length() == 0)
			return null;

		Matcher matcher = op_pattern_op.matcher(str);

		if(matcher.find())
		{
			String name = matcher.group(1);
			String op = matcher.group(2);
			Object value = SimpleAttribute.ValueFromStr(matcher.group(3));

			IEntityList.EQueryType type = DELIMS.get(op);

			if(type == null)
				throw new ExceptionParseError("unsupported operation: " + op);

			return Pair.of(new SimpleAttribute(name, value), type);
		}

		matcher = op_pattern_word.matcher(str);

		if(matcher.find())
			return Pair.of(new SimpleAttribute(str, null), IEntityList.EQueryType.NONE);

		throw new ExceptionParseError("can not parse: " + str);
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
			= new LinkedList<>();

		if(str == null || str.isEmpty())
			return result;

		String[] attrs = str.split(",");

		for(String attr : attrs)
		{
			result.add(AttrFromString(attr.replaceAll("\\s", ""))); // whitespace characters
		}

		return result;
	}

	private static final Pattern token_pattern = Pattern.compile("([a-zA-Z_\\-]+)\\(([^)]*)\\)\\.?");

	private static List<PathToken> ParseTokens(String path)
		throws ExceptionParseError
	{
		List<PathToken> result = new LinkedList<>();

// matches <id>(<anything but closing bracket>)<optional dot>
		Matcher matcher = token_pattern.matcher(path);

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
