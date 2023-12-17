package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMovesEnd

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bayraktar.healthybackandneck.R

class MovesEndFragment : Fragment() {

    companion object {
        fun newInstance() = MovesEndFragment()
    }

    private lateinit var viewModel: MovesEndViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_moves_end, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MovesEndViewModel::class.java)
        // TODO: Use the ViewModel
    }

}