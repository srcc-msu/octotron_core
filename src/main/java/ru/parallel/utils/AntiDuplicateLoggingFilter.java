package ru.parallel.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * checks that logging messages does not repeat too often
 * */
public class AntiDuplicateLoggingFilter implements Filter
{
	private static final int LOOKBACK_COUNT = 5;
	private Queue<LogRecord> records = new LinkedList<>();
	private final long ms_threshold;

	public AntiDuplicateLoggingFilter(long ms_threshold)
	{
		this.ms_threshold = ms_threshold;
	}

	private LogRecord GetLastOrNull(LogRecord current_record)
	{
		LogRecord result = null;

		for(LogRecord record : records)
		{
			if(record.getMessage().equals(current_record.getMessage()))
				result = record;
		}

		return result;
	}

	private void RotateRecord(LogRecord record, LogRecord last_record)
	{
		records.add(record);

		if(last_record != null)
			records.remove(last_record);

		if(records.size() > LOOKBACK_COUNT)
			records.remove();
	}

	@Override
	public synchronized boolean isLoggable(LogRecord check_record)
	{
		LogRecord last_record = GetLastOrNull(check_record);

		RotateRecord(check_record, last_record);

		if(last_record == null)
			return true;

		return check_record.getMillis() - last_record.getMillis() > ms_threshold;
	}
}
