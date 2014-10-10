/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.parallel.utils.JsonUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GlobalSettings
{
	private static final int DEFAULT_THREADS = 4;

	public static class Credential
	{
		public final String user;
		public final String password;

		Credential(String user, String password)
		{
			this.user = user;
			this.password = password;
		}
	}

	private String model_name;
	private String model_path;
	private String model_main;

	private String db_path;

	private String sys_path;
	private int threads;

	private boolean db = false;

	private final Map<String, String> log_map = new HashMap<>();
	private final Map<String, String> script_map = new HashMap<>();

	private List<String> object_index = new LinkedList<>();
	private List<String> link_index = new LinkedList<>();

	private int http_port;

	private Credential view_credentials;
	private Credential modify_credentials;
	private Credential control_credentials;

	public GlobalSettings(String json_config)
	{
		ParseSettings(json_config);
	}

	/**
	 * dummy config for testing
	 * */
	public GlobalSettings(int port)
	{
		DummySettings(port);
	}

	public boolean IsDb()
	{
		return db;
	}

	private static Credential GetCredential(JsonObject http_conf, String request)
	{
		JsonObject cfg = JsonUtils.MustPresent(http_conf, request).getAsJsonObject();

		String user = JsonUtils.MustPresent(cfg, "user").getAsString();
		String password = JsonUtils.MustPresent(cfg, "password").getAsString();

		return new Credential(user, password);
	}

	private void DummySettings(int port)
	{
		model_name = "test";
		model_path = "";
		model_main = "";

// --- sys config

		sys_path = "";

		threads = 2;

// --- db config

		db = false;

// --- logging config

// --- scripts config

// --- graph settings

// --- http config
		http_port = port;

// --- credentials config
		view_credentials = new Credential("", "");
		modify_credentials = new Credential("", "");
		control_credentials = new Credential("", "");
	}

	private void ParseSettings(String json_config)
	{
		JsonObject root = new JsonParser().parse(json_config).getAsJsonObject();

// --- model config

		JsonObject model_conf = JsonUtils.MustPresent(root, "model").getAsJsonObject();

		model_name = JsonUtils.MustPresent(model_conf, "name").getAsString();
		model_path = JsonUtils.MustPresent(model_conf, "path").getAsString();
		model_main = JsonUtils.MustPresent(model_conf, "main").getAsString();

// --- sys config

		if(JsonUtils.IsPresent(root, "sys"))
		{
			JsonObject sys_conf = JsonUtils.MustPresent(root, "sys").getAsJsonObject();

			sys_path = JsonUtils.MustPresent(sys_conf, "path").getAsString();

			if(JsonUtils.IsPresent(sys_conf, "threads"))
				threads = JsonUtils.MustPresent(sys_conf, "threads").getAsInt();
			else
				threads = DEFAULT_THREADS;
		}

// --- db config

		if(JsonUtils.IsPresent(root, "db"))
		{
			JsonObject db_conf = JsonUtils.MustPresent(root, "db").getAsJsonObject();

			db_path = JsonUtils.MustPresent(db_conf, "path").getAsString();

			db = true;
		}

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

	public String GetModelName()
	{
		return model_name;
	}
	public String GetModelPath()
	{
		return model_path;
	}
	public String GetModelMain()
	{
		return model_main;
	}

	public String GetSysPath()
	{
		return sys_path;
	}
	public int GetNumThreads()
	{
		return threads;
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

	public Credential GetViewCredentials()
	{
		return view_credentials;
	}

	public Credential GetModifyCredentials()
	{
		return modify_credentials;
	}

	public Credential GetControlCredentials()
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
}
