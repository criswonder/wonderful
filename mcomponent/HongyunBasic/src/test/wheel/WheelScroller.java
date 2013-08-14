
package test.wheel;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Scroller class handles scrolling events and updates the 
 */
public class WheelScroller {
	private final String TAG=this.getClass().getSimpleName();
    /**
     * Scrolling listener interface
     */
    public interface ScrollingListener {
        /**
         * Scrolling callback called when scrolling is performed.
         * @param distance the distance to scroll
         */
        void onScroll(int distance);

        /**
         * Starting callback called when scrolling is started
         */
        void onStarted();
        
        /**
         * Finishing callback called after justifying
         */
        void onFinished();
        
        /**
         * Justifying callback called to justify a view when scrolling is ended
         */
        void onJustify();
    }
    
    /** Scrolling duration */
    private static final int SCROLLING_DURATION = 400;

    /** Minimum delta for scrolling */
    public static final int MIN_DELTA_FOR_SCROLLING = 1;

    // Listener
    private ScrollingListener listener;
    
    // Context
    private Context context;
    
    // Scrolling
    private GestureDetector gestureDetector;
    private Scroller scroller;
    //private int lastScrollY;
    //private float lastTouchedY;
    private int lastScrollX;
    private float lastTouchedX;    
    private boolean isScrollingPerformed;

    /**
     * Constructor
     * @param context the current context
     * @param listener the scrolling listener
     */
    public WheelScroller(Context context, ScrollingListener listener) {
        gestureDetector = new GestureDetector(context, gestureListener);
        gestureDetector.setIsLongpressEnabled(false);
        
        scroller = new Scroller(context);

        this.listener = listener;
        this.context = context;
    }
    
    /**
     * Set the the specified scrolling interpolator
     * @param interpolator the interpolator
     */
    public void setInterpolator(Interpolator interpolator) {
        scroller.forceFinished(true);
        scroller = new Scroller(context, interpolator);
    }
    
    /**
     * Scroll the wheel
     * @param distance the scrolling distance
     * @param time the scrolling duration
     */
    public void scroll(int distance, int time) {
        scroller.forceFinished(true);

        //lastScrollY = 0;
        lastScrollX = 0;
        /*-----------------------------------------------------------------------------------------------
         * dx Horizontal distance to travel. Positive numbers will scroll the content to the left.
         -----------------------------------------------------------------------------------------------*/
        scroller.startScroll(0, 0,distance,0, time != 0 ? time : SCROLLING_DURATION);
        setNextMessage(MESSAGE_SCROLL);
        
        startScrolling();
    }
   
    /**
     * Stops scrolling
     */
    public void stopScrolling() {
        scroller.forceFinished(true);
    }
    
    /**
     * Handles Touch event 
     * @param event the motion event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
    	//Log.d("scr", "Scroller onTouchEvent");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchedX = event.getX();
                scroller.forceFinished(true);
                clearMessages();
                break;
    
            case MotionEvent.ACTION_MOVE:
                // perform scrolling
            	Log.d(TAG, "------------onTouchEvent MotionEvent.ACTION_MOVE----------");
                int distanceX = (int)(event.getX() - lastTouchedX);
                if (distanceX != 0) {
                    startScrolling();
                    listener.onScroll(distanceX);
                    lastTouchedX = event.getX();
                }
                break;
        }
        
        if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
        	Log.d(TAG, "------------onTouchEvent justify----------");
            justify();
        }

        return true;
    }
    
    // gesture listener
    private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Do scrolling in onTouchEvent() since onScroll() are not call immediately
            //  when user touch and move the wheel
        	Log.d(TAG, "------------gestureListener onScroll----------");
            return true;
        }
        
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "------------gestureListener onFling----------");
        	
        	lastScrollX = 0;
            final int maxX = 0x7FFFFFFF;
            final int minX = -maxX;
            scroller.fling(lastScrollX, 0,  (int) -velocityX,0, minX, maxX, 0, 0);
            setNextMessage(MESSAGE_SCROLL);
            return true;
        }
    };

    // Messages
    private final int MESSAGE_SCROLL = 0;
    private final int MESSAGE_JUSTIFY = 1;
    
    /**
     * Set next message to queue. Clears queue before.
     * 
     * @param message the message to set
     */
    private void setNextMessage(int message) {
        clearMessages();
        animationHandler.sendEmptyMessage(message);
    }

    /**
     * Clears messages from queue
     */
    private void clearMessages() {
        animationHandler.removeMessages(MESSAGE_SCROLL);
        animationHandler.removeMessages(MESSAGE_JUSTIFY);
    }
    
    // animation handler
    private Handler animationHandler = new Handler() {
        public void handleMessage(Message msg) {
        	//Log.d("scr", "handleMessage");
            scroller.computeScrollOffset();
            int currX = scroller.getCurrX();
            int delta = lastScrollX - currX;
            lastScrollX = currX;
            if (delta != 0) {
                listener.onScroll(delta);
            }
            
            // scrolling is not finished when it comes to final Y
            // so, finish it manually 
            if (Math.abs(currX - scroller.getFinalX()) < MIN_DELTA_FOR_SCROLLING) {
                currX = scroller.getFinalX();
                scroller.forceFinished(true);
            }
            if (!scroller.isFinished()) {
                animationHandler.sendEmptyMessage(msg.what);
            } else if (msg.what == MESSAGE_SCROLL) {
                justify();
            } else {
                finishScrolling();
            }
        }
    };
    
    /**
     * Justifies wheel
     */
    private void justify() {
        listener.onJustify();
        setNextMessage(MESSAGE_JUSTIFY);
    }

    /**
     * Starts scrolling
     */
    private void startScrolling() {
        if (!isScrollingPerformed) {
            isScrollingPerformed = true;
            listener.onStarted();
        }
    }

    /**
     * Finishes scrolling
     */
    void finishScrolling() {
        if (isScrollingPerformed) {
            listener.onFinished();
            isScrollingPerformed = false;
        }
    }
}
