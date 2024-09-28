package com.example.junglegame.Activities

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.junglegame.R
import com.example.junglegame.Utillities.KangarooObstacle
import kotlin.random.Random

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var car: ImageView
    private lateinit var gameLayout: RelativeLayout
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var distanceText: TextView
    private lateinit var livesContainer: LinearLayout
    private var distance = 0
    private var screenWidth: Int = 0
    private val laneCount = 5
    private var currentLane = 3
    private var lives = 3
    private var isGameOver = false
    private lateinit var crashSound: MediaPlayer
    private var speed = 10f
    private val fastSpeed = 20f
    private var normalSpeed = 10f
    private var slowSpeed = 5f
    private val minObstacleSpeed = 5f
    private val maxObstacleSpeed = 30f
    private var controlMode: String? = null
    private var coinsCollected = 0
    private lateinit var coinTextView: TextView

    // List to track all active obstacles
    private val activeObstacles = mutableListOf<KangarooObstacle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()
        crashSound = MediaPlayer.create(this, R.raw.crash)

        controlMode = intent.getStringExtra("controlMode")
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!

        val displayMetrics = resources.displayMetrics
        screenWidth = displayMetrics.widthPixels

        if (controlMode == "fast") {
            speed = fastSpeed
        }

        updateCarPosition()
        showLives()
        startDistanceTracking()
        startSpawningObstacles()
        startSpawningCoins()
        when (controlMode) {
            "button", "fast" -> setupButtonControls()
            "sensor" -> setupSensorControls()
        }
    }

    private fun findViews() {
        car = findViewById(R.id.car)
        gameLayout = findViewById(R.id.game_layout)
        distanceText = findViewById(R.id.distance_text)
        livesContainer = findViewById(R.id.lives_container)
        coinTextView = findViewById(R.id.coin_text)
    }

    private fun setupButtonControls() {
        val leftButton: Button = findViewById(R.id.button_left)
        val rightButton: Button = findViewById(R.id.button_right)

        leftButton.setOnClickListener {
            moveCarLeft()
        }

        rightButton.setOnClickListener {
            moveCarRight()
        }

        leftButton.visibility = View.VISIBLE
        rightButton.visibility = View.VISIBLE
    }

    private fun setupSensorControls() {
        val leftButton: Button = findViewById(R.id.button_left)
        val rightButton: Button = findViewById(R.id.button_right)

        leftButton.visibility = View.GONE
        rightButton.visibility = View.GONE
    }

    private fun moveCarLeft() {
        if (currentLane > 1) {
            currentLane--
            updateCarPosition()
        }
    }

    private fun moveCarRight() {
        if (currentLane < laneCount) {
            currentLane++
            updateCarPosition()
        }
    }

    private fun updateCarPosition() {
        val laneWidth = screenWidth / laneCount
        val carX = (currentLane - 1) * laneWidth + (laneWidth - car.width) / 2
        car.x = carX.toFloat()
    }

    private fun startSpawningObstacles() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                if (!isGameOver) {
                    createRandomKangarooObstacle()
                }

                val randomDelay = Random.nextLong(1000, 2000)
                handler.postDelayed(this, randomDelay)
            }
        })
    }

    private fun createRandomKangarooObstacle() {
        val laneWidth = screenWidth / laneCount
        val randomLane = Random.nextInt(1, laneCount + 1)
        val obstacleX = (randomLane - 1) * laneWidth + (laneWidth - obstacleWidth) / 2

        // Create and track the new obstacle
        val obstacle = KangarooObstacle(this, gameLayout, screenWidth, car, speed) {
            onCollision()
        }
        obstacle.setPosition(obstacleX)

        // Add to the active obstacles list
        activeObstacles.add(obstacle)
    }

    private val obstacleWidth = 100

    private fun onCollision() {
        lives -= 1
        showLives()
        crashSound.start()

        if (lives <= 0) {
            gameOver()
        }
    }

    private fun gameOver() {
        isGameOver = true

        // Save the score and coins collected
        saveHighScores(distance, coinsCollected)

        // Redirect to the HighScoreActivity to display the updated high scores
        val intent = Intent(this, HighScoreActivity::class.java)
        startActivity(intent)
        finish() // End the current activity
    }

    private fun saveHighScores(newScore: Int, coinsCollected: Int) {
        val sharedPreferences = getSharedPreferences("HighScorePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Get the existing high scores and coins
        val existingScoresString = sharedPreferences.getString("highScores", "") ?: ""
        val existingCoinsString = sharedPreferences.getString("coinScores", "") ?: ""

        val highScores: MutableList<Int> = if (existingScoresString.isNotEmpty()) {
            existingScoresString.split(",").map { it.toInt() }.toMutableList()
        } else {
            mutableListOf()
        }

        val coinScores: MutableList<Int> = if (existingCoinsString.isNotEmpty()) {
            existingCoinsString.split(",").map { it.toInt() }.toMutableList()
        } else {
            mutableListOf()
        }

        // Add the new score and coins
        highScores.add(newScore)
        coinScores.add(coinsCollected)

        // Sort the lists in descending order by score
        highScores.sortDescending()
        coinScores.sortDescending()

        // Limit to the top 10
        if (highScores.size > 10) {
            highScores.subList(10, highScores.size).clear()
        }

        if (coinScores.size > 10) {
            coinScores.subList(10, coinScores.size).clear()
        }

        // Save the updated scores and coins as comma-separated strings
        val highScoreString = highScores.joinToString(",")
        val coinScoreString = coinScores.joinToString(",")
        editor.putString("highScores", highScoreString)
        editor.putString("coinScores", coinScoreString)
        editor.apply() // Apply changes asynchronously
    }

    private fun showLives() {
        livesContainer.removeAllViews()

        for (i in 0 until lives) {
            val koalaImage = ImageView(this)
            koalaImage.setImageResource(R.drawable.koala)

            val params = LinearLayout.LayoutParams(100, 100)
            params.setMargins(8, 0, 8, 0)
            koalaImage.layoutParams = params

            livesContainer.addView(koalaImage)
        }
    }

    private fun startDistanceTracking() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                if (!isGameOver) {
                    updateDistance()

                    // Adjust the delay based on speed (faster speed means shorter delay)
                    val delay = (1000 / (speed / 10)).toLong() // Higher speed means shorter delay
                    handler.postDelayed(this, delay)
                }
            }
        })
    }

    private fun updateDistance() {
        distance += 1
        distanceText.text = "Distance: $distance"
    }

    override fun onResume() {
        super.onResume()
        if (controlMode == "sensor") {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        if (controlMode == "sensor") {
            sensorManager.unregisterListener(this)
        }
    }
    private fun startSpawningCoins() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                if (!isGameOver) {
                    createRandomCoin()
                }

                // Random delay between coin spawns (2 to 5 seconds)
                val randomDelay = Random.nextLong(2000, 5000)
                handler.postDelayed(this, randomDelay)
            }
        })
    }

    private fun createRandomCoin() {
        // Randomly choose a lane
        val laneWidth = screenWidth / laneCount
        val randomLane = Random.nextInt(1, laneCount + 1) // Lanes are from 1 to laneCount

        // Create the coin ImageView and set its position
        val coinImage = ImageView(this)
        coinImage.setImageResource(R.drawable.coin) // Ensure "coin.png" exists in the drawable folder
        val params = RelativeLayout.LayoutParams(100, 100) // Adjust size as needed
        params.topMargin = 0 // Start at the top
        val coinX = (randomLane - 1) * laneWidth + (laneWidth / 2) // Position it in a random lane
        coinImage.x = coinX.toFloat()

        // Add the coin to the game layout
        runOnUiThread {
            gameLayout.addView(coinImage, params)
        }

        // Flag to prevent double removal
        var coinCollected = false

        // Move the coin down the screen and detect if the player collects it
        coinImage.post(object : Runnable {
            override fun run() {
                // Move the coin down by updating its translationY
                if (coinImage.y < gameLayout.height && !coinCollected) {
                    coinImage.translationY = coinImage.translationY + 10f // Adjust this value for speed

                    // Check for collision with the car
                    if (checkCollision(car, coinImage)) {
                        coinCollected = true // Prevent multiple removals
                        runOnUiThread {
                            gameLayout.removeView(coinImage) // Safely remove the coin from the layout
                        }
                        onCoinCollected(coinImage) // Handle coin collection (increase score, update UI, etc.)
                    } else {
                        coinImage.postDelayed(this, 16) // Continue moving the coin (~60fps)
                    }
                } else {
                    // If the coin moves off-screen, remove it
                    runOnUiThread {
                        if (!coinCollected) gameLayout.removeView(coinImage)
                    }
                }
            }
        })
    }
    private fun checkCollision(car: ImageView, coin: ImageView): Boolean {
        val carRect = Rect()
        val coinRect = Rect()

        car.getHitRect(carRect)
        coin.getHitRect(coinRect)



        return Rect.intersects(carRect, coinRect)
    }

    private fun onCoinCollected(coinImage: ImageView) {
        // Increase the player's coin count
        coinsCollected += 1
        updateCoinDisplay() // Update the UI to show the updated coin count

        // Safely remove the coin from the layout on the UI thread
        runOnUiThread {
            try {
                if (coinImage.parent != null) {
                    gameLayout.removeView(coinImage) // Only remove the view if it's still in the layout
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the error for debugging purposes
            }
        }
    }


    private fun updateCoinDisplay() {
        // Update any UI element to show the number of collected coins
        coinTextView.text = "Coins: $coinsCollected" // Assuming you have a TextView to display the coin count
    }


    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            // Get the tilt values from the accelerometer
            val tiltX = it.values[0]  // X-axis tilt (left-right)
            val tiltY = it.values[1]  // Y-axis tilt (forward-backward)

            // Move the car left and right based on X-axis tilt
            val currentX = car.x
            var newX = currentX - tiltX * 15 // Adjust the multiplier for sensitivity

            // Make sure the car stays within the screen bounds
            if (newX < 0) newX = 0f
            if (newX + car.width > screenWidth) newX = (screenWidth - car.width).toFloat()
            car.x = newX

            // Adjust the game speed based on Y-axis tilt
            // Tilt forward (positive Y) increases speed, tilt backward (negative Y) decreases speed
            if (tiltY > 0.5) {  // Adjust threshold to suit your game
                speed = fastSpeed // Tilt forward increases speed
            } else if (tiltY < -0.5) { // Tilt backward decreases speed
                speed = slowSpeed // Define slowSpeed as a lower value
            } else {
                speed = normalSpeed // Set to normal speed when tilt is neutral
            }
        }
    }


    private fun adjustObstacleSpeed(tiltY: Float) {
        if (tiltY < 0) {
            speed = (speed + 1f).coerceAtMost(maxObstacleSpeed)
        } else if (tiltY > 0) {
            speed = (speed - 1f).coerceAtLeast(minObstacleSpeed)
        }

        // Update speed for all active obstacles
        activeObstacles.forEach { it.setObstacleSpeed(speed) }
    }

    private fun updateCurrentLane() {
        val laneWidth = screenWidth / laneCount
        val carCenterX = car.x + car.width / 2
        currentLane = (carCenterX / laneWidth).toInt() + 1
        if (currentLane < 1) currentLane = 1
        if (currentLane > laneCount) currentLane = laneCount
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
