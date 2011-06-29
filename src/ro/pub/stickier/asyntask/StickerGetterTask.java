package ro.pub.stickier.asyntask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import ro.pub.stickier.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import static ro.pub.stickier.Application.*;

public class StickerGetterTask extends AsyncTask<String,Integer,Boolean> {
	
	private Activity caller;
	
	/*
	 * A request is identified by the stickerId
	 */
	private String stickerId;
	/*
	 * Flag to mark weather the resource is already in cache or not
	 */
	boolean cached;
	/*
	 * The resource should be cached using a file
	 */
	//File f; 
	
	/*
	 * Constructor
	 */
	public StickerGetterTask(String _stickerId, Activity _caller){
		stickerId = _stickerId;
		caller = _caller;
		//cached = false; //In case you are unaware of the obvious
	}
	
	/*
	 * Set the cached flag properly
	 * 
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		/*
		 * Get the folder asociated with the current app and create
		 * a file to store the resource
		 */
		//f = new File(caller.getFilesDir(),stickerId + ".jpg");
		
		//@Debugging
		//f = new File("/mnt/sdcard/stickercache/"+stickerId+".jpg");
		
		/*
		 * Check weather the demanded resource is already in cache
		 */
		if (new File(caller.getFilesDir(),stickerId + ".jpg").exists()) {
			cached = true;
		}
		
	}
	
	
	/*
	 * 
	 * Gets the required resource
	 * 
	 */
	@Override
	protected Boolean doInBackground(String... params) {
		
		/*
		 * No need to issue a new request
		 */
		if (cached) return true;
		
		Log.d("REQUEST", "New request for sticker");
		
		HttpPost post = new HttpPost(caller.getString(R.string.sticker_image_feeder_url));
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		/*
		 * Set the parameter
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

			StatusLine line = response.getStatusLine();
			
			
			if (line.getStatusCode() % 100 == 4)
				return false;
			
			HttpEntity entity = response.getEntity();
			
			FileOutputStream output = caller.openFileOutput(stickerId + ".jpg", 0);
			
			entity.writeTo(output);
			
			output.close();
			
		} catch (ClientProtocolException e) {
			Log.e("RESPONSE", "Protocol Problem");
			return false;
		} catch (IOException e) {
			Log.e("RESPONSE", "IO Exception");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
	}
	
}
