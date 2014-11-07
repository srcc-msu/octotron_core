package ru.parallel.utils.format;

public class TextString extends TypedString
{
	public TextString(String string)
	{
		super(string);
	}

	@Override
	public String GetContentType()
	{
		return "text/plain";
	}
}
