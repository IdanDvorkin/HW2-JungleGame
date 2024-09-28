package com.example.junglegame.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.junglegame.R
import kotlin.random.Random

class MapFragment : Fragment() {

    private lateinit var mapLayout: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout and reference the RelativeLayout for dynamic view addition
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapLayout = view.findViewById(R.id.map_layout)  // The container RelativeLayout
        return view
    }

    // Function to clear only the markers, keeping the background intact
    private fun clearMarkers() {
        // Loop through child views and remove only the dynamically added markers
        for (i in mapLayout.childCount - 1 downTo 0) {
            val child = mapLayout.getChildAt(i)
            // Assuming that dynamically added markers are TextViews, we can remove those
            if (child is TextView) {
                mapLayout.removeView(child)
            }
        }
    }

    // Function to add random markers on the map
    fun addRandomMarker(highScoreText: String) {
        // Clear all previous markers before adding a new one
        clearMarkers()

        // Create a new TextView to act as the marker (make it smaller)
        val marker = TextView(context).apply {
            text = highScoreText
            textSize = 7f  // Smaller text size to make the marker smaller
            setBackgroundResource(R.drawable.marker)  // Optional background for the marker
        }

        mapLayout.post {
            // Dimensions for the marker (making it smaller, e.g., 10x smaller)
            val markerWidth = 100  // Adjusted width of the marker (smaller)
            val markerHeight = 100  // Adjusted height of the marker (smaller)

            // Ensure layout dimensions are greater than the marker size to prevent layout issues
            if (mapLayout.width > markerWidth && mapLayout.height > markerHeight) {
                // Generate random X and Y coordinates within the map layout's bounds
                val randomX = Random.nextInt(400, 700 - markerWidth)
                val randomY = Random.nextInt(400, 700 - markerHeight)

                // Set up the marker's LayoutParams with the calculated margins
                val params = RelativeLayout.LayoutParams(
                    markerWidth,
                    markerHeight  // Make the marker smaller in height
                )
                params.leftMargin = randomX  // X position
                params.topMargin = randomY   // Y position

                // Add the marker to the layout
                mapLayout.addView(marker, params)
            }
        }
    }
}
