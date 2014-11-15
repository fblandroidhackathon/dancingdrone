package de.yadrone.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.musicg.wave.Wave;
import com.musicg.wave.extension.Spectrogram;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by evelina on 15/11/14.
 */
public class PlayerService extends Service {

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
	public void onDestroy() {
		super.onDestroy();
		mThread.quit();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		PlayerService getService() {
			return PlayerService.this;
		}
	}

	public void play(String filename) {
		Message msg = Message.obtain(mHandler);
		msg.obj = filename;
		msg.sendToTarget();
	}

	public class PlayHandler extends Handler {

		public PlayHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			String filename = (String) msg.obj;
			try {
				InputStream in = getAssets().open(filename);
				Wave wave = new Wave(in);

				Spectrogram spec = wave.getSpectrogram(256, 0);
				//spectrogram[time][frequency]=intensity
				double[][] data = spec.getNormalizedSpectrogramData();

				int[][] groups = new int[data.length][3];

				for(int i = 0; i < data.length; ++i){
					double[] frame = data[i];

					double sumBase =  0;
					for(int j = 0; j < frame.length/3; ++j){
						sumBase += frame[j];
					}
					int avgBase = (int)((sumBase/(frame.length/3))*100);

					double sumMid =  0;
					for(int j = frame.length/3; j < (frame.length*2)/3; ++j){
						sumMid += frame[j];
					}
					int avgMid = (int)((sumMid/(frame.length/3))*100);

					double sumPitch =  0;
					for(int j = (frame.length*2)/3; j < frame.length; ++j){
						sumPitch += frame[j];
					}
					int avgPitch = (int)((sumPitch/(frame.length/3))*100);

					groups[i][0] = avgBase;
					groups[i][1] = avgMid;
					groups[i][2] = avgPitch;
				}

				Log.d("WAVE",groups.toString());

			} catch (IOException e) {
				Log.e("WAVE", e.getMessage(), e);
			}
		}
	}
}
