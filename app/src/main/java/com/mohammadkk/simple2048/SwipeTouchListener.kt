package com.mohammadkk.simple2048

import android.annotation.SuppressLint

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


open class SwipeTouchListener(private val context: Context, private val swiper: Swiper) : View.OnTouchListener {
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
                            swiper.onSwipeRight()
                        } else {
                            swiper.onSwipeLeft()
                        }
                        result = true
                    }
                } else if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        swiper.onSwipeDown()
                    } else {
                        swiper.onSwipeUp()
                    }
                    result = true
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }
    }

    init {
        gestureDetector = GestureDetector(context, GestureHelper())
    }
    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }
}