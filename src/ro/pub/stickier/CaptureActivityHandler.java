/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ro.pub.stickier;

import com.google.zxing.Result;
import ro.pub.stickier.camera.CameraManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

	private static final String TAG = CaptureActivityHandler.class.getSimpleName();

	private final CaptureActivity activity;
	private final DecodeThread decodeThread;
	private State state;

	private enum State {
		PREVIEW,
		SUCCESS,
		DONE
	}

	CaptureActivityHandler(CaptureActivity activity, String characterSet) {
		this.activity = activity;
		decodeThread = new DecodeThread(activity, characterSet);
		decodeThread.start();
		state = State.SUCCESS;

		// Start ourselves capturing previews and decoding.
		CameraManager.get().startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case R.id.auto_focus:
			//Log.d(TAG, "Got auto-focus message");
			// When one auto focus pass finishes, start another. This is the closest thing to
			// continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
			
			if (state == State.PREVIEW) {
				CameraManager.get().requestAutoFocus(this);
			}
			break;
		case R.id.restart_preview:
			Log.d(TAG, "Got restart preview message");
			restartPreviewAndDecode();
			break;
		case R.id.decode_succeeded:
			Log.d(TAG, "Got decode succeeded message");
			state = State.SUCCESS;

			Bundle bundle = message.getData();
			Pair barcodeDimmensions = bundle == null ? null :
				(Pair) bundle.getParcelable(DecodeThread.BARCODE_DIMMENSIONS);
			activity.handleDecode((Result) message.obj, barcodeDimmensions);
			break;
		case R.id.decode_failed:
			// We're decoding as fast as possible, so when one decode fails, start another.
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
			break;
		case R.id.timeout:
			activity.hideExpandActionButton();
			break;
		case R.id.show_waiting:
			activity.showWaiting();
			Log.d(TAG,"Primit mesaj show_waiting");
			break;
		case R.id.hide_waiting:
			activity.hideWaiting();
			Log.d(TAG,"Primit mesaj hide_waiting");
			break;
		}
	}

	public void quitSynchronously() {
		state = State.DONE;
		CameraManager.get().stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
		quit.sendToTarget();
		try {
			decodeThread.join();
		} catch (InterruptedException e) {
			// continue
		}

		// Be absolutely sure we don't send any queued up messages
		removeMessages(R.id.decode_succeeded);
		removeMessages(R.id.decode_failed);
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
		}
	}

}
