package com.bayraktar.healthybackandneck.ui.Statistics

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.databinding.FragmentStatisticsBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    val binding get() = _binding!!

    private val viewModel : StatisticsViewModel by viewModels()

    private lateinit var dataStoreManager: DataStoreManage



    private lateinit var mAdView: AdView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //statistics banner ıd => ca-app-pub-4754194669476617/2871909027 // şuanlık xml de id var, canlıya çıkarken değişecek

        MobileAds.initialize(requireContext()) {}

        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        observeCount()
        dataStoreManager = DataStoreManage.getInstance(requireContext())
        buttonClicks()


        viewModel.getCountFromRoom()
        observeCalculates()

        dataStoreManager.getActivityLevel().asLiveData().observe(viewLifecycleOwner) { level ->
            if (level != 0) {
                println(level)
            }
        }
    }
    private fun observeCount() = with(binding) {
        viewModel.getCount.observe(viewLifecycleOwner, Observer { count ->
            if (count != null) {
                if (count.count > 0) {
                    txtExerciseCount.text = "${count.count*7}"
                    txtTimespent.text = "${8*count.count}"
                    txtKcal.text = "${300*count.count}"
                }else {
                    txtExerciseCount.text = "0"
                    txtTimespent.text = "0"
                    txtKcal.text = "0"
                }
            }else {
                println("xx")
            }

        })
    }
    private fun observeCalculates() = with(binding) {

        lifecycleScope.launch(Dispatchers.Main) {
            combine(
                dataStoreManager.getIndeks(),
                dataStoreManager.getCategory(),
                dataStoreManager.getCalorie(),
                dataStoreManager.getFatRate(),

                ) { indeks, category, calorie, fatrate ->
                if (indeks != "") {
                    val formattedindeks = indeks.replace(',', '.').toDouble()
                    bmiIndeks.text = indeks.toString()
                    when (formattedindeks) {
                        in Double.MIN_VALUE..18.5 -> {
                            viewDown1.visibility = View.VISIBLE
                            viewDown2.visibility = View.INVISIBLE
                            viewDown3.visibility = View.INVISIBLE
                            viewDown4.visibility = View.INVISIBLE
                        }
                        in 18.5..25.0 -> {
                            viewDown2.visibility = View.VISIBLE
                            viewDown1.visibility = View.INVISIBLE
                            viewDown3.visibility = View.INVISIBLE
                            viewDown4.visibility = View.INVISIBLE
                        }
                        in 25.0..29.9 -> {
                            viewDown3.visibility = View.VISIBLE
                            viewDown1.visibility = View.INVISIBLE
                            viewDown2.visibility = View.INVISIBLE
                            viewDown4.visibility = View.INVISIBLE
                        }
                        in 30.0..1000.0 -> {
                            viewDown4.visibility = View.VISIBLE
                            viewDown1.visibility = View.INVISIBLE
                            viewDown2.visibility = View.INVISIBLE
                            viewDown3.visibility = View.INVISIBLE
                        }
                        else ->  {
                            Log.d(TAG,"Error")
                        }
                    }
                }
                if (category != "") {
                    bmiKategori.text = category
                }
                if (calorie != "") {
                    txtCaloriOfDay.text = calorie.toString()
                }
                if (fatrate != "") {
                    txtfatrate.text = fatrate.toString()
                }
            }.collectLatest {
                // You can add additional logic here if needed
            }
        }

    }

    private fun buttonClicks() {
        with(binding) {
            editEndeks.setOnClickListener {
                val action = StatisticsFragmentDirections.actionIdStatisticsFragmentToBMIFragment()
                view?.findNavController()?.navigate(action)
            }
            editDayofCalorie.setOnClickListener {
                val action =
                    StatisticsFragmentDirections.actionIdStatisticsFragmentToDailyCalorieFragment()
                view?.findNavController()?.navigate(action)
            }
            editfatrate.setOnClickListener {
                val action =
                    StatisticsFragmentDirections.actionIdStatisticsFragmentToBodyFatRateFragment()
                view?.findNavController()?.navigate(action)
            }
        }
    }


}