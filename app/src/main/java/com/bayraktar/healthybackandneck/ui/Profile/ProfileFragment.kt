package com.bayraktar.healthybackandneck.ui.Profile

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.recreate
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentProfileBinding
import com.bayraktar.healthybackandneck.utils.LocaleHelper
import java.util.Locale


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    val binding get() = _binding!!


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

        binding.consLanguage.setOnClickListener {
            btnLanguageclicked()
        }
    }

    private fun btnLanguageclicked() {
        val languages = arrayOf("English", "Turkish", "German")
        val languageCodes = arrayOf("en", "tr", "de")

        val currentLanguage = Locale.getDefault().language
        val currentLanguageIndex = languageCodes.indexOf(currentLanguage)

        AlertDialog.Builder(requireContext())
            .setTitle("Select Language")
            .setSingleChoiceItems(languages, currentLanguageIndex) { dialog, which ->
                // Change the language when the user selects a new language
                val selectedLanguageCode = languageCodes[which]
                LocaleHelper.setLocale(requireContext(), selectedLanguageCode)
                recreate(requireActivity()) // Restart the activity to apply the language change
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


}