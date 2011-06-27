package ro.pub.stickier;

import ro.pub.stickier.camera.CameraManager;
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

	private Handler mHandler;
	private OverlayDrawer mGraphics;
	private long mT;

	private PolyState mCurrent;
	private PolyState mDest;

	public TransitionThread(OverlayDrawer drawer){
		state = State.NOTHING;

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
					}
					break;
				}
			}
		};
		mGraphics = drawer;

		mT = 20;
	}

	@Override
	public void run(){
		//Log.d(TAG, "Runned!");
		if(state == State.ANIMATING){
			//Log.d(TAG, "Animated!");
			compute();
			mGraphics.recieve(mCurrent);
			mGraphics.postInvalidate();
		}
		mHandler.removeCallbacks(this);
		mHandler.postDelayed(this, (long)mT);
	}

	private void compute(){
		final float speed = 30.0f;

		if(mDest.ul[0] > mCurrent.ul[0]){
			mCurrent.ul[0] += speed;
		}else{
			mCurrent.ul[0] -= speed;
		}
		
		if(mDest.ur[0] > mCurrent.ur[0]){
			mCurrent.ur[0] += speed;
		}else{
			mCurrent.ur[0] -= speed;
		}
		
		if(mDest.bl[0] > mCurrent.bl[0]){
			mCurrent.bl[0] += speed;
		}else{
			mCurrent.bl[0] -= speed;
		}
		
		
		if(mDest.ul[1] > mCurrent.ul[1]){
			mCurrent.ul[1] += speed;
		}else{
			mCurrent.ul[1] -= speed;
		}
		
		if(mDest.ur[1] > mCurrent.ur[1]){
			mCurrent.ur[1] += speed;
		}else{
			mCurrent.ur[1] -= speed;
		}
		
		if(mDest.bl[1] > mCurrent.bl[1]){
			mCurrent.bl[1] += speed;
		}else{
			mCurrent.bl[1] -= speed;
		}
	}

	public Handler getHandler(){
		return mHandler;
	}
}
