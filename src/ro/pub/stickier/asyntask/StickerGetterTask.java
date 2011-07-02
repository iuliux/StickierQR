package ro.pub.stickier.asyntask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import ro.pub.stickier.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;

import static ro.pub.stickier.Application.*;

public class StickerGetterTask extends AsyncTask<String,Integer,Bitmap> {
	
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
	//File f
	
	StickerGetterCallback callback;
	
	Bitmap stickerImage = null;
	
	/*
	 * Constructor
	 */
	public StickerGetterTask(String _stickerId, Activity _caller, StickerGetterCallback _callback){
		stickerId = _stickerId;
		caller = _caller;
		callback = _callback;
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
		 * Check whether the demanded resource is already in cache
		 */
		File f;
		if ((f = new File(caller.getFilesDir(),stickerId + ".jpg")).exists()) {
			cached = true;
			try {
				stickerImage = BitmapFactory.decodeStream(new FileInputStream(f));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
			}
		}
		
	}
	
	
	/*
	 * 
	 * Gets the required resource
	 * 
	 */
	@Override
	protected Bitmap doInBackground(String... params) {
		
		/*
		 * No need to issue a new request
		 */
		if (cached) {
			return stickerImage;
		}
		
		//Log.d("REQUEST", "New request for sticker");
		
		HttpPost post = new HttpPost(caller.getString(R.string.sticker_image_feeder_url));
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		/*
		 * Set the parameter
		 */
		pairs.add(new BasicNameValuePair("stickerid", stickerId));
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs));
		} catch (UnsupportedEncodingException e) {
			//Log.e("URL", "URL Encoded Exception");
			return null;
		}
		
		/*
		 * Issue a request
		 */
		try {
			
			HttpResponse response = client.execute(post);

			StatusLine line = response.getStatusLine();
			
			
			if (line.getStatusCode() % 100 == 4)
				return null;
			
			HttpEntity entity = response.getEntity();
			
			FileOutputStream output = caller.openFileOutput(stickerId + ".jpg", 0);
			
			entity.writeTo(output);
			
			output.flush();
			output.close();
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			final int windowFormat = caller.getWindow().getAttributes().format;
			DisplayMetrics metrics = new DisplayMetrics();
			caller.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
			opts.inDither = true;
			opts.inDensity = DisplayMetrics.DENSITY_HIGH;
			opts.inTargetDensity = metrics.densityDpi;
			
			switch(windowFormat){
				case PixelFormat.RGB_565:
				case PixelFormat.OPAQUE:
					opts.inPreferredConfig = Bitmap.Config.RGB_565;
					break;
				case PixelFormat.RGBA_8888:
				default:
					opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
					break;
			}
			stickerImage = BitmapFactory.decodeStream(
					new FileInputStream(new File(caller.getFilesDir(),stickerId + ".jpg")),
					null,
					opts);
					
		} catch (ClientProtocolException e) {
			//Log.e("RESPONSE", "Protocol Problem");
			return null;
		} catch (IOException e) {
			//Log.e("RESPONSE", "IO Exception");
			e.printStackTrace();
			return null;
		}
		
		return stickerImage;
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		callback.getterCallback(result);
		
	}
	
}
