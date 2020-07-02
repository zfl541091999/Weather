package com.zfl.weather.mvvm.v.widget

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

/**
 * 将此manager设置为recyclerView的layoutManager
 * recyclerView在调用smoothScrollToPosition方法时
 * 会将targetPosition的View滚动到置顶
 */
class TopSmoothScrollManager(context: Context?) : LinearLayoutManager(context) {

    val context: Context?

    init {
        this.context = context
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int
    ) {
        val linearSmoothScroller = object : LinearSmoothScroller(context) {

            override fun getHorizontalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }

            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                displayMetrics?.let {
                    return 5f / it.densityDpi
                }
                return super.calculateSpeedPerPixel(displayMetrics)
            }
        }

        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)

    }

}