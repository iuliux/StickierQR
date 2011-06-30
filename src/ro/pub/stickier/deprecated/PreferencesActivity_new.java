package ro.pub.stickier.deprecated;

import ro.pub.stickier.R;
import ro.pub.stickier.SplashActivity;
import ro.pub.stickier.R.string;
import ro.pub.stickier.R.xml;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class PreferencesActivity_new extends PreferenceActivity {

	private EditTextPreference pass,username;
	String passChanged, userChanged;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		pass = (EditTextPreference)findPreference(getString(R.string.preferences_login_pass));
		username = (EditTextPreference)findPreference(getString(R.string.preferences_login_user));
		
		passChanged = pass.getSharedPreferences().getString(pass.getKey(), "vizitator");
		userChanged = username.getSharedPreferences().getString(username.getKey(), "vizitator");
		
		pass.setOnPreferenceChangeListener( new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (! passChanged.equals(newValue)){
					askAuthenticate();
					passChanged = (String) newValue;
				}
				return true;
			}
		});
		username.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (! userChanged.equals(newValue)){
					askAuthenticate();
					userChanged = (String) newValue;
				}
				return true;
			}
		});
		
		
	}
	
	private void askAuthenticate(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Would you like to reauthenticate using the new settings ?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   PreferencesActivity_new.this.startActivity(new Intent(PreferencesActivity_new.this,SplashActivity.class));
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//askAuthenticate();
	}
	
	
}
