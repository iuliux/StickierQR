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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class CaptureActivity extends Activity implements SurfaceHolder.Callback {

	private static final String TAG = CaptureActivity.class.getSimpleName();

	private SurfaceView surfaceView;
	private OverlayDrawer mOverlay;
	private CaptureActivityHandler handler;
	private ImageView settingsActionButton;
	private ImageView expandActionButton;
	private ImageView expandDelimiter;
	private ImageView mWaitView;
	private boolean hasSurface;
	private String characterSet;
	private Animation mFadeAnim;
	
	private static final float ACCEPT_LIMIT_DISTANCE = 50;
	private Handler mTransitionHandler;
	private TransitionThread transition;
	private float[] lastBarcodePosition;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		CameraManager.init(getApplication());
		hasSurface = false;
		handler = null;
		transition = null;
		lastBarcodePosition = new float[]{-100, -100};

		surfaceView = (SurfaceView) findViewById(R.id.camera_view);
		mOverlay = (OverlayDrawer) findViewById(R.id.drawer);
		settingsActionButton = (ImageView) findViewById(R.id.settings_action_button);
		expandActionButton = (ImageView) findViewById(R.id.expand_action_button);
		expandDelimiter = (ImageView) findViewById(R.id.expand_delimiter);
		mWaitView = (ImageView) findViewById(R.id.waiting);
		mWaitView.setDrawingCacheEnabled(true);
		
		mOverlay.setDrawingCacheEnabled(true);
		
		mFadeAnim = AnimationUtils.loadAnimation(this, R.anim.fade);
		
		settingsActionButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent().setClass(CaptureActivity.this, PreferencesActivity.class));
			}
		});
		mOverlay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent().setClass(CaptureActivity.this, DisplayActivity.class));
			}
		});
	}

	@Override
	public void onResume(){
		super.onResume();

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
		
		if (transition != null) {
			transition.getHandler().removeCallbacks(transition);
			transition.interrupt();
			transition = null;
			mTransitionHandler = null;
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
				handler = new CaptureActivityHandler(this, characterSet);
			}
			
			if (transition == null) {
				transition = new TransitionThread(mOverlay, handler, this);
				transition.start();
				mTransitionHandler = transition.getHandler();
			}
			
			handler.sendEmptyMessage(R.id.auto_focus);
			
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

	public void handleDecode(Result rawResult, Bitmap barcode) {
		ResultHandler resultHandler = new ResultHandler(this, rawResult);
		//historyManager.addHistoryItem(rawResult, resultHandler);

		if (barcode == null) {
			// This is from history -- no saved barcode
			handleDecodeInternally(rawResult, resultHandler, null);
		} else {
			//beepManager.playBeepSoundAndVibrate();
			handleDecodeInternally(rawResult, resultHandler, barcode);
			if (handler != null) {
				handler.sendEmptyMessage(R.id.restart_preview);
			}
		}
	}

	//E apelata cand un tag e gasit
	//Se ocupa cu afisarea rezultatelor gasite
	private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
		if (barcode != null) {
			
			//dimensiunile imaginii initiale
			final int pic_width  = barcode.getWidth();
			final int pic_height = barcode.getHeight();
			//dimensiunile imaginii rotite
			final int h = pic_width;
			final int w = pic_height;
			
			//rata de scalare necesara
			final float ratio = (float)mOverlay.getHeight() / (float)h; 
			Log.d(TAG, "Ratio: " + ratio);
			
			//extrage colturile
			ResultPoint[] points = rawResult.getResultPoints();
			//si le transforma
			//	(rotire 90 de grade)
			//	(scalare cu ratio)
			float[] corner = new float[2];
			corner[0] = ratio*(w-points[1].getY()); //X
			corner[1] = ratio * points[1].getX(); //Y
			
			//calculeaza distanta
			final float currentDistance = (float)
				Math.sqrt((lastBarcodePosition[0] - corner[0]) *
						(lastBarcodePosition[0] - corner[0]) +
					(lastBarcodePosition[1] - corner[1]) * (lastBarcodePosition[1] - corner[1]));
			
			if(currentDistance > ACCEPT_LIMIT_DISTANCE){
				//Only if the new barcode is found far enough make it count
				mTransitionHandler.sendMessage(Message.obtain(
						mTransitionHandler, R.id.new_objective, 
						new PolyState(corner, (String)resultHandler.getDisplayContents())));
				
				lastBarcodePosition[0] = corner[0];
				lastBarcodePosition[1] = corner[1];
			}
			
			mTransitionHandler.sendEmptyMessage(R.id.start_timeout);
			showExpandActionButton();
		}
	}
	
	//Se ocupa de curatarea overlayului
	//Apelata atunci cand pierde tagul
	public void hideExpandActionButton() {
		expandActionButton.setVisibility(View.GONE);
		expandDelimiter.setVisibility(View.GONE);
	}
	
	public void showExpandActionButton(){
		expandActionButton.setVisibility(View.VISIBLE);
		expandDelimiter.setVisibility(View.VISIBLE);
	}
	
	public OverlayDrawer getOverlay(){
		return mOverlay;
	}
	
	public void showWaiting(){
		mWaitView.setVisibility(View.VISIBLE);
		mWaitView.startAnimation(mFadeAnim);
	}
	
	public void hideWaiting(){
		mWaitView.clearAnimation();
		mWaitView.setVisibility(View.GONE);
	}
	
	private void displayFrameworkBugMessageAndExit(String info) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage("["+info+"] "+getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}
}