package com.example.weatherguide.favoriteScreen.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherguide.R
import com.example.weatherguide.databinding.FragmentFavoritesBinding
import com.example.weatherguide.db.WeatherLocalDataSourceImpl
import com.example.weatherguide.favoriteScreen.OnClickListener
import com.example.weatherguide.favoriteScreen.viewModel.FavoritesViewModel
import com.example.weatherguide.favoriteScreen.viewModel.FavoritesViewModelFactory
import com.example.weatherguide.network.ApiState
import com.example.weatherguide.mapScreen.view.MapActivity
import com.example.weatherguide.model.FavoriteLocation
import com.example.weatherguide.model.WeatherRepositoryImpl
import com.example.weatherguide.network.WeatherRemoteSourceDataImpl
import kotlinx.coroutines.launch


class FavoritesFragment : Fragment(), OnClickListener<FavoriteLocation> {
    private lateinit var adapter: FavoriteLocationAdapter
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var favoritesViewModelFactory: FavoritesViewModelFactory
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
        binding.fabAddFavoriteLocation.setOnClickListener{
            val intent = Intent(requireContext(), MapActivity::class.java)
            startActivity(intent)
        }
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


    override fun onClickRemove(favoriteLocation: FavoriteLocation) {
        favoritesViewModel.removeLocation(favoriteLocation)
    }

    override fun onClickLocationFavorite(favoriteLocation: FavoriteLocation) {
        val spSettings = requireContext().getSharedPreferences("MySettings", Context.MODE_PRIVATE)
        val location = spSettings.getString("location", "")

        if (location == requireContext().resources.getString(R.string.gps)) {
            spSettings.edit().putString("location", requireContext().resources.getString(R.string.map)).apply()
        }
        val lat = favoriteLocation.lat
        val long = favoriteLocation.lon
        val sharedPreferences = requireContext().getSharedPreferences("current-location", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("latitudeFromMap", lat.toFloat())
        editor.putFloat("longitudeFromMap", long.toFloat())
        editor.apply()
        val intent = activity?.intent
        activity?.finish()
        startActivity(intent!!)
    }
}
