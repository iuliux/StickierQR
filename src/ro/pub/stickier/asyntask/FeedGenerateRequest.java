package ro.pub.stickier.asyntask;

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

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import static ro.pub.stickier.Application.*;
import ro.pub.stickier.*;

public class FeedGenerateRequest extends AsyncTask<String,String,Boolean> {

	
private Activity caller;
	
	/*
	 * An authentification request is identified by the username, password pair 
	 */
	private String stickerId; 
	
	/*
	 * Constructor
	 */
	public FeedGenerateRequest(String _stickerId, Activity _caller){
		stickerId = _stickerId;
		caller = _caller;
	}
	
	/*
	 * 
	 * Does a POST request which should result in a new session with
	 * the parameters properly assigned 
	 * 
	 */
	@Override
	protected Boolean doInBackground(String... params) {
		
		/*
		 * No need to issue a new request
		 */
		//if (cached) return "Already in cache";
		
		Log.d("FEED REQUEST", "New feed request");
		
		HttpPost post = new HttpPost(caller.getString(R.string.feed_generator_url));
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		/*
		 * Set the parameters of the request
		 */
		pairs.add(new BasicNameValuePair("stickerid", stickerId));
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs));
		} catch (UnsupportedEncodingException e) {
			Log.e("URL", "URL Encoded Exception");
			return false;
		}
		
		/*
		 * Issue a request
		 */
		try {
			
			HttpResponse response = client.execute(post);
			
			String status = response.getFirstHeader("status").getValue();
			
			Log.d("HEADER", status);
			
			if (! status.equals("ok"))
				return false;
			
		} catch (ClientProtocolException e) {
			Log.e("RESPONSE", "Protocol Problem");
			return false;
		} catch (IOException e) {
			Log.e("RESPONSE", "IO Exception");
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	    
		((DisplayActivity)caller).getFeedStatus().setText(result.toString());
		
	    new CacheUpdaterTask(caller,"sticker").execute();
		
	}
	
}
