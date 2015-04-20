/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.collections;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.model.ModelObject;

import java.util.List;

public class ModelObjectList extends ModelList<ModelObject, ModelObjectList>
{
	public ModelObjectList()
	{
		super();
	}

/**
	 * list copy
	 */
	public ModelObjectList(ModelObjectList list)
	{
		super(list);
	}

/**
	 * list reuse
	 */
	protected ModelObjectList(List<ModelObject> list)
	{
		super(list);
	}

	@Override
	protected ModelObjectList Instance(List<ModelObject> new_list)
	{
		return new ModelObjectList(new_list);
	}

// --------------------

	public ModelObjectList GetInNeighbors(String link_name, Value link_value)
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetInNeighbors(link_name, link_value));

		return new_list;
	}

	public ModelObjectList GetOutNeighbors(String link_name, Value link_value)
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetOutNeighbors(link_name, link_value));

		return new_list;
	}

// --------------------

	public ModelObjectList GetInNeighbors(String link_name, Object link_value)
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetInNeighbors(link_name, Value.Construct(link_value)));

		return new_list;
	}

	public ModelObjectList GetOutNeighbors(String link_name, Object link_value)
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetOutNeighbors(link_name, Value.Construct(link_value)));

		return new_list;
	}

	public ModelObjectList GetUndirectedNeighbors(String link_name, Object link_value)
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetUndirectedNeighbors(link_name, Value.Construct(link_value)));

		return new_list;
	}

	public ModelObjectList GetAllNeighbors(String link_name, Object link_value)
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetAllNeighbors(link_name, Value.Construct(link_value)));

		return new_list;
	}

// --------------------

	public ModelObjectList GetInNeighbors(String link_name)
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetInNeighbors(link_name));

		return new_list;
	}

	public ModelObjectList GetOutNeighbors(String link_name)
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetOutNeighbors(link_name));

		return new_list;
	}

	public ModelObjectList GetUndirectedNeighbors(String link_name)
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetUndirectedNeighbors(link_name));

		return new_list;
	}

	public ModelObjectList GetAllNeighbors(String link_name)
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetAllNeighbors(link_name));

		return new_list;
	}

// --------------------

	public ModelObjectList GetInNeighbors()
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetInNeighbors());

		return new_list;
	}

	public ModelObjectList GetOutNeighbors()
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetOutNeighbors());

		return new_list;
	}

	public ModelObjectList GetUndirectedNeighbors()
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetUndirectedNeighbors());

		return new_list;
	}

	public ModelObjectList GetAllNeighbors()
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetAllNeighbors());

		return new_list;
	}

// --------------------

	public ModelLinkList GetInLinks()
	{
		ModelLinkList new_list = new ModelLinkList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetInLinks());

		return new_list;
	}

	public ModelLinkList GetOutLinks()
	{
		ModelLinkList new_list = new ModelLinkList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetOutLinks());

		return new_list;
	}

	public ModelLinkList GetUndirectedLinks()
	{
		ModelLinkList new_list = new ModelLinkList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetUndirectedLinks());

		return new_list;
	}

	public ModelLinkList GetAllLinks()
	{
		ModelLinkList new_list = new ModelLinkList();

		for(ModelObject obj : list)
			new_list = new_list.append(obj.GetAllLinks());

		return new_list;
	}
}
