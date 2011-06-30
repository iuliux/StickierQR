/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ro.pub.stickier;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is an example of a custom preference type. The preference counts the
 * number of clicks it has received and stores/retrieves it from the storage.
 */
public class LoginPreference extends Preference {
    private Context mContext;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private LayoutInflater inflater;
    private View layout;
    
    private Dialog dialog;
    private SharedPreferences prefs;
    
    /*
     * Get the username adn password fields
     */
    EditText username, password;
	/*
	 * Get the three control buttons
	 */
    Button saveAndLogin, save, cancel;
    
    // This is the constructor called by the inflater
    public LoginPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
   
        
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        //Context mContext = getApplicationContext();
        dialog = new Dialog(mContext);

        dialog.setContentView(R.layout.login_preferences);
        //dialog.setTitle("Custom Dialog");
        
        prefs = context.getSharedPreferences("userpass", Context.MODE_PRIVATE);
        
        cancel = (Button) dialog.findViewById(R.id.login_cancel_button);
        cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				//Toast.makeText(dialog.getContext(), prefs.getString("uusername", "noting"), Toast.LENGTH_SHORT).show();
				dialog.cancel();	
				
			}
		});
        
        username = (EditText) dialog.findViewById(R.id.login_user);
        username.setText(prefs.getString(mContext.getString(R.string.preferences_login_user), "Undefined"));
        password = (EditText) dialog.findViewById(R.id.login_user);
        password.setText(prefs.getString(mContext.getString(R.string.preferences_login_pass), "Undefined"));

        save =  (Button) dialog.findViewById(R.id.login_save_button);
        save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				SharedPreferences.Editor edit = prefs.edit();
				
				edit.putString("username", username.getText().toString());
				edit.putString("password", password.getText().toString());
				edit.commit();
				
				dialog.cancel();
				
				//Log.d("CE A SCRIS", prefs.getString("uusername", "nimic"));
				
			}
		});
        
        saveAndLogin =  (Button) dialog.findViewById(R.id.login_now_button);
        saveAndLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				SharedPreferences.Editor edit = prefs.edit();
				
				edit.putString(mContext.getString(R.string.preferences_login_user), username.getText().toString());
				edit.putString(mContext.getString(R.string.preferences_login_pass), password.getText().toString());
				edit.commit();
				
				mContext.startActivity(new Intent(mContext,SplashActivity.class));
				
			}
		});
        
        
        
        
        
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        
//        layout = inflater.inflate(R.layout.login_preferences,
//                (ViewGroup) view.findViewById(R.id.login_dialog_layout));
//        
//        builder = new AlertDialog.Builder(mContext);
//        builder.setView(layout);
//        alertDialog = builder.create();
//        
//        username = (EditText) alertDialog.findViewById(R.id.login_user);
//        password = (EditText) alertDialog.findViewById(R.id.login_user);
//        
//        save =  (Button) alertDialog.findViewById(R.id.login_save_button);
//        cancel =  (Button) alertDialog.findViewById(R.id.login_now_button);
//        saveAndLogin =  (Button) alertDialog.findViewById(R.id.login_cancel_button);
//        
//        

    }

    @Override
    protected void onClick() {
    	//alertDialog.show();
    	
    	dialog.show();
    	
    }
    
    
}
