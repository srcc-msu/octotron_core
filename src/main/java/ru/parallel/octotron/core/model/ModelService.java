package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

public final class ModelService
{
	public static enum EMode
	{
		CREATION, LOAD, OPERATION
	}

	private static ModelService INSTANCE = null;

	public static void Init(EMode mode, String path, String name)
	{
		ModelService.INSTANCE = new ModelService(mode, path, name);
	}

	public static void Init(EMode mode)
	{
		ModelService.INSTANCE = new ModelService(mode);
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

	private EMode mode;
	private final boolean db;

	private final ModelCache cache;

	private ModelObjectList objects;
	private ModelLinkList links;

	private ModelService(EMode mode, boolean db, String path, String name)
	{
		objects = new ModelObjectList();
		links = new ModelLinkList();

		cache = new ModelCache();

		this.mode = mode;

		this.db = db;
	}

	protected ModelService(EMode mode)
	{
		this(mode, false, "", "");
	}

	protected ModelService(EMode mode, String path, String name)
	{
		this(mode, true, path, name);
	}

	public EMode GetMode()
	{
		return mode;
	}

	public void Operate()
	{
		mode = EMode.OPERATION;
	}

	public ModelLink AddLink(ModelObject source, ModelObject target)
	{
		ModelLink link = new ModelLink(source, target);

		source.GetBuilder().AddOutLink(link);
		target.GetBuilder().AddInLink(link);

		links.add(link);
		link.GetBuilder().DeclareConst("AID", link.GetID());

		return link;
	}

	public ModelObject AddObject()
	{
		ModelObject object = new ModelObject();

		objects.add(object);
		object.GetBuilder().DeclareConst("AID", object.GetID());


		return object;
	}

	public ModelObjectList GetAllObjects()
	{
		return objects;
	}

	public ModelLinkList GetAllLinks()
	{
		return links;
	}

	public void EnableLinkIndex(String name)
	{
		cache.EnableLinkIndex(name, links);
	}

	public void EnableObjectIndex(String name)
	{
		cache.EnableObjectIndex(name, objects);
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

	public ModelObjectList GetObjects(SimpleAttribute attribute)
	{
		return cache.GetObjects(attribute);
	}

	public ModelObjectList GetObjects(String name)
	{
		return cache.GetObjects(name);
	}

	public ModelLinkList GetLinks(SimpleAttribute attribute)
	{
		return cache.GetLinks(attribute);
	}

	public ModelLinkList GetLinks(String name)
	{
		return cache.GetLinks(name);
	}

	public void Clean()
	{
		objects = new ModelObjectList();
		links = new ModelLinkList();

		cache.Clean();
	}
}
