package com.example.myapplicationnoset

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

const val TimeInterval: Long = 50  // 内部的には 50 ms ごとに処理
const val TimeShowing: Long = 1000 / TimeInterval  // 表示時間 1.0 s
const val TimeAnswer: Long = 3000 / TimeInterval  // 回答時間 3.0 s
const val TimeNext: Long = 1500 / TimeInterval  // 次の問題までの時間 1.5 s

const val LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

const val GAME_REQUEST = 2
const val HS_REQUEST = 3

val dtFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
val dtFormatterShort: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d")

val levelsToTypesN: Map<Int, List<Int>> = mapOf(
    // level to (type, N)
    0 to listOf(1, 1),
    1 to listOf(2, 1),
    2 to listOf(2, 2),
    3 to listOf(2, 3),
    4 to listOf(3, 2),
    5 to listOf(2, 4),
    6 to listOf(2, 5),
    7 to listOf(3, 3),
    8 to listOf(3, 4),
    9 to listOf(3, 5),
    10 to listOf(4, 2),
    11 to listOf(4, 3),
    12 to listOf(4, 4),
    13 to listOf(4, 5),
    14 to listOf(4, 6),
    15 to listOf(4, 7),
    16 to listOf(4, 8),
    17 to listOf(4, 9),
    18 to listOf(4, 10),
    19 to listOf(4, 11),
    20 to listOf(4, 12)
)
// (type, N) to level
val typesNToLevels = levelsToTypesN.entries.associate { (k, v) -> v to k }

fun levelToTypes(levelInt: Int): Int {
    val typesInt = levelsToTypesN[levelInt]?.get(0) ?: 1
    return typesInt
}

fun levelToN(levelInt: Int): Int {
    val nInt = levelsToTypesN[levelInt]?.get(1) ?: 1
    return nInt
}

fun typesNToLevel(types: Int, n: Int): Int {
    val levelInt = typesNToLevels[listOf(types, n)] ?: 0
    return levelInt
}

enum class KeysToSave(val defval: Int) {
    LevelSaved(0),
    LivesSaved(10)
}

enum class HighScoreSave(val defval: Int) {
    HS0(0),
    HS1(0),
    HS2(0),
    HS3(0),
    HS4(0),
    HS5(0),
    HS6(0),
    HS7(0),
    HS8(0),
    HS9(0),
    HS10(0),
    HS11(0),
    HS12(0),
    HS13(0),
    HS14(0),
    HS15(0),
    HS16(0),
    HS17(0),
    HS18(0),
    HS19(0),
    HS20(0)
}

enum class RecentTrials(val defval: Int) {
    Today(20191201),
    RT0(0),
    RT1(0),
    RT2(0),
    RT3(0),
    RT4(0),
    RT5(0),
    RT6(0)
}

