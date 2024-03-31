package com.example.weatherguide

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.weatherguide.databinding.FragmentSettingsBinding
import java.util.Locale

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
            requireContext().getString(R.string.arabic) -> binding.languageRadioGroup.check(R.id.radioButtonArabic)
            requireContext().getString(R.string.english) -> binding.languageRadioGroup.check(R.id.radioButtonEnglish)
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
        binding.locationRadioGroup.setOnCheckedChangeListener { group, checkedId ->
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
        binding.languageRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButtonArabic -> {
                    sharedPreferences.edit()
                        .putString("language", requireContext().getString(R.string.arabic)).apply()
                    setAppLocale("ar", "EG")

                    requireActivity().recreate()
                }

                R.id.radioButtonEnglish -> {
                    sharedPreferences.edit()
                        .putString("language", requireContext().getString(R.string.english)).apply()
                    setAppLocale("en", "")
                    requireActivity().recreate()
                }
            }
        }
        binding.notifyRadioGroup.setOnCheckedChangeListener { group, checkedId ->
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
        binding.windSpeedRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButtonSpeedUnitMS -> {
                    sharedPreferences.edit()
                        .putString("windSpeed", requireContext().getString(R.string.meter_sec))
                        .apply()
                }

                R.id.radioButtonSpeedUnitMH -> {
                    sharedPreferences.edit()
                        .putString("windSpeed", requireContext().getString(R.string.mile_hour))
                        .apply()
                }
            }
        }
        binding.tempRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButtonCelsius -> {
                    sharedPreferences.edit()
                        .putString("temperature", requireContext().getString(R.string.celsius))
                        .apply()
                }

                R.id.radioButtonKelvin -> {
                    sharedPreferences.edit()
                        .putString("temperature", requireContext().getString(R.string.kelvin))
                        .apply()
                }

                R.id.radioButtonFahrenheit -> {
                    sharedPreferences.edit()
                        .putString("temperature", requireContext().getString(R.string.fahrenheit))
                        .apply()
                }
            }
            binding.themeRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radioButtonLight -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        requireActivity().setTheme(R.style.Base_Theme_WeatherGuide_Light)
                        sharedPreferences.edit()
                            .putString("theme", requireContext().getString(R.string.light))
                            .apply()
                        requireActivity().recreate()
                    }

                    R.id.radioButtonDark -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        requireActivity().setTheme(R.style.Base_Theme_WeatherGuide_Dark)
                        sharedPreferences.edit()
                            .putString("theme", requireContext().getString(R.string.dark))
                            .apply()
                        requireActivity().recreate()
                    }
                }
            }
        }
    }

    private fun setAppLocale(language: String, country: String) {
        val locale = Locale(language, country)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        requireContext().resources.updateConfiguration(
            config,
            requireContext().resources.displayMetrics
        )
    }
}
