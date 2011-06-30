package ro.pub.stickier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class OverlayDrawer extends View implements View.OnTouchListener {

	private static final String TAG = OverlayDrawer.class.getSimpleName();
	
	private Paint paint;
	private Bitmap mBitmap;
	
	//deplasarea in directia stanga sus a pozei
	private float mOffset;
	
	private PolyState mCurrent;
	
	public OverlayDrawer(Context context, AttributeSet attrs){
		super(context, attrs);
		paint = new Paint();
		
		mOffset = 100;
		
		mCurrent = null;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if(mCurrent != null && mBitmap != null){
			
			canvas.save();
			canvas.drawBitmap(mBitmap, mCurrent.x - mOffset, mCurrent.y - mOffset, paint);
			canvas.restore();
			
			//Log.d(TAG, "Redrawn!");
		} else { // TIMEOUT
			
		}
	}
	
	public void recieve(PolyState newState, Bitmap bmp){
		if(bmp != null)
			mBitmap = bmp;
		mCurrent = newState;
		//Log.d(TAG, "Recieved!");
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		
		return true;
	}
}
