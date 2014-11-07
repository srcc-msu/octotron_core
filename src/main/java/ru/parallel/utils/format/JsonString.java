package ru.parallel.utils.format;

public class JsonString extends TypedString
{
	public JsonString(String string)
	{
		super(string);
	}

	@Override
	public String GetContentType()
	{
		return "application/json";
	}
}
