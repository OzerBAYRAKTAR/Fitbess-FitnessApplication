package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMovesRest

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bayraktar.healthybackandneck.R

class MovesRestFragment : Fragment() {

    companion object {
        fun newInstance() = MovesRestFragment()
    }

    private lateinit var viewModel: MovesRestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_moves_rest, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MovesRestViewModel::class.java)
        // TODO: Use the ViewModel
    }

}