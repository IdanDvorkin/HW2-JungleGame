package com.example.junglegame.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment
import com.example.junglegame.Activities.HighScoreActivity
import com.example.junglegame.R

class HighScoreListFragment : ListFragment() {

    private var listener: OnHighScoreSelectedListener? = null

    interface OnHighScoreSelectedListener {
        fun onHighScoreSelected(position: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHighScoreSelectedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnHighScoreSelectedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_high_score_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Fetch the high scores from the parent activity
        val highScoreActivity = activity as? HighScoreActivity
        highScoreActivity?.let {
            val highScores = it.fetchTopHighScores()

            // Create a list of strings in the format "1. (score)"
            val formattedScores = highScores.mapIndexed { index, score -> "${index + 1}. ($score)" }

            // Display the formatted high scores using a ListView and ArrayAdapter
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, formattedScores)
            listAdapter = adapter
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        listener?.onHighScoreSelected(position)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
