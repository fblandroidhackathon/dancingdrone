package de.yadrone.android;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Message;

/**
 * Created by anatriep on 15/11/2014.
 */
public class DroneCommandThread extends HandlerThread {

    private DroneHandler d_handler;

    public DroneCommandThread(String name) {
        super(name);
    }

    public DroneCommandThread(String name, int priority) {
        super(name, priority);
    }

    public synchronized void waitUntilReady(Context context) {
        d_handler = new DroneHandler(context, getLooper());
    }

    public void sendMessage(DroneDanceMessage message) {
        Message msg = new Message();
        msg.obj = message;
        d_handler.sendMessage(msg);
    }
}
