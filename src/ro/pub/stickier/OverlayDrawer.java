package ro.pub.stickier;

import java.util.ArrayList;

import com.google.zxing.common.Collections;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class OverlayDrawer extends View {

	private static final String TAG = OverlayDrawer.class.getSimpleName();
	
	private Paint paint;
	private Bitmap mBitmap;
	private Handler mHandler;
	
	//rata de scalare (marire) a pozei afisate
	private float scaleRatio = 1.0f;
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
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ed202);
		
		Matrix  matrix = new Matrix();
		matrix.postScale(scaleRatio, scaleRatio);
		
		mBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
		
		mBitmap.prepareToDraw();
		mBmpHeight = mBitmap.getHeight();
		mBmpWidth = mBitmap.getWidth();
		
		/*xOffset = -(float)((scaleRatio - 1) * mBmpWidth) / 2.0f;
		yOffset = -(float)((scaleRatio - 1) * mBmpHeight) / 2.0f;*/
		//---
		
		xOffset = 100;
		yOffset = 100;
		
		mCurrent = null;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if(mCurrent != null){
			
			canvas.save();
			canvas.drawBitmap(mBitmap, mCurrent.x - xOffset, mCurrent.y - yOffset, paint);
			canvas.restore();
			
			//afiseaza punctul albastru in coltul stanga sus
			/*Paint paint2 = new Paint(paint);
			paint2.setARGB(255, 10, 0, 200);
			canvas.drawPoint(mCurrent.ul[0], mCurrent.ul[1], paint2);

			Log.d(TAG, "ReDrawn!");*/
		} else { // TIMEOUT
			
		}
	}
	
	public void recieve(PolyState poly){
		mCurrent = poly;
		//Log.d(TAG, "Recieved!");
	}
}
