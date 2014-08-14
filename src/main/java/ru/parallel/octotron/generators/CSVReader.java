/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.collections.EntityList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
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

	public static void Declare(EntityList<? extends ModelEntity, ?> list, String file_name)
		throws ExceptionParseError, IOException
	{
		au.com.bytecode.opencsv.CSVReader reader = new au.com.bytecode.opencsv.CSVReader(new FileReader(file_name));

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
					throw new ExceptionModelFail("not enough data in csv, read: " + read + " expected: " + list.size());
				}

				if(next_line.length != fields.length)
					throw new ExceptionModelFail("some fields are missing, read: " + read + " expected: " + list.size());

				for(int i = 0; i < fields.length; i++)
				{
					String str_val = next_line[i];

					Object val = SimpleAttribute.ValueFromStr(str_val);

					entity.DeclareConstant(fields[i], val);
				}

				read++;
			}

			if((next_line = reader.readNext()) != null)
				LOGGER.log(Level.WARNING, "some data from csv " + file_name + " were not assigned, read: " + read
					+ " expected: " + list.size() + " next line: " + Arrays.toString(next_line));
		}
		finally
		{
			reader.close();
		}
	}
}
