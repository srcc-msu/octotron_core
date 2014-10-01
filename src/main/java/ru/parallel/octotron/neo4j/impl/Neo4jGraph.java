/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.neo4j.impl;

import com.google.common.collect.Iterables;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;
import org.neo4j.tooling.GlobalGraphOperations;
import org.neo4j.visualization.graphviz.GraphvizWriter;
import org.neo4j.walk.Walker;
import ru.parallel.octotron.core.graph.IGraph;
import ru.parallel.octotron.core.graph.IIndex;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.Uid;
import ru.parallel.octotron.core.primitive.exception.ExceptionDBError;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * provides access to neo4j BD<br>
 * implements {@link IGraph} interfaces<br>
 * */
public final class Neo4jGraph implements IGraph
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

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
		this.db_name = name;
		this.bootstrap = bootstrap;

		if(op == Op.LOAD)
			Load();
		else if(op == Op.CONNECT)
			Connect();
		else if(op == Op.CREATE)
			Create();
		else if(op == Op.RECREATE)
		{
			Delete();
			Create();
		}
	}

	private void DBInit()
	{
		graph_db = new GraphDatabaseFactory()
			.newEmbeddedDatabaseBuilder(db_name)
			.setConfig(GraphDatabaseSettings.keep_logical_logs, "false")
			.setConfig(GraphDatabaseSettings.use_memory_mapped_buffers, "true")
			.newGraphDatabase();

		if(bootstrap)
			DoBootstrap();

		index = new Neo4jIndex(this);

		transaction = new NinjaTransaction(graph_db, Neo4jGraph.COUNT_THRESHOLD);
	}

	/**
	 * private function for bootstrap configuration<br>
	 * */
	@SuppressWarnings("deprecation")
	private void DoBootstrap()
	{
		ServerConfigurator config
			= new ServerConfigurator((GraphDatabaseAPI)graph_db);

		config.configuration()
			.setProperty(Configurator.WEBSERVER_ADDRESS_PROPERTY_KEY, "0.0.0.0");

		new WrappingNeoServerBootstrapper((GraphDatabaseAPI) graph_db, config)
			.start();

		LOGGER.log(Level.INFO, "neo4j is accessible through the web");
	}

	public void Load()
		throws ExceptionSystemError
	{
		if(FileUtils.IsDirEmpty(db_name))
			throw new ExceptionDBError("database does not exist: " + db_name);

		DBInit();

		LOGGER.log(Level.INFO, "db loaded: " + db_name);
	}

	public void Create()
		throws ExceptionSystemError
	{
		if(!FileUtils.IsDirEmpty(db_name))
			throw new ExceptionDBError("directory is not empty: " + db_name);

		DBInit();

		LOGGER.log(Level.INFO, "db created: " + db_name);
	}

	/**
	 * connect to running database on \address<br>
	 * */
	public void Connect()
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
		throws ExceptionSystemError
	{
		Shutdown();
		Load();
	}

	/**
	 * shutdown current database<br>
	 */
	public void Shutdown()
	{
		transaction.Close();

		graph_db.shutdown();

		LOGGER.log(Level.INFO, "db shutdown: " + db_name);
	}

	/**
	 * delete current database<br>
	 * this means delete all files from \db_name directory<br>
	 * */
	public void Delete()
		throws ExceptionSystemError
	{
		FileUtils.WipeDir(db_name);
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
	public void AddNodeLabel(Uid uid, String label)
	{
		MatchType(uid, EEntityType.OBJECT);

		graph_db.getNodeById(uid.getUid()).addLabel(DynamicLabel.label(label));
	}

	@Override
	public boolean TestNodeLabel(Uid uid, String label)
	{
		MatchType(uid, EEntityType.OBJECT);

		return graph_db.getNodeById(uid.getUid()).hasLabel(DynamicLabel.label(label));
	}

	@Override
	public List<Uid> GetAllLabeledNodes(String label)
	{
		List<Uid> list = new LinkedList<>();

		for(Node node : GlobalGraphOperations.at(graph_db)
			.getAllNodesWithLabel(DynamicLabel.label(label)))
		{
			list.add(new Uid(node.getId(), EEntityType.OBJECT));
		}

		return list;
	}

	@Override
	public Uid AddLink(Uid source, Uid target, String link_type)
	{
		transaction.Write();

		Node source_node = graph_db.getNodeById(source.getUid());
		Node target_node = graph_db.getNodeById(target.getUid());

		RelationshipType type = DynamicRelationshipType.withName(link_type);

		Relationship relationship = source_node.createRelationshipTo(target_node, type);

		return new Uid(relationship.getId(), EEntityType.LINK);
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
		catch (NotFoundException e)
		{
			throw new ExceptionModelFail(e);
		}
		catch (IllegalArgumentException e)
		{
			// this exception can be thrown by setProperty method if value has incorrect type
			throw new ExceptionDBError(e);
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

				for(String n : GetObjectAttributes(uid))
					rep.append(n)
						.append(" : ")
						.append(GetObjectAttribute(uid, n))
						.append(System.lineSeparator());

				throw new ExceptionModelFail("attribute not found: " + name + System.lineSeparator() + rep);
			}
		}
		catch (NotFoundException e)
		{
			throw new ExceptionModelFail(e);
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
		catch (NotFoundException e)
		{
			throw new ExceptionModelFail(e);
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

				for(String n : GetLinkAttributes(uid))
					rep.append(n)
						.append(" : ")
						.append(GetLinkAttribute(uid, n))
						.append(System.lineSeparator());

				throw new ExceptionModelFail("attribute not found: " + name + System.lineSeparator() + rep);
			}
		}
		catch (NotFoundException e)
		{
			throw new ExceptionModelFail(e);
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
		catch (NotFoundException e)
		{
			throw new ExceptionModelFail(e);
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
		catch (NotFoundException e)
		{
			throw new ExceptionModelFail(e);
		}
	}

	@Override
	public List<Uid> GetAllObjects()
	{
		transaction.Read();

		List<Uid> list = new LinkedList<>();

		for(Node node : GlobalGraphOperations.at(graph_db).getAllNodes())
		{
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
		catch (NotFoundException e)
		{
			throw new ExceptionModelFail(e);
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
		catch (NotFoundException e)
		{
			throw new ExceptionModelFail(e);
		}

		return new Uid(rel.getStartNode().getId(), EEntityType.OBJECT);
	}

	@Override
	public List<String> GetObjectAttributes(Uid uid)
	{
		transaction.Read();
		MatchType(uid, EEntityType.OBJECT);

		List<String> attrs = new LinkedList<>();

		Node node = graph_db.getNodeById(uid.getUid());

		for (String name : node.getPropertyKeys())
			attrs.add(name);

		return attrs;
	}

	@Override
	public List<String> GetLinkAttributes(Uid uid)
	{
		transaction.Read();
		MatchType(uid, EEntityType.LINK);

		List<String> attrs = new LinkedList<>();

		Relationship rel = graph_db.getRelationshipById(uid.getUid());

		for (String name : rel.getPropertyKeys())
			attrs.add(name);

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
	public String ExportDot(List<Uid> uids)
	{
		List<Node> nodes = new LinkedList<>();

		for(Uid uid  : uids)
			nodes.add(graph_db.getNodeById(uid.getUid()));

		return ExportDot(nodes);
	}

	private String ExportDot(Iterable<Node> nodes)
	{
		OutputStream out = new ByteArrayOutputStream();

		GraphvizWriter writer = new GraphvizWriter();

		Iterable<RelationshipType> types = GlobalGraphOperations.at(graph_db).getAllRelationshipTypes();

		try
		{
			writer.emit(out, Walker.crosscut(nodes
				, Iterables.toArray(types, RelationshipType.class)));
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
