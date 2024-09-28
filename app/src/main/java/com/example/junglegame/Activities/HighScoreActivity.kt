package com.example.junglegame.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.junglegame.R
import com.example.junglegame.fragments.HighScoreListFragment
import com.example.junglegame.fragments.MapFragment

class HighScoreActivity : AppCompatActivity(), HighScoreListFragment.OnHighScoreSelectedListener {

    private lateinit var mapFragment: MapFragment
    private lateinit var highScores: List<Pair<Int, Int>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_score)
        if (savedInstanceState == null) {
            val highScoreListFragment = HighScoreListFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.high_score_list_fragment, highScoreListFragment)
                .commit()

            mapFragment = MapFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.map_fragment, mapFragment)
                .commit()
        }
        highScores = fetchTopHighScores()
    }


    override fun onHighScoreSelected(position: Int) {
        if (position < highScores.size) {
            val highScoreText = "High Score: ${highScores[position]}"
            mapFragment.addRandomMarker(highScoreText)
        }
    }
    fun fetchTopHighScores(): List<Pair<Int, Int>> {
        val sharedPreferences = getSharedPreferences("HighScorePrefs", MODE_PRIVATE)
        val highScoresString = sharedPreferences.getString("highScores", "") ?: ""
        val coinScoresString = sharedPreferences.getString("coinScores", "") ?: ""

        val highScores: List<Int> = if (highScoresString.isNotEmpty()) {
            highScoresString.split(",").map { it.toInt() }
        } else {
            listOf()
        }

        val coinScores: List<Int> = if (coinScoresString.isNotEmpty()) {
            coinScoresString.split(",").map { it.toInt() }
        } else {
            listOf()
        }
        return highScores.zip(coinScores).sortedByDescending { it.first }.take(10)
    }

}
