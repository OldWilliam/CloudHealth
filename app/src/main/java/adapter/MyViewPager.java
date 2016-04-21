package adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Created by ein on 2016/4/3.
 */
public class MyViewPager extends ViewPager {
    private static final String TAG = "MyViewPager";
    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onTouchEvent: ");
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent: ");
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return super.onInterceptTouchEvent(ev);
        Log.d(TAG, "onInterceptTouchEvent: ");
        /**
         * I suppose that when you scroll left or right on ViewPager, it will intercept the touch
         * event(it just return true).So I make it always return false (the ViewPager will always
         * send the touch event to its child view like button or MapView)to let my map view can scroll left
         * and right.
         *
         * But why the other fragment like HealthFragment and SettingFragment can also scroll.Because
         * when the child view's method onTouchEvent() perform, it will return false and the event will
         * send to its (ViewPager)parent's view's onTouchEvent(),so the ViewPager can also scroll.
         *
         * And on the other hand, I guess the MapFragment's MapView will consume the touch event (return true)
         * so the event will not return to its father,finally the Viewpager at MapFragment can not scroll.
         *
         * Just Guess~
         */
        return false;
    }
}
