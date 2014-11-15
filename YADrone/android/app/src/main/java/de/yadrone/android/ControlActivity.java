package de.yadrone.android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Random;

import de.yadrone.base.IARDrone;

public class ControlActivity extends Activity {

	public LinkedList<Message> messages = new LinkedList<Message>();

	private final Random rand = new Random();

	private DroneCommandThread thread;

	private PlayService mPlayService;
	private ServiceConnection mConnection;

	private boolean mIsBound;
    private IARDrone mDrone;


    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);

		initButtons();
		Toast.makeText(this, "Touch and hold the buttons", Toast.LENGTH_SHORT).show();

		thread = ((YADroneApplication) getApplicationContext()).thread;

		mConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
				mPlayService = ((PlayService.LocalBinder)iBinder).getService();
			}

			@Override
			public void onServiceDisconnected(ComponentName componentName) {
				mPlayService = null;
			}
		};
		doBindService();
	}

	void doBindService() {
		// Establish a connection with the service.  We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).
		bindService(new Intent(this, PlayService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}


    public void sendMockMessages(DroneCommandThread thread) {
        //sendRandomMovements(thread);
        sendCoreography(thread);
    }

    private void sendCoreography(DroneCommandThread thread) {
        int length = 800;

        for (int i = 0; i < 2; i++) {
            DroneDanceMessage msg = createMsg(length, 100, 10, 10);
            thread.sendMessage(msg);
        }
        for (int i = 0; i < 2; i++) {
            DroneDanceMessage msg = createMsg(length, 10, 100, 10);
            thread.sendMessage(msg);
        }
        for (int i = 0; i < 2; i++) {
            DroneDanceMessage msg = createMsg(length, 10, 10, 100);
            thread.sendMessage(msg);
        }
        mDrone.getCommandManager().landing();

    }

    private DroneDanceMessage createMsg(int length, int high, int low, int mid) {
        return new DroneDanceMessage.Builder()
                .startTimestamp(System.currentTimeMillis())
                .endTimestamp(System.currentTimeMillis() + length)
                .highAmplitude(high)
                .lowAmplitude(low)
                .midAmplitude(mid)
                .build();
    }


    private void sendRandomMovements(DroneCommandThread thread) {
        for (int i = 0; i < 10; i++) {
            DroneDanceMessage msg = new DroneDanceMessage.Builder()
                    .startTimestamp(System.currentTimeMillis())
                    .endTimestamp(System.currentTimeMillis() + (300 + rand.nextInt(700)))
                    .highAmplitude(20 + rand.nextInt(81))
                    .lowAmplitude(20 + rand.nextInt(81))
                    .midAmplitude(20 + rand.nextInt(81))
                    .build();
            thread.sendMessage(msg);
        }
    }

	private void initButtons() {
		YADroneApplication app = (YADroneApplication) getApplication();
        mDrone = app.getARDrone();

		Button forward = (Button) findViewById(R.id.cmd_forward);
		forward.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					mDrone.getCommandManager().forward(20);
				else if (event.getAction() == MotionEvent.ACTION_UP)
					mDrone.hover();

				return true;
			}
		});

		Button backward = (Button) findViewById(R.id.cmd_backward);
		backward.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					mDrone.getCommandManager().backward(20);
				else if (event.getAction() == MotionEvent.ACTION_UP)
					mDrone.hover();

				return true;
			}
		});


		Button left = (Button) findViewById(R.id.cmd_left);
		left.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					mDrone.getCommandManager().goLeft(20);
				else if (event.getAction() == MotionEvent.ACTION_UP)
					mDrone.hover();

				return true;
			}
		});


		Button right = (Button) findViewById(R.id.cmd_right);
		right.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					mDrone.getCommandManager().goRight(20);
				else if (event.getAction() == MotionEvent.ACTION_UP)
					mDrone.hover();

				return true;
			}
		});

		Button up = (Button) findViewById(R.id.cmd_up);
		up.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					mDrone.getCommandManager().up(40);
				else if (event.getAction() == MotionEvent.ACTION_UP)
					mDrone.hover();

				return true;
			}
		});

		Button down = (Button) findViewById(R.id.cmd_down);
		down.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					mDrone.getCommandManager().down(40);
				else if (event.getAction() == MotionEvent.ACTION_UP)
					mDrone.hover();

				return true;
			}
		});


		Button spinLeft = (Button) findViewById(R.id.cmd_spin_left);
		spinLeft.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					mDrone.getCommandManager().spinLeft(20);
				else if (event.getAction() == MotionEvent.ACTION_UP)
					mDrone.hover();

				return true;
			}
		});


		Button spinRight = (Button) findViewById(R.id.cmd_spin_right);
		spinRight.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					mDrone.getCommandManager().spinRight(20);
				else if (event.getAction() == MotionEvent.ACTION_UP)
					mDrone.hover();

				return true;
			}
		});

		final Button landing = (Button) findViewById(R.id.cmd_landing);
		landing.setOnClickListener(new OnClickListener() {
			boolean isFlying = false;

			public void onClick(View v) {
				if (!isFlying) {
					mDrone.takeOff();
					landing.setText("Landing");
				} else {
					mDrone.landing();
					landing.setText("Take Off");
				}
				isFlying = !isFlying;
			}
		});

		Button freeze = (Button) findViewById(R.id.cmd_freeze);
		freeze.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mDrone.freeze();
			}
		});

		Button emergency = (Button) findViewById(R.id.cmd_emergency);
		emergency.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mDrone.reset();
			}
		});

		findViewById(R.id.cmd_dance).setOnClickListener(new OnClickListener() {
			boolean isDancing = false;

			@Override
			public void onClick(View view) {
				if (!isDancing) {
					dance();
				}
			}
		});
	}

	private void dance() {
		sendMockMessages(thread);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_control, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
			case R.id.menuitem_navdata:
				i = new Intent(this, NavDataActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				return true;
			case R.id.menuitem_main:
				i = new Intent(this, MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				return true;
//	    	case R.id.menuitem_video:
//	    		i = new Intent(this, VideoActivity.class);
//	    		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//	    		startActivity(i);
//		        return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}


}
