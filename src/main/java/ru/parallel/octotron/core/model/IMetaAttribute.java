package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.graph.IAttribute;
import ru.parallel.octotron.core.model.impl.attribute.EAttributeType;
import ru.parallel.octotron.core.model.impl.attribute.VaryingAttribute;
import ru.parallel.octotron.neo4j.impl.Marker;

import java.util.List;

public interface IMetaAttribute extends IAttribute
{
	Object GetLastValue();

	long GetCTime();
	long GetATime();

	double GetSpeed();

	boolean IsValid();

	void SetValid();
	void SetInvalid();

	EAttributeType GetType();

	void AddDependant(VaryingAttribute attribute);

	AttributeList<VaryingAttribute> GetDependant();

	void AddReaction(OctoReaction reaction);
	List<OctoReaction> GetReactions();

	List<OctoResponse> GetReadyReactions();
	List<OctoResponse> GetExecutedReactions();

	List<Marker> GetMarkers();
	long AddMarker(OctoReaction reaction, String description, boolean suppress);
	void DeleteMarker(long id);
}
