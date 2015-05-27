/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * read a csv file and and fill a given IEntityList with attributes
 * 1st csv string must contain names for attributes
 * rest lines will be taken as values
 * */
public final class CSVReader
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private CSVReader() {}

	public static void Declare(Iterable<? extends ModelEntity> list, String file_name)
		throws ExceptionParseError, IOException
	{
		au.com.bytecode.opencsv.CSVReader reader = new au.com.bytecode.opencsv.CSVReader(new FileReader(file_name), ',', '\'');

		try
		{
			String[] fields = reader.readNext();

			if(fields == null)
			{
				throw new ExceptionModelFail("csv file has no data");
			}

			String[] next_line;

			int read = 0;

			for(ModelEntity entity : list)
			{
				next_line = reader.readNext();

				if(next_line == null)
				{
					throw new ExceptionModelFail("not enough data in csv, read: " + read);
				}

				if(next_line.length != fields.length)
					throw new ExceptionModelFail("some fields are missing, line[" + read + "]: "
						+ Arrays.toString(next_line));

				for(int i = 0; i < fields.length; i++)
				{
					String str_val = next_line[i];

					Value val = Value.ValueFromStr(str_val);

					entity.GetBuilder().DeclareConst(fields[i], val.GetRaw());
				}

				read++;
			}

			if(reader.readNext() != null)
				LOGGER.log(Level.WARNING, "some data from csv " + file_name + " were not assigned, read: " + read);
		}
		finally
		{
			reader.close();
		}
	}

	public static <T extends ModelEntity> void Declare(T object, String file_name)
		throws ExceptionParseError, IOException
	{
		Declare(ModelList.Single(object), file_name);
	}

	public static ModelObjectList OrderByColumn(ModelObjectList input, String file_name, int... column)
		throws ExceptionParseError, IOException
	{
		au.com.bytecode.opencsv.CSVReader reader = new au.com.bytecode.opencsv.CSVReader(new FileReader(file_name), ',', '\'');

		try
		{
			String[] fields = reader.readNext();

			if(fields == null)
			{
				throw new ExceptionModelFail("csv file has no data");
			}

			ModelObjectList result = new ModelObjectList();

			Map<Value, ModelObjectList> cache = new HashMap<>();

			for(ModelObject object : input)
			{
				Value value = object.GetAttribute(fields[column[0]]).GetValue();

				ModelObjectList tmp = cache.get(value);

				if(tmp == null)
					tmp = new ModelObjectList();

				tmp.add(object);

				cache.put(value, tmp);
			}

			String[] next_line = reader.readNext();

			while(next_line != null)
			{
				if(next_line.length != fields.length)
					throw new ExceptionModelFail("some fields are missing: "
						+ Arrays.toString(next_line));

				ModelObjectList tmp = cache.get(Value.ValueFromStr(next_line[column[0]]));

				for(int i = 1; i < column.length; i++)
				{
					if(column[i] >= fields.length)
					{
						throw new ExceptionModelFail("csv file has not enough columns: " + Arrays.toString(fields));
					}

					String column_name = fields[column[i]];

					tmp = tmp.Filter(column_name, Value.ValueFromStr(next_line[column[i]]));
				}

				result.add(tmp.Only());
				next_line = reader.readNext();
			}

			return result;
		}
		finally
		{
			reader.close();
		}
	}
}
