/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators;

import au.com.bytecode.opencsv.CSVReader;
import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.utils.IEntityList;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * read a csv file and and fill a given CEntitList with attributes
 * 1st csv string must contain names for attributes
 * rest lines will be taken as values
 * */
public final class CsvFiller
{
	private CsvFiller() {}

	public static void Read(String file_name, IEntityList<? extends OctoEntity> list)
		throws ExceptionParseError, IOException
	{
		CSVReader reader = new CSVReader(new FileReader(file_name));

		try
		{
			String[] fields = reader.readNext();

			if(fields == null)
			{
				throw new ExceptionModelFail("csv file has no data");
			}

			String[] next_line;

			int read = 0;

			for(OctoEntity entity : list)
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

					entity.DeclareAttribute(fields[i], val);
				}

				read++;
			}

			if((next_line = reader.readNext()) != null)
				System.err.println("some data from csv " + file_name + " were not assigned, read: " + read
					+ " expected: " + list.size() + " next line: " + Arrays.toString(next_line));
		}
		finally
		{
			reader.close();
		}
	}
}
