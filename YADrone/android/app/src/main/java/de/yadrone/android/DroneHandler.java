package de.yadrone.android;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import de.yadrone.base.IARDrone;

public class DroneHandler extends Handler {

    final IARDrone drone;

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
        drone.getCommandManager().goLeft(message.getHighAmplitude());
        try {
            Thread.sleep(duration / 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drone.getCommandManager().goRight(message.getHighAmplitude());
        try {
            Thread.sleep(duration / 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}