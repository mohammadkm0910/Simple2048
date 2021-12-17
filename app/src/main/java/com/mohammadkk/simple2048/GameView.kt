package com.mohammadkk.simple2048

import android.content.Context
import android.graphics.*
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View.MeasureSpec
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import kotlin.math.log

class GameView : AppCompatImageView {
    private lateinit var paint: Paint
    private lateinit var textPaint: Paint
    private var mediaPlayerOne: MediaPlayer? = null
    private var mediaPlayerTwo: MediaPlayer? = null
    private var mediaPlayerThree: MediaPlayer? = null
    private var px: Float = 0f
    private var number = Array(4) {
        IntArray(
            4
        )
    }

    constructor(context: Context) : super(context) {
        initialize()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }
    private fun initialize() {
        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics)
        paint = Paint()
        textPaint = Paint()
        paint.apply {
            color = ContextCompat.getColor(context, R.color.emptyTile)
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
        }
        textPaint.apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            textSize = px * 18
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                number[i][j] = 0
            }
        }
    }
    fun getNumber(): Array<IntArray> {
        return number
    }
    fun setNumber(number: Array<IntArray>) {
        this.number = number
        invalidate()
    }
    fun setAnimation(anim: AnimationMoves) {
        when (anim) {
            AnimationMoves.UP -> {
                val gameViewAnimUp = AnimationUtils.loadAnimation(context, R.anim.game_view_up)
                startAnimation(gameViewAnimUp)
                gameViewAnimUp.endAnimation { mergedTiles.clear() }
            }
            AnimationMoves.RIGHT -> {
                val gameViewAnimRight = AnimationUtils.loadAnimation(context, R.anim.game_view_right)
                startAnimation(gameViewAnimRight)
                gameViewAnimRight.endAnimation { mergedTiles.clear() }
            }
            AnimationMoves.DOWN -> {
                val gameViewAnimDown = AnimationUtils.loadAnimation(context, R.anim.game_view_down)
                startAnimation(gameViewAnimDown)
                gameViewAnimDown.endAnimation { mergedTiles.clear() }
            }
            AnimationMoves.LEFT -> {
                val gameViewAnimLeft = AnimationUtils.loadAnimation(context, R.anim.game_view_left)
                startAnimation(gameViewAnimLeft)
                gameViewAnimLeft.endAnimation { mergedTiles.clear() }
            }
        }
    }
    fun setPlaySound(sound: PlaySounds) {
        when (sound) {
            PlaySounds.SWIPE_GAME -> {
                mediaPlayerOne?.release()
                mediaPlayerOne = MediaPlayer.create(context, R.raw.swipe)
                mediaPlayerOne?.start()
            }
            PlaySounds.FINISH_GAME -> {
                mediaPlayerTwo = MediaPlayer.create(context, R.raw.finish)
                mediaPlayerTwo?.start()
            }
            PlaySounds.CLICK_BUTTON -> {
                mediaPlayerThree = MediaPlayer.create(context, R.raw.click)
                mediaPlayerThree?.start()
            }
        }
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        fixedSize()
        val margin = px * 8
        val size = ((width - margin) / 4 - margin).toInt()
        val sizeR = size + margin
        val round = px * 5
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                val value = number[j][i]
                val rectF = createRect(i, j, size, margin)
                val color = getColors(value)
                paint.color = ContextCompat.getColor(context, getColors(value))
                textPaint.color = ContextCompat.getColor(context, R.color.color_tile)
                for (k in 0 until mergedTiles.size) {
                    val merged = mergedTiles[k].split("/")
                    if (merged[0] == j.toString() && merged[1] == i.toString()) {
                        paint.color = ContextCompat.getColor(context, R.color.background_merged_tile)
                    }
                }
                canvas?.drawRoundRect(rectF, round, round, paint)
                if (value > 0) {
                    textPaint.textSize = if (value <= 512) size / 2.4f else size / 3f
                    canvas?.drawText(value.toString(), (i + 0.5f) * sizeR + margin / 1.85f, (j + 0.65f) * sizeR + margin / 2.2f, textPaint)
                }
            }
        }
    }
    private fun getPosCell(size: Int, margin: Float, index: Int): Float {
       return size * index + margin * (index + 1)
    }
    private fun getColors(value: Int): Int{
        return when (value) {
            0 -> R.color.emptyTile
            2 -> R.color.background_tile_2
            4 -> R.color.background_tile_4
            8 -> R.color.background_tile_8
            16 -> R.color.background_tile_16
            32 -> R.color.background_tile_32
            64 -> R.color.background_tile_64
            128 -> R.color.background_tile_128
            256 -> R.color.background_tile_256
            512 -> R.color.background_tile_512
            1024 -> R.color.background_tile_1024
            2048 -> R.color.background_tile_2048
            else -> R.color.background_tile_order
        }
    }
    private fun createRect(x: Int, y: Int, size: Int, margin: Float) : RectF {
        val left = getPosCell(size, margin, x)
        val top = getPosCell(size, margin, y)
        val right = left + size
        val bottom = top + size
        return RectF(left, top, right, bottom)
    }
    private fun fixedSize() {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels - marginLeft - marginRight
        layoutParams.width = width
        layoutParams.height = width
        requestLayout()
    }
    companion object {
        var mergedTiles: ArrayList<String> = ArrayList()
    }
}