package ru.parallel.utils.format;

public class JsonpString extends TypedString
{
	public JsonpString(String string)
	{
		super(string);
	}

	@Override
	public String GetContentType()
	{
		return "application/javascript";
	}
}
