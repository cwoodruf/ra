package ca.researchassistant;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.Toast;

public class FlingView extends WebView implements OnGestureListener {
	private Context me;
	private long lasttoast;
	// TODO: should really find out from system how long Toasts last
	private final long TOAST_THRESHOLD = 2000; //ms
	
    private GestureDetector gestureDetector;

    public FlingView(Context context) {
        super(context);
        init(context);
    }
    
    public FlingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    public void init(Context context) {
    	me = context;
    	lasttoast = 0; 
        gestureDetector = new GestureDetector(this.getContext(), this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return (gestureDetector.onTouchEvent(e) || super.onTouchEvent(e));
    }

    /* OnGestureListener events */

    public boolean onDown(MotionEvent e1) {
    	// lets WebView handle this
    	return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1 == null || e2 == null) return false;
        if(e1.getPointerCount() > 1 || e2.getPointerCount() > 1) return false;
        try { // right to left swipe .. go to next page
            if(e1.getX() - e2.getX() > 180 && Math.abs(velocityX) > 600) {
            	tryShowToast("Next question");
                this.loadUrl("javascript: next_question();");
                return true;
            } //left to right swipe .. go to prev page
            else if (e2.getX() - e1.getX() > 180 && Math.abs(velocityX) > 600) {
            	tryShowToast("Prev question");
                this.loadUrl("javascript: prev_question();");
                return true;
            } //bottom to top, go to next document
            /*
            else if(e1.getY() - e2.getY() > 100 && Math.abs(velocityY) > 800 
                    && webView.getScrollY() >= webView.getScale() * (webView.getContentHeight() - webView.getHeight())) {
                //do your stuff
                return true;
            } //top to bottom, go to prev document
            else if (e2.getY() - e1.getY() > 100 && Math.abs(velocityY) > 800 ) {
                //do your stuff
                return true;
            } 
            */
        } catch (Exception e) { // nothing
        }
        return false;
    }
    
    public void tryShowToast(String msg) {
    	long now = System.currentTimeMillis();
    	if (Math.abs(now - lasttoast) > TOAST_THRESHOLD) {
    		lasttoast = now;
    		Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
    	}
    }
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}
