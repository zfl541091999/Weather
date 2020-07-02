package com.zfl.weather.mvvm.v.widget;

import android.content.Context;
import android.graphics.Rect;

import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 用于配合实现悬浮吸顶效果的recyclerView，主要用于屏蔽悬停吸顶View的点击事件
 */
public class ZFLStickyRecyclerView extends RecyclerView {

    private Rect mInterceptRect;
    private boolean isIntercept;

    public ZFLStickyRecyclerView(@NonNull Context context) {
        super(context);
    }

    public ZFLStickyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ZFLStickyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void interceptTouchEvent(Rect rect) {
        mInterceptRect = rect;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (mInterceptRect != null && mInterceptRect.contains((int) (e.getX() + 0.5f), (int) (e.getY() + 0.5f))) {
            isIntercept = true;
            return true;
        }
        isIntercept = false;
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (isIntercept) {
            return true;
        }
        return super.onTouchEvent(e);
    }
}
