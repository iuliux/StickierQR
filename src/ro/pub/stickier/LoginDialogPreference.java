package ro.pub.stickier;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginDialogPreference extends DialogPreference {
	
	private EditText mUserBox;
	private EditText mPassBox;
    
    private CheckBox mShowchar;
    private Context mContext;
    
    public LoginDialogPreference(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	mContext = context;
    	
    	setDialogLayoutResource(R.layout.login_preferences);
    }

    @Override
    protected void onBindDialogView(View view) {

    	mUserBox = (EditText) view.findViewById(R.id.login_user);
    	mPassBox = (EditText) view.findViewById(R.id.login_pass);

        SharedPreferences pref = getSharedPreferences();

        mUserBox.setText(pref.getString(PreferencesActivity.KEY_USER, ""));
        mPassBox.setText(pref.getString(PreferencesActivity.KEY_PASS, ""));

        super.onBindDialogView(view);
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {

        if(!positiveResult)
            return;

        SharedPreferences.Editor editor = getEditor();
        editor.putString(PreferencesActivity.KEY_USER, mUserBox.getText().toString());
        editor.putString(PreferencesActivity.KEY_PASS, mPassBox.getText().toString());
        editor.commit();

        super.onDialogClosed(positiveResult);
    }
    
    /*@Override
    protected View onCreateDialogView() {
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
    	
    	LinearLayout layout = new LinearLayout(mContext);
    	layout.setOrientation(LinearLayout.VERTICAL);
    	layout.setPadding(10, 10, 10, 10);
    	layout.setBackgroundColor(0xFF000000);

    	mUsername = new TextView(mContext);
    	mUsername.setText("Username:");
    	mUsername.setTextColor(0xFFFFFFFF);
    	mUsername.setPadding(0, 8, 0, 3);

    	mUserBox = new EditText(mContext);
    	mUserBox.setText(settings.getString(PreferencesActivity.KEY_USER, ""));
    	mUserBox.setSingleLine(true); 
    	mUserBox.setSelectAllOnFocus(true);

    	mPassword = new TextView(mContext);
    	mPassword.setText("Password:");
    	mPassword.setTextColor(0xFFFFFFFF);

    	mPassBox = new EditText(mContext);
    	mPassBox.setText(settings.getString(PreferencesActivity.KEY_PASS, ""));
    	mPassBox.setSingleLine(true);
    	mPassBox.setSelectAllOnFocus(true);
    	
    	mShowchar = new CheckBox(mContext);
		mShowchar.setOnCheckedChangeListener(new OnCheckedChangeListener(){
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
    			if(!isChecked) {
    	    		mPassBox.setTransformationMethod(new PasswordTransformationMethod());
    	    	} else {
    	    		mPassBox.setTransformationMethod(new SingleLineTransformationMethod());
    	    	}
    		}
    	});
    	mShowchar.setText("Show Characters");
    	mShowchar.setTextColor(0xFFFFFFFF);
    	mShowchar.setChecked(false);

    	layout.addView(mUsername);
    	layout.addView(mUserBox);
    	layout.addView(mPassword);
    	layout.addView(mPassBox);
    	layout.addView(mShowchar);

    	return layout;
    }*/
    
    /*@Override
    public void onClick(DialogInterface dialog, int which) {
    	// if statement to set save/cancel button roles
    	if (which == -1) {
    		//Toast.makeText(mContext, "Save was clicked\nUsername: " + mUserbox.getText().toString() +"\nPassword is: " + mPassbox.getText().toString(), Toast.LENGTH_SHORT).show();       
    		// Save user preferences
    		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
    		SharedPreferences.Editor editor = settings.edit();
    		editor.putString(PreferencesActivity.KEY_USER, mUserBox.getText().toString());
    		editor.putString(PreferencesActivity.KEY_PASS, mPassBox.getText().toString());
    		editor.commit();
    	}
    	else {
    		//Toast.makeText(mContext, "Cancel was clicked", Toast.LENGTH_SHORT).show();
    	}
    }*/
}
