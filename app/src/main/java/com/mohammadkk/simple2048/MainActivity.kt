package com.mohammadkk.simple2048

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mohammadkk.simple2048.GameView.Companion.mergedTiles
import com.mohammadkk.simple2048.databinding.ActivityMainBinding
import java.util.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = getSharedPreferences("game_state", Context.MODE_PRIVATE)
        bestScore = sp.getInt(KEY_BEST_SCORE, 0)
        binding.bestScoreVal.text = bestScore.toString()
        number = binding.gameView.getNumber()
        binding.titleCurrentScore.setFont(this,"minister_bold")
        binding.titleBestScore.setFont(this,"minister_bold")
        if (!sp.getBoolean(KEY_PLAYED, false)) {
            createCustomToast("به بازی خوش آمدید")
            startGame()
            saveState()
        } else {
            loadState()
        }
        onTouchGame()
        binding.btnResetGame.setOnClickListener {
            binding.gameView.setPlaySound(PlaySounds.CLICK_BUTTON)
            resetGameDialog()
        }
        binding.btnUndoGame.setOnClickListener {
            if (sp.contains("undo_$KEY_SCORE")) {
                loadUndoState()
            } else {
                createCustomToast("چیزی برای برگشت وجود ندارد", R.drawable.ic_close, R.color.red_danger)
            }
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
                    binding.gameView.setPlaySound(PlaySounds.FINISH_GAME)
                    finishGameDialog()
                }
                break
            }
            val row = Random.nextInt(4)
            val col = Random.nextInt(4)
            if (number[row][col] == 0) {
                number[row][col] = if (Math.random() < 0.8) 2 else 4
                binding.gameView.setNumber(number)
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
        binding.gameView.setNumber(number)
    }
    private fun changeScore(newScore: Int) {
        if (newScore == 0) {
            score = 0
        } else {
            score += (newScore / 2)
        }
        binding.currentScore.text = score.toString()
        if (score > bestScore) {
            bestScore = score
            binding.bestScoreVal.text = bestScore.toString()
            sp.edit().putInt(KEY_BEST_SCORE, bestScore).apply()
        }
    }
    private fun setAnimationAddScore(addScore: Int) {
        val gameAddScoreAnim = AnimationUtils.loadAnimation(this, R.anim.game_add_score)
        binding.scoreAdded.apply {
            text = String.format(Locale.ENGLISH,"+%d", addScore)
            visibility = View.VISIBLE
        }
        binding.scoreAdded.startAnimation(gameAddScoreAnim)
        gameAddScoreAnim.endAnimation {
            binding.scoreAdded.visibility = View.GONE
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
    private fun saveUndoState() {
        val editor = sp.edit()
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                editor.putInt("undo_$i$j", number[i][j])
            }
        }
        editor.putInt("undo_$KEY_SCORE", score)
        editor.apply()
    }
    private fun loadState() {
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                number[i][j] = sp.getInt("$i$j", number[i][j])
            }
        }
        binding.gameView.setNumber(number)
        score = sp.getInt(KEY_SCORE, 0)
        binding.currentScore.text = score.toString()
    }
    private fun loadUndoState() {
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                number[i][j] = sp.getInt("undo_$i$j", number[i][j])
            }
        }
        binding.gameView.setNumber(number)
        score = sp.getInt("undo_$KEY_SCORE", 0)
        binding.currentScore.text = score.toString()
        removeUndoState()
    }
    private fun removeUndoState() {
        val editor = sp.edit()
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                editor.remove("undo_$i$j")
            }
        }
        editor.remove("undo_$KEY_SCORE")
        editor.apply()
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
        binding.gameView.setOnTouchListener(object : SwipeTouchListener(this@MainActivity) {
            override fun onSwiperToUp() {
                binding.gameView.setPlaySound(PlaySounds.SWIPE_GAME)
                saveUndoState()
                moveVertical(true)
                binding.gameView.setAnimation(AnimationMoves.UP)
                nextNumber()
                saveState()
            }
            override fun onSwiperToRight() {
                binding.gameView.setPlaySound(PlaySounds.SWIPE_GAME)
                saveUndoState()
                moveHorizontal(true)
                binding.gameView.setAnimation(AnimationMoves.RIGHT)
                nextNumber()
                saveState()
            }
            override fun onSwiperToDown() {
                binding.gameView.setPlaySound(PlaySounds.SWIPE_GAME)
                saveUndoState()
                moveVertical(false)
                binding.gameView.setAnimation(AnimationMoves.DOWN)
                nextNumber()
                saveState()
            }
            override fun onSwiperToLeft() {
                binding.gameView.setPlaySound(PlaySounds.SWIPE_GAME)
                saveUndoState()
                moveHorizontal(false)
                binding.gameView.setAnimation(AnimationMoves.LEFT)
                nextNumber()
                saveState()
            }
        })
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
            removeUndoState()
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
}