fun weeklyRecordsToIntArray(weeklyRecords: SharedPreferences): Pair<IntArray, Array<String>> {
    val lastPlayDateInt: Int = weeklyRecords.getInt(RecentTrials.Today.name, RecentTrials.Today.defval)
    val lastPlayDate: LocalDate = LocalDate.parse(lastPlayDateInt.toString(), dtFormatter)

    val localDate: LocalDate = LocalDate.now()

    val localDateStr0: String = localDate.format(dtFormatterShort)
    val localDateStr1: String = localDate.minusDays(1).format(dtFormatterShort)
    val localDateStr2: String = localDate.minusDays(2).format(dtFormatterShort)
    val localDateStr3: String = localDate.minusDays(3).format(dtFormatterShort)
    val localDateStr4: String = localDate.minusDays(4).format(dtFormatterShort)
    val localDateStr5: String = localDate.minusDays(5).format(dtFormatterShort)
    val localDateStr6: String = localDate.minusDays(6).format(dtFormatterShort)

    val daysStr: Array<String> =
        arrayOf(localDateStr6, localDateStr5, localDateStr4, localDateStr3, localDateStr2, localDateStr1, localDateStr0)

    val localDateInt: Int = localDate.format(dtFormatter).toInt()
    val diffDate = Period.between(lastPlayDate, localDate)
    val diffInt = diffDate.days

    val rt0 = weeklyRecords.getInt(RecentTrials.RT0.name, RecentTrials.RT0.defval)
    val rt1 = weeklyRecords.getInt(RecentTrials.RT1.name, RecentTrials.RT1.defval)
    val rt2 = weeklyRecords.getInt(RecentTrials.RT2.name, RecentTrials.RT2.defval)
    val rt3 = weeklyRecords.getInt(RecentTrials.RT3.name, RecentTrials.RT3.defval)
    val rt4 = weeklyRecords.getInt(RecentTrials.RT4.name, RecentTrials.RT4.defval)
    val rt5 = weeklyRecords.getInt(RecentTrials.RT5.name, RecentTrials.RT5.defval)
    val rt6 = weeklyRecords.getInt(RecentTrials.RT6.name, RecentTrials.RT6.defval)

    val rts = intArrayOf(rt6, rt5, rt4, rt3, rt2, rt1, rt0)

    if (diffInt != 0) {
        if (diffInt > 0) {
            for (i in 0..6) {
                rts[i] = if (i + diffInt <= 6) {
                    rts[i + diffInt]
                } else {
                    0
                }
            }
        } else if (diffInt < 0) {
            // ないとは思うが
            for (i in 6 downTo 0) {
                rts[i] = if (i + diffInt >= 0) {
                    rts[i + diffInt]
                } else {
                    0
                }
            }
        }
        val editor = weeklyRecords.edit()
        editor.putInt(RecentTrials.Today.name, localDateInt)
        editor.putInt(RecentTrials.RT6.name, rts[0])
        editor.putInt(RecentTrials.RT5.name, rts[1])
        editor.putInt(RecentTrials.RT4.name, rts[2])
        editor.putInt(RecentTrials.RT3.name, rts[3])
        editor.putInt(RecentTrials.RT2.name, rts[4])
        editor.putInt(RecentTrials.RT1.name, rts[5])
        editor.putInt(RecentTrials.RT0.name, rts[6])
        editor.apply()
    }

    return Pair(rts, daysStr)
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // title
        setTitle(R.string.label_main)

        // 直近7日間の記録
        val weeklyRecords: SharedPreferences = getSharedPreferences("WeeklyRecords", Context.MODE_PRIVATE)
        val p = weeklyRecordsToIntArray(weeklyRecords)
        val rts = p.first
        val daysStr = p.second

        findViewById<TextView>(R.id.rec6).text = getString(R.string.rec, rts[0])
        findViewById<TextView>(R.id.rec5).text = getString(R.string.rec, rts[1])
        findViewById<TextView>(R.id.rec4).text = getString(R.string.rec, rts[2])
        findViewById<TextView>(R.id.rec3).text = getString(R.string.rec, rts[3])
        findViewById<TextView>(R.id.rec2).text = getString(R.string.rec, rts[4])
        findViewById<TextView>(R.id.rec1).text = getString(R.string.rec, rts[5])
        findViewById<TextView>(R.id.rec0).text = getString(R.string.rec, rts[6])

        findViewById<TextView>(R.id.recd6).text = daysStr[0]
        findViewById<TextView>(R.id.recd5).text = daysStr[1]
        findViewById<TextView>(R.id.recd4).text = daysStr[2]
        findViewById<TextView>(R.id.recd3).text = daysStr[3]
        findViewById<TextView>(R.id.recd2).text = daysStr[4]
        findViewById<TextView>(R.id.recd1).text = daysStr[5]
        findViewById<TextView>(R.id.recd0).text = daysStr[6]

        // 保存された設定
        val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        var levelInt = dataStore.getInt(KeysToSave.LevelSaved.name, KeysToSave.LevelSaved.defval)
        var typesInt = levelToTypes(levelInt)
        var nInt = levelToN(levelInt)
        var livesInt = dataStore.getInt(KeysToSave.LivesSaved.name, KeysToSave.LivesSaved.defval)

        findViewById<TextView>(R.id.levelSettingText).text = getString(R.string.level_setting, levelInt)
        findViewById<TextView>(R.id.typesSettingText).text = getString(R.string.types_setting, typesInt)
        findViewById<TextView>(R.id.nSettingText).text = getString(R.string.n_setting, nInt)
        findViewById<TextView>(R.id.livesSettingText).text = getString(R.string.lives_setting, livesInt)

        // レベル上下
        levelUpButton.setOnClickListener {
            if (levelInt < 20) {
                levelInt++
            }
            findViewById<TextView>(R.id.levelSettingText).text = getString(R.string.level_setting, levelInt)
            typesInt = levelToTypes(levelInt)
            findViewById<TextView>(R.id.typesSettingText).text = getString(R.string.types_setting, typesInt)
            nInt = levelToN(levelInt)
            findViewById<TextView>(R.id.nSettingText).text = getString(R.string.n_setting, nInt)
        }
        levelUUpButton.setOnClickListener {
            when {
                levelInt < 5 -> {
                    levelInt = 5
                }
                levelInt < 10 -> {
                    levelInt = 10
                }
                levelInt < 15 -> {
                    levelInt = 15
                }
                else -> {
                    levelInt = 20
                }
            }
            findViewById<TextView>(R.id.levelSettingText).text = getString(R.string.level_setting, levelInt)
            typesInt = levelToTypes(levelInt)
            findViewById<TextView>(R.id.typesSettingText).text = getString(R.string.types_setting, typesInt)
            nInt = levelToN(levelInt)
            findViewById<TextView>(R.id.nSettingText).text = getString(R.string.n_setting, nInt)
        }

        levelDownButton.setOnClickListener {
            if (levelInt > 0) {
                levelInt--
            }
            findViewById<TextView>(R.id.levelSettingText).text = getString(R.string.level_setting, levelInt)
            typesInt = levelToTypes(levelInt)
            findViewById<TextView>(R.id.typesSettingText).text = getString(R.string.types_setting, typesInt)
            nInt = levelToN(levelInt)
            findViewById<TextView>(R.id.nSettingText).text = getString(R.string.n_setting, nInt)
        }
        levelDDownButton.setOnClickListener {
            when {
                levelInt > 15 -> {
                    levelInt = 15
                }
                levelInt > 10 -> {
                    levelInt = 10
                }
                levelInt > 5 -> {
                    levelInt = 5
                }
                else -> {
                    levelInt = 0
                }
            }
            findViewById<TextView>(R.id.levelSettingText).text = getString(R.string.level_setting, levelInt)
            typesInt = levelToTypes(levelInt)
            findViewById<TextView>(R.id.typesSettingText).text = getString(R.string.types_setting, typesInt)
            nInt = levelToN(levelInt)
            findViewById<TextView>(R.id.nSettingText).text = getString(R.string.n_setting, nInt)
        }

        // タイプ上下
        typesUpButton.setOnClickListener {
            if (typesInt < 4) {
                typesInt++
            }
            findViewById<TextView>(R.id.typesSettingText).text = getString(R.string.types_setting, typesInt)
            while (!levelsToTypesN.containsValue(listOf(typesInt, nInt))) {
                nInt++
            }
            findViewById<TextView>(R.id.nSettingText).text = getString(R.string.n_setting, nInt)
            levelInt = typesNToLevel(typesInt, nInt)
            findViewById<TextView>(R.id.levelSettingText).text = getString(R.string.level_setting, levelInt)
        }

        typesDonwButton.setOnClickListener {
            if (typesInt > 1) {
                typesInt--
            }
            findViewById<TextView>(R.id.typesSettingText).text = getString(R.string.types_setting, typesInt)
            while (!levelsToTypesN.containsValue(listOf(typesInt, nInt))) {
                nInt--
            }
            findViewById<TextView>(R.id.nSettingText).text = getString(R.string.n_setting, nInt)
            levelInt = typesNToLevel(typesInt, nInt)
            findViewById<TextView>(R.id.levelSettingText).text = getString(R.string.level_setting, levelInt)
        }

        // N 上下
        nUpButton.setOnClickListener {
            if (nInt < 12) {
                nInt++
            }
            findViewById<TextView>(R.id.nSettingText).text = getString(R.string.n_setting, nInt)
            while (!levelsToTypesN.containsValue(listOf(typesInt, nInt))) {
                typesInt++
            }
            findViewById<TextView>(R.id.typesSettingText).text = getString(R.string.types_setting, typesInt)
            levelInt = typesNToLevel(typesInt, nInt)
            findViewById<TextView>(R.id.levelSettingText).text = getString(R.string.level_setting, levelInt)
        }
        nDownButton.setOnClickListener {
            if (nInt > 1) {
                nInt--
            }
            findViewById<TextView>(R.id.nSettingText).text = getString(R.string.n_setting, nInt)
            while (!levelsToTypesN.containsValue(listOf(typesInt, nInt))) {
                typesInt--
            }
            findViewById<TextView>(R.id.typesSettingText).text = getString(R.string.types_setting, typesInt)
            levelInt = typesNToLevel(typesInt, nInt)
            findViewById<TextView>(R.id.levelSettingText).text = getString(R.string.level_setting, levelInt)
        }

        // ライフ上下
        livesUpButton.setOnClickListener {
            if (livesInt < 20) {
                livesInt++
            }
            findViewById<TextView>(R.id.livesSettingText).text = getString(R.string.lives_setting, livesInt)
        }
        livesUUpButton.setOnClickListener {
            when {
                livesInt < 5 -> {
                    livesInt = 5
                }
                livesInt < 10 -> {
                    livesInt = 10
                }
                livesInt < 15 -> {
                    livesInt = 15
                }
                else -> {
                    livesInt = 20
                }
            }
            findViewById<TextView>(R.id.livesSettingText).text = getString(R.string.lives_setting, livesInt)
        }

        livesDownButton.setOnClickListener {
            if (livesInt > 0) {
                livesInt--
            }
            findViewById<TextView>(R.id.livesSettingText).text = getString(R.string.lives_setting, livesInt)
        }
        livesDDownButton.setOnClickListener {
            when {
                livesInt > 15 -> {
                    livesInt = 15
                }
                livesInt > 10 -> {
                    livesInt = 10
                }
                livesInt > 5 -> {
                    livesInt = 5
                }
                else -> {
                    livesInt = 0
                }
            }
            findViewById<TextView>(R.id.livesSettingText).text = getString(R.string.lives_setting, livesInt)
        }

        // ゲームスタート
        startButton.setOnClickListener {
            val myGameIntent = Intent(this, GameActivity::class.java)
            val levelTypesN = arrayListOf(levelInt, typesInt, nInt, livesInt)
            myGameIntent.putExtra(GameActivity.SETTED, levelTypesN)

            val editor = dataStore.edit()
            editor.putInt(KeysToSave.LevelSaved.name, levelInt)
            editor.putInt(KeysToSave.LivesSaved.name, livesInt)
            editor.apply()

            startActivityForResult(myGameIntent, GAME_REQUEST)
        }

        // ハイスコア画面へ
        highScoreButton.setOnClickListener {
            val editor = dataStore.edit()
            editor.putInt(KeysToSave.LevelSaved.name, levelInt)
            editor.putInt(KeysToSave.LivesSaved.name, livesInt)
            editor.apply()

            val myScoreIntent = Intent(this, ScoreActivity::class.java)
            startActivityForResult(myScoreIntent, HS_REQUEST)
        }

        // アプリ終了
        exitAllButton.setOnClickListener {
            val editor = dataStore.edit()
            editor.putInt(KeysToSave.LevelSaved.name, levelInt)
            editor.putInt(KeysToSave.LivesSaved.name, livesInt)
            editor.apply()
            finish()
        }
    }

    // ゲームから戻ってきたときの処理
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            // Activity 遷移前後の日付
            val weeklyRecords: SharedPreferences = getSharedPreferences("WeeklyRecords", Context.MODE_PRIVATE)
            val lastPlayDateInt: Int = weeklyRecords.getInt(RecentTrials.Today.name, RecentTrials.Today.defval)
            val localDateInt: Int = LocalDate.now().format(dtFormatter).toInt()

            if ((requestCode == GAME_REQUEST) || ((requestCode == HS_REQUEST) && (lastPlayDateInt != localDateInt))) {
                val p = weeklyRecordsToIntArray(weeklyRecords)
                val rts = p.first
                val daysStr = p.second
                findViewById<TextView>(R.id.rec6).text = getString(R.string.rec, rts[0])
                findViewById<TextView>(R.id.rec5).text = getString(R.string.rec, rts[1])
                findViewById<TextView>(R.id.rec4).text = getString(R.string.rec, rts[2])
                findViewById<TextView>(R.id.rec3).text = getString(R.string.rec, rts[3])
                findViewById<TextView>(R.id.rec2).text = getString(R.string.rec, rts[4])
                findViewById<TextView>(R.id.rec1).text = getString(R.string.rec, rts[5])
                findViewById<TextView>(R.id.rec0).text = getString(R.string.rec, rts[6])
                findViewById<TextView>(R.id.recd6).text = daysStr[0]
                findViewById<TextView>(R.id.recd5).text = daysStr[1]
                findViewById<TextView>(R.id.recd4).text = daysStr[2]
                findViewById<TextView>(R.id.recd3).text = daysStr[3]
                findViewById<TextView>(R.id.recd2).text = daysStr[4]
                findViewById<TextView>(R.id.recd1).text = daysStr[5]
                findViewById<TextView>(R.id.recd0).text = daysStr[6]
            }
        }
    }

    override fun onBackPressed() {
        exitAllButton.callOnClick()
    }
}
