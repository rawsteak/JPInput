package com.rawsteak.jpinput;

import java.util.ArrayList;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureStroke;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.Toast;

//////////// 650  milliseconds //////////////////

public class GestureActivity extends Activity implements OnGesturePerformedListener {
	private Gesture mGesture;
	private GestureLibrary mLibrary;
	
//	private Region regionTop = new Region(0,0,250,120);
//	private Region regionLeft = new Region(0,0,120,250);
//	private Region regionTop = new Region(35,23,289,150);
//	private Region regionLeft = new Region(35,23,160,275);
//	private Region regionBottom = new Region(0,120,250,250);
	private RectF rectFTop = new RectF(35,25,288,144);
	private RectF rectFLeft = new RectF(35,25,160,280);
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        GestureOverlayView overlay = (GestureOverlayView) findViewById(R.id.gestures_overlay);
        overlay.addOnGesturePerformedListener(this);
    }

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		// TODO Auto-generated method stub
		mGesture = overlay.getGesture();
		
		int strokeCount = gesture.getStrokesCount();
		String guess = new String();
		
		switch (strokeCount) {
		case 1:
			guess = gestureOneStroke(gesture);
			break;
		case 2:
			guess = gestureTwoStrokes(gesture);
			break;
		default:
			Toast.makeText(getBaseContext(), "Too many strokes!" , Toast.LENGTH_SHORT).show();
		}

		Toast.makeText(getBaseContext(), "Guess: " + guess, Toast.LENGTH_SHORT).show();
		
	}
	
	public String gestureOneStroke(Gesture gesture) {
		mLibrary = null;
		mLibrary = GestureLibraries.fromRawResource(getBaseContext(), R.raw.jp_letters_1);
		if (!mLibrary.load()) {
        	finish();
        }
		
        ArrayList<Prediction> Predictions = mLibrary.recognize(gesture);
        mLibrary = null;
        return Predictions.get(0).name;
	}
	
	public String gestureTwoStrokes(Gesture gesture) {
		// Region regStroke1, regStroke2;
		RectF rectFStroke1;
		
		// ********
		// ******** add new strokes 
		// ******** 
		
		GestureStroke stroke1 = null;
		GestureStroke stroke2 = null;

		stroke1 = gesture.getStrokes().get(0);
		stroke2 = gesture.getStrokes().get(1);
		
		int pointCount1 = stroke1.points.length;
		int pointCount2 = stroke2.points.length;
		
		pointCount2 = pointCount2 / 3;
		if (pointCount2 % 2 != 0) {
			pointCount2 = pointCount2 + 1;
		}
		
		float x1 = stroke1.points[0];
		float y1 = stroke1.points[1];
		float x2 = stroke1.points[pointCount1 - 2];
		float y2 = stroke1.points[pointCount1 - 1];

		float x3 = stroke2.points[0];
		float y3 = stroke2.points[1];
		float x4 = stroke2.points[(pointCount2 - 2)];
		float y4 = stroke2.points[(pointCount2 - 1)];
		
		boolean isIntersecting = isIntersect(x1, y1, x2, y2, x3, y3, x4, y4);

		rectFStroke1 = stroke1.boundingBox;
		
		boolean isTop = rectFTop.contains(rectFStroke1); // && !rectFTop.intersect(rectFStroke1);
		//boolean isBig = rectFTop.intersect(rectFStroke1);
		boolean isLeft = rectFLeft.contains(rectFStroke1);
		
		if (isTop) {
			if (isIntersecting) {
				mLibrary = null;
				mLibrary = GestureLibraries.fromRawResource(this, R.raw.jp_letters_21);
				if (!mLibrary.load()) {
		        	finish();		
				}
			} else {
				mLibrary = null;
				mLibrary = GestureLibraries.fromRawResource(this, R.raw.jp_letters_20);
				if (!mLibrary.load()) {
		        	finish();
				}
			}
		} else if (isLeft) {
			if (isIntersecting) {
				mLibrary = null;
				mLibrary = GestureLibraries.fromRawResource(this, R.raw.jp_letters_23);
				if (!mLibrary.load()) {
		        	finish();
				}
			} else { 
				mLibrary = null;
				mLibrary = GestureLibraries.fromRawResource(this, R.raw.jp_letters_22);
				if (!mLibrary.load()) {
		        	finish();
				}
			}
		} else {
			mLibrary = null;
			mLibrary = GestureLibraries.fromRawResource(this, R.raw.jp_letters_24);
			if (!mLibrary.load()) {
	        	finish();
	        }			
		}
		
        ArrayList<Prediction> Predictions = mLibrary.recognize(gesture);
        mLibrary = null;
        return Predictions.get(0).name;
	}
	
	private float abs(float f) {
		// TODO Auto-generated method stub
		if (f < 0) {
			return (f * -1);
		} else {
			return f;
		}
	}

	public Rect rectFToInt(RectF src) {
		return new Rect((int) src.bottom,(int) src.left,(int) src.right,(int) src.top);
	}
	
	public boolean isIntersect(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		float ua_t = ((x4 - x3) * (y1 - y3)) - ((y4 - y3) * (x1 - x3));
		float ub_t = ((x2 - x1) * (y1 - y3)) - ((y2 - y1) * (x1 - x3));
		float u_b = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));
				
		if (u_b != 0) {
			float ua = ua_t / u_b;
			float ub = ub_t / u_b;
			
			if ((0 <= ua) && (ua <= 1) && (0 <= ub) && (ub <= 1)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public int initDirection(float x1, float y1, float x2, float y2) {
		if (x1 == x2) {
			if (y2 > y1) {
				return 0;			// up
			} else { 
				return 4;			// down
			}
		} else if (y2 == y1) {
			if (x2 > x1) {
				return 2;			// right
			} else {
				return 6;			// left
			}
		} else {
			float m = (y2 - y1) / (x2 - x1);
			
			if (m > 0) {
				if (x2 > x1) {
					return 1;		// upper right
				} else {
					return 5;		// lower left
				}
			} else {
				if (x2 > x1) {
					return 3;		// lower right
				} else {
					return 7;		// upper left
				}
			}
		}
	}
}


/*    private class GestureProcessor implements GestureOverlayView.OnGestureListener {

		@Override
		public void onGesture(GestureOverlayView overlay, MotionEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGestureCancelled(GestureOverlayView overlay,
				MotionEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
			// TODO Auto-generated method stub
			mGesture = overlay.getGesture();
			
			int strokeCount = mGesture.getStrokesCount();
			String guess = new String();
			
			switch (strokeCount) {
			case 1:
				guess = gestureOneStroke(mGesture);
				break;
			default:
				Toast.makeText(getBaseContext(), "Too many strokes!" , Toast.LENGTH_LONG).show();
			}
			
			Toast.makeText(getBaseContext(), "Guess: " + guess, Toast.LENGTH_LONG).show();
			
		}

		@Override
		public void onGestureStarted(GestureOverlayView overlay,
				MotionEvent event) {
			// TODO Auto-generated method stub
			
		}

		public String gestureOneStroke(Gesture gesture) {
			mLibrary = null;
			mLibrary = GestureLibraries.fromRawResource(getBaseContext(), R.raw.jp_letters_1);
			if (!mLibrary.load()) {
	        	finish();
	        }
			
	        ArrayList<Prediction> Predictions = mLibrary.recognize(gesture);
	        mLibrary = null;
	        return Predictions.get(0).name;
		}
    }
*/