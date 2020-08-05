package com.example.imagetopdf.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

public class NonSwipeableViewPager extends ViewPager {
    public NonSwipeableViewPager(@NonNull Context context) {
        super(context);
        setMyscroller();
    }

    public NonSwipeableViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setMyscroller();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    private void setMyscroller() {
        try{
            Class<?>viewPager=ViewPager.class;
            Field scroller=viewPager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this,new MyScroller(getContext()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private class MyScroller extends Scroller {
        public MyScroller(Context context) {
            super(context,new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy,400);
        }
    }
}
