package ru.parallel.utils.format;

public class ErrorString extends TypedString
{
	public ErrorString(String string)
	{
		super(string);
	}

	@Override
	public String GetContentType()
	{
		return "text/plain";
	}
}
