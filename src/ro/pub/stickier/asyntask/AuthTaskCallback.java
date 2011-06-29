package ro.pub.stickier.asyntask;

public interface AuthTaskCallback {
	 /**
	  * Callback for when AuthTask finishes the job
	  */
	public void authCallback(boolean success, int result);
}
