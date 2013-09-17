package bgsep.model;

import java.util.Observable;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Gyro extends Observable implements SensorEventListener{

	private SensorManager 	sensorManager;
	private Sensor 			sensorAccelerometer;
	private float			indication;
	private boolean			enabled;
	
	/**
	 * Initializes the gyro. Enabled by default.
	 * @param manager An initialized SensorManager.
	 */
	public Gyro(SensorManager manager)
	{
		sensorManager 		= manager;
		sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		registerListener();
		indication 		= 0;
	}
	
	public void registerListener()
	{
		sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void unregisterListener()
	{
		sensorManager.unregisterListener(this);
	}
	
	public float getIndication()
	{
		return indication;
	}
	
	public float getMaximumRange()
	{
		return sensorAccelerometer.getMaximumRange();
	}

	public boolean getEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if(enabled)
			if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			{
				indication = event.values[1];
				setChanged();
				notifyObservers();
			}
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}

}
