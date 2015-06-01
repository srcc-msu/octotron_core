/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.path;

import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

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
	static final PathOperations.PathToken[] TOKENS =
	{
		PathOperations.obj, PathOperations.link
		, PathOperations.q
		, PathOperations.in_n, PathOperations.out_n, PathOperations.all_n
		, PathOperations.uniq
		, PathOperations.in_l, PathOperations.out_l, PathOperations.all_l
		, PathOperations.source, PathOperations.target
	};

	private static final Map<String, ModelList.EQueryType> DELIMS
		= new HashMap<>();

	static
	{
		PathParser.DELIMS.put("==", ModelList.EQueryType.EQ);
		PathParser.DELIMS.put("!=", ModelList.EQueryType.NE);
		PathParser.DELIMS.put("<=", ModelList.EQueryType.LE);
		PathParser.DELIMS.put(">=", ModelList.EQueryType.GE);
		PathParser.DELIMS.put("<", ModelList.EQueryType.LT);
		PathParser.DELIMS.put(">", ModelList.EQueryType.GT);
	}


// matched <id><any chars !=<>.><value>
	private static final Pattern op_pattern_op = Pattern.compile("^([a-zA-Z_\\-0-9\\.]+)([!=<>]+)(.+)$");
	private static final Pattern op_pattern_word = Pattern.compile("^([a-zA-Z_\\-0-9\\.]+)$");

/**
 * parse SimpleAttribute from string "<name><op><value>"<br>
 * */
	public static PathOperations.Query AttrFromStringOrNull(String str)
		throws ExceptionParseError
	{
		if(str.length() == 0)
			return null;

		Matcher matcher = op_pattern_op.matcher(str);

		if(matcher.find())
		{
			String name = matcher.group(1);
			String op = matcher.group(2);
			Value value = Value.ValueFromString(matcher.group(3));

			ModelList.EQueryType type = DELIMS.get(op);

			if(type == null)
				throw new ExceptionParseError("unsupported operation: " + op);

			return new PathOperations.Query(name, value, type);
		}

		matcher = op_pattern_word.matcher(str);

		if(matcher.find())
			return new PathOperations.Query(str, null, ModelList.EQueryType.NONE);

		throw new ExceptionParseError("can not parse: " + str);
	}

/**
 * parse list of attributes, separated by commas<br>
 * name=val,name2,name3=val<br>
 * may be empty<br>
 * */
	private static List<PathOperations.Query> StrToAttrList(String str)
		throws ExceptionParseError
	{
		List<PathOperations.Query> result
			= new LinkedList<>();

		if(str == null || str.isEmpty())
			return result;

		String[] attrs = str.split(",");

		for(String attr : attrs)
		{
			result.add(AttrFromStringOrNull(attr.replaceAll("\\s", ""))); // whitespace characters
		}

		return result;
	}

// matches <id>(<anything but closing bracket>)<optional dot> or
// matches <id><optional dot>
	private static final Pattern token_pattern = Pattern.compile("([a-zA-Z_\\-]+)(\\(([^)]*)\\))?\\.?");

	static List<PathOperations.PathToken> ParseTokens(String path)
		throws ExceptionParseError
	{
		List<PathOperations.PathToken> result = new LinkedList<>();

		Matcher matcher = token_pattern.matcher(path);

		int last = -1;

		while(matcher.find())
		{
			String name;
			String params;

			if(matcher.groupCount() == 3) // single command, no params
			{
				name = matcher.group(1);
				params = matcher.group(3);
			}
			else if(matcher.groupCount() == 1) // single command, no args
			{
				name = matcher.group(1);
				params = null;
			}
			else
				throw new ExceptionParseError("could not parse path");

			PathOperations.PathToken base_token = null;

			for(PathOperations.PathToken token : TOKENS)
				if(token.GetName().equals(name))
				{
					base_token = token;
					break;
				}

			if(base_token == null)
				throw new ExceptionParseError("unknown path token: " + name);

			result.add(new PathOperations.PathToken(base_token, StrToAttrList(params)));

			last = matcher.end();
		}

		if(last != path.length())
			throw new ExceptionParseError("Unclaimed suffix: " + path.substring(last));

		return result;
	}

/**
 * check that each token follows after allowed token<br>
 * and start/end tokens are correct<br>
 * returns type of the last token if succeeded<br>
 * throws ExceptionParseError otherwise<br>
 * */
	private static void TypeCheck(List<PathOperations.PathToken> tokens)
		throws ExceptionParseError
	{
		if(tokens.isEmpty())
			throw new ExceptionParseError("empty request");

		PathOperations.CHAIN_TYPE type = PathOperations.CHAIN_TYPE.E_START;

		for(PathOperations.PathToken token : tokens)
		{
			if(token.GetIn() == PathOperations.CHAIN_TYPE.E_ANY)
			{
				type = token.GetOut();
				continue;
			}

			else if(token.GetIn() == PathOperations.CHAIN_TYPE.E_MATCH)
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
			List<PathOperations.PathToken> tokens = ParseTokens(path);

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
