package com.mohammadkk.simple2048

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.mohammadkk.simple2048.databinding.ToastBinding


enum class AnimationMoves {
    UP, RIGHT, DOWN, LEFT
}
enum class PlaySounds {
    SWIPE_GAME, FINISH_GAME, CLICK_BUTTON
}
fun Animation.endAnimation(callback: () -> Unit) {
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
fun Activity.createCustomToast(
    text: String,
    @DrawableRes icon: Int = R.drawable.ic_done,
    @ColorRes color: Int = R.color.green_success
) {
    val layout = ToastBinding.inflate(layoutInflater)
    layout.text.setFont(this, FONT_PERSIAN_TWO_NAME)
    layout.toastLayoutRoot.setBackgroundRes(color)
    layout.text.text = text
    layout.image.setImageResource(icon)
    layout.image.scaleType = ImageView.ScaleType.CENTER
    val toast = Toast(applicationContext)
    toast.apply {
        setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        duration = Toast.LENGTH_LONG
        @Suppress("DEPRECATION")
        view = layout.root
    }
    toast.show()
}
const val KEY_BEST_SCORE = "best_score_game"
const val KEY_SCORE = "score_game"
const val KEY_PLAYED = "is_played_game"

@Suppress("SpellCheckingInspection")
const val FONT_PERSIAN_TWO_NAME = "samim_bold_fd"