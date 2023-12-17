package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMovesReady

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bayraktar.healthybackandneck.R

class MovesReadyFragment : Fragment() {

    companion object {
        fun newInstance() = MovesReadyFragment()
    }

    private lateinit var viewModel: MovesReadyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_moves_ready, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MovesReadyViewModel::class.java)
        // TODO: Use the ViewModel
    }

}