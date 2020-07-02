package com.zfl.weather.mvvm.v.widget;

import android.graphics.Canvas;
import android.graphics.Rect;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于实现recyclerView悬停吸顶功能。
 * 要实现以下功能目标：
 * 1、可以自定义DecorView
 * 2、可以自定义DecorView出现的条件
 * 3、可以自定义DecorView堆叠的数量（TODO 这个后续再实现）
 */
public class ZFLStickyItemDecoration extends RecyclerView.ItemDecoration {
    //悬浮view
    View stickyView;
    //stickyView的MeasuredHeight
    int stickyViewHeight;
    /**
     * 悬停视图距离顶部的距离
     */
    private int mStickyViewMarginTop;
    /**
     * sticky数据源
     */
    private Map<Integer, StickItemCacheData> mStickyData = new HashMap<>(100);

    IStickyCallBack stickyCallBack;

    /**
     * 保存上一次绘制的悬停视图的数据结构，无论top>0还是top<0，都需要绘制
     */
    private StickItemCacheData mPreStickyCacheData = null;
    /**
     * 上一次缓存hover数据的position
     */
    private int mPreObtainHoverPosition = Integer.MAX_VALUE;
    /**
     * 上一次缓存hover数据的data，区别于{@link #mPreStickyCacheData}，只有在top>0的时候才会获取该数据
     */
    private StickItemCacheData mPreObtainHoverData = null;

