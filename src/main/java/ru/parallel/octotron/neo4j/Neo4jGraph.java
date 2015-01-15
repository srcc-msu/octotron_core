/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.neo4j;

import com.google.common.collect.Iterables;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;
import org.neo4j.shell.ShellSettings;
import org.neo4j.tooling.GlobalGraphOperations;
import org.neo4j.visualization.graphviz.GraphvizWriter;
import org.neo4j.walk.Walker;
import ru.parallel.octotron.core.graph.EGraphType;
import ru.parallel.octotron.core.graph.IGraph;
import ru.parallel.octotron.core.graph.IIndex;
import ru.parallel.octotron.core.primitive.ID;
import ru.parallel.octotron.core.primitive.exception.ExceptionDBError;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * provides access to neo4j BD<br>
 * */
public final class Neo4jGraph implements IGraph
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private GraphDatabaseService graph_db;

	private NinjaTransaction transaction;

	private String db_name;

	private static final int COUNT_THRESHOLD = 10000; // write counts to commit new transaction

	private boolean bootstrap = false;
	private WrappingNeoServerBootstrapper webserver;

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
			.setConfig(ShellSettings.remote_shell_enabled, "true")
			.setConfig(ShellSettings.remote_shell_port, "1337")
			.setConfig(ShellSettings.remote_shell_read_only, "true")
			.newGraphDatabase();

		if(bootstrap)
			DoBootstrap();

		transaction = new NinjaTransaction(graph_db, Neo4jGraph.COUNT_THRESHOLD);
	}

	/**
	 * use this in web browser:
	 * :config maxNeighbours: 200
	 * */
	@SuppressWarnings("deprecation")
	private void DoBootstrap()
	{
		ServerConfigurator config
			= new ServerConfigurator((GraphDatabaseAPI)graph_db);

		config.configuration()
			.setProperty(Configurator.WEBSERVER_ADDRESS_PROPERTY_KEY, "0.0.0.0");

		webserver = new WrappingNeoServerBootstrapper((GraphDatabaseAPI) graph_db, config);
		webserver.start();

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

		if(bootstrap)
			webserver.stop();

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
	private static void MatchType(ID<EGraphType> uid, EGraphType type)
	{
		if(uid.GetType() != type)
			throw new ExceptionModelFail
				("Mismatch entity type for operation");
	}

	public ID<EGraphType> AddObject()
	{
		transaction.Write();

		Node node = graph_db.createNode();

		return new ID<>(node.getId(), EGraphType.OBJECT);
	}

	/**
	 * something is not right here, there is some kind of correlation
	 * with transactions, pay attention
	 * */
	public void DeleteObject(ID<EGraphType> uid)
	{
		transaction.Delete();

		MatchType(uid, EGraphType.OBJECT);

		Node node = graph_db.getNodeById(uid.GetID());
		Iterable<Relationship> rels = node.getRelationships();

		for(Relationship rel : rels)
			rel.delete();

		node.delete();
	}

	/**
	 * something is not right here, there is some kind of correlation
	 * with transactions, pay attention
	 * */
	public void DeleteLink(ID<EGraphType> uid)
	{
		transaction.Delete();

		MatchType(uid, EGraphType.LINK);

		graph_db.getRelationshipById(uid.GetID()).delete();
	}

	public void AddNodeLabel(ID<EGraphType> uid, String label)
	{
		MatchType(uid, EGraphType.OBJECT);

		graph_db.getNodeById(uid.GetID()).addLabel(DynamicLabel.label(label));
	}

	public boolean TestNodeLabel(ID<EGraphType> uid, String label)
	{
		MatchType(uid, EGraphType.OBJECT);

		return graph_db.getNodeById(uid.GetID()).hasLabel(DynamicLabel.label(label));
	}

	public List<ID<EGraphType>> GetAllLabeledNodes(String label)
	{
		List<ID<EGraphType>> list = new LinkedList<>();

		for(Node node : GlobalGraphOperations.at(graph_db)
			.getAllNodesWithLabel(DynamicLabel.label(label)))
		{
			list.add(new ID<>(node.getId(), EGraphType.OBJECT));
		}

		return list;
	}


	final Neo4jIndex index = new Neo4jIndex(this);

	@Override
	public IIndex GetIndex()
	{
		return index;
	}

	public ID<EGraphType> AddLink(ID<EGraphType> source, ID<EGraphType> target, String link_type)
	{
		transaction.Write();

		Node source_node = graph_db.getNodeById(source.GetID());
		Node target_node = graph_db.getNodeById(target.GetID());

		RelationshipType type = DynamicRelationshipType.withName(link_type);

		Relationship relationship = source_node.createRelationshipTo(target_node, type);

		return new ID<>(relationship.getId(), EGraphType.LINK);
	}

	public void SetObjectAttribute(ID<EGraphType> uid, String name, Object value)
	{
		transaction.Write();

		MatchType(uid, EGraphType.OBJECT);

		try
		{
			Node node = graph_db.getNodeById(uid.GetID());
			node.setProperty(name, value);
		}
		catch(NotFoundException e)
		{
			throw new ExceptionModelFail(e);
		}
		catch(IllegalArgumentException e)
		{
			// this exception can be thrown by setProperty method if value has incorrect type
			throw new ExceptionDBError(e);
		}
	}

	public Object GetObjectAttribute(ID<EGraphType> uid, String name)
	{
		transaction.Read();

		MatchType(uid, EGraphType.OBJECT);

		try
		{
			Node node = graph_db.getNodeById(uid.GetID());

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
		catch(NotFoundException e)
		{
			throw new ExceptionModelFail(e);
		}
	}

	public void SetLinkAttribute(ID<EGraphType> uid, String name, Object value)
	{
		transaction.Write();

		MatchType(uid, EGraphType.LINK);

		try
		{
			Relationship rel = graph_db.getRelationshipById(uid.GetID());
			rel.setProperty(name, value);
		}
		catch(NotFoundException e)
		{
			throw new ExceptionModelFail(e);
		}
	}

	public Object GetLinkAttribute(ID<EGraphType> uid, String name)
	{
		transaction.Read();

		MatchType(uid, EGraphType.LINK);

		try
		{
			Relationship rel = graph_db.getRelationshipById(uid.GetID());

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
		catch(NotFoundException e)
		{
			throw new ExceptionModelFail(e);
		}
	}

	private static List<ID<EGraphType>> FromRelIter(Iterator<Relationship> it)
	{
		List<ID<EGraphType>> list = new LinkedList<>();

		while(it.hasNext())
		{
			Relationship rel = it.next();

			list.add(new ID<>(rel.getId(), EGraphType.LINK));
		}

		return list;
	}

	private static List<ID<EGraphType>> FromNodeIter(Iterator<Node> it)
	{
		List<ID<EGraphType>> list = new LinkedList<>();

		while(it.hasNext())
		{
			Node rel = it.next();

			list.add(new ID<>(rel.getId(), EGraphType.OBJECT));
		}

		return list;
	}

	public List<ID<EGraphType>> GetOutLinks(ID<EGraphType> uid)
	{
		transaction.Read();

		MatchType(uid, EGraphType.OBJECT);

		try
		{
			Node node  = graph_db.getNodeById(uid.GetID());

			return FromRelIter
				(node.getRelationships(Direction.OUTGOING).iterator());
		}
		catch(NotFoundException e)
		{
			throw new ExceptionModelFail(e);
		}
	}

	public List<ID<EGraphType>> GetInLinks(ID<EGraphType> uid)
	{
		transaction.Read();

		MatchType(uid, EGraphType.OBJECT);

		try
		{
			Node node  = graph_db.getNodeById(uid.GetID());

			return FromRelIter
				(node.getRelationships(Direction.INCOMING).iterator());
		}
		catch(NotFoundException e)
		{
			throw new ExceptionModelFail(e);
		}
	}

	public List<ID<EGraphType>> GetAllObjects()
	{
		transaction.Read();

		List<ID<EGraphType>> list = new LinkedList<>();

		for(Node node : GlobalGraphOperations.at(graph_db).getAllNodes())
		{
			list.add(new ID<>(node.getId(), EGraphType.OBJECT));
		}

		return list;
	}

	public List<ID<EGraphType>> GetAllLinks()
	{
		transaction.Read();

		List<ID<EGraphType>> list = new LinkedList<>();

		for(Relationship rel : GlobalGraphOperations.at(graph_db).getAllRelationships())
		{
			list.add(new ID<>(rel.getId(), EGraphType.LINK));
		}

		return list;
	}

	public ID<EGraphType> GetLinkTarget(ID<EGraphType> uid)
	{
		transaction.Read();

		MatchType(uid, EGraphType.LINK);

		// this try/catch is used to find out if relationship \\uid exist in \\graphDb
		Relationship rel;

		try
		{
			rel = graph_db.getRelationshipById(uid.GetID());
		}
		catch(NotFoundException e)
		{
			throw new ExceptionModelFail(e);
		}

		return new ID<>(rel.getEndNode().getId(), EGraphType.OBJECT);
	}

	public ID<EGraphType> GetLinkSource(ID<EGraphType> uid)
	{
		transaction.Read();

		MatchType(uid, EGraphType.LINK);

		Relationship rel;
		// this try/catch is used to find out if relationship \\uid exist in \\graphDb
		try
		{
			rel = graph_db.getRelationshipById(uid.GetID());
		}
		catch(NotFoundException e)
		{
			throw new ExceptionModelFail(e);
		}

		return new ID<>(rel.getStartNode().getId(), EGraphType.OBJECT);
	}

	public List<String> GetObjectAttributes(ID<EGraphType> uid)
	{
		transaction.Read();
		MatchType(uid, EGraphType.OBJECT);

		List<String> attrs = new LinkedList<>();

		Node node = graph_db.getNodeById(uid.GetID());

		for(String name : node.getPropertyKeys())
			attrs.add(name);

		return attrs;
	}

	public List<String> GetLinkAttributes(ID<EGraphType> uid)
	{
		transaction.Read();
		MatchType(uid, EGraphType.LINK);

		List<String> attrs = new LinkedList<>();

		Relationship rel = graph_db.getRelationshipById(uid.GetID());

		for(String name : rel.getPropertyKeys())
			attrs.add(name);

		return attrs;
	}

	public void DeleteObjectAttribute(ID<EGraphType> uid, String name)
	{
		transaction.Write();
		MatchType(uid, EGraphType.OBJECT);

		Node node = graph_db.getNodeById(uid.GetID());

		if(!node.hasProperty(name))
			throw new ExceptionModelFail("attribute not found: " + name);

		node.removeProperty(name);
	}

	public void DeleteLinkAttribute(ID<EGraphType> uid, String name)
	{
		transaction.Write();
		MatchType(uid, EGraphType.LINK);

		Relationship rel = graph_db.getRelationshipById(uid.GetID());

		if(!rel.hasProperty(name))
			throw new ExceptionModelFail("attribute not found: " + name);

		rel.removeProperty(name);
	}

	public boolean TestObjectAttribute(ID<EGraphType> uid, String name)
	{
		transaction.Read();
		MatchType(uid, EGraphType.OBJECT);

		boolean res;

		try
		{
			res = graph_db.getNodeById(uid.GetID()).hasProperty(name);
		}
		catch(IllegalStateException e)
		{
			return false;
		}

		return res;
	}

	public boolean TestLinkAttribute(ID<EGraphType> uid, String name)
	{
		transaction.Read();
		MatchType(uid, EGraphType.LINK);

		boolean res;

		try
		{
			res = graph_db.getRelationshipById(uid.GetID()).hasProperty(name);
		}
		catch(IllegalStateException e)
		{
			return false;
		}

		return res;
	}

	/** packet only, get access to inner neo4j index */
	IndexManager GetInnerIndex()
	{
		return graph_db.index();
	}

	public String ExportDot(List<ID<EGraphType>> uids)
	{
		List<Node> nodes = new LinkedList<>();

		for(ID<EGraphType> uid  : uids)
			nodes.add(graph_db.getNodeById(uid.GetID()));

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
		catch(IOException e)
		{
			throw new ExceptionModelFail
				("Traverse during export to .dot file failed: "
					+ e.getMessage());
		}

		return out.toString();
	}
}