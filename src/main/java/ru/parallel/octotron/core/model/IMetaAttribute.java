package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.IAttribute;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.logic.Marker;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.impl.attribute.EAttributeType;
import ru.parallel.octotron.core.model.impl.attribute.VaryingAttribute;
import ru.parallel.octotron.core.model.impl.meta.ReactionObject;

import java.util.List;

public interface IMetaAttribute extends IAttribute
{
	Object GetLastValue();

	long GetCTime();

	double GetSpeed();

	boolean IsValid();

	void SetValid();
	void SetInvalid();

	EAttributeType GetType();

	void AddDependant(VaryingAttribute attribute);

	AttributeList<VaryingAttribute> GetDependant();

	void AddReaction(Reaction reaction);
	List<ReactionObject> GetReactions();

	List<Response> ProcessReactions();
	List<Response> GetCurrentReactions();

	List<Marker> GetMarkers();
	long AddMarker(Reaction reaction, String description, boolean suppress);
	void DeleteMarker(long id);

}
