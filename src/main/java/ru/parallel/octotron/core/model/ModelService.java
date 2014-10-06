package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

public final class ModelService
{
	private static ModelService INSTANCE = null;

	public static void Init(Neo4jGraph graph)
	{
		ModelService.INSTANCE = new ModelService();
	}

	public static void Finish()
	{
		ModelService.INSTANCE = null;
	}

	public static ModelService Get()
	{
		return INSTANCE;
	}

// -------------------------

	ModelObjectList objects;
	ModelLinkList links;

	public ModelLink AddLink(ModelObject source, ModelObject target)
	{
		ModelLink link = new ModelLink(source, target);
		source.GetBuilder().AddOutLink(link);
		target.GetBuilder().AddInLink(link);
		return link;
	}

	public ModelObject AddObject()
	{
		return new ModelObject();
	}

	public ModelObjectList GetAllObjects()
	{
		return objects;
	}

	public ModelLinkList GetAllLinks()
	{
		return links;
	}

	public void EnableLinkIndex(String attr)
	{
	}

	public void EnableObjectIndex(String aid)
	{
	}

	public void MakeRuleDependencies()
	{
		for(ModelObject object : GetAllObjects())
		{
			for(VarAttribute attribute : object.GetVar())
			{
				attribute.GetBuilder().MakeDependant();
			}
		}
	}

	public static String ExportDot()
	{
		throw new ExceptionModelFail("NIY");
	}

	public static String ExportDot(ModelObjectList objects)
	{
		throw new ExceptionModelFail("NIY");
	}

	public static ModelObjectList GetObjects(SimpleAttribute attribute)
	{
		return null;
	}

	public static ModelObjectList GetObjects(String name)
	{
		return null;
	}

	public static ModelLinkList GetLinks(SimpleAttribute attr)
	{
		return null;
	}

	public static ModelLinkList GetLinks(String name)
	{
		return null;
	}

	public static void Init()
	{
		ModelService.INSTANCE = new ModelService();
	}

	public void Clean()
	{
		objects = new ModelObjectList();
		links = new ModelLinkList();
	}
}
