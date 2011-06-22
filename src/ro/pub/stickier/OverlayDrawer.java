package ro.pub.stickier;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class OverlayDrawer extends View {

	private static final String TAG = OverlayDrawer.class.getSimpleName();
	
	private Paint paint;
	private ArrayList<Float> pts;
	
	public OverlayDrawer(Context context, AttributeSet attrs){
		super(context, attrs);
		paint = new Paint();
		paint.setARGB(255, 10, 200, 10);
		paint.setStrokeWidth(10.0f);
		
		pts = new ArrayList<Float>();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if(!pts.isEmpty()){
			float[] ptsArray = new float[pts.size()];
	
			for (int i = 0; i < pts.size(); i++) {
			    Float f = pts.get(i);
			    ptsArray[i] = (f != null ? f : Float.NaN); // Or whatever default you want.
			}
			
			canvas.drawPoints(ptsArray, paint);
			Log.d(TAG, "Overlay ReDrawn!");
		}
	}
	
	public void addPoint(float x, float y){
		pts.add(x);
		pts.add(y);
		invalidate();
		
		Log.d(TAG, "Point added!");
	}
	public void clear(){
		pts.clear();
		invalidate();
		
		Log.d(TAG, "Points list cleared!");
	}
}
