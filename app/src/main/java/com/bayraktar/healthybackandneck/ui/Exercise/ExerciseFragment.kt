package com.bayraktar.healthybackandneck.ui.Exercise

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bayraktar.healthybackandneck.Models.Exercise.AbsModel
import com.bayraktar.healthybackandneck.Models.Exercise.ArmModel
import com.bayraktar.healthybackandneck.Models.Exercise.FixPostureModel
import com.bayraktar.healthybackandneck.Models.Exercise.LegButtModel
import com.bayraktar.healthybackandneck.Models.Exercise.NeckBackModel
import com.bayraktar.healthybackandneck.Models.Exercise.WarmUpModel
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseBinding
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.AbsAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.ArmAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.FixPostureAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.LegButtAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.NeckBackAdapter
import com.bayraktar.healthybackandneck.ui.HomePage.Adapters.WarmUpAdapter
import kotlin.math.abs


class ExerciseFragment : Fragment() {

    private var _binding: FragmentExerciseBinding? = null
    val binding get() = _binding!!

    //WarmUp


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



}