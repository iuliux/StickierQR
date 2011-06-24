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
	private ArrayList<Float> pts;
	
	//rata de scalare (marire) a pozei afisate
	private float scaleRatio = 0.4f;
	//deplasarea in directia stanga sus a pozei
	private float xOffset;
	private float yOffset;
	private int mBmpHeight;
	private int mBmpWidth;
	
	private float lastPtX;
	private float lastPtY;
	private float destPtX;
	private float destPtY;
	private String lastSticker;
	
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
		
		lastSticker = "";
		
		pts = new ArrayList<Float>();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if(!pts.isEmpty()){
			/*for (int i = 0; i < pts.size(); i++)
				Log.d(TAG, "["+i+"] "+ptsArr[i]);*/
			
			/*final float m = (ptsArr[4] - ptsArr[2]) / (ptsArr[5] - ptsArr[3]);
			final float rAng = (float)Math.toDegrees(Math.atan(m));
			Log.d(TAG, "rAng = "+rAng);*/
			
			Matrix  mMatrix = new Matrix();
			final float[] src = new float[]{
					xOffset, yOffset,
					xOffset, yOffset + mBitmap.getHeight() * scaleRatio,
					xOffset + mBitmap.getWidth() * scaleRatio, yOffset};
			final float[] dst = new float[]{
					pts.get(2), pts.get(3), 
					pts.get(0), pts.get(1),
					pts.get(4), pts.get(5)};
			
			canvas.save();
			mMatrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
            canvas.concat(mMatrix);
			canvas.drawBitmap(mBitmap, 0, 0, paint);
			canvas.restore();
			
			//afiseaza punctul albastru in coltul stanga sus
			/*Paint paint2 = new Paint(paint);
			paint2.setARGB(255, 10, 0, 200);
			canvas.drawPoint(pts.get(2), pts.get(3), paint2);*/
		}
	}
	
	/*private float distance(float p1x, float p1y, float p2x, float p2y){
		return (float)Math.sqrt((p1x - p1y)*(p1x - p1y) + (p2x - p2y)*(p2x - p2y));
	}*/
	
	public void addPoints(float[] points){
		for(int i = 0; i < points.length; i++){
			pts.add(points[i]);
		}
		invalidate();
		
		Log.d(TAG, "Points added!");
	}
	public void clear(){
		pts.clear();
		invalidate();
		
		Log.d(TAG, "Points list cleared!");
	}
}
