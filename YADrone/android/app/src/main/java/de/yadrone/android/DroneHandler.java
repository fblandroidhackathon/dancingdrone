package de.yadrone.android;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import de.yadrone.base.IARDrone;

public class DroneHandler extends Handler {

	final IARDrone drone;

	private enum Movement {UP_DOWN, LEFT_RIGHT, SPIN, AHEAD_BACK}

	public DroneHandler(Context context, Looper looper) {
		super(looper);
		YADroneApplication app = (YADroneApplication) context.getApplicationContext();
		drone = app.getARDrone();
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		DroneDanceMessage message = (DroneDanceMessage) msg.obj;
		long duration = message.getEndTimestamp() - message.getStartTimestamp();
		Movement movement = getMovement(message);
		int speed = getMaxAmplitude(message);
		makeFirstHalf(movement, speed);
		try {
			Thread.sleep(duration / 2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		makeSencondHalf(movement, speed);
		try {
			Thread.sleep(duration / 2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void makeFirstHalf(Movement movement, int speed) {

		switch (movement) {
			case UP_DOWN:
				drone.getCommandManager().up(speed);
				break;
			case AHEAD_BACK:
				drone.getCommandManager().backward(speed);
				break;
			case LEFT_RIGHT:
				drone.getCommandManager().goLeft(speed);
				break;
			case SPIN:
				drone.getCommandManager().spinLeft(speed);
				break;
		}
	}

	private void makeSencondHalf(Movement movement, int speed) {

		switch (movement) {
			case UP_DOWN:
				drone.getCommandManager().down(speed);
				break;
			case AHEAD_BACK:
				drone.getCommandManager().forward(speed);
				break;
			case LEFT_RIGHT:
				drone.getCommandManager().goRight(speed);
				break;
			case SPIN:
				drone.getCommandManager().spinRight(speed);
				break;
		}
	}

	private Movement getMovement(DroneDanceMessage message) {
		int max = getMaxAmplitude(message);
		if (max == message.getHighAmplitude()) {
			return Movement.SPIN;
		} else if (max == message.getLowAmplitude()) {
			if (max > 50) {
				return Movement.AHEAD_BACK;
			}
			return Movement.LEFT_RIGHT;
		} else {
			return Movement.UP_DOWN;
		}
	}

	private int getMaxAmplitude(DroneDanceMessage message) {

		return Math.max(message.getHighAmplitude(), Math.max(message.getLowAmplitude(), message.getMidAmplitude()));
	}
}