/* **************************************************
 Copyright (c) 2012, University of Cambridge
 Neal Lathia, neal.lathia@cl.cam.ac.uk
 Kiran Rachuri, kiran.rachuri@cl.cam.ac.uk

This library was developed as part of the EPSRC Ubhave (Ubiquitous and
Social Computing for Positive Behaviour Change) Project. For more
information, please visit http://www.emotionsense.org

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 ************************************************** */

package com.ubhave.sensormanager.sensors;

import android.content.Context;
import android.content.pm.PackageManager;

import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.config.SensorConfig;

public abstract class AbstractSensor implements SensorInterface
{

	protected boolean isSensing;
	protected final Context applicationContext;
	protected final Object senseCompleteNotify;
	protected final SensorConfig sensorConfig;

	public AbstractSensor(Context context)
	{
		applicationContext = context;
		senseCompleteNotify = new Object();
		sensorConfig = SensorUtils.getDefaultSensorConfig(getSensorType());
	}

	protected static boolean permissionGranted(Context context, String permission)
	{
		return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
	}

	protected abstract boolean startSensing();

	protected abstract void stopSensing();

	protected abstract String getLogTag();

	public boolean isSensing()
	{
		return isSensing;
	}

	public void setSensorConfig(String configKey, Object configValue) throws ESException
	{
		// default parameters can be overridden through this
		// method

		if (!sensorConfig.containsParameter(configKey))
		{
			throw new ESException(ESException.INVALID_SENSOR_CONFIG, "Invalid sensor config, key: " + configKey + " value: " + configValue);
		}

		// check permissions for the config
		if (configKey.equals(SensorConfig.LOCATION_ACCURACY_FINE))
		{
			if (!permissionGranted(applicationContext, "android.permission.ACCESS_FINE_LOCATION"))
			{
				throw new ESException(ESException.PERMISSION_DENIED, "Location Sensor: Fine Location Permission Not Granted!");
			}
		}

		sensorConfig.setParameter(configKey, configValue);
	}

	public Object getSensorConfig(String configKey) throws ESException
	{
		if (sensorConfig.containsParameter(configKey))
		{
			return sensorConfig.getParameter(configKey);
		}
		else
		{
			throw new ESException(ESException.INVALID_SENSOR_CONFIG, "Invalid sensor config, key: " + configKey);
		}
	}

}