    public ZFLStickyItemDecoration(RecyclerView.Adapter adapter, @NonNull IStickyCallBack iStickyCallBack) {
        stickyCallBack = iStickyCallBack;
        stickyView = stickyCallBack.createStickyView();
        if (adapter == null) {
            return;
        }
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                clearOldData();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                clearOldData();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                clearOldData();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                clearOldData();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                clearOldData();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                clearOldData();
            }
        });
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (parent.getAdapter().getItemCount() <= 0) return;
        // 绘制前先重置拦截事件的区域
        interceptStickyTouchEvent(parent, 0);
        // 是否已经绘制过悬停视图
        boolean hasDrawHoverView = false;
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildLayoutPosition(view);
            StickItemCacheData currentCacheData = mStickyData.get(position);
            if (currentCacheData != null && currentCacheData.baseStickyData != null) {
                // 第一阶段：绘制所有内部的sticky view分隔视图(类似于分割线)
                bindData(currentCacheData);
                drawStickyView(c, currentCacheData.height - view.getTop());
                // 只绘制一次sticky view
                if (hasDrawHoverView) {
                    continue;
                }
                // 第二阶段：绘制悬停视图
                hasDrawHoverView = true;
                StickItemCacheData stickCacheData = currentCacheData;
                // 获取首个sticky view的top值
                int currentViewTop = view.getTop() - currentCacheData.height;
                // 如果当前的首个sticky view的顶部值大于0(即：在页面中完全可见)，说明需要悬停的是上一个sticky view的视图
                // 则需要获取上一个悬停视图的数据结构
                if (currentViewTop > 0) {
                    // 优化：每次都调用obtainHoverData，在position一致的情况下，不需要重复调用
                    if (mPreObtainHoverPosition != position - 1) {
                        mPreObtainHoverPosition = position - 1;
                        stickCacheData = new StickItemCacheData();
                        stickCacheData.baseStickyData = stickyCallBack.obtainHoverData(mPreObtainHoverPosition);
                        // 数据结构为null，则不绘制
                        if (stickCacheData.baseStickyData == null) {
                            mPreObtainHoverData = null;
                            continue;
                        }
                        stickCacheData.width = currentCacheData.width;
                        // 绑定数据 & measure & layout
                        bindData(stickCacheData);
                        stickCacheData.height = getStickyItemHeight(stickCacheData.baseStickyData);
                        mPreObtainHoverData = stickCacheData;
                    } else if ((stickCacheData = mPreObtainHoverData) == null) {
                        continue;
                    }
                }
                mPreStickyCacheData = stickCacheData;
                // 重新绑定数据
                bindData(stickCacheData);
                // 当前的首个sticky view的顶部值大于0，并且其小于悬停视图的高度
                // (说明：下个sticky view将要被盖在悬停的视图下面，故需要将悬停的视图联动)
                if (currentViewTop > 0 && currentViewTop <= stickyViewHeight) {
                    mStickyViewMarginTop = stickyViewHeight - currentViewTop;
                } else {
                    mStickyViewMarginTop = 0;
                    // 查找下一个sticky view，在当前悬停的视图还没有被remove的情况下，需要与下一个sticky view联动
                    // (比如悬停的视图很高，下一个sticky view高度很小，
                    // 此时currentViewTop < 0并且下一个sticky view距离顶部的距离已经小于当前悬停的view的高度)
                    int nextStickyViewTop = getNextStickyViewTop(parent);
                    if (nextStickyViewTop > 0 && nextStickyViewTop <= stickyViewHeight) {
                        mStickyViewMarginTop = stickyViewHeight - nextStickyViewTop;
                    }
                }
                drawStickyView(c, mStickyViewMarginTop);
                interceptStickyTouchEvent(parent, stickyViewHeight - mStickyViewMarginTop);
            }
        }
        // 处理当前展示的ui中没有sticky view
        if (!hasDrawHoverView && mPreStickyCacheData != null) {
            bindData(mPreStickyCacheData);
            drawStickyView(c, 0);
            interceptStickyTouchEvent(parent, stickyViewHeight);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildLayoutPosition(view);
        if (mStickyData.get(position) == null) {
            BaseStickyData data = stickyCallBack.obtainInsideDataByPosition(position);
            if (data == null) {
                return;
            }
            StickItemCacheData cacheData = new StickItemCacheData();
            cacheData.width = parent.getWidth();
            cacheData.baseStickyData = data;
            mStickyData.put(position, cacheData);
            if (data.definitelyHeight <= 0 || stickyView.getMeasuredHeight() <= 0) {
                bindData(cacheData);
            }
            cacheData.height = getStickyItemHeight(data);
            outRect.set(0, cacheData.height, 0, 0);
        } else {
            StickItemCacheData cacheData = mStickyData.get(position);
            outRect.set(0, cacheData.height, 0, 0);
        }
    }

    /**
     * 获取sticky item的高度
     */
    private int getStickyItemHeight(BaseStickyData data) {
        if (data != null && data.definitelyHeight > 0) {
            return data.definitelyHeight;
        }
        return stickyView.getMeasuredHeight();
    }

    /**
     * 得到下一个sticky View
     *
     * @param parent
     * @return
     */
    private int getNextStickyViewTop(RecyclerView parent) {
        int num = 0;
        View nextStickyView = null;
        StickItemCacheData nextStickyData = null;
        for (int m = 0, size = parent.getChildCount(); m < size; m++) {
            View view = parent.getChildAt(m);
            StickItemCacheData data = mStickyData.get(parent.getChildLayoutPosition(view));
            if (data != null) {
                nextStickyView = view;
                nextStickyData = data;
                num++;
            }
            if (num == 2) break;
        }
        return num >= 2 ? nextStickyView.getTop() - nextStickyData.height : -1;
    }

    /**
     * 给StickyView绑定数据，刷新视图
     */
    private void bindData(StickItemCacheData data) {
        if (data == null || data.baseStickyData == null) {
            return;
        }
        // 先更新数据源
        stickyCallBack.bindView(stickyView, data.baseStickyData);
        // 对于definitelyHeight>0的case，不做measure和layout，以减少耗时
        if (stickyView.getMeasuredHeight() <= 0
                || data.baseStickyData.definitelyHeight <= 0) {
            measureLayout(data.width);
        }
        stickyViewHeight = stickyView.getMeasuredHeight();
    }

    private void drawStickyView(Canvas canvas, int transY) {
        if (stickyView == null) return;
        int saveCount = canvas.save();
        canvas.translate(0, -transY);
        stickyView.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    /**
     * 在数据重新刷新的时候(notifyDataSetChanged()之前)，要调用一下，清理一些缓存的sticky脏数据
     */
    private void clearOldData() {
        mStickyData.clear();
        mPreObtainHoverPosition = Integer.MAX_VALUE;
        mPreStickyCacheData = null;
        mPreObtainHoverData = null;
    }

    /**
     * 计算布局sticky view的高度
     *
     * @param parentWidth
     */
    private void measureLayout(int parentWidth) {
        if (stickyView == null || !stickyView.isLayoutRequested()) return;

        int widthSpec = View.MeasureSpec.makeMeasureSpec(parentWidth, View.MeasureSpec.EXACTLY);
        int heightSpec;

        ViewGroup.LayoutParams layoutParams = stickyView.getLayoutParams();
        if (layoutParams != null && layoutParams.height > 0) {
            heightSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY);
        } else {
            heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        stickyView.measure(widthSpec, heightSpec);
        stickyView.layout(0, 0, stickyView.getMeasuredWidth(), stickyView.getMeasuredHeight());
    }

    /**
     * 拦截RecyclerView的点击事件，防止sticky view 区域会响应点击事件
     */
    private void interceptStickyTouchEvent(RecyclerView parent, int bottom) {
        if (parent instanceof ZFLStickyRecyclerView) {
            ((ZFLStickyRecyclerView) parent).interceptTouchEvent(new Rect(0, 0, parent.getWidth(), bottom));
        }
    }

    private static class StickItemCacheData {
        /**
         * sticky item 的高度
         */
        int height;
        /**
         * sticky item 的宽度
         */
        int width;
        /**
         * sticky view数据源
         */
        BaseStickyData baseStickyData;
    }

    /**
     * 通过此回调来实现以下需求：
     * 1、更新stickyView
     */
    public interface IStickyCallBack<StickyView extends View, StickData extends BaseStickyData> {

        StickyView createStickyView();

        /**
         * 获取页面内部嵌入的sticky view的数据结构
         * 根据当前position，判断该position之上是否需要嵌入sticky view，需要则返回相应数据结构，不需要则必须返回null
         *
         * @param position
         */
        StickData obtainInsideDataByPosition(int position);

        /**
         * 用数据更新视图
         *
         * @param view 视图
         * @param data 视图所需数据
         */
        void bindView(StickyView view, StickData data);

        /**
         * 获取在顶部悬停的sticky view的数据结构
         * 根据当前position，判断该position之上是否有悬停的sticky view，有则返回悬停的sticky view的数据结构，不需要悬停则返回null
         */
        StickData obtainHoverData(int headPosition);

    }

    /**
     * 该类定义了悬停视图的数据结构，自定义的数据结构需要继承自该类
     */
    public static class BaseStickyData {
        int definitelyHeight = 0;
    }

}
