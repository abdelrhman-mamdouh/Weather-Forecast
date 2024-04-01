package com.example.weatherguide.settingsScreen

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.weatherguide.R
import com.example.weatherguide.databinding.FragmentSettingsBinding
import com.example.weatherguide.utills.Util

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences =
            requireContext().getSharedPreferences("MySettings", Context.MODE_PRIVATE)

        val location = sharedPreferences.getString("location", "")
        val notification = sharedPreferences.getString("notification", "")
        val language = sharedPreferences.getString("language", "")
        val windSpeed = sharedPreferences.getString("windSpeed", "")
        val temperature = sharedPreferences.getString("temperature", "")
        val theme = sharedPreferences.getString("theme", "")

        when (location) {
            requireContext().getString(R.string.map) -> binding.locationRadioGroup.check(R.id.radioButtonMap)
            requireContext().getString(R.string.gps) -> binding.locationRadioGroup.check(R.id.radioButtonGPS)
        }

        when (language) {
            "ar" -> binding.languageRadioGroup.check(R.id.radioButtonArabic)
            "en" -> binding.languageRadioGroup.check(R.id.radioButtonEnglish)
        }
        when (notification) {
            requireContext().getString(R.string.enable) -> binding.notifyRadioGroup.check(R.id.radioButtonEnable)
            requireContext().getString(R.string.disable) -> binding.notifyRadioGroup.check(R.id.radioButtonDisable)
        }
        when (windSpeed) {
            requireContext().getString(R.string.meter_sec) -> binding.windSpeedRadioGroup.check(R.id.radioButtonSpeedUnitMS)
            requireContext().getString(R.string.mile_hour) -> binding.windSpeedRadioGroup.check(R.id.radioButtonSpeedUnitMH)
        }
        when (temperature) {
            requireContext().getString(R.string.celsius) -> binding.tempRadioGroup.check(R.id.radioButtonCelsius)
            requireContext().getString(R.string.kelvin) -> binding.tempRadioGroup.check(R.id.radioButtonKelvin)
            requireContext().getString(R.string.fahrenheit) -> binding.tempRadioGroup.check(R.id.radioButtonFahrenheit)
        }
        when (theme) {
            requireContext().getString(R.string.light) -> binding.themeRadioGroup.check(R.id.radioButtonLight)
            requireContext().getString(R.string.dark) -> binding.themeRadioGroup.check(R.id.radioButtonDark)
        }
        binding.locationRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonMap -> {
                    sharedPreferences.edit()
                        .putString("location", requireContext().getString(R.string.map)).apply()
                }

                R.id.radioButtonGPS -> {
                    sharedPreferences.edit()
                        .putString("location", requireContext().getString(R.string.gps)).apply()
                }
            }
        }
        binding.languageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val currentLanguage = sharedPreferences.getString("language", "en")
            val newLanguage = when (checkedId) {
                R.id.radioButtonArabic -> "ar"
                R.id.radioButtonEnglish -> "en"
                else -> currentLanguage
            }
            if (newLanguage != currentLanguage) {
                sharedPreferences.edit().putString("language", newLanguage).apply()
                if (newLanguage != null) {
                    if (newLanguage == "ar") {
                        Util.setAppLocale(newLanguage, "EG", requireActivity())
                        requireActivity().window.decorView.layoutDirection =
                            View.LAYOUT_DIRECTION_RTL
                    } else {
                        Util.setAppLocale(newLanguage, "", requireActivity())
                        requireActivity().window.decorView.layoutDirection =
                            View.LAYOUT_DIRECTION_LTR
                    }
                }
                requireActivity().recreate()
            } else {
                sharedPreferences.edit().putString("language", "en").apply()
            }
        }
        binding.notifyRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonEnable -> {
                    sharedPreferences.edit()
                        .putString("notification", requireContext().getString(R.string.enable))
                        .apply()
                }

                R.id.radioButtonDisable -> {
                    sharedPreferences.edit()
                        .putString("notification", requireContext().getString(R.string.disable))
                        .apply()
                }
            }
        }

        binding.tempRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonCelsius -> {
                    sharedPreferences.edit()
                        .putString("temperature", requireContext().getString(R.string.celsius))
                        .apply()
                    sharedPreferences.edit()
                        .putString("windSpeed", requireContext().getString(R.string.meter_sec))
                        .apply()
                    binding.radioButtonSpeedUnitMS.isChecked = true
                    binding.radioButtonSpeedUnitMH.isEnabled = false
                }

                R.id.radioButtonKelvin -> {
                    sharedPreferences.edit()
                        .putString("temperature", requireContext().getString(R.string.kelvin))
                        .apply()
                    sharedPreferences.edit()
                        .putString("windSpeed", requireContext().getString(R.string.meter_sec))
                        .apply()
                    binding.radioButtonSpeedUnitMS.isChecked = true
                    binding.radioButtonSpeedUnitMH.isEnabled = false
                }

                R.id.radioButtonFahrenheit -> {
                    sharedPreferences.edit()
                        .putString("temperature", requireContext().getString(R.string.fahrenheit))
                        .apply()
                    sharedPreferences.edit()
                        .putString("windSpeed", requireContext().getString(R.string.mile_hour))
                        .apply()
                    binding.radioButtonSpeedUnitMH.isChecked = true
                    binding.radioButtonSpeedUnitMS.isEnabled = false
                }
            }
        }
        binding.themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonLight -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    sharedPreferences.edit()
                        .putString("theme", requireContext().getString(R.string.light))
                        .apply()
                }

                R.id.radioButtonDark -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    sharedPreferences.edit()
                        .putString("theme", requireContext().getString(R.string.dark))
                        .apply()

                }
            }
        }
    }


}



