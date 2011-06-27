package ro.pub.stickier;

import java.util.ArrayList;

import com.google.zxing.common.Collections;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class OverlayDrawer extends View {

	private static final String TAG = OverlayDrawer.class.getSimpleName();
	
	private Paint paint;
	private Bitmap mBitmap;
	
	//rata de scalare (marire) a pozei afisate
	private float scaleRatio = 0.4f;
	//deplasarea in directia stanga sus a pozei
	private float xOffset;
	private float yOffset;
	private int mBmpHeight;
	private int mBmpWidth;
	
	private PolyState mCurrent;
	
	public OverlayDrawer(Context context, AttributeSet attrs){
		super(context, attrs);
		paint = new Paint();
		paint.setARGB(255, 10, 200, 10);
		paint.setStrokeWidth(8.0f);
		
		//de mutat in functia in care primeste poza de afisat
		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fav);
		mBitmap.prepareToDraw();
		mBmpHeight = mBitmap.getHeight();
		mBmpWidth = mBitmap.getWidth();
		
		xOffset = -(float)((scaleRatio - 1) * mBmpWidth) / 2.0f;
		yOffset = -(float)((scaleRatio - 1) * mBmpHeight) / 2.0f;
		//---
		
		mCurrent = null;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if(mCurrent != null){
			
			Matrix  mMatrix = new Matrix();
			final float[] src = new float[]{
					xOffset, yOffset,
					xOffset, yOffset + mBitmap.getHeight() * scaleRatio,
					xOffset + mBitmap.getWidth() * scaleRatio, yOffset};
			/*final float[] dst = new float[]{
					pts.get(2), pts.get(3), 
					pts.get(0), pts.get(1),
					pts.get(4), pts.get(5)};*/
			
			//voi primi toate coordonatele aici, pentru ca voi calcula pt fiecare punct in parte
			final float[] dst = new float[]{
					mCurrent.ul[0], mCurrent.ul[1], 
					mCurrent.bl[0], mCurrent.bl[1],
					mCurrent.ur[0], mCurrent.ur[1]};
			
			canvas.save();
			mMatrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
            canvas.concat(mMatrix);
			canvas.drawBitmap(mBitmap, 0, 0, paint);
			canvas.restore();
			
			//afiseaza punctul albastru in coltul stanga sus
			/*Paint paint2 = new Paint(paint);
			paint2.setARGB(255, 10, 0, 200);
			canvas.drawPoint(mCurrent.ul[0], mCurrent.ul[1], paint2);

			Log.d(TAG, "ReDrawn!");*/
		}
	}
	
	public void recieve(PolyState poly){
		mCurrent = poly;
		//Log.d(TAG, "Recieved!");
	}
}
