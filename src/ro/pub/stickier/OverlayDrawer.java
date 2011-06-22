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
		paint.setStrokeWidth(8.0f);
		
		pts = new ArrayList<Float>();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if(!pts.isEmpty()){
			float[] ptsArr = new float[pts.size()];
	
			
			for (int i = 0; i < pts.size(); i++) {
			    Float f = pts.get(i);
			    ptsArr[i] = (f != null ? f : Float.NaN); // Or whatever default you want.
			}
			
			Log.d(TAG, "pts.size() = "+pts.size()); 
			if(pts.size() == 6){
				for (int i = 0; i < pts.size(); i++)
					Log.d(TAG, "["+i+"] "+ptsArr[i]);
				
				final float d12 = distance(ptsArr[0], ptsArr[1], ptsArr[2], ptsArr[3]);
				final float d23 = distance(ptsArr[2], ptsArr[3], ptsArr[4], ptsArr[5]);
				final float d13 = distance(ptsArr[0], ptsArr[1], ptsArr[4], ptsArr[5]);
				
				Log.d(TAG, "d 1->2 "+d12);
				Log.d(TAG, "d 2->3 "+d23);
				Log.d(TAG, "d 1->3 "+d13);
				
				//TODO: calculare unghiuri si determinare pe cel de (cat mai) 90 grade
				
				canvas.drawLine(ptsArr[0], ptsArr[1], ptsArr[2], ptsArr[3], paint);
				canvas.drawLine(ptsArr[0], ptsArr[1], ptsArr[4], ptsArr[5], paint);
				canvas.drawLine(ptsArr[4], ptsArr[5], ptsArr[2], ptsArr[3], paint);
			}else{
				canvas.drawPoints(ptsArr, paint);
				Log.d(TAG, "Overlay ReDrawn!");
			}
		}
	}
	
	private float distance(float p1x, float p1y, float p2x, float p2y){
		return (float)Math.sqrt((p1x - p1y)*(p1x - p1y) + (p2x - p2y)*(p2x - p2y));
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
