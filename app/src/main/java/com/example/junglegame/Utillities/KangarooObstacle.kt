package com.example.junglegame.Utillities

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import com.example.junglegame.R

class KangarooObstacle(
    private val context: Context,
    private val gameLayout: RelativeLayout,
    private val screenWidth: Int,
    private val car: ImageView,
    private var obstacleSpeed: Float, // Speed is now dynamic
    private val onCollision: () -> Unit // Callback when collision happens
) {

    private val obstacleImage: ImageView = ImageView(context)
    private var hasCollided = false

    init {
        // Create the kangaroo obstacle ImageView and set its initial properties
        obstacleImage.setImageResource(R.drawable.kangero_obstacle)

        // Set the obstacle's size
        val params = RelativeLayout.LayoutParams(150, 150)
        params.topMargin = 0

        // Add the obstacle to the game layout
        gameLayout.addView(obstacleImage, params)

        // Start the movement
        moveObstacle()
    }

    // Set obstacle position
    fun setPosition(x: Int) {
        obstacleImage.x = x.toFloat()
    }

    // Set the speed of the obstacle
    fun setObstacleSpeed(newSpeed: Float) {
        obstacleSpeed = newSpeed
    }

    private fun moveObstacle() {
        obstacleImage.post(object : Runnable {
            override fun run() {
                if (obstacleImage.top < gameLayout.height && !hasCollided) {
                    // Use the speed variable from MainActivity to adjust the obstacle movement
                    obstacleImage.translationY = obstacleImage.translationY + obstacleSpeed // Adjust speed

                    // Check for collision with the car
                    if (checkCollision(car, obstacleImage)) {
                        hasCollided = true
                        onCollision() // Call the callback to handle lives reduction
                    }

                    obstacleImage.postDelayed(this, 16)
                } else {
                    gameLayout.removeView(obstacleImage)
                }
            }
        })
    }


    // Check collision (same as before)
    private fun checkCollision(car: ImageView, obstacle: ImageView): Boolean {
        val carRect = android.graphics.Rect()
        val obstacleRect = android.graphics.Rect()
        car.getHitRect(carRect)
        obstacle.getHitRect(obstacleRect)
        return android.graphics.Rect.intersects(carRect, obstacleRect)
    }
}
