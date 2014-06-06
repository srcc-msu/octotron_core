/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.neo4j.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.parallel.octotron.core.IGraph;
import ru.parallel.octotron.core.IIndex;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.primitive.EEntityType;
import ru.parallel.octotron.primitive.Uid;
import ru.parallel.octotron.primitive.exception.ExceptionDBError;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.utils.OctoObjectList;
import ru.parallel.utils.FileUtils;

import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;
import org.neo4j.tooling.GlobalGraphOperations;
import org.neo4j.visualization.graphviz.GraphvizWriter;
import org.neo4j.walk.Walker;

/**
 * provides access to neo4j BD<br>
 * implements {@link IGraph} interfaces<br>
 * */
public final class Neo4jGraph implements IGraph
{
	private GraphDatabaseService graph_db;

	private Neo4jIndex index;

	private NinjaTransaction transaction;

	private String db_name;

	private static final int COUNT_THRESHOLD = 100000; // write counts to commit new transaction

	private boolean bootstrap = false;

	/**
	 * Get access to current transaction<br>
	 * */
	public NinjaTransaction GetTransaction()
	{
		return transaction;
	}

	/**
	 * Define graph creation flags<br>
	 * LOAD - load existing base<br>
	 * CREATE - create new<br>
	 * RECREATE - remove existing and create new<br>
	 * CONNECT - connect to already running db (NIY)<br>
	 */
	public enum Op
	{
		LOAD, CREATE, RECREATE, CONNECT
	}

	private Neo4jGraph(){}

	/**
	 * load graph with \\name name<br>
	 * */
	public Neo4jGraph(String name, Op op)
		throws ExceptionSystemError
	{
		this(name, op, false);
	}

	/**
	 * load graph with \\name name<br>
	 * */
	public Neo4jGraph(String name, Op op, boolean bootstrap)
		throws ExceptionSystemError
	{
		this.bootstrap = bootstrap;

		if(op == Op.LOAD)
			Load(name);
		else if(op == Op.CONNECT)
			Connect(name);
		else if(op == Op.CREATE)
			Create(name);
		else if(op == Op.RECREATE)
		{
			Delete(name);
			Create(name);
		}
	}

	/**
	 * predefine relationship types -<br>
	 * it is workaround for neo4j, since it requires a enum<br>
	 * they will be reused with our own names<br>
	 */
	private enum RelTypes implements RelationshipType
	{
		TYPE_0,
		TYPE_1,
		TYPE_2,
		TYPE_3,
		TYPE_4,
		TYPE_5,
		TYPE_6,
		TYPE_7,
		TYPE_8,
		TYPE_9,
		TYPE_10,
		TYPE_11,
		TYPE_12,
		TYPE_13,
		TYPE_14,
		TYPE_15,
		TYPE_16,
		TYPE_17,
		TYPE_18,
		TYPE_19
	}

	/**
	 * service list that allows to iterate enum values<br>
	 * */
	private final List<RelTypes> relations
		= new LinkedList<>(Arrays.asList(RelTypes.values()));

	/**
	 * mapping between enum constants and strings<br>
	 * */
	private final Map<String, RelTypes> rel_str_mapping
		= new HashMap<>();

	/**
	 * register \\link_type as a new relation type<br>
	 * neo4j will get one constant from {@link RelTypes}<br>
	 * and we will be remember mapping<br>
	 * */
	private void RegisterLinkType(String link_type)
		throws ExceptionDBError
	{
		if(relations.isEmpty())
			throw new ExceptionDBError("no more relationships availiable");

		rel_str_mapping.put(link_type, relations.remove(0));
	}

	/**
	 * method to convert from string \\link_type to RelTypes enum<br>
	 * if the \\link_type does not exist - creates new relation type<br>
	 */
	private RelTypes LinkTypeToRelType(String link_type)
		throws ExceptionDBError
	{
		if(!rel_str_mapping.containsKey(link_type))
			RegisterLinkType(link_type);

		return rel_str_mapping.get(link_type);
	}

	/**
	 * Create empty graph with \name<br>
	 * */
	public void Create(String name)
		throws ExceptionDBError
	{
		// first check if directory \name is empty
		File file = new File(name);
		if (file.exists())
		{
			if (file.isDirectory())
			{
				if (file.list().length > 0)
				{
					throw new ExceptionDBError("Database with name "
						+ name +" is not empty!");
				}
			}
		}

		Map<String, String> config = new HashMap<>();
		config.put("use_memory_mapped_buffers", "true");
		config.put("keep_logical_logs", "=false");//MAX_LOG_SIZE + "G size");

		db_name = name;

		graph_db = new GraphDatabaseFactory()
			.newEmbeddedDatabaseBuilder(db_name)
			.setConfig(config)
			.newGraphDatabase();

		if(bootstrap)
			DoBootstrap();

		index = new Neo4jIndex(this);

		transaction = new NinjaTransaction(graph_db, Neo4jGraph.COUNT_THRESHOLD);

		System.out.println("db created: " + db_name);
	}

