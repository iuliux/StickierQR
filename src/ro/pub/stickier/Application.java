package ro.pub.stickier;


import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;


public class Application extends android.app.Application {
	
	static public Cache<String> cache = new Cache<String>();
	
	/*
	 * The HttpClient object shared by the components of the application
	 */
	static public HttpClient client = new DefaultHttpClient();
	
	/*
	 * The username used in the system
	 */
	public static String authUsername;

	
}
