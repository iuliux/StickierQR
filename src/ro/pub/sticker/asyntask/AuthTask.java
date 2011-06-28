package ro.pub.sticker.asyntask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import ro.pub.stickier.*;

import android.os.AsyncTask;
import android.util.Log;

import static ro.pub.stickier.Application.*;

public class AuthTask extends AsyncTask<String,String,Integer> {

	
private AuthTaskCallback callback;
	
	/*
	 * An authentification request is identified by the username, password pair 
	 */
	private String username, password; 
	
	boolean authentificated;
	
	/*
	 * Constructor
	 */
	public AuthTask(String _username, String _password, AuthTaskCallback _callback){
		username = _username;
		password = _password;
		callback = _callback;
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
	 * Response codes:
	 * 		Error:
	 * 			1 - client error
	 * 			2 - client protocol error
	 * 			3 - communication error
	 * 		Success:
	 * 			5 - authentificated
	 */
	@Override
	protected Integer doInBackground(String... params) {
		
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
			return 1; //client error
		}
		
		/*
		 * Issue a request
		 */
		try {
			
			HttpResponse response = client.execute(post);
			
			authUsername = response.getFirstHeader("username").getValue();
			
		} catch (ClientProtocolException e) {
			Log.e("RESPONSE", "Protocol Problem");
			return 2; //client protocol error
		} catch (IOException e) {
			Log.e("RESPONSE", "IO Exception");
			return 3; //Communication error
		}
		
		return 5; //Authentificated
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		
		final boolean ok = (result >= 5); //outside the error codes area
		
		callback.authCallback(ok, result);
	}
	
}