	/**
	 * private function for bootstrap configuration<br>
	 * */
	private void DoBootstrap()
	{
		ServerConfigurator config
			= new ServerConfigurator((GraphDatabaseAPI)graph_db);

		config.configuration()
			.setProperty(Configurator.WEBSERVER_ADDRESS_PROPERTY_KEY, "0.0.0.0");

		new WrappingNeoServerBootstrapper((GraphDatabaseAPI) graph_db, config)
			.start();

		System.out.println("neo4j is accessible through web");
	}

	/**
	 * load \name database<br>
	 * */
	public void Load(String name)
		throws ExceptionDBError
	{
		String err = "Database " + name + " does not exist!";

		File file = new File(name);
		if (file.exists())
		{
			if (file.isDirectory())
			{
				if (file.list().length == 0)
				{
					throw new ExceptionDBError(err);
				}
			}
			else throw new ExceptionDBError(err);
		}
		else throw new ExceptionDBError(err);

		db_name = name;

		graph_db = new GraphDatabaseFactory()
			.newEmbeddedDatabase(db_name);

		if(bootstrap)
			DoBootstrap();

		System.out.println("db loaded: " + db_name);

		index = new Neo4jIndex(this);

		transaction = new NinjaTransaction(graph_db, Neo4jGraph.COUNT_THRESHOLD);
	}

	/**
	 * connect to running database on \address<br>
	 * */
	public void Connect(String address)
		throws ExceptionDBError
	{
		throw new ExceptionDBError("NIY");
	}

	/**
	 * save current database to disk<br>
	 * As it appears, we can't be sure that files on disk have
	 * up-to-date DB version till we shutdown it
	 * (proof: http://docs.neo4j.org/chunked/stable/performance-guide.html)<br>
	 * */
	public void Save()
		throws ExceptionDBError
	{
		Shutdown();
		Load(db_name);
	}

	/**
	 * shutdown current database<br>
	 */
	public void Shutdown()
	{
		transaction.Close();

		graph_db.shutdown();

		System.out.println("db shutdown: " + db_name);
	}

	/**
	 * delete current database<br>
	 * this means delete all files from \db_name directory<br>
	 * */
	public void Delete()
		throws ExceptionSystemError
	{
		Delete(db_name);
	}

	public static void Delete(String name)
		throws ExceptionSystemError
	{
		File file = new File(name);

		if(file.exists())
			FileUtils.WipeDir(file);
	}

	/**
	 * checks that uid type matches the given /type<br>
	 * throws exception otherwise<br>
	 * */
	private static void MatchType(Uid uid, EEntityType type)
	{
		if(uid.getType() != type)
			throw new ExceptionModelFail
				("Mismatch entity type for operation");
	}

	@Override
	public Uid AddObject()
	{
		transaction.Write();

		Node node = graph_db.createNode();

		return new Uid(node.getId(), EEntityType.OBJECT);
	}

	/**
	 * something is not right here, there is some kind of correlation
	 * with transactions, pay attention
	 * */
	@Override
	public void DeleteObject(Uid uid)
	{
		transaction.Delete();

		MatchType(uid, EEntityType.OBJECT);

		Node node = graph_db.getNodeById(uid.getUid());
		Iterable<Relationship> rels = node.getRelationships();

		for(Relationship rel : rels)
			rel.delete();

		node.delete();
	}

	/**
	 * something is not right here, there is some kind of correlation
	 * with transactions, pay attention
	 * */
	@Override
	public void DeleteLink(Uid uid)
	{
		transaction.Delete();

		MatchType(uid, EEntityType.LINK);

		graph_db.getRelationshipById(uid.getUid()).delete();
	}

	@Override
	public Uid AddLink(Uid source, Uid target, String link_type)
	{
		transaction.Write();

		Relationship rel;
		Node source_node;
		Node target_node;

		source_node = graph_db.getNodeById(source.getUid());
		target_node = graph_db.getNodeById(target.getUid());

		RelTypes rel_type = LinkTypeToRelType(link_type);

		rel = source_node.createRelationshipTo(target_node, rel_type);

		return new Uid(rel.getId(), EEntityType.LINK);
	}

