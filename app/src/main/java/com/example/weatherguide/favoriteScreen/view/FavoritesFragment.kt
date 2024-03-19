package com.example.weatherguide.favoriteScreen.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherguide.R
import com.example.weatherguide.databinding.FragmentFavoritesBinding
import com.example.weatherguide.db.WeatherLocalDataSourceImpl
import com.example.weatherguide.favoriteScreen.OnClickListener
import com.example.weatherguide.favoriteScreen.viewModel.FavoritesViewModel
import com.example.weatherguide.favoriteScreen.viewModel.FavoritesViewModelFactory
import com.example.weatherguide.homeScreen.ApiState
import com.example.weatherguide.model.FavoriteLocation
import com.example.weatherguide.model.WeatherRepositoryImpl
import com.example.weatherguide.network.WeatherRemoteSourceDataImpl
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch


class FavoritesFragment : Fragment(), OnClickListener {
    private lateinit var adapter: FavoriteLocationAdapter
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var favoritesViewModelFactory: FavoritesViewModelFactory
    private lateinit var loader: ProgressBar
    private lateinit var binding: FragmentFavoritesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FavoriteLocationAdapter(
            emptyList(), this, requireContext()
        )
        binding.favoritesRecyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.favoritesRecyclerView.layoutManager = linearLayoutManager
        favoritesViewModelFactory = FavoritesViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherRemoteSourceDataImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())
            )
        )
        favoritesViewModel =
            ViewModelProvider(
                requireActivity(),
                favoritesViewModelFactory
            )[FavoritesViewModel::class.java]
        binding.favoritesRecyclerView.adapter = adapter
        lifecycleScope.launch {
            favoritesViewModel.favoriteLocations.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        binding.favoritesRecyclerView.visibility=View.GONE
                        showLoading(true)
                    }

                    is ApiState.Success -> {
                        showLoading(false)
                        binding.favoritesRecyclerView.visibility=View.VISIBLE
                        Log.i("TAG", "onViewCreated: ${state.data}")
                        showData(state.data)
                    }

                    else -> {
                        showLoading(false)
                    }
                }
            }
        }
    }


    fun onClick(latitude: Double, longitude: Double, locationName: String) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val customView = layoutInflater.inflate(R.layout.custom_alert_dialog_search, null)
        val btnSave = customView.findViewById<Button>(R.id.btnSave)
        val btnChooseAnother = customView.findViewById<Button>(R.id.btnChooseAnother)
        btnSave.setOnClickListener {
            val sharedPreferences =
                requireContext().getSharedPreferences(
                    "location_prefs",
                    Context.MODE_PRIVATE
                )
            val editor = sharedPreferences.edit()
            editor.putFloat("latitudeFromSearch", latitude.toFloat())
            editor.putFloat("longitudeFromSearch", longitude.toFloat())
            editor.apply()
            bottomSheetDialog.dismiss()
        }

        btnChooseAnother.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(customView)
        bottomSheetDialog.show()
    }
    private fun showData(favoriteLocation: List<FavoriteLocation>) {
        adapter.apply {
            setList(favoriteLocation)
            notifyDataSetChanged()
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onClick(favoriteLocation: FavoriteLocation) {
       favoritesViewModel.removeLocation(favoriteLocation)
    }
}
