package ro.pub.sticker.asyntask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import ro.pub.sticker.*;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import static ro.pub.stickier.Application.*;

public class AuthTask extends AsyncTask<String,String,String> {

	
private Activity caller;
	
	/*
	 * An authentification request is identified by the username, password pair 
	 */
	private String username, password; 
	
	boolean authentificated;
	
	/*
	 * Constructor
	 */
	public AuthTask(String _username, String _password, Activity _caller){
		username = _username;
		password = _password;
		caller = _caller;
	}
	
	/*
	 * Set the authentificated flag properly
	 * 
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		if (authUsername != null)
			authentificated = true;
		
	}
	
	
	/*
	 * 
	 * Does a POST request which should result in a new session with
	 * the parameters properly assigned 
	 * 
	 */
	@Override
	protected String doInBackground(String... params) {
		
		/*
		 * No need to issue a new request
		 */
		//if (cached) return "Already in cache";
		
		Log.d("SESSION REQUEST", "New session request");
		
		HttpPost post = new HttpPost("https://and2sticker.appspot.com//newSession");
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		/*
		 * Set the parameters of the request
		 */
		pairs.add(new BasicNameValuePair("username", username));
		pairs.add(new BasicNameValuePair("password", password));
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs));
		} catch (UnsupportedEncodingException e) {
			Log.e("URL", "URL Encoded Exception");
			return "Client error";
		}
		
		/*
		 * Issue a request
		 */
		try {
			
			HttpResponse response = client.execute(post);
			
			authUsername = response.getFirstHeader("username").getValue();
			
		} catch (ClientProtocolException e) {
			Log.e("RESPONSE", "Protocol Problem");
			return "Client protocol error";
		} catch (IOException e) {
			Log.e("RESPONSE", "IO Exception");
			return "Communication error";
		}
		
		return "Authentificated with username "+ authUsername;
	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		
	}
	
}
