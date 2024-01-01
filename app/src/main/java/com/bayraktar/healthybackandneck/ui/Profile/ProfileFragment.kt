package com.bayraktar.healthybackandneck.ui.Profile

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.recreate
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentProfileBinding
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.bayraktar.healthybackandneck.utils.LanguagePreference
import com.bayraktar.healthybackandneck.utils.LocaleHelper
import com.bayraktar.healthybackandneck.utils.NotificationHelper
import com.bayraktar.healthybackandneck.utils.showToast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import kotlinx.coroutines.selects.select
import java.util.Locale
import java.util.Timer
import java.util.TimerTask


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    val binding get() = _binding!!
    private var timer: Timer? = null
    private var isReminderOn = false
    private var isWaterReminderOn = false
    private lateinit var notificationHelper: NotificationHelper
    private var  reviewInfo: ReviewInfo?= null
    private lateinit var reviewManager: ReviewManager
    private lateinit var mAdView: AdView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        MobileAds.initialize(requireContext()) {}

        // profile banner id => ca-app-pub-4754194669476617/7416253720
        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)



        val savedLanguageCode = LanguagePreference.getLanguageCode(requireContext())
        savedLanguageCode?.let {
            LocaleHelper.setLocale(requireContext(), it)
        }

        activateReviewInfo()
        btnRateUsClicked()
        notificationHelper = NotificationHelper(requireContext())
        setBindingThings()
        btnShareClicked()


    }
    private fun changeLanguageAndSave(languageCode: String) {
        LocaleHelper.setLocale(requireContext(), languageCode)
        LanguagePreference.setLanguageCode(requireContext(), languageCode)
    }
    private fun activateReviewInfo() {
        reviewManager = ReviewManagerFactory.create(requireContext())
        val managerInfoTask: Task<ReviewInfo> = reviewManager.requestReviewFlow()
        managerInfoTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
            }else {
                val reviewError = getString(R.string.label_reviewerror)
                showToast(requireContext(),reviewError,Gravity.CENTER,0,0)
            }
        }
    }
    private fun startReviewFlow() {
        if (reviewInfo != null) {
            val flow : Task<Void> = reviewManager.launchReviewFlow(requireActivity(), reviewInfo!!)
            flow.addOnCompleteListener { task ->
                val successMessage = getString(R.string.label_ratingsuccess)
                showToast(requireContext(),successMessage,Gravity.CENTER,0,0)
            }
        }
    }

    private fun setBindingThings() = with(binding) {

        waterReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            isWaterReminderOn = isChecked
            if (isChecked) {
                startReminder()
            } else {
                stopReminder()
            }
        }
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            isReminderOn = isChecked
            if (isChecked) {
                startFitnessReminder()
            } else {
                stopFitnessReminder()
            }
        }

        apply {
            consLanguage.setOnClickListener {
                btnLanguageClicked()
            }
            constRestart.setOnClickListener {
                btnRestartClicked()
            }
            constTheme.setOnClickListener {
                btnThemeclicked()
            }
        }

    }

    private fun startFitnessReminder() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                notificationHelper.showFitnessNotification()
            }
        }, 0, 12 * 60 * 60 * 1000) // Fitness reminder interval: 12 hours
    }

    private fun stopFitnessReminder() {
        timer?.cancel()
        timer?.purge()
    }

    private fun startReminder() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                notificationHelper.showNotification()
            }
        }, 0, 12 * 60 * 60 * 1000)
    }

    private fun stopReminder() {
        timer?.cancel()
        timer?.purge()
    }

    private fun btnThemeclicked() {
        val languages = arrayOf("Light", "Dark Theme")
        val languageCodes = arrayOf("en", "tr", "de")

        val currentLanguage = Locale.getDefault().language
        val currentLanguageIndex = languageCodes.indexOf(currentLanguage)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.label_selectlanguage))
            .setIcon(R.drawable.themee)
            //which -> like a selected position of list
            .setSingleChoiceItems(languages, currentLanguageIndex) { dialog, which ->
                // Change the language when the user selects a new language
                val selectedLanguageCode = languageCodes[which]
                LocaleHelper.setLocale(requireContext(), selectedLanguageCode)
                recreate(requireActivity()) // Restart the activity to apply the language change
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.label_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun btnRestartClicked() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.label_attention))
            .setMessage(getString(R.string.label_restartmessage))
            .setIcon(R.drawable.restartt)
            .setCancelable(false)
            .setPositiveButton("Evet") { dialog, _ ->
                dialog.dismiss()
            }.setNegativeButton("Hayır") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun btnLanguageClicked() {
        val languages = arrayOf("Turkish", "English", "German", "Russian")
        val languageCodes = arrayOf("tr", "en", "de", "ru")

        val currentLanguage = Locale.getDefault().language
        val currentLanguageIndex = languageCodes.indexOf(currentLanguage)
        val defaultLanguageIndex = 0

        val selectedIndex = if (currentLanguageIndex != -1) currentLanguageIndex else defaultLanguageIndex

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Language")
            .setIcon(R.drawable.translation)
            .setSingleChoiceItems(languages, selectedIndex) { dialog, which ->
                // Change the language when the user selects a new language
                val selectedLanguageCode = languageCodes[which]
                changeLanguageAndSave(selectedLanguageCode)
                setNewLocale(requireContext(), selectedLanguageCode)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    private fun setNewLocale(context: Context, language: String) {
        val newLocale = Locale(language)
        Locale.setDefault(newLocale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(newLocale)
        context.createConfigurationContext(configuration)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        val intent = Intent(context, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    private fun btnShareClicked() = with(binding) {
        consShare.setOnClickListener {
            val link = "App.link"
            val selectedText = getString(R.string.label_shareus)
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, selectedText)
            shareIntent.type = "text/plain"

            if (activity?.packageManager?.let { it2 ->
                    shareIntent.resolveActivity(it2)
                } != null) {
                startActivity(Intent.createChooser(shareIntent, getString(R.string.label_sharevia)))
            } else {
                val shareError = getString(R.string.label_shareerror)
                showToast(requireContext(), shareError, Gravity.CENTER, 0, 0)
            }

        }
    }
    private fun btnRateUsClicked() = with(binding) {
        startReviewFlow()
    }
}
