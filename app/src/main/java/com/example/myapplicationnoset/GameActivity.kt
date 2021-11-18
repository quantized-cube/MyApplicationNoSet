package com.example.myapplicationnoset

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.math.max

class GameActivity : AppCompatActivity() {

    companion object {
        const val SETTED = "setted"
    }

    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val dw = dm.widthPixels
        val cellLen: Int = dw * 2 / 9
        buttonSquare(cellLen)

        val buttonLenBottom: Int = dw * 2 / 5
        buttonPosition.width = buttonLenBottom
        buttonLetter.width = buttonLenBottom
        buttonColor.width = buttonLenBottom
        buttonShape.width = buttonLenBottom

        val intToCellID: Map<Int, Int> = mapOf(
            // int to cell_id
            1 to R.id.buttonNorthWest,
            2 to R.id.buttonNorth,
            3 to R.id.buttonNorthEast,
            4 to R.id.buttonWest,
            5 to R.id.buttonCenter,
            6 to R.id.buttonEast,
            7 to R.id.buttonSouthWest,
            8 to R.id.buttonSouth,
            9 to R.id.buttonSouthEast
        )

        @Suppress("UNCHECKED_CAST")
        val levelTypesN = intent.getSerializableExtra(SETTED) as ArrayList<Int>
        val levelInt = levelTypesN[0]
        val typesInt = levelTypesN[1]
        val nInt = levelTypesN[2]
        val totalLives = levelTypesN[3]
        showMyLevel(levelInt, typesInt, nInt, totalLives)
        findViewById<TextView>(R.id.totalCorrect).text = getString(R.string.total, 0, 0)

        val weeklyRecords: SharedPreferences = getSharedPreferences("WeeklyRecords", Context.MODE_PRIVATE)
        weeklyRecordsToIntArray(weeklyRecords)

        var totalQuestions = -nInt
        var totalTrues = 0
        var totalFalses = 0
        var timeValue = 0  // timeInterval ms
        var answerFlag = 0
        var runFlag = 1

        var trueFlag: Int
        var positionFlag = 0
        var letterFlag = 0
        var colorFlag = 0
        var shapeFlag = 0

        var gameOverFlag = 0

        var cellNow: Int = intToCellID[1]!!

        // 位置などの履歴
        val latestPos: Array<Int> = Array(nInt + 1, { 0 })
        val latestLetters: Array<String> = Array(nInt + 1, { "A" })
        val latestColors: Array<Int> = Array(nInt + 1, { 0 })
        val latestShapes: Array<Int> = Array(nInt + 1, { 0 })
        var modNum = 0
        var modNumLast = 1

