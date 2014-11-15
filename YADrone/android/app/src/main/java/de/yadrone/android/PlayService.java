package de.yadrone.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

/**
 * Created by evelina on 15/11/14.
 */
public class PlayService extends Service {

	public static final String FILENAME = "filename";

	private HandlerThread mThread;
	private PlayHandler mHandler;
	private LocalBinder mBinder;


	@Override
	public void onCreate() {
		super.onCreate();
		mBinder = new LocalBinder();
		mThread = new HandlerThread("PlayService");
		mThread.start();
		mHandler = new PlayHandler(mThread.getLooper());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String filename = intent.getStringExtra(FILENAME);
		Message msg = Message.obtain(mHandler);
		msg.obj = filename;
		msg.sendToTarget();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		PlayService getService() {
			return PlayService.this;
		}
	}

	public class PlayHandler extends Handler {

		public PlayHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			String filename = (String) msg.obj;

		}
	}
}
