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

import com.google.zxing.DecodeHintType;

import android.os.Handler;
import android.os.Looper;

import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

/**
 * This thread does all the heavy lifting of decoding the images.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
final class DecodeThread extends Thread {

  public static final String BARCODE_BITMAP = "barcode_bitmap";

  private final CaptureActivity activity;
  private final Hashtable<DecodeHintType, Object> hints;
  private Handler handler;
  private final CountDownLatch handlerInitLatch;

  DecodeThread(CaptureActivity activity,
               String characterSet) {

    this.activity = activity;
    handlerInitLatch = new CountDownLatch(1);

    hints = new Hashtable<DecodeHintType, Object>(3);

    if (characterSet != null) {
      hints.put(DecodeHintType.CHARACTER_SET, characterSet);
    }
  }

  Handler getHandler() {
    try {
      handlerInitLatch.await();
    } catch (InterruptedException ie) {
      // continue?
    }
    return handler;
  }

  @Override
  public void run() {
    Looper.prepare();
    handler = new DecodeHandler(activity, hints);
    handlerInitLatch.countDown();
    Looper.loop();
  }

}
