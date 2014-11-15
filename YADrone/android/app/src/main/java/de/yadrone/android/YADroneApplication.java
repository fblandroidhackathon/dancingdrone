package de.yadrone.android;

import android.app.Application;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;

public class YADroneApplication extends Application
{
	/**
	 * The drone is kept in the application context so that all activities use the same drone instance
	 */
	private IARDrone drone;

	public DroneCommandThread thread;
	
	public void onCreate()
	{
		drone = new ARDrone("192.168.1.1", null); // null because of missing video support on Android
		thread = new DroneCommandThread("DroneThread");
		thread.start();
		thread.waitUntilReady(this);
	}

	public IARDrone getARDrone()
	{
		return drone;
	}
}
