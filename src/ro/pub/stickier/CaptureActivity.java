package ro.pub.stickier;

import java.io.IOException;
import java.util.Vector;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import ro.pub.stickier.result.ResultHandler;

import ro.pub.stickier.camera.CameraManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CaptureActivity extends Activity implements SurfaceHolder.Callback {

	private static final String TAG = CaptureActivity.class.getSimpleName();

	private OverlayDrawer mDrawer;
	private CaptureActivityHandler handler;
	private ImageView settingsActionButton;
	private ImageView expandActionButton;
	private ImageView expandDelimiter;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	//private HistoryManager historyManager;
	private Result lastResult;
	private String characterSet;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		CameraManager.init(getApplication());
		hasSurface = false;
		handler = null;

		mDrawer = (OverlayDrawer) findViewById(R.id.drawer);
		settingsActionButton = (ImageView) findViewById(R.id.settings_action_button);
		expandActionButton = (ImageView) findViewById(R.id.expand_action_button);
		expandDelimiter = (ImageView) findViewById(R.id.expand_delimiter);

		settingsActionButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent().setClass(CaptureActivity.this, PreferencesActivity.class));
			}
		});
	}

	@Override
	public void onResume(){
		super.onResume();

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.camera_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the camera.
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
			}
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit("IOException");
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializating camera", e);
			displayFrameworkBugMessageAndExit("RuntimeException");
		}
	}

	public Handler getHandler() {
		return handler;
	}

	private void drawResultPoints(Bitmap barcode, Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_image_border));
			paint.setStrokeWidth(3.0f);
			paint.setStyle(Paint.Style.STROKE);
			Rect border = new Rect(2, 2, barcode.getWidth() - 2, barcode.getHeight() - 2);
			canvas.drawRect(border, paint);

			paint.setColor(getResources().getColor(R.color.result_points));
			if (points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1]);
			} else if (points.length == 4 &&
					(rawResult.getBarcodeFormat().equals(BarcodeFormat.UPC_A) ||
							rawResult.getBarcodeFormat().equals(BarcodeFormat.EAN_13))) {
				// Hacky special case -- draw two lines, for the barcode and metadata
				drawLine(canvas, paint, points[0], points[1]);
				drawLine(canvas, paint, points[2], points[3]);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					canvas.drawPoint(point.getX(), point.getY(), paint);
				}
			}
		}
	}

	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b) {
		canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
	}

	public void handleDecode(Result rawResult, Bitmap barcode) {
		//inactivityTimer.onActivity();
		lastResult = rawResult;
		ResultHandler resultHandler = new ResultHandler(this, rawResult);
		//historyManager.addHistoryItem(rawResult, resultHandler);

		if (barcode == null) {
			// This is from history -- no saved barcode
			handleDecodeInternally(rawResult, resultHandler, null);
		} else {
			//beepManager.playBeepSoundAndVibrate();
			drawResultPoints(barcode, rawResult);
			handleDecodeInternally(rawResult, resultHandler, barcode);
			if (handler != null) {
				handler.sendEmptyMessage(R.id.restart_preview);
			}
		}
	}

	private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
		//viewfinderView.setVisibility(View.GONE);

		//ImageView barcodeImageView = (ImageView) findViewById(R.id.actionbar_bottom);
		
		if (barcode != null) {
			//dimensiunile imaginii initiale
			final int pic_width  = barcode.getWidth();
			final int pic_height = barcode.getHeight();
			
			//creeaza matricea de rotire
			/*Matrix mtx = new Matrix();
			mtx.postRotate(90);
			//roteste bitmapul
			Bitmap rotated = Bitmap.createBitmap(barcode, 0, 0, pic_width, pic_height, mtx, true);*/
			
			//barcodeImageView.setImageBitmap(rotated);
			
			//dimensiunile imaginii rotite
			final int h = pic_width;
			final int w = pic_height;
			
			final float ratio = (float)mDrawer.getHeight() / (float)h; 
			Log.d(TAG, "Ratio: " + ratio);
			
			//extrage colturile
			ResultPoint[] points = rawResult.getResultPoints();
			
			mDrawer.clear();
			for(int i = 0; i < points.length; i++)
				mDrawer.addPoint(ratio*(w-points[i].getY()), ratio * points[i].getX());
			
			showExpandActionButton();
		}
		
		Functions.makeToast(resultHandler.getDisplayContents()+"", this);
	}
	
	private void displayFrameworkBugMessageAndExit(String info) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage("["+info+"] "+getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}
	
	public void drawOverlay() {
		hideExpandActionButton();
		mDrawer.clear();
	}
	
	public void showExpandActionButton(){
		expandActionButton.setVisibility(View.VISIBLE);
		expandDelimiter.setVisibility(View.VISIBLE);
	}
	public void hideExpandActionButton(){
		expandActionButton.setVisibility(View.GONE);
		expandDelimiter.setVisibility(View.GONE);
	}
}