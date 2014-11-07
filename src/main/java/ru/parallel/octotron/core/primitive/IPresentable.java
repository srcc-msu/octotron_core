package ru.parallel.octotron.core.primitive;

import java.util.Map;

public interface IPresentable
{
	Map<String, Object> GetLongRepresentation();
	Map<String, Object> GetShortRepresentation();
}
