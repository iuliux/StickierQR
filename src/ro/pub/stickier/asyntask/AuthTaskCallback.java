package ro.pub.stickier.asyntask;

/**
 * Response codes:
 * 		Error:
 * 			1 - client error
 * 			2 - client protocol error
 * 			3 - communication error
 * 		Success:
 * 			5 - authentificated
 */

public interface AuthTaskCallback {
	 /**
	  * Callback for when AuthTask finishes the job
	  */
	public void authCallback(boolean success, int result);
}
