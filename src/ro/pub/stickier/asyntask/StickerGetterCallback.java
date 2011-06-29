package ro.pub.stickier.asyntask;

import android.graphics.Bitmap;

public interface StickerGetterCallback {
	 /**
	  * Callback for when StickerGetterTask finishes the job
	  */
	public void getterCallback(Bitmap result);
}
