package ro.pub.stickier;

import android.app.Activity;
import android.os.Bundle;

public class SplashActivity extends Activity {

	private static final String TAG = SplashActivity.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
	}
}