	@Override
	public void SetObjectAttribute(Uid uid, String name, Object value)
		{
		transaction.Write();

		MatchType(uid, EEntityType.OBJECT);

		try
		{
			Node node = graph_db.getNodeById(uid.getUid());
			node.setProperty(name, value);
		}
		catch (NotFoundException ex)
		{
			throw new ExceptionModelFail("element not found");
		}
		catch (IllegalArgumentException ex)
		{
			// this exception can be thrown by setProperty method if value has incorrect type
			throw new ExceptionDBError("illegal argument");
		}
	}

	@Override
	public Object GetObjectAttribute(Uid uid, String name)
	{
		transaction.Read();

		MatchType(uid, EEntityType.OBJECT);

		try
		{
			Node node = graph_db.getNodeById(uid.getUid());

			if(node.hasProperty(name))
				return node.getProperty(name);
			else
			{
				StringBuilder rep = new StringBuilder();

				for(Pair<String, Object> att : GetObjectAttributes(uid))
					rep.append(att.getKey())
						.append(" : ")
						.append(att.getValue())
						.append(System.lineSeparator());

				throw new ExceptionModelFail("attribute not found: " + name + System.lineSeparator() + rep);
			}
		}
		catch (NotFoundException ex)
		{
			throw new ExceptionModelFail("element not found");
		}
	}

	@Override
	public void SetLinkAttribute(Uid uid, String name, Object value)
	{
		transaction.Write();

		MatchType(uid, EEntityType.LINK);

		try
		{
			Relationship rel = graph_db.getRelationshipById(uid.getUid());
			rel.setProperty(name, value);
		}
		catch (NotFoundException ex)
		{
			throw new ExceptionModelFail("element not found");
		}
	}

	@Override
	public Object GetLinkAttribute(Uid uid, String name)
	{
		transaction.Read();

		MatchType(uid, EEntityType.LINK);

		try
		{
			Relationship rel = graph_db.getRelationshipById(uid.getUid());

			if(rel.hasProperty(name))
				return rel.getProperty(name);

			else
			{
				StringBuilder rep = new StringBuilder();

				for(Pair<String, Object> att : GetLinkAttributes(uid))
					rep.append(att.getKey())
						.append(" : ")
						.append(att.getValue())
						.append(System.lineSeparator());

				throw new ExceptionModelFail("attribute not found: " + name + System.lineSeparator() + rep);
			}
		}
		catch (NotFoundException ex)
		{
			throw new ExceptionModelFail("element not found");
		}
	}

	private static List<Uid> FromRelIter(Iterator<Relationship> it)
	{
		List<Uid> list = new LinkedList<>();

		while(it.hasNext())
		{
			Relationship rel = it.next();

			list.add(new Uid(rel.getId(), EEntityType.LINK));
		}

		return list;
	}

	private static List<Uid> FromNodeIter(Iterator<Node> it)
	{
		List<Uid> list = new LinkedList<>();

		while(it.hasNext())
		{
			Node rel = it.next();

			list.add(new Uid(rel.getId(), EEntityType.OBJECT));
		}

		return list;
	}

	@Override
	public List<Uid> GetOutLinks(Uid uid)
	{
		transaction.Read();

		MatchType(uid, EEntityType.OBJECT);

		try
		{
			Node node  = graph_db.getNodeById(uid.getUid());

			return FromRelIter
				(node.getRelationships(Direction.OUTGOING).iterator());
		}
		catch (NotFoundException ex)
		{
			throw new ExceptionModelFail("element not found");
		}
	}

	@Override
	public List<Uid> GetInLinks(Uid uid)
	{
		transaction.Read();

		MatchType(uid, EEntityType.OBJECT);

		try
		{
			Node node  = graph_db.getNodeById(uid.getUid());

			return FromRelIter
				(node.getRelationships(Direction.INCOMING).iterator());
		}
		catch (NotFoundException ex)
		{
			throw new ExceptionModelFail("element not found");
		}
	}

	@Override
	public List<Uid> GetAllObjects()
	{
		transaction.Read();

		List<Uid> list = new LinkedList<>();

		for(Node node : GlobalGraphOperations.at(graph_db).getAllNodes())
		{
			if(node.getId() != 0)
				list.add(new Uid(node.getId(), EEntityType.OBJECT));
		}

		return list;
	}

