package com.example.weatherguide

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weatherguide.databinding.FragmentSettingsBinding
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
            requireContext().getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
        val selectedOption = sharedPreferences.getString("selected_option", null)

        binding.locationRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButtonMap -> {
                    sharedPreferences.edit().putString("selected_option", "Map").apply()
                }

                R.id.radioButtonGPS -> {
                    sharedPreferences.edit().putString("selected_option", "GPS").apply()
                }
            }
        }
        selectedOption?.let {
            when (it) {
                "Map" -> binding.radioButtonMap.isChecked = true
                "GPS" -> binding.radioButtonGPS.isChecked = true
            }
        }
    }
}
