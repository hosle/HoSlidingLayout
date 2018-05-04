package com.hosle.slidinglayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Scroller

/**
 * Created by tanjiahao on 2018/4/28
 * Original Project SlidingLayout
 */
class HoSlidingLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val TAG = "SlidingLayout"

    private var lastPosX: Float = 0f
    private var lastPosY: Float = 0f
    private var subOffsetX: Float = 0f

    /**customized params start*/
    private var defaultMaxOffsetX: Float = 500f
    var edgeThreshold = 80
    var slideCoverAlpha = 0.6
    var slideCoverColor = Color.parseColor("#333333")
    var slidable = true
    /**customized params end*/

    private var isIntercept = false
    private var isSlideOpen = false

    private val scroller = Scroller(context)

    private val paint = Paint()

    private val slidingListeners = ArrayList<OnSlidingListener>()

    init {
        isClickable = true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val interceptX: Float = ev?.x ?: 0f
        val interceptY: Float = ev?.y ?: 0f

        if (!slidable) {
            isIntercept = false
        } else {
            when (ev?.actionMasked) {
                MotionEvent.ACTION_DOWN -> run {
                    isIntercept = if (isSlideOpen) {
                        interceptX > defaultMaxOffsetX
                    } else {
                        interceptX < edgeThreshold
                    }
                }
            }
        }

        lastPosX = interceptX
        lastPosY = interceptY

        return isIntercept
    }

    override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
        val result = super.drawChild(canvas, child, drawingTime)

        paint.color = slideCoverColor
        paint.alpha = Math.round(subOffsetX / defaultMaxOffsetX * (255 * slideCoverAlpha).toFloat())
        canvas?.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)

        return result
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (!isIntercept && !isSlideOpen)
            return false

        var offset: Int

        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> run {
                onStart()
            }

            MotionEvent.ACTION_UP -> run {

                if (subOffsetX > defaultMaxOffsetX / 2) {
                    openSlideBoard()
                } else {
                    closeSlideBoard()
                }
            }

            MotionEvent.ACTION_MOVE -> run {

                offset = (event.x - lastPosX).toInt()

                subOffsetX = Math.max(0f, subOffsetX + offset)
                subOffsetX = Math.min(subOffsetX, defaultMaxOffsetX)

                when {
                    subOffsetX >= defaultMaxOffsetX -> {
                        offset = (defaultMaxOffsetX + scrollX).toInt()
                        onSlideOpen()
                    }
                    subOffsetX <= 0 -> {
                        offset = scrollX
                        onSlideClose()
                    }
                    else -> onSliding()
                }

                scrollBy(-offset, 0)

                lastPosX = event.x

                printPosition()
            }
        }

        return true
    }

    private fun printPosition() {
        val localScrollX = scrollX
        Log.i(TAG, "lastDistance:${subOffsetX}\nscrollX:${localScrollX}")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val slideWidth = defaultMaxOffsetX.toInt()
        val slideWidthMeasureSpec = MeasureSpec.makeMeasureSpec(slideWidth, MeasureSpec.getMode(widthMeasureSpec))

        try {
            getChildAt(0).measure(slideWidthMeasureSpec, heightMeasureSpec)
        } catch (e: Exception) {
        }

        for (i in 1 until childCount) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec)
        }

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        printPosition()

        try {
            getChildAt(0).layout(-defaultMaxOffsetX.toInt(), t, 0, b)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        for (i in 1 until childCount) {
            getChildAt(i).layout(l, t, r, b)
        }
    }

    private fun onStart() {
        for (item in slidingListeners) {
            item.onStart(isSlideOpen)
        }
        Log.i(TAG, "Listen : onStart")
    }

    private fun onSliding() {
        for (item in slidingListeners) {
            item.onSliding()
        }
        Log.i(TAG, "Listen : onSliding")
    }

    private fun  onSlideOpen() {
        if (!isSlideOpen) {
            isSlideOpen = true
            for (item in slidingListeners) {
                item.onOpen()
            }
            Log.i(TAG, "Listen : onSlideOpen")
        }
    }

    private fun onSlideClose() {
        if (isSlideOpen) {
            isSlideOpen = false
            for (item in slidingListeners) {
                item.onClose()
            }
            Log.i(TAG, "Listen : onSlideClose")
        }
    }

    public fun openSlideBoard() {
        if(!slidable){
            return
        }
        onStart()
        subOffsetX = defaultMaxOffsetX
        scrollToDestination()
    }

    public fun closeSlideBoard() {
        if(!slidable){
            return
        }
        onStart()
        subOffsetX = 0f
        scrollToDestination()
    }

    private fun scrollToDestination() {
        val dx = (-subOffsetX - scrollX).toInt()
        scroller.startScroll(scrollX, 0, dx, 0)
        invalidate()
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            when {
                scroller.currX <= -defaultMaxOffsetX -> onSlideOpen()
                scroller.currX >= 0 -> onSlideClose()
                else -> onSliding()
            }
            invalidate()
            printPosition()
        }
    }

    public fun addOnSlidingListener(onSlidingListener: OnSlidingListener) {
        slidingListeners.add(onSlidingListener)
    }

    fun setSlidingWidth(width:Int){
        defaultMaxOffsetX = width.toFloat()
    }

    interface OnSlidingListener {
        fun onStart(isOpenNow: Boolean)
        fun onSliding()
        fun onOpen()
        fun onClose()
    }
}