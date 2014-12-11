/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.exec.services.ModelService;
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

	public static void Declare(ModelService service, Iterable<? extends ModelEntity> list, String file_name)
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
					throw new ExceptionModelFail("not enough data in csv, read: " + read);
				}

				if(next_line.length != fields.length)
					throw new ExceptionModelFail("some fields are missing, line[" + read + "]: "
						+ Arrays.toString(next_line));

				for(int i = 0; i < fields.length; i++)
				{
					String str_val = next_line[i];

					Value val = Value.ValueFromStr(str_val);

					entity.GetBuilder(service).DeclareConst(fields[i], val.GetRaw());
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

	public static <T extends ModelEntity> void Declare(ModelService service, T object, String file_name)
		throws ExceptionParseError, IOException
	{
		Declare(service, ModelList.Single(object), file_name);
	}
}
