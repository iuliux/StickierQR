package ro.pub.stickier;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TransitionThread extends Thread {

	private static final String TAG = TransitionThread.class.getSimpleName();

	private State state;

	private enum State {
		NOTHING,
		ANIMATING
	}
	
	private static final long TIMEOUT = 3 * 1000;

	private Handler mHandler;
	private Handler mActivityHandler;
	private OverlayDrawer mGraphics;
	private long mT;
	private long startTime;

	private PolyState mCurrent;
	private PolyState mDest;
	
	private float vX;
	private float vY;
	private float initV;
	private float a;

	public TransitionThread(OverlayDrawer drawer, Handler activityHandler){
		state = State.NOTHING;
		startTime = 0;

		mHandler = new Handler(){
			@Override
			public void handleMessage(Message message) {
				switch (message.what) {
				case R.id.new_objective:
					Log.d(TAG, "Got new_objective message");
					PolyState newPoly = (PolyState) message.obj;
					// Just recieved a new decoded tag which is the new objective
					if (state == State.NOTHING) {
						//schimba starea in Animating
						state = State.ANIMATING;
						//seteaza atat pozitia initiala cat si cea finala la ce primesc in mesaj
						mCurrent = newPoly.copy();
						mDest = newPoly.copy();
					} else if (state == State.ANIMATING) {
						//doar seteaza obiectivul final la ce primesc in mesaj
						mDest = newPoly.copy();
						//seteaza viteza curenta pe (x, y) la viteza initiala
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

						//from Galileo's equation results:  a = -(v0 ^ 2) / (2 * d)
						final float dist = (float)Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
						a = -(initV * initV) / (2.0f * dist);
						//Log.d(TAG, "a = " + a);
					}
					//notez timpul pentru a masura timeoutul
					
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
				mGraphics.recieve(mCurrent);
			}else{
				mGraphics.recieve(null);
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
		Log.d(TAG, "v(x) = " + vX);
		Log.d(TAG, "v(y) = " + vY);
	}

	public Handler getHandler(){
		return mHandler;
	}
}
