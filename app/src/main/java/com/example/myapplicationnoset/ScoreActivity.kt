package com.example.myapplicationnoset

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_score.*

class ScoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        val highScoreStore: SharedPreferences = getSharedPreferences("HighScoreStore", Context.MODE_PRIVATE)

        val hS0 = highScoreStore.getInt(HighScoreSave.HS0.name, HighScoreSave.HS0.defval)
        val hS1 = highScoreStore.getInt(HighScoreSave.HS1.name, HighScoreSave.HS1.defval)
        val hS2 = highScoreStore.getInt(HighScoreSave.HS2.name, HighScoreSave.HS2.defval)
        val hS3 = highScoreStore.getInt(HighScoreSave.HS3.name, HighScoreSave.HS3.defval)
        val hS4 = highScoreStore.getInt(HighScoreSave.HS4.name, HighScoreSave.HS4.defval)
        val hS5 = highScoreStore.getInt(HighScoreSave.HS5.name, HighScoreSave.HS5.defval)
        val hS6 = highScoreStore.getInt(HighScoreSave.HS6.name, HighScoreSave.HS6.defval)
        val hS7 = highScoreStore.getInt(HighScoreSave.HS7.name, HighScoreSave.HS7.defval)
        val hS8 = highScoreStore.getInt(HighScoreSave.HS8.name, HighScoreSave.HS8.defval)
        val hS9 = highScoreStore.getInt(HighScoreSave.HS9.name, HighScoreSave.HS9.defval)
        val hS10 = highScoreStore.getInt(HighScoreSave.HS10.name, HighScoreSave.HS10.defval)
        val hS11 = highScoreStore.getInt(HighScoreSave.HS11.name, HighScoreSave.HS11.defval)
        val hS12 = highScoreStore.getInt(HighScoreSave.HS12.name, HighScoreSave.HS12.defval)
        val hS13 = highScoreStore.getInt(HighScoreSave.HS13.name, HighScoreSave.HS13.defval)
        val hS14 = highScoreStore.getInt(HighScoreSave.HS14.name, HighScoreSave.HS14.defval)
        val hS15 = highScoreStore.getInt(HighScoreSave.HS15.name, HighScoreSave.HS15.defval)
        val hS16 = highScoreStore.getInt(HighScoreSave.HS16.name, HighScoreSave.HS16.defval)
        val hS17 = highScoreStore.getInt(HighScoreSave.HS17.name, HighScoreSave.HS17.defval)
        val hS18 = highScoreStore.getInt(HighScoreSave.HS18.name, HighScoreSave.HS18.defval)
        val hS19 = highScoreStore.getInt(HighScoreSave.HS19.name, HighScoreSave.HS19.defval)
        val hS20 = highScoreStore.getInt(HighScoreSave.HS20.name, HighScoreSave.HS20.defval)

        findViewById<TextView>(R.id.hsTextViewHS0).text = getString(R.string.hs_score, hS0)
        findViewById<TextView>(R.id.hsTextViewHS1).text = getString(R.string.hs_score, hS1)
        findViewById<TextView>(R.id.hsTextViewHS2).text = getString(R.string.hs_score, hS2)
        findViewById<TextView>(R.id.hsTextViewHS3).text = getString(R.string.hs_score, hS3)
        findViewById<TextView>(R.id.hsTextViewHS4).text = getString(R.string.hs_score, hS4)
        findViewById<TextView>(R.id.hsTextViewHS5).text = getString(R.string.hs_score, hS5)
        findViewById<TextView>(R.id.hsTextViewHS6).text = getString(R.string.hs_score, hS6)
        findViewById<TextView>(R.id.hsTextViewHS7).text = getString(R.string.hs_score, hS7)
        findViewById<TextView>(R.id.hsTextViewHS8).text = getString(R.string.hs_score, hS8)
        findViewById<TextView>(R.id.hsTextViewHS9).text = getString(R.string.hs_score, hS9)
        findViewById<TextView>(R.id.hsTextViewHS10).text = getString(R.string.hs_score, hS10)
        findViewById<TextView>(R.id.hsTextViewHS11).text = getString(R.string.hs_score, hS11)
        findViewById<TextView>(R.id.hsTextViewHS12).text = getString(R.string.hs_score, hS12)
        findViewById<TextView>(R.id.hsTextViewHS13).text = getString(R.string.hs_score, hS13)
        findViewById<TextView>(R.id.hsTextViewHS14).text = getString(R.string.hs_score, hS14)
        findViewById<TextView>(R.id.hsTextViewHS15).text = getString(R.string.hs_score, hS15)
        findViewById<TextView>(R.id.hsTextViewHS16).text = getString(R.string.hs_score, hS16)
        findViewById<TextView>(R.id.hsTextViewHS17).text = getString(R.string.hs_score, hS17)
        findViewById<TextView>(R.id.hsTextViewHS18).text = getString(R.string.hs_score, hS18)
        findViewById<TextView>(R.id.hsTextViewHS19).text = getString(R.string.hs_score, hS19)
        findViewById<TextView>(R.id.hsTextViewHS20).text = getString(R.string.hs_score, hS20)

        exitScoreButton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
