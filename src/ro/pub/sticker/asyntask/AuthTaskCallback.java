package ro.pub.sticker.asyntask;

public interface AuthTaskCallback {
	 /**
	  * Callback for when AuthTask finishes the job
	  */
	public void authCallback(boolean success, int result);
}
