package com.mohammadkk.simple2048.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.mohammadkk.simple2048.R

class LinearRadius : LinearLayout {
    private var radius: Float = 0f
    private var background: Int = 0

    constructor(context: Context?) : super(context) {
        initialize(context, null, 0)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context, attrs, 0)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs, defStyleAttr)
    }
    private fun initialize(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context!!.obtainStyledAttributes(attrs, R.styleable.LinearRadius, defStyleAttr, 0)
        try {
            radius = a.getDimension(R.styleable.LinearRadius_radiusCorner, 1f)
            background = a.getColor(R.styleable.LinearRadius_backgroundColor, ContextCompat.getColor(context, R.color.white))
        } finally {
            a.recycle()
        }
        createBackground()
    }
    fun setBackgroundRes(@ColorRes color: Int) {
        background = ContextCompat.getColor(context, color)
        createBackground()
    }
    private fun createBackground() {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.apply {
            setColor(background)
            cornerRadius = radius
        }
        setBackground(gradientDrawable)
    }
}