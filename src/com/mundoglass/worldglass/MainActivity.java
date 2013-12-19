package com.mundoglass.worldglass;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.widget.RelativeLayout;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

/**
 * @author ander.martinez@mundoglass.es
 * @see www.mundoglass.es
 */
public class MainActivity extends Activity {
	
	private final static String TAG = "MainActivity";
	private GestureDetector mGestureDetector;
	private RelativeLayout mLayout;

	static final int SPEECH_REQUEST = 0;
	public static final int STREAM_REQUEST = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLayout = (RelativeLayout) findViewById(R.id.activity_main);
        mGestureDetector = createGestureDetector(this);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	private GestureDetector createGestureDetector(Context context) {
	    GestureDetector gestureDetector = new GestureDetector(context);
	        //Create a base listener for generic gestures
	        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
	            @Override
	            public boolean onGesture(Gesture gesture) {
	                if (gesture == Gesture.TAP) {
	                    Log.i(TAG, "onGesture TAP");
	                    mLayout.playSoundEffect(SoundEffectConstants.CLICK);
	                    Intent intent = new Intent (getApplicationContext(), com.mundoglass.worldglass.CameraActivity.class);
	                    startActivityForResult(intent, 0);
	                    return true;
	                } else if (gesture == Gesture.TWO_TAP) {
	                    Log.i(TAG, "onGesture TWO TAP");
	                	return true;
	                } else if (gesture == Gesture.SWIPE_RIGHT) {
	                	Log.i(TAG, "onGesture SWIPE RIGHT");
	                    return true;
	                } else if (gesture == Gesture.SWIPE_LEFT) {
	                	Log.i(TAG, "onGesture SWIPE LEFT");
	                	return true;
	                }
	                
	                return false;
	            }
	        });
	        gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
	            @Override
	            public void onFingerCountChanged(int previousCount, int currentCount) {
	              // do something on finger count changes
	            }
	        });
	        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
	            @Override
	            public boolean onScroll(float displacement, float delta, float velocity) {
	            	Log.i(TAG, "onScroll");
	            	return true;
	            }
	        });
	        return gestureDetector;
	}

    /*
     * Send generic motion events to the gesture detector
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == STREAM_REQUEST) {
             if (resultCode == RESULT_OK) {
                finish();
             }
        }
	}
	
}
