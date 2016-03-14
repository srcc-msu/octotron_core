/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations.impl;

import ru.parallel.octotron.core.attributes.impl.Sensor;
import ru.parallel.octotron.core.attributes.impl.Trigger;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.exception.ExceptionModelFail;
import ru.parallel.octotron.exception.ExceptionParseError;
import ru.parallel.octotron.exception.ExceptionSystemError;
import ru.parallel.octotron.bg_services.ServiceLocator;
import ru.parallel.octotron.http.operations.ModelOperation;
import ru.parallel.utils.format.TextString;
import ru.parallel.utils.format.TypedString;

import java.util.Map;

public class Modify
{
	/**
	 * adds a single import value to import queue<br>
	 * */
	public static class activate_op extends ModelOperation
	{
		public activate_op() {super("activate", false);}

		@Override
		public TypedString Execute(Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.StrictParams(params, "name");

			String name = params.get("name");

			ModelEntity target = entities.Only();

			Trigger trigger = target.GetTriggerOrNull(name);

			if(trigger == null)
				return new TextString("trigger not found");

			ServiceLocator.INSTANCE.GetModificationService().Activate(target, name);
			return new TextString("added to modification queue");
		}
	}

	/**
	 * adds a single import value to import queue<br>
	 * */
	public static class import_op extends ModelOperation
	{
		public import_op() {super("import", false);}

		@Override
		public TypedString Execute(Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.StrictParams(params, "name", "value");

			String name = params.get("name");
			String value_str = params.get("value");

			Value value = Value.ValueFromString(value_str);

			ModelEntity target = entities.Only();

			Sensor sensor = target.GetSensorOrNull(name);

			if(sensor == null)
				return new TextString("sensor not found");

			ServiceLocator.INSTANCE.GetModificationService().Import(sensor, value);
			return new TextString("added to modification queue");
		}
	}

	/**
	 * adds a single import value to unchecked import queue<br>
	 * */
	public static class unchecked_import extends ModelOperation
	{
		public unchecked_import() {super("unchecked_import", false);}

		@Override
		public TypedString Execute(Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.StrictParams(params, "name", "value");

			String name = params.get("name");
			String value_str = params.get("value");

			Value value = Value.ValueFromString(value_str);

			ModelEntity target = entities.Only();

			try
			{
				if(ServiceLocator.INSTANCE.GetModificationService().Import(target, name, value, false))
					return new TextString("added to import queue");
				else
					return new TextString("attribute not found, but registered, import skipped");
			}
			catch(ExceptionSystemError e)
			{
				throw new ExceptionModelFail(e);
			}
		}
	}

	/**
	 * set invalid state to all given entities<br>
	 * */
	public static class set_valid extends ModelOperation
	{
		public set_valid() {super("set_valid", true);}

		@Override
		public TypedString Execute(Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.StrictParams(params, "name");

			String name = params.get("name");

			String res = "";

			for(ModelEntity entity : entities)
			{
				Sensor sensor = entity.GetSensorOrNull(name);

				if(sensor != null)
				{
					sensor.SetUserValid();
					sensor.UpdateDependant();

					res += "set sensor to invalid: " + name
						+ " on object: " + entity.GetInfo().GetID()
						+ System.lineSeparator();
				}
				else
					res += "sensor: " + name
						+ " not found on object: " + entity.GetInfo().GetID()
						+ System.lineSeparator();
			}

			return new TextString(res);
		}
	}

	/**
	 * set invalid state to all given entities<br>
	 * */
	public static class set_invalid extends ModelOperation
	{
		public set_invalid() {super("set_invalid", true);}


		@Override
		public TypedString Execute(Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.StrictParams(params, "name");

			String name = params.get("name");

			String res = "";

			for(ModelEntity entity : entities)
			{
				Sensor sensor = entity.GetSensorOrNull(name);

				if(sensor != null)
				{
					sensor.SetUserInvalid();
					sensor.UpdateDependant();

					res += "set sensor to invalid: " + name
						+ " on object: " + entity.GetInfo().GetID()
						+ System.lineSeparator();
				}
				else
					res += "sensor: " + name
						+ " not found on object: " + entity.GetInfo().GetID()
						+ System.lineSeparator();
			}

			return new TextString(res);
		}
	}

	/**
	 * adds the marker to the all given entities<br>
	 * */
	public static class suppress extends ModelOperation
	{
		public suppress() {super("suppress", true);}


		@Override
		public TypedString Execute(Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.RequiredParams(params, "name");
			Utils.AllParams(params, "name", "description");

			String name = params.get("name");

			String description = params.get("description");
			if(description == null)
				description = "";

			String res = "";

			for(ModelEntity entity : entities)
			{
				long AID = ServiceLocator.INSTANCE.GetModelService()
					.SetSuppress(entity, name, true, description);

				if(AID != -1)
				{
					res += "suppressed reaction: " + name
						+ " on object: " + entity.GetInfo().GetID()
						+ System.lineSeparator();
				}
				else
				{
					res += "reaction: " + name
						+ " not found on object: " + entity.GetInfo().GetID()
						+ System.lineSeparator();
				}
			}

			return new TextString(res);
		}
	}


	/**
	 * adds the marker to the all given entities<br>
	 * */
	public static class unsuppress extends ModelOperation
	{
		public unsuppress() {super("unsuppress", true);}

		@Override
		public TypedString Execute(Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.RequiredParams(params, "name");
			Utils.AllParams(params, "name");

			String name = params.get("name");

			String res = "";

			for(ModelEntity entity : entities)
			{
				long AID = ServiceLocator.INSTANCE.GetModelService()
					.SetSuppress(entity, name, false, "");

				if(AID != -1)
				{
					res += "unsuppressed reaction: " + name
						+ " on object: " + entity.GetInfo().GetID()
						+ System.lineSeparator();
				}
				else
				{
					res += "reaction: " + name
						+ " not found on object: " + entity.GetInfo().GetID()
						+ System.lineSeparator();
				}
			}

			return new TextString(res);
		}
	}
}
