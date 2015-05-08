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
	private int db_port = 7474;

	private String sys_path;
	private String log_dir;

	private int threads;

	private boolean db = false;
	private boolean start_silent = false;
	private boolean notify_timeout = false;

	private final Map<String, String> script_map = new HashMap<>();

	private final List<String> object_index = new LinkedList<>();
	private final List<String> link_index = new LinkedList<>();

	private int http_port;
	private String host;

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

//-------- sys config
		sys_path = "";
		log_dir = "";

		threads = 4;

//-------- http config
		http_port = port;
		host = "127.0.0.1";

//-------- credentials config
		view_credentials = new Credential("", "");
		modify_credentials = new Credential("", "");
		control_credentials = new Credential("", "");
	}

	private void ParseSettings(String json_config)
	{
		JsonObject root = new JsonParser().parse(json_config).getAsJsonObject();

//-------- model config
		JsonObject model_conf = JsonUtils.MustPresent(root, "model").getAsJsonObject();

		model_name = JsonUtils.MustPresent(model_conf, "name").getAsString();
		model_path = JsonUtils.MustPresent(model_conf, "path").getAsString();
		model_main = JsonUtils.MustPresent(model_conf, "main").getAsString();

//-------- sys config
		JsonObject sys_conf = JsonUtils.MustPresent(root, "system").getAsJsonObject();

		log_dir = JsonUtils.MustPresent(sys_conf, "log_dir").getAsString();
		sys_path = JsonUtils.MustPresent(sys_conf, "path").getAsString();

		if(JsonUtils.IsPresent(sys_conf, "threads"))
			threads = JsonUtils.MustPresent(sys_conf, "threads").getAsInt();
		else
			threads = DEFAULT_THREADS;

		if(JsonUtils.IsPresent(sys_conf, "start_silent"))
		{
			start_silent = JsonUtils.MustPresent(sys_conf, "start_silent").getAsBoolean();
		}

		if(JsonUtils.IsPresent(sys_conf, "notify_timeout"))
		{
			notify_timeout = JsonUtils.MustPresent(sys_conf, "notify_timeout").getAsBoolean();
		}

//-------- db config
		if(JsonUtils.IsPresent(root, "db"))
		{
			JsonObject db_conf = JsonUtils.MustPresent(root, "db").getAsJsonObject();

			db_path = JsonUtils.MustPresent(db_conf, "path").getAsString();

			if(JsonUtils.IsPresent(db_conf, "port"))
				db_port = JsonUtils.MustPresent(db_conf, "port").getAsInt();

			db = true;
		}

//-------- scripts config
		JsonObject scripts_conf = JsonUtils.MustPresent(root, "scripts").getAsJsonObject();

		for(Entry<String, JsonElement> pair : scripts_conf.entrySet())
			script_map.put(pair.getKey(), pair.getValue().getAsString());

//-------- graph settings
		JsonObject graph_conf = JsonUtils.MustPresent(root, "graph").getAsJsonObject();

		if(graph_conf.get("object_index") != null)
			for(JsonElement elem : JsonUtils.MustPresent(graph_conf, "object_index").getAsJsonArray())
				object_index.add(elem.getAsString());

		if(graph_conf.get("link_index") != null)
			for(JsonElement elem : JsonUtils.MustPresent(graph_conf, "link_index").getAsJsonArray())
				link_index.add(elem.getAsString());

//-------- http config
		JsonObject http_conf = JsonUtils.MustPresent(root, "http").getAsJsonObject();

		http_port = JsonUtils.MustPresent(http_conf, "port").getAsInt();

		if(JsonUtils.IsPresent(http_conf, "host"))
			host = JsonUtils.MustPresent(http_conf, "host").getAsString();
		else
			host = "127.0.0.1";

//-------- credentials config
		view_credentials = GlobalSettings.GetCredential(http_conf, "view");
		modify_credentials = GlobalSettings.GetCredential(http_conf, "modify");
		control_credentials = GlobalSettings.GetCredential(http_conf, "control");
	}

	public int GetPort()
	{
		return http_port;
	}

	public String GetHost()
	{
		return host;
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

	public String GetDbPath()
	{
		return db_path;
	}

	public boolean IsStartSilent() { return start_silent; }

	public boolean IsNotifyTimeout() { return notify_timeout; }

	public boolean IsDb() { return db; }

	public String GetSysPath()
	{
		return sys_path;
	}
	public String GetLogDir()
	{
		return log_dir;
	}
	public int GetNumThreads()
	{
		return threads;
	}

	public String GetScriptByKeyOrNull(String key)
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

	public int GetDbPort()
	{
		return db_port;
	}

}
