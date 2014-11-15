package de.yadrone.android;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.MediaController;
import android.widget.TextView;

import java.io.IOException;

public class PlayerActivity extends Activity implements OnPreparedListener, MediaController.MediaPlayerControl {
    private static final String TAG = "AudioPlayer";

    public static final String FILE_URI = "AUDIO_FILE_URI";
    private static final String SONG_NAME = "SONG_NAME";

    private MediaPlayer mMediaPlayer;
    private MediaController mMediaController;
    private Uri mAudioFile;
    private String mSongName;

    private Handler handler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_player);
        mSongName=getIntent().getStringExtra(SONG_NAME);
        mAudioFile = this.getIntent().getParcelableExtra(FILE_URI);
        ((TextView) findViewById(R.id.now_playing_text)).setText(mSongName);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);

        mMediaController = new MediaController(this);

        try {
            mMediaPlayer.setDataSource(this, mAudioFile);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "Could not open file " + mAudioFile + " for playback.", e);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
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