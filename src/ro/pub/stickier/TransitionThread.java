package ro.pub.stickier;

import ro.pub.stickier.asyntask.StickerGetterCallback;
import ro.pub.stickier.asyntask.StickerGetterTask;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TransitionThread extends Thread implements StickerGetterCallback {

	private static final String TAG = TransitionThread.class.getSimpleName();

	private State state;

	private enum State {
		NOTHING,
		WAITING,
		ANIMATING
	}
	
	private static final long TIMEOUT = 3 * 1000;

	private Handler mHandler;
	private Handler mActivityHandler;
	private OverlayDrawer mGraphics;
	private long mT;
	private long startTime;
	
	private boolean initialized;

	private PolyState mCurrent;
	private PolyState mDest;
	private Bitmap mBitmap;
	private StickerGetterTask mGetter;
	private StickerGetterCallback meAsCallback;
	private Activity mCaptureActivity;
	
	private float vX;
	private float vY;
	private float initV;
	private float a;

	public TransitionThread(OverlayDrawer drawer, Handler activityHandler, Activity activity){
		state = State.NOTHING;
		startTime = 0;
		mCurrent = null;
		mBitmap = null;
		initialized = false;
		meAsCallback = this;
		//bad practice, but...
		mCaptureActivity = activity;

		mHandler = new Handler(){
			@Override
			public void handleMessage(Message message) {
				switch (message.what) {
				case R.id.new_objective:
					// Just received a new decoded tag which is the new objective
					Log.d(TAG, "Got new_objective message. ["+state+"]");
					PolyState newPoly = (PolyState) message.obj;
					
					if(initialized && state != State.WAITING)
						Log.d(TAG, "Verificare mCurrent.sticker: "+mCurrent);
					if(initialized && state != State.WAITING && !mCurrent.sticker.equals(newPoly.sticker))
						uninitialize();

					if(state == State.WAITING){
						// If download in progress, do nothing, just remember the position
						mCurrent = (PolyState) message.obj;
						break;
					} else {
						//If the sticker needs to be (re)downloaded
						if(!initialized){
							// Then, start initializing it
							Log.d(TAG,"Download starting: "+ newPoly.sticker);
							state = State.WAITING;
							mCurrent = newPoly.copy();
							// Now make CaptureActivity to show the waiting animation
							mActivityHandler.sendEmptyMessage(R.id.show_waiting);

							mGetter = new StickerGetterTask(newPoly.sticker, mCaptureActivity, meAsCallback);
							mGetter.execute();
							break;
						}

						if (state == State.NOTHING) {
							// Changes state in Animating
							state = State.ANIMATING;
							// Sets both initial and final position to what I recieve
							if(newPoly != null){
								mCurrent = newPoly.copy();
								mDest = newPoly.copy();
							}
						} else if (state == State.ANIMATING) {
							// Sets only the objective to what I recieve
							mDest = newPoly.copy();
							// Sets current speed on (x, y) to the initial speed
							final float deltaX = mDest.x - mCurrent.x;
							final float deltaY = mDest.y - mCurrent.y;
							final float slope = Math.abs(deltaY / deltaX);
							float angle = (float)Math.atan(slope);
							if(deltaY > 0){
								if(deltaX < 0)
									angle += (float)Math.PI/2;
							}else{
								if(deltaX < 0)
									angle += (float)Math.PI;
								else
									angle += (float)Math.PI * (3.0f/2.0f);
							}
							vX = (float)Math.cos(angle) * initV;
							vY = (float)Math.sin(angle) * initV;
							//Log.d(TAG, "angle = " + Math.toDegrees(angle));

							// From Galileo's equation results:  a = -(v0 ^ 2) / (2 * d)
							final float dist = (float)Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
							a = -(initV * initV) / (2.0f * dist);
							//Log.d(TAG, "a = " + a);
						}
					}
					break;
					
				//Starts the timeout timer
				case R.id.start_timeout:
					startTime = System.currentTimeMillis();
					break;
				}
			}
		};
		
		mGraphics = drawer;
		mActivityHandler = activityHandler;

		mT = 5;
		vX = 0;
		vY = 0;
		a = 0;
		initV = 3.0f;
	}

	@Override
	public void run(){
		final long beforeComputingTime = System.nanoTime();
		//Log.d(TAG, "Runned! "+ (System.currentTimeMillis() - startTime));
		if(state == State.ANIMATING){
			//Log.d(TAG, "Animated!");
			if(System.currentTimeMillis() - startTime < TIMEOUT){
				if(vX != 0 || vY!=0) compute();
				mGraphics.recieve(mCurrent, null);
			}else{
				mGraphics.recieve(null, null);
				state = State.NOTHING;
				mActivityHandler.sendEmptyMessage(R.id.timeout);
			}
			mGraphics.postInvalidate();
		}
		mHandler.removeCallbacks(this);
		mHandler.postDelayed(this, Math.max(0, (long)mT - (System.nanoTime() - beforeComputingTime)));
	}

	private void compute(){

		mCurrent.x += Math.signum(vX) * Math.max(0, Math.abs(vX) * mT + a * mT * mT / 2.0f);
		mCurrent.y += Math.signum(vY) * Math.max(0, Math.abs(vY) * mT + a * mT * mT / 2.0f);
		
		vX = Math.signum(vX) * Math.max(0, Math.abs(vX) + a * mT);
		vY = Math.signum(vY) * Math.max(0, Math.abs(vY) + a * mT);
		//Log.d(TAG, "v(x) = " + vX);
		//Log.d(TAG, "v(y) = " + vY);
	}
	
	private void uninitialize(){
		initialized = false;
	}

	public Handler getHandler(){
		return mHandler;
	}

	@Override
	public void getterCallback(Bitmap result) {
		mBitmap = result;
		Log.d(TAG, "Downloaded! "+mBitmap);
		
		state = State.NOTHING;
		initialized = true;
		mGraphics.recieve(mCurrent, mBitmap);
		mActivityHandler.sendEmptyMessage(R.id.hide_waiting);
	}
}
