package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.UniqueID;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Reaction extends UniqueID<EEntityType>
{
	final ReactionTemplate template;

	Map<Long, Marker> markers;

	private long state;
	private long stat;

	public Reaction(ReactionTemplate template)
	{
		super(EEntityType.REACTION);
		this.template = template;

		markers = new HashMap<>();

		state = 0;
		stat = 0;
	}


	public ReactionTemplate GetTemplate()
	{
		return template;
	}

	public Object GetState()
	{
		return state;
	}


	public long GetStat()
	{
		return stat;
	}

	public long AddMarker(String descr, boolean suppress)
	{
		Marker m = new Marker(this, descr, suppress);

		markers.put(m.GetID(), m);
		return m.GetID();
	}

	public Collection<Marker> GetMarkers()
	{
		return markers.values();
	}

	public void DeleteMarker(long id)
	{
		markers.remove(id);
	}
}
