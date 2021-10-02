package com.mohammadkk.simple2048

import android.content.Context
import android.graphics.Typeface
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView


enum class AnimationMoves {
    UP,RIGHT,DOWN, LEFT
}
enum class PlaySounds {
    SWIPE_GAME, FINISH_GAME, CLICK_BUTTON
}
fun Animation.endAnimation(callback: ()-> Unit) {
    this.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {}
        override fun onAnimationEnd(p0: Animation?) {
            callback()
        }
        override fun onAnimationRepeat(p0: Animation?) {}
    })
}
fun TextView.setFont(context: Context, name: String) {
    val typeFace = Typeface.createFromAsset(context.assets, "fonts/$name.ttf")
    this.typeface = typeFace
}
fun Button.setFont(context: Context, name: String) {
    val typeFace = Typeface.createFromAsset(context.assets, "fonts/$name.ttf")
    this.typeface = typeFace
}
const val KEY_BEST_SCORE = "best_score_game"
const val KEY_SCORE = "score_game"
const val KEY_PLAYED = "is_played_game"

@Suppress("SpellCheckingInspection")
const val FONT_PERSIAN_TWO_NAME = "samim_bold_fd"