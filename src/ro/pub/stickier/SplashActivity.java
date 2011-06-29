package ro.pub.stickier;

import ro.pub.stickier.asyntask.AuthTask;
import ro.pub.stickier.asyntask.AuthTaskCallback;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SplashActivity extends Activity implements AuthTaskCallback {

	private static final String TAG = SplashActivity.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
    	String user = settings.getString(PreferencesActivity.KEY_USER,"vizitator");
    	String pass = settings.getString(PreferencesActivity.KEY_PASS,"vizitator");
		
		AuthTask auth = new AuthTask(user, pass, this);
		auth.execute();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
	}

	@Override
	public void authCallback(boolean success, int result) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "["+result+"] "+Application.authUsername, Toast.LENGTH_SHORT).show();
		
		startActivity(new Intent(this, CaptureActivity.class));
		
		//Now finish the caller activity, so when the user wants to come back he won't see this again
		finish();
	}
}