        val runnable = object : Runnable {
            override fun run() {
                timeValue++
                if (totalQuestions == -nInt) {
                    if (timeValue == 1) {
                        cellShow(R.id.buttonWest, "D", 1, 1)
                        cellShow(R.id.buttonNorth, "N", 2, 2)
                        cellShow(R.id.buttonSouthEast, "B", 3, 1)
                    } else if (timeValue == TimeAnswer.toInt()) {
                        cellTrans(R.id.buttonWest)
                        cellTrans(R.id.buttonNorth)
                        cellTrans(R.id.buttonSouthEast)
                    } else if (timeValue >= TimeAnswer + TimeNext) {
                        totalQuestions++
                        timeValue = 0
                    }
                } else {
                    if (timeValue == 1) {
                        // 問題表示
                        // number
                        var rand_int = (1..9).random()
                        if (totalQuestions >= 1) {
                            val rand_int_bias = (1..7).random()
                            if (rand_int_bias <= 3) {
                                rand_int = latestPos[modNumLast]
                            }
                        }
                        latestPos[modNum] = rand_int
                        cellNow = intToCellID[rand_int]!!

                        // string
                        var rand_str = "A"
                        if (typesInt >= 2) {
                            rand_str = LETTERS.random().toString()
                            if (totalQuestions >= 1) {
                                val rand_str_bias = (1..9).random()
                                if (rand_str_bias <= 4) {
                                    rand_str = latestLetters[modNumLast]
                                }
                            }
                            latestLetters[modNum] = rand_str
                        }

                        // color
                        var rand_color = 1
                        if (typesInt >= 3) {
                            rand_color = (1..3).random()
                            latestColors[modNum] = rand_color
                        }

                        // shape
                        var rand_shape = 1
                        if (typesInt == 4) {
                            rand_shape = (1..2).random()
                            latestShapes[modNum] = rand_shape
                        }

                        cellShow(cellNow, rand_str, rand_color, rand_shape)

                    } else if (timeValue == TimeShowing.toInt()) {
                        // 問題表示消去
                        cellTrans(cellNow)
                    } else if ((timeValue >= TimeAnswer) and (answerFlag == 1)) {
                        // 答え合わせ
                        answerFlag = 0
                        var falseCount = 0

                        if (((latestPos[modNum] == latestPos[modNumLast]) and (positionFlag == 1)) or
                            ((latestPos[modNum] != latestPos[modNumLast]) and (positionFlag == 0))
                        ) {
                            buttonToTrue(R.id.buttonPosition)
                            trueFlag = 1
                        } else {
                            buttonToFalse(R.id.buttonPosition)
                            falseCount++
                            trueFlag = 0
                        }
                        if (typesInt >= 2) {
                            if (((latestLetters[modNum] == latestLetters[modNumLast]) and (letterFlag == 1)) or
                                ((latestLetters[modNum] != latestLetters[modNumLast]) and (letterFlag == 0))
                            ) {
                                buttonToTrue(R.id.buttonLetter)
                            } else {
                                buttonToFalse(R.id.buttonLetter)
                                falseCount++
                                trueFlag = 0
                            }
                        }
                        if (typesInt >= 3) {
                            if (((latestColors[modNum] == latestColors[modNumLast]) and (colorFlag == 1)) or
                                ((latestColors[modNum] != latestColors[modNumLast]) and (colorFlag == 0))
                            ) {
                                buttonToTrue(R.id.buttonColor)
                            } else {
                                buttonToFalse(R.id.buttonColor)
                                falseCount++
                                trueFlag = 0
                            }
                        }
                        if (typesInt == 4) {
                            if (((latestShapes[modNum] == latestShapes[modNumLast]) and (shapeFlag == 1)) or
                                ((latestShapes[modNum] != latestShapes[modNumLast]) and (shapeFlag == 0))
                            ) {
                                buttonToTrue(R.id.buttonShape)
                            } else {
                                buttonToFalse(R.id.buttonShape)
                                falseCount++
                                trueFlag = 0
                            }
                        }

                        if (trueFlag == 1) {
                            totalTrues++
                        }
                        findViewById<TextView>(R.id.totalCorrect).text =
                            getString(R.string.total, totalTrues, totalQuestions)
                        totalFalses += falseCount
                        val lives_remain = max(totalLives - totalFalses, 0)
                        findViewById<TextView>(R.id.life).text =
                            getString(R.string.lives, lives_remain, totalLives)
                        if ((totalLives > 0) and (lives_remain <= 0)) {
                            gameOverFlag = 1
                        }
                    } else if (timeValue >= TimeAnswer + TimeNext) {
                        // 次の問いへ
                        totalQuestions++

                        if (modNum < nInt) {
                            modNum++
                        } else {
                            modNum = 0
                        }

                        if (modNumLast < nInt) {
                            modNumLast++
                        } else {
                            modNumLast = 0
                        }

                        if (totalQuestions >= 1) {
                            answerFlag = 1
                            findViewById<TextView>(R.id.totalCorrect).text =
                                getString(R.string.total, totalTrues, totalQuestions)
                            buttonToOff(R.id.buttonPosition)
                            positionFlag = 0
                            if (typesInt >= 2) {
                                buttonToOff(R.id.buttonLetter)
                                letterFlag = 0
                            }
                            if (typesInt >= 3) {
                                buttonToOff(R.id.buttonColor)
                                colorFlag = 0
                            }
                            if (typesInt == 4) {
                                buttonToOff(R.id.buttonShape)
                                shapeFlag = 0
                            }
                        }

                        timeValue = 0
                    }
                }

                if (gameOverFlag == 0) {
                    handler.postDelayed(this, TimeInterval)
                } else {
                    handler.removeCallbacks(this)
                    runFlag = 0

                    // その日に解いた回数
                    val rt0 = weeklyRecords.getInt(RecentTrials.RT0.name, RecentTrials.RT0.defval)
                    val editorRec = weeklyRecords.edit()
                    editorRec.putInt(RecentTrials.RT0.name, rt0 + totalQuestions)
                    editorRec.apply()

                    // ハイスコア
                    val newScoreFlag: Int
                    if (totalLives == 10) {
                        val highScoreStore: SharedPreferences =
                            getSharedPreferences("HighScoreStore", Context.MODE_PRIVATE)

                        val oldHighScore: Int = when (levelInt) {
                            0 -> highScoreStore.getInt(HighScoreSave.HS0.name, HighScoreSave.HS0.defval)
                            1 -> highScoreStore.getInt(HighScoreSave.HS1.name, HighScoreSave.HS1.defval)
                            2 -> highScoreStore.getInt(HighScoreSave.HS2.name, HighScoreSave.HS2.defval)
                            3 -> highScoreStore.getInt(HighScoreSave.HS3.name, HighScoreSave.HS3.defval)
                            4 -> highScoreStore.getInt(HighScoreSave.HS4.name, HighScoreSave.HS4.defval)
                            5 -> highScoreStore.getInt(HighScoreSave.HS5.name, HighScoreSave.HS5.defval)
                            6 -> highScoreStore.getInt(HighScoreSave.HS6.name, HighScoreSave.HS6.defval)
                            7 -> highScoreStore.getInt(HighScoreSave.HS7.name, HighScoreSave.HS7.defval)
                            8 -> highScoreStore.getInt(HighScoreSave.HS8.name, HighScoreSave.HS8.defval)
                            9 -> highScoreStore.getInt(HighScoreSave.HS9.name, HighScoreSave.HS9.defval)
                            10 -> highScoreStore.getInt(HighScoreSave.HS10.name, HighScoreSave.HS10.defval)
                            11 -> highScoreStore.getInt(HighScoreSave.HS11.name, HighScoreSave.HS11.defval)
                            12 -> highScoreStore.getInt(HighScoreSave.HS12.name, HighScoreSave.HS12.defval)
                            13 -> highScoreStore.getInt(HighScoreSave.HS13.name, HighScoreSave.HS13.defval)
                            14 -> highScoreStore.getInt(HighScoreSave.HS14.name, HighScoreSave.HS14.defval)
                            15 -> highScoreStore.getInt(HighScoreSave.HS15.name, HighScoreSave.HS15.defval)
                            16 -> highScoreStore.getInt(HighScoreSave.HS16.name, HighScoreSave.HS16.defval)
                            17 -> highScoreStore.getInt(HighScoreSave.HS17.name, HighScoreSave.HS17.defval)
                            18 -> highScoreStore.getInt(HighScoreSave.HS18.name, HighScoreSave.HS18.defval)
                            19 -> highScoreStore.getInt(HighScoreSave.HS19.name, HighScoreSave.HS19.defval)
                            20 -> highScoreStore.getInt(HighScoreSave.HS20.name, HighScoreSave.HS20.defval)
                            else -> 0
                        }

                        if (totalQuestions > oldHighScore) {
                            newScoreFlag = 1
                            val editor = highScoreStore.edit()

                            when (levelInt) {
                                0 -> editor.putInt(HighScoreSave.HS0.name, totalQuestions)
                                1 -> editor.putInt(HighScoreSave.HS1.name, totalQuestions)
                                2 -> editor.putInt(HighScoreSave.HS2.name, totalQuestions)
                                3 -> editor.putInt(HighScoreSave.HS3.name, totalQuestions)
                                4 -> editor.putInt(HighScoreSave.HS4.name, totalQuestions)
                                5 -> editor.putInt(HighScoreSave.HS5.name, totalQuestions)
                                6 -> editor.putInt(HighScoreSave.HS6.name, totalQuestions)
                                7 -> editor.putInt(HighScoreSave.HS7.name, totalQuestions)
                                8 -> editor.putInt(HighScoreSave.HS8.name, totalQuestions)
                                9 -> editor.putInt(HighScoreSave.HS9.name, totalQuestions)
                                10 -> editor.putInt(HighScoreSave.HS10.name, totalQuestions)
                                11 -> editor.putInt(HighScoreSave.HS11.name, totalQuestions)
                                12 -> editor.putInt(HighScoreSave.HS12.name, totalQuestions)
                                13 -> editor.putInt(HighScoreSave.HS13.name, totalQuestions)
                                14 -> editor.putInt(HighScoreSave.HS14.name, totalQuestions)
                                15 -> editor.putInt(HighScoreSave.HS15.name, totalQuestions)
                                16 -> editor.putInt(HighScoreSave.HS16.name, totalQuestions)
                                17 -> editor.putInt(HighScoreSave.HS17.name, totalQuestions)
                                18 -> editor.putInt(HighScoreSave.HS18.name, totalQuestions)
                                19 -> editor.putInt(HighScoreSave.HS19.name, totalQuestions)
                                20 -> editor.putInt(HighScoreSave.HS20.name, totalQuestions)
                            }

                            editor.apply()
                        } else {
                            newScoreFlag = 0
                        }
                    } else {
                        newScoreFlag = 0
                    }

                    AlertDialog.Builder(this@GameActivity).apply {
                        setTitle("GAME OVER")
                        if (newScoreFlag == 1) {
                            setMessage(
                                "Conguratulations!\n" +
                                        "New Best Score: " + totalQuestions.toString() +
                                        "\nPlay again?"
                            )
                        } else {
                            setMessage("Play again?")
                        }
                        setNegativeButton("YES, RESTART") { _, _ -> recreate() }
                        setPositiveButton("NO, EXIT") { _, _ ->
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        setOnCancelListener {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        show()
                    }
                }
            }
        }

        handler.post(runnable)

        buttonPauseGame.setOnClickListener {
            if (runFlag == 1) {
                handler.removeCallbacks(runnable)
                findViewById<TextView>(R.id.buttonPauseGame).text = getString(R.string.resume)
                runFlag = 0
            } else {
                handler.post(runnable)
                findViewById<TextView>(R.id.buttonPauseGame).text = getString(R.string.pause)
                runFlag = 1
            }
        }

        buttonExitGame.setOnClickListener {
            val runFlagTemp: Int
            if (runFlag == 1) {
                runFlagTemp = 1
                handler.removeCallbacks(runnable)
                runFlag = 0
            } else {
                runFlagTemp = 0
            }

            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.apply {
                setTitle("EXIT")
                setMessage("Want to exit?")
                setNeutralButton("restart") { _, _ -> recreate() }
                setNegativeButton("no, continue") { _, _ ->
                    if (runFlagTemp == 1) {
                        handler.post(runnable)
                        runFlag = 1
                    }
                }
                setPositiveButton("yes, exit") { _, _ -> finish() }
            }
            val alertDiaglog = alertBuilder.create()
            alertDiaglog.setCanceledOnTouchOutside(false)
            alertDiaglog.setOnCancelListener {
                if (runFlagTemp == 1) {
                    handler.post(runnable)
                    runFlag = 1
                }
            }
            alertDiaglog.show()
        }

        // 下のボタンたち
        buttonPosition.setOnClickListener {
            if (answerFlag == 1 && runFlag == 1) {
                if (positionFlag == 0) {
                    buttonToOn(R.id.buttonPosition)
                    positionFlag = 1
                } else {
                    buttonToOff(R.id.buttonPosition)
                    positionFlag = 0
                }
            }
        }

        buttonLetter.setOnClickListener {
            if ((answerFlag == 1) && (typesInt >= 2) && (runFlag == 1)) {
                if (letterFlag == 0) {
                    buttonToOn(R.id.buttonLetter)
                    letterFlag = 1
                } else {
                    buttonToOff(R.id.buttonLetter)
                    letterFlag = 0
                }
            }
        }

        buttonColor.setOnClickListener {
            if ((answerFlag == 1) && (typesInt >= 3) && (runFlag == 1)) {
                if (colorFlag == 0) {
                    buttonToOn(R.id.buttonColor)
                    colorFlag = 1
                } else {
                    buttonToOff(R.id.buttonColor)
                    colorFlag = 0
                }
            }
        }

        buttonShape.setOnClickListener {
            if ((answerFlag == 1) && (typesInt >= 4) && (runFlag == 1)) {
                if (shapeFlag == 0) {
                    buttonToOn(R.id.buttonShape)
                    shapeFlag = 1
                } else {
                    buttonToOff(R.id.buttonShape)
                    shapeFlag = 0
                }
            }
        }

    }

    override fun onBackPressed() {
        buttonExitGame.callOnClick()
    }

    fun showMyLevel(levelInt: Int, typesInt: Int, nInt: Int, totalLives: Int) {
        findViewById<TextView>(R.id.levelGame).text = getString(R.string.level_setting, levelInt)
        findViewById<TextView>(R.id.typesGame).text = getString(R.string.types_setting, typesInt)
        findViewById<TextView>(R.id.nGame).text = getString(R.string.n_setting, nInt)
        if (typesInt <= 3) {
            cellTrans(R.id.buttonShape)
        }
        if (typesInt <= 2) {
            cellTrans(R.id.buttonColor)
        }
        if (typesInt == 1) {
            cellTrans(R.id.buttonLetter)
        }
        findViewById<TextView>(R.id.life).text =
            getString(R.string.lives, totalLives, totalLives)
    }

    fun buttonSquare(buttonLen: Int) {
        buttonNorthWest.width = buttonLen
        buttonNorthWest.height = buttonLen
        buttonNorth.width = buttonLen
        buttonNorth.height = buttonLen
        buttonNorthEast.width = buttonLen
        buttonNorthEast.height = buttonLen
        buttonWest.width = buttonLen
        buttonWest.height = buttonLen
        buttonCenter.width = buttonLen
        buttonCenter.height = buttonLen
        buttonEast.width = buttonLen
        buttonEast.height = buttonLen
        buttonSouthWest.width = buttonLen
        buttonSouthWest.height = buttonLen
        buttonSouth.width = buttonLen
        buttonSouth.height = buttonLen
        buttonSouthEast.width = buttonLen
        buttonSouthEast.height = buttonLen
    }

    fun cellShow(cellID: Int, str: String = "A", cl: Int = 1, shp: Int = 1) {
        findViewById<TextView>(cellID).text = str
        if (shp == 1) {
            when (cl) {
                1 -> {
                    findViewById<TextView>(cellID).background = getDrawable(R.drawable.square_button_1)
                }
                2 -> {
                    findViewById<TextView>(cellID).background = getDrawable(R.drawable.square_button_2)
                }
                3 -> {
                    findViewById<TextView>(cellID).background = getDrawable(R.drawable.square_button_3)
                }
            }
        } else {
            when (cl) {
                1 -> {
                    findViewById<TextView>(cellID).background = getDrawable(R.drawable.round_button_1)
                }
                2 -> {
                    findViewById<TextView>(cellID).background = getDrawable(R.drawable.round_button_2)
                }
                3 -> {
                    findViewById<TextView>(cellID).background = getDrawable(R.drawable.round_button_3)
                }
            }
        }
    }

    fun cellTrans(cellID: Int) {
        findViewById<TextView>(cellID).setBackgroundColor(getColor(R.color.buttonTrans))
        findViewById<TextView>(cellID).text = null
    }

    fun buttonToOff(button: Int) {
        findViewById<TextView>(button).setBackgroundColor(getColor(R.color.buttonOff))
    }

    fun buttonToOn(button: Int) {
        findViewById<TextView>(button).setBackgroundColor(getColor(R.color.buttonOn))
    }

    fun buttonToTrue(button: Int) {
        findViewById<TextView>(button).setBackgroundColor(getColor(R.color.buttonTrue))
    }

    fun buttonToFalse(button: Int) {
        findViewById<TextView>(button).setBackgroundColor(getColor(R.color.buttonFalse))
    }
}
