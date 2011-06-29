package ro.pub.stickier;

import ro.pub.stickier.asyntask.AuthTask;
import ro.pub.stickier.asyntask.AuthTaskCallback;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SplashActivity extends Activity implements AuthTaskCallback {

	private static final String TAG = SplashActivity.class.getSimpleName();
	
	private Activity mMe;
	
	private ImageView mIcon;
	private Button mRetry;
	private TextView mTextbox;
	private AuthTask mAuth;
	
	private String mUser;
	private String mPass;
	
	private Animation mFadeAnim;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		mMe = this;
		
		mFadeAnim = AnimationUtils.loadAnimation(this, R.anim.slow_fade);
		mIcon = (ImageView) findViewById(R.id.splash_icon);
		
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
    	mUser = settings.getString(PreferencesActivity.KEY_USER,"vizitator");
    	mPass = settings.getString(PreferencesActivity.KEY_PASS,"vizitator");
    	
    	mRetry = (Button) findViewById(R.id.retry);
    	mTextbox = (TextView) findViewById(R.id.textbox);
    	mTextbox.setText(getString(R.string.splash_authenticating) + " " + mUser);
    	
    	mAuth = new AuthTask(mUser, mPass, this);
    	mAuth.execute();
    	
    	mRetry.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			mTextbox.setText(getString(R.string.splash_retrying));
    			mRetry.setVisibility(View.INVISIBLE);
    			
    			mAuth = new AuthTask(mUser, mPass, (AuthTaskCallback)mMe);
    			mAuth.execute();
    		}
    	});
	}
	
	@Override
	public void onResume(){
		super.onResume();
		mIcon.startAnimation(mFadeAnim);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mIcon.clearAnimation();
	}

	@Override
	public void authCallback(boolean success, int result) {
		
		if(success){
			Toast.makeText(this, getString(R.string.splash_authenticated) + " " + Application.authUsername, Toast.LENGTH_SHORT).show();
			startActivity(new Intent(this, CaptureActivity.class));
			//Now finish the caller activity, so when the user wants to come back he won't see this again
			finish();
		} else {
			mRetry.setVisibility(View.VISIBLE);
			StringBuilder txt = new StringBuilder();
			if(result == 1)
				txt.append(getString(R.string.splash_client_error));
			else if(result == 2)
				txt.append(getString(R.string.splash_client_protocol_error));
			else if(result == 3)
				txt.append(getString(R.string.splash_communication_error));
			txt.append(" ").append(getString(R.string.splash_error));
			mTextbox.setText(txt.toString());
		}
	}
}
