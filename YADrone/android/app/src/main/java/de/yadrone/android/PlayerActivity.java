package de.yadrone.android;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;

import de.yadrone.android.DroneDanceMessage.Builder;
import de.yadrone.base.IARDrone;

public class PlayerActivity extends Activity implements OnPreparedListener, MediaController.MediaPlayerControl {
	private static final String TAG = "AudioPlayer";

	public static final String FILE_URI = "AUDIO_FILE_URI";
	public static final String SONG_NAME = "SONG_NAME";

	private MediaPlayer mMediaPlayer;
	private MediaController mMediaController;
	private Uri mAudioFile;
	private String mSongName;
	private Visualizer mVisualizer;

	private Handler handler = new Handler();

	private long lastTimestamp;
	private final static long THREASHOLD = 1 * 1000;
	private final static int BASE_MIN = 0;
	private final static int BASE_MAX = 49;
	private final static int MID_MIN=50;
	private final static int MID_MAX=199;
	private final static int PITCH_MIN=250;
	private final static int PITCH_MAX=256;

	private double avgBase = 0.0;
	private double avgMid = 0.0;
	private double avgPitch = 0.0;
	private IARDrone mDrone;
	private Handler mHandler;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler=new Handler();
		YADroneApplication app = (YADroneApplication) getApplication();
		mDrone = app.getARDrone();
		mDrone.start();
		setContentView(R.layout.audio_player);
		mSongName = getIntent().getStringExtra(SONG_NAME);
		mAudioFile = this.getIntent().getParcelableExtra(FILE_URI);
		((TextView) findViewById(R.id.now_playing_text)).setText(mSongName);

		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(this);

		mMediaController = new MediaController(this);
		mMediaController.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
		try {
			mMediaPlayer.setDataSource(this, mAudioFile);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (IOException e) {
			Log.e(TAG, "Could not open file " + mAudioFile + " for playback.", e);
		}
		mDrone.takeOff();
		mDrone.up();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mDrone.freeze();
				mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
				mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

				mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
					public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
						double sumBase =  0;
						for(int j = 0; j < bytes.length/3; ++j){
							sumBase += bytes[j];
						}
						int base = (int)((sumBase/(bytes.length/3)));
						avgBase = avgBase > 0 ? (avgBase+base) / 2 : base;

						double sumMid =  0;
						for(int j = bytes.length/3; j < (bytes.length*2)/3; ++j){
							sumMid += bytes[j];
						}
						int mid = (int)((sumMid/(bytes.length/3)));
						avgMid = avgMid > 0 ? (avgMid+mid) / 2 : mid;

						double sumPitch =  0;
						for(int j = (bytes.length*2)/3; j < bytes.length; ++j){
							sumPitch += bytes[j];
						}
						int pitch = (int)((sumPitch/(bytes.length/3)));
						avgPitch = avgPitch > 0 ? (avgPitch+pitch) / 2 : pitch;

						if(System.currentTimeMillis() - lastTimestamp > THREASHOLD){
							DroneDanceMessage msg = new Builder()
									.midAmplitude(clean(avgMid))
									.highAmplitude(clean(avgPitch))
									.lowAmplitude(clean(avgBase))
									.startTimestamp( System.currentTimeMillis())
									.endTimestamp( System.currentTimeMillis()+THREASHOLD)
									.build();

							Log.i("WAVE", msg.toString());

							((YADroneApplication) getApplicationContext()).thread.sendMessage(msg);

							lastTimestamp = System.currentTimeMillis();
							avgPitch = 0;
							avgBase = 0;
							avgMid = 0;
						}

					}
					public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
					}
				}, Visualizer.getMaxCaptureRate() / 2, true, false);
				mVisualizer.setEnabled(true);

				mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mediaPlayer) {
						mVisualizer.setEnabled(false);
						mDrone.freeze();
						mDrone.landing();
					}
				});
			}
		},6000);

	}

	private int clean(double val){
		return (int) Math.min(Math.abs(val), 100);
	}


	@Override
	protected void onStop() {
		super.onStop();
		mVisualizer.setEnabled(false);
		mDrone.freeze();
		mDrone.landing();
		mVisualizer.release();
		mMediaController.hide();
		mMediaPlayer.stop();
		mMediaPlayer.release();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//the MediaController will hide after 3 seconds - tap the screen to make it appear again
		mMediaController.show();
		return false;
	}

	//--MediaPlayerControl methods----------------------------------------------------
	public void start() {
		mMediaPlayer.start();
	}

	public void pause() {
		mMediaPlayer.pause();
		mVisualizer.setEnabled(false);
		mDrone.freeze();
		mDrone.landing();
		if (isFinishing()) {
			mVisualizer.release();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	public int getCurrentPosition() {
		return mMediaPlayer.getCurrentPosition();
	}

	public void seekTo(int i) {
		mMediaPlayer.seekTo(i);
	}

	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}

	public int getBufferPercentage() {
		return 0;
	}

	public boolean canPause() {
		return true;
	}

	public boolean canSeekBackward() {
		return true;
	}

	public boolean canSeekForward() {
		return true;
	}

	public void onPrepared(MediaPlayer mediaPlayer) {
		mMediaController.setMediaPlayer(this);
		mMediaController.setAnchorView(findViewById(R.id.main_audio_view));

		handler.post(new Runnable() {
			public void run() {
				mMediaController.setEnabled(true);
				mMediaController.show();
			}
		});
	}
}

