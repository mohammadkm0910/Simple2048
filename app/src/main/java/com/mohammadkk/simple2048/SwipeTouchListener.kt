package com.mohammadkk.simple2048

import android.annotation.SuppressLint

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


abstract class SwipeTouchListener(private val context: Context) : View.OnTouchListener {
    private val gestureDetector: GestureDetector

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(p1)
    }
    private inner class GestureHelper: GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            var result = false
            try {
                val diffY = e2!!.y - e1!!.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwiperToRight()
                        } else {
                            onSwiperToLeft()
                        }
                        result = true
                    }
                } else if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwiperToDown()
                    } else {
                        onSwiperToUp()
                    }
                    result = true
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }
    }

    abstract fun onSwiperToUp()
    abstract fun onSwiperToRight()
    abstract fun onSwiperToDown()
    abstract fun onSwiperToLeft()

    init {
        gestureDetector = GestureDetector(context, GestureHelper())
    }
    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }
}