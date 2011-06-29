package ro.pub.stickier.asyntask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.commons.io.IOUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import ro.pub.stickier.DisplayActivity;
import ro.pub.stickier.R;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import static ro.pub.stickier.Application.*;

import ro.pub.stickier.*;

public class CacheUpdaterTask extends AsyncTask<String,String,String> {

	private Activity caller;
	
	/*
	 * A request is identified by the username, password pair 
	 * and the id of the desired resource
	 */
	private String stickerId;
	
	/*
	 * Flag marking weather the feed for the current sticker has been cached
	 * or not 
	 */
	boolean cached;
	
	/*
	 * Constructor
	 */
	public CacheUpdaterTask(Activity _caller,String _stickerId){
		caller = _caller;
		stickerId = _stickerId;
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
	
		if (cache.getCacheId() != null && cache.getCacheId().equals(stickerId)){
			cached = true;
		}
		
	}
	
	
	/*
	 * 
	 * Gets the required resource
	 * 
	 */
	@Override
	protected String doInBackground(String... params) {
		
		/*
		 * No need to issue a new request, the cache contains the feed
		 */
		if (cached) return "Already in cache";
		
		Log.d("FEED D START", "Starting to download the feed");
		
		/*
		 * We reset the cache and set the new cacheId
		 */
		cache.reset();
		cache.setCacheId(stickerId);
		
		Log.d("FEED ID", cache.getCacheId());
		
		HttpResponse response;
		HttpEntity entity;
		String content;
		
		/*
		 * We issue a request for each feed item and then
		 * put it in the cache
		 */			
		feedLoop : for (;;){
		
		Log.d("FEED ITEM REQUEST", "-!-");	
		
		HttpPost post = new HttpPost(caller.getString(R.string.feeder_url));
		
		try {
			
			response = client.execute(post);
			
			StatusLine line = response.getStatusLine();
			
			Log.d("STATUS LINE", ((Integer)line.getStatusCode()).toString());
			
			if (line.getStatusCode() % 100 >= 4)
				break feedLoop;
			
			entity = response.getEntity();
			
			content = IOUtils.toString(entity.getContent());
			
			publishProgress(content);
			
		} catch (ClientProtocolException e) {
			Log.e("RESPONSE ERROR", "Protocol Problem");
			return "Protocol Problem";
		} catch (IOException e) {
			Log.e("RESPONSE ERROR", "IO Exception");
			e.printStackTrace();
			return "IO Exception";
		}}
		
		return "Feed items were downloaded";
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		
		/*
		 * Add the new item to the cache
		 */
		cache.add(values[0]);
		
		if (cache.size() == 1){
			
			((DisplayActivity)caller).initView();
			
		} else {
			if (((DisplayActivity)caller).index < cache.size() - 1)
				((DisplayActivity)caller).next.setEnabled(true);		
		}
		
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
				
		if (cache != null && cache.size() == 0){
			((DisplayActivity)caller).status.setText("Empty feed");
		}
		
		Log.d("CATE", ((Integer)Application.cache.size()).toString());
		
	}
	
}