	@Override
	public List<Uid> GetAllLinks()
	{
		transaction.Read();

		List<Uid> list = new LinkedList<>();

		for(Relationship rel : GlobalGraphOperations.at(graph_db).getAllRelationships())
		{
			list.add(new Uid(rel.getId(), EEntityType.LINK));
		}

		return list;
	}

	@Override
	public Uid GetLinkTarget(Uid uid)
	{
		transaction.Read();

		MatchType(uid, EEntityType.LINK);

		// this try/catch is used to find out if relationship \\uid exist in \\graphDb
		Relationship rel;

		try
		{
			rel = graph_db.getRelationshipById(uid.getUid());
		}
		catch (NotFoundException ex)
		{
			throw new ExceptionModelFail("element not found");
		}

		return new Uid(rel.getEndNode().getId(), EEntityType.OBJECT);
	}

	@Override
	public Uid GetLinkSource(Uid uid)
	{
		transaction.Read();

		MatchType(uid, EEntityType.LINK);

		Relationship rel;
		// this try/catch is used to find out if relationship \\uid exist in \\graphDb
		try
		{
			rel = graph_db.getRelationshipById(uid.getUid());
		}
		catch (NotFoundException ex)
		{
			throw new ExceptionModelFail("element not found");
		}

		return new Uid(rel.getStartNode().getId(), EEntityType.OBJECT);
	}

	@Override
	public List<Pair<String, Object>> GetObjectAttributes(Uid uid)
	{
		transaction.Read();
		MatchType(uid, EEntityType.OBJECT);

		List<Pair<String, Object>> attrs = new LinkedList<>();

		Node node = graph_db.getNodeById(uid.getUid());

		for (String name : node.getPropertyKeys())
			attrs.add(Pair.of(name, node.getProperty(name)));

		return attrs;
	}

	@Override
	public List<Pair<String, Object>> GetLinkAttributes(Uid uid)
	{
		transaction.Read();
		MatchType(uid, EEntityType.LINK);

		List<Pair<String, Object>> attrs = new LinkedList<>();

		Relationship rel = graph_db.getRelationshipById(uid.getUid());

		for (String name : rel.getPropertyKeys())
			attrs.add(Pair.of(name, rel.getProperty(name)));

		return attrs;
	}

	@Override
	public void DeleteObjectAttribute(Uid uid, String name)
	{
		transaction.Write();
		MatchType(uid, EEntityType.OBJECT);

		Node node = graph_db.getNodeById(uid.getUid());

		if (!node.hasProperty(name))
			throw new ExceptionModelFail("attribute not found: " + name);

		node.removeProperty(name);
	}

	@Override
	public void DeleteLinkAttribute(Uid uid, String name)
	{
		transaction.Write();
		MatchType(uid, EEntityType.LINK);

		Relationship rel = graph_db.getRelationshipById(uid.getUid());

		if (!rel.hasProperty(name))
			throw new ExceptionModelFail("attribute not found: " + name);

		rel.removeProperty(name);
	}

	@Override
	public boolean TestObjectAttribute(Uid uid, String name)
	{
		transaction.Read();
		MatchType(uid, EEntityType.OBJECT);

		boolean res;

		try
		{
			res = graph_db.getNodeById(uid.getUid()).hasProperty(name);
		}
		catch(IllegalStateException e)
		{
			return false;
		}

		return res;
	}

	@Override
	public boolean TestLinkAttribute(Uid uid, String name)
	{
		transaction.Read();
		MatchType(uid, EEntityType.LINK);

		boolean res;

		try
		{
			res = graph_db.getRelationshipById(uid.getUid()).hasProperty(name);
		}
		catch(IllegalStateException e)
		{
			return false;
		}

		return res;
	}

	@Override
	public IIndex GetIndex()
	{
		return index;
	}

	/** packet only, get access to inner neo4j index */
	IndexManager GetInnerIndex()
	{
		return graph_db.index();
	}

	@Override
	public String ExportDot(OctoObjectList objects)
	{
		List<Node> nodes = new LinkedList<>();

		for(OctoObject obj : objects)
			nodes.add(graph_db.getNodeById(obj.GetUID().getUid()));

		return ExportDot(nodes);
	}

	private static String ExportDot(Iterable<Node> nodes)
	{
		OutputStream out = new ByteArrayOutputStream();

		GraphvizWriter writer = new GraphvizWriter();

		try
		{
			writer.emit(out, Walker.crosscut(nodes, RelTypes.values()));
		}
		catch (IOException e)
		{
			throw new ExceptionModelFail
				("Traverse during export to .dot file failed: "
					+ e.getMessage());
		}

		return out.toString();
	}
}
