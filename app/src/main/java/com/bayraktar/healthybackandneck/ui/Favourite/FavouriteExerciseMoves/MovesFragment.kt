package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteExerciseMoves

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bayraktar.healthybackandneck.R

class MovesFragment : Fragment() {

    companion object {
        fun newInstance() = MovesFragment()
    }

    private lateinit var viewModel: MovesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_moves, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MovesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}