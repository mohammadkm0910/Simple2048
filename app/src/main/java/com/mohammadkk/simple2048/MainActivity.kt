package com.mohammadkk.simple2048

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.mohammadkk.simple2048.GameView.Companion.mergedTiles
import kotlin.math.ceil
import kotlin.random.Random

import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils

import android.widget.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    private lateinit var titleCurrentScore: TextView
    private lateinit var titleBestScore: TextView
    private lateinit var currentScore: TextView
    private lateinit var bestScoreVal: TextView
    private lateinit var scoreAdded: TextView
    private lateinit var gameView: GameView
    private lateinit var btnResetScore: Button
    private lateinit var sp: SharedPreferences
    private var score: Int = 0
    private var bestScore: Int = 0
    private var tempScore: Int = 0
    private var number = Array(4) {
        IntArray(
            4
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        titleCurrentScore = findViewById(R.id.titleCurrentScore)
        titleBestScore = findViewById(R.id.titleBestScore)
        currentScore = findViewById(R.id.currentScore)
        bestScoreVal = findViewById(R.id.bestScoreVal)
        scoreAdded = findViewById(R.id.scoreAdded)
        gameView = findViewById(R.id.gameView)
        btnResetScore = findViewById(R.id.btnResetScore)
        sp = getSharedPreferences("game_state", Context.MODE_PRIVATE)
        bestScore = sp.getInt(KEY_BEST_SCORE, 0)
        bestScoreVal.text = bestScore.toString()
        number = gameView.getNumber()
        titleCurrentScore.setFont(this,"minister_bold")
        titleBestScore.setFont(this,"minister_bold")
        btnResetScore.setFont(this,"minister_bold")
        if (!sp.getBoolean(KEY_PLAYED, false)) {
            alertWelcome()
            startGame()
        } else {
            loadState()
        }
        onTouchGame()
        btnResetScore.setOnClickListener {
            gameView.setPlaySound(PlaySounds.CLICK_BUTTON)
            resetGameDialog()
        }
    }
    override fun onStart() {
        super.onStart()
        sp.edit().putBoolean(KEY_PLAYED, true).apply()
    }
    private fun nextNumber() {
        while (true) {
            var zeros = 0
            for (i in 0 until 4) {
                for (j in 0 until 4) {
                    if (number[i][j] == 0) {
                        zeros++
                    }
                }
            }
            if (zeros == 0) {
                var finishGame = true
                for (i in 0 until 4) {
                    for (j in 0 until 3) {
                        if (number[i][j] == number[i][j+1]) {
                            finishGame = false
                            break
                        }
                    }
                }
                for (i in 0 until 3) {
                    for (j in 0 until 4) {
                        if (number[i][j] == number[i+1][j]) {
                            finishGame = false
                            break
                        }
                    }
                }
                if (finishGame) {
                    gameView.setPlaySound(PlaySounds.FINISH_GAME)
                    finishGameDialog()
                }
                break
            }
            val row = Random.nextInt(4)
            val col = Random.nextInt(4)
            if (number[row][col] == 0) {
                number[row][col] = if (Math.random() < 0.8) 2 else 4
                gameView.setNumber(number)
                break
            }
        }

    }
    private fun startGame() {
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                number[i][j] = 0
            }
        }
        nextNumber()
        nextNumber()
        gameView.setNumber(number)
    }
    private fun changeScore(newScore: Int) {
        if (newScore == 0) {
            score = 0
        } else {
            score += (newScore / 2)
        }
        currentScore.text = score.toString()
        if (score > bestScore) {
            bestScore = score
            bestScoreVal.text = bestScore.toString()
            sp.edit().putInt(KEY_BEST_SCORE, bestScore).apply()
        }
    }
    private fun setAnimationAddScore(addScore: Int) {
        val gameAddScoreAnim = AnimationUtils.loadAnimation(this, R.anim.game_add_score)
        scoreAdded.apply {
            text = String.format(Locale.ENGLISH,"+%d", addScore)
            visibility = View.VISIBLE
        }
        scoreAdded.startAnimation(gameAddScoreAnim)
        gameAddScoreAnim.endAnimation {
            scoreAdded.visibility = View.GONE
        }
    }
    private fun saveState() {
        val editor = sp.edit()
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                editor.putInt("$i$j", number[i][j])
            }
        }
        editor.putInt(KEY_SCORE, score)
        editor.apply()
    }
    private fun loadState() {
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                number[i][j] = sp.getInt("$i$j", number[i][j])
            }
        }
        gameView.setNumber(number)
        score = sp.getInt(KEY_SCORE, 0)
        currentScore.text = score.toString()
    }
    private fun moveHorizontal(isRight: Boolean) {
        var col: Int
        for (i in 0 until 4) {
            if (isRight) {
                for (j in 2 downTo 0) {
                    if (number[i][j] != 0) {
                        col = j
                        while (col < 3 && number[i][col+1] == 0) {
                            number[i][col+1] = number[i][col]
                            number[i][col] = 0
                            col++
                        }
                        if (col < 3) {
                            if (number[i][col] == number[i][col+1]) {
                                number[i][col+1] = number[i][col+1] * 2
                                changeScore(number[i][col+1])
                                tempScore += number[i][col+1] / 2
                                mergedTiles.add("$i/${col+1}")
                                number[i][col] = 0
                            }
                        }
                    }
                }
            } else {
                for (j in 1 until 4) {
                    if (number[i][j] != 0) {
                        col = j
                        while (col > 0 && number[i][col-1] == 0) {
                            number[i][col-1] = number[i][col]
                            number[i][col] = 0
                            col--
                        }
                        if (col > 0) {
                            if (number[i][col] == number[i][col-1]) {
                                number[i][col-1] = number[i][col-1] * 2
                                changeScore(number[i][col-1])
                                tempScore += number[i][col-1] / 2
                                mergedTiles.add("$i/${col-1}")
                                number[i][col] = 0
                            }
                        }
                    }
                }
            }
        }
        if (tempScore != 0) {
            setAnimationAddScore(tempScore)
            tempScore = 0
        }
    }
    private fun moveVertical(isUp: Boolean) {
        var row: Int
        for (j in 0 until 4) {
            if (isUp) {
                for (i in 1 until 4) {
                    if (number[i][j] != 0) {
                        row = i
                        while (row > 0 && number[row-1][j] == 0) {
                            number[row-1][j] = number[row][j]
                            number[row][j] = 0
                            row--
                        }
                        if (row > 0) {
                            if (number[row][j] == number[row-1][j]) {
                                number[row-1][j] = number[row-1][j] * 2
                                changeScore(number[row-1][j])
                                tempScore += number[row-1][j] / 2
                                mergedTiles.add("${row-1}/$j")
                                number[row][j] = 0
                            }
                        }
                    }
                }
            } else {
                for (i in 2 downTo 0) {
                    if (number[i][j] != 0) {
                        row = i
                        while (row < 3 && number[row+1][j] == 0) {
                            number[row+1][j] = number[row][j]
                            number[row][j] = 0
                            row++
                        }
                        if (row < 3) {
                            if (number[row][j] == number[row+1][j]) {
                                number[row+1][j] = number[row+1][j] * 2
                                changeScore(number[row+1][j])
                                tempScore += number[row+1][j] / 2
                                mergedTiles.add("${row+1}/$j")
                                number[row][j] = 0
                            }
                        }
                    }
                }
            }
        }
        if (tempScore != 0) {
            setAnimationAddScore(tempScore)
            tempScore = 0
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchGame() {
        gameView.setOnTouchListener(SwipeTouchListener(this, object : Swiper {
            override fun onSwipeUp() {
                gameView.setPlaySound(PlaySounds.SWIPE_GAME)
                moveVertical(true)
                gameView.setAnimation(AnimationMoves.UP)
                nextNumber()
            }
            override fun onSwipeRight() {
                gameView.setPlaySound(PlaySounds.SWIPE_GAME)
                moveHorizontal(true)
                gameView.setAnimation(AnimationMoves.RIGHT)
                nextNumber()
            }
            override fun onSwipeDown() {
                gameView.setPlaySound(PlaySounds.SWIPE_GAME)
                moveVertical(false)
                gameView.setAnimation(AnimationMoves.DOWN)
                nextNumber()
            }
            override fun onSwipeLeft() {
                gameView.setPlaySound(PlaySounds.SWIPE_GAME)
                moveHorizontal(false)
                gameView.setAnimation(AnimationMoves.LEFT)
                nextNumber()
            }
        }))
    }
    override fun onDestroy() {
        saveState()
        super.onDestroy()
    }
    private fun resetGameDialog() {
        val alertDialog = AlertDialog.Builder(this)
        val margin = resources.getDimension(R.dimen.margin_horizontal_dialog).toInt()
        val view = layoutInflater.inflate(R.layout.dialog_reset_game, findViewById(R.id.dialogReset), false)
        val titleResetDialog: TextView = view.findViewById(R.id.titleResetDialog)
        val titleBestScoreResetDialog: TextView = view.findViewById(R.id.titleBestScoreResetDialog)
        val titleBestScoreValResetDialog: TextView = view.findViewById(R.id.titleBestScoreValResetDialog)
        val btnNoResetDialog: Button = view.findViewById(R.id.btnNoResetDialog)
        val btnYesResetDialog: Button = view.findViewById(R.id.btnYesResetDialog)
        titleResetDialog.setFont(this,"minister_bold")
        titleBestScoreResetDialog.setFont(this,"minister_bold")
        btnYesResetDialog.setFont(this,"minister_bold")
        btnNoResetDialog.setFont(this,"minister_bold")
        titleBestScoreValResetDialog.text = bestScore.toString()
        alertDialog.setView(view).setCancelable(false)
        val dialog = alertDialog.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.show()
        dialog.window?.setLayout(
            resources.displayMetrics.widthPixels - margin,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        btnNoResetDialog.setOnClickListener { dialog.dismiss() }
        btnYesResetDialog.setOnClickListener {
            startGame()
            changeScore(0)
            saveState()
            dialog.dismiss()
        }
    }
    private fun finishGameDialog() {
        val alertDialog = AlertDialog.Builder(this)
        val margin = resources.getDimension(R.dimen.margin_horizontal_dialog).toInt()
        val view = layoutInflater.inflate(R.layout.dialog_finish_game, findViewById(R.id.finishDialog), false)
        val titleFinishDialog: TextView = view.findViewById(R.id.titleFinishDialog)
        val titleYourScoreFinishDialog: TextView = view.findViewById(R.id.titleYourScoreFinishDialog)
        val valYourScoreFinishDialog: TextView = view.findViewById(R.id.valYourScoreFinishDialog)
        val bestScoreFinishDialog: TextView = view.findViewById(R.id.bestScoreFinishDialog)
        val btnCloseFinishDialog: ImageButton = view.findViewById(R.id.btnCloseFinishDialog)
        val btnGameAgainFinishDialog: Button = view.findViewById(R.id.btnGameAgainFinishDialog)
        titleFinishDialog.setFont(this,"minister_bold")
        titleYourScoreFinishDialog.setFont(this,"minister_bold")
        bestScoreFinishDialog.setFont(this,"minister_bold")
        btnGameAgainFinishDialog.setFont(this,"minister_bold")
        valYourScoreFinishDialog.text = score.toString()
        alertDialog.setView(view).setCancelable(false)
        val dialog = alertDialog.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.show()
        dialog.window?.setLayout(
            resources.displayMetrics.widthPixels - margin,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        btnCloseFinishDialog.setOnClickListener { dialog.dismiss() }
        btnGameAgainFinishDialog.setOnClickListener {
            startGame()
            changeScore(0)
            saveState()
            dialog.dismiss()
        }
    }
    private fun alertWelcome() {
        val layout = layoutInflater.inflate(R.layout.toast, findViewById(R.id.toast_layout_root), false)
        val image: ImageView = layout.findViewById(R.id.image)
        image.setImageResource(R.drawable.ic_done)
        val text: TextView = layout.findViewById(R.id.text)
        text.setFont(this, FONT_PERSIAN_TWO_NAME)
        text.text = getString(R.string.welcome_message)
        val toast = Toast(applicationContext)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }
}