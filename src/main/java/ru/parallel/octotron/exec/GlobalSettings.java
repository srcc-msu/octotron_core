/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.tuple.Pair;
import ru.parallel.utils.JsonUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GlobalSettings
{
	private final int hash;

	private String db_name;
	private String db_path;

	private final Map<String, String> log_map = new HashMap<>();
	private final Map<String, String> script_map = new HashMap<>();

	private List<String> object_index;
	private List<String> link_index;

	private int http_port;

	private Pair<String, String> view_credentials = Pair.of("", "");
	private Pair<String, String> modify_credentials = Pair.of("", "");
	private Pair<String, String> control_credentials = Pair.of("", "");

	public GlobalSettings(String json_config)
	{
		hash = json_config.hashCode();
		ParseSettings(json_config);
	}

	private static Pair<String, String> GetCredential(JsonObject http_conf, String request)
	{
		JsonObject cfg = JsonUtils.MustPresent(http_conf, request).getAsJsonObject();

		String user = JsonUtils.MustPresent(cfg, "user").getAsString();
		String pass = JsonUtils.MustPresent(cfg, "password").getAsString();

		return Pair.of(user, pass);
	}

	private void ParseSettings(String json_config)
	{
		JsonObject root = new JsonParser().parse(json_config).getAsJsonObject();

// --- db config
		JsonObject db_conf = JsonUtils.MustPresent(root, "db").getAsJsonObject();

		db_name = JsonUtils.MustPresent(db_conf, "name").getAsString();
		db_path = JsonUtils.MustPresent(db_conf, "path").getAsString();

// --- logging config
		JsonObject logs_conf = JsonUtils.MustPresent(root, "logs").getAsJsonObject();

		for(Entry<String, JsonElement> pair : logs_conf.entrySet())
			log_map.put(pair.getKey(), pair.getValue().getAsString());

// --- scripts config
		JsonObject scripts_conf = JsonUtils.MustPresent(root, "scripts").getAsJsonObject();

		for(Entry<String, JsonElement> pair : scripts_conf.entrySet())
			script_map.put(pair.getKey(), pair.getValue().getAsString());

// --- graph settings
		JsonObject graph_conf = JsonUtils.MustPresent(root, "graph").getAsJsonObject();

		object_index = new LinkedList<>();
		link_index = new LinkedList<>();

		if(graph_conf.get("object_index") != null)
			for(JsonElement elem : JsonUtils.MustPresent(graph_conf, "object_index").getAsJsonArray())
				object_index.add(elem.getAsString());

		if(graph_conf.get("link_index") != null)
			for(JsonElement elem : JsonUtils.MustPresent(graph_conf, "link_index").getAsJsonArray())
				link_index.add(elem.getAsString());


// --- http config
		JsonObject http_conf = JsonUtils.MustPresent(root, "http").getAsJsonObject();

		http_port = JsonUtils.MustPresent(http_conf, "port").getAsInt();

// --- credentials config
		view_credentials = GlobalSettings.GetCredential(http_conf, "view");
		modify_credentials = GlobalSettings.GetCredential(http_conf, "modify");
		control_credentials = GlobalSettings.GetCredential(http_conf, "control");
	}

	public int GetPort()
	{
		return http_port;
	}

	public String GetDbName()
	{
		return db_name;
	}

	public String GetDbPath()
	{
		return db_path;
	}

	public String GetLogByKey(String key)
	{
		return log_map.get(key);
	}

	public String GetScriptByKey(String key)
	{
		return script_map.get(key);
	}

	public Pair<String, String> GetViewCredentials()
	{
		return view_credentials;
	}

	public Pair<String, String> GetModifyCredentials()
	{
		return modify_credentials;
	}

	public Pair<String, String> GetControlCredentials()
	{
		return control_credentials;
	}

	public List<String> GetObjectIndex()
	{
		return object_index;
	}

	public List<String> GetLinkIndex()
	{
		return link_index;
	}

	public int GetHash()
	{
		return hash;
	}
}
