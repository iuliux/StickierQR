package ro.pub.stickier;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import static ro.pub.stickier.Application.*;

/**
 * The main settings activity.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class PreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	private static final String TAG = ro.pub.stickier.PreferencesActivity.class.getSimpleName();

	public static final String KEY_PLAY_BEEP = "preferences_play_beep";
	public static final String KEY_VIBRATE = "preferences_vibrate";
	
	public static final String KEY_USER = "preferences_user";
	public static final String KEY_PASS = "preferences_pass";

	public static final String KEY_HELP_VERSION_SHOWN = "preferences_help_version_shown";
	
	public static final String KEY_CLEAR_CACHE = "preferences_clear_cache";
	public static final String KEY_LOGIN_USER = "login_user";
	public static final String KEY_LOGIN_PASS = "login_pass";

	private Activity context; 
	
	/*
	 * Fields for every setting
	 * 
	 * (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	Preference clearCache;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.preferences);
		context = this;

		PreferenceScreen preferences = getPreferenceScreen();
		preferences.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
		clearCache = findPreference(KEY_CLEAR_CACHE);
		clearCache.setOnPreferenceClickListener( new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
			
				cache.reset();
				Toast.makeText(context, R.string.preferences_cache_cleared, Toast.LENGTH_SHORT).show();
				
				return true;
			}
		});
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

	}

}