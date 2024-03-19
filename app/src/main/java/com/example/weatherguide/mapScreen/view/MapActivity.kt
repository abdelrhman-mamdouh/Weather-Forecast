package com.example.weatherguide.mapScreen.view

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherguide.R
import com.example.weatherguide.db.WeatherLocalDataSourceImpl
import com.example.weatherguide.mapScreen.ApiLocationState
import com.example.weatherguide.mapScreen.OnItemLocationClickListener
import com.example.weatherguide.mapScreen.viewModel.MapViewModel
import com.example.weatherguide.mapScreen.viewModel.MapViewModelFactory
import com.example.weatherguide.model.FavoriteLocation
import com.example.weatherguide.model.WeatherRepositoryImpl
import com.example.weatherguide.network.WeatherRemoteSourceDataImpl
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.mylocation.SimpleLocationOverlay

class MapActivity : AppCompatActivity() , OnItemLocationClickListener {
    private lateinit var mapView: MapView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fabGetCurrentLocation :FloatingActionButton
    private var selectedMarker: Marker? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocationSuggestionsAdapter

    private lateinit var favoritesViewModel: MapViewModel
    private lateinit var searchEditText: EditText
    private lateinit var loader: ProgressBar
    private lateinit var favoritesViewModelFactory: MapViewModelFactory
    private val sharedFlow = MutableSharedFlow<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        recyclerView = findViewById(R.id.recyclerView)

        searchEditText = findViewById(R.id.searchEditText)
        loader = findViewById(R.id.loader)
        adapter = LocationSuggestionsAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.visibility=View.GONE
        searchEditText.addTextChangedListener { editable ->
            val query = editable.toString()
            lifecycleScope.launch(Dispatchers.Main) {
                sharedFlow.emit(query)
            }
        }
        favoritesViewModelFactory = MapViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherRemoteSourceDataImpl.getInstance(),
                WeatherLocalDataSourceImpl(this)
            ), sharedFlow
        )
        favoritesViewModel = ViewModelProvider(
            this,
            favoritesViewModelFactory
        )[MapViewModel::class.java]

        lifecycleScope.launch(Dispatchers.Main) {
            showLoading(false)
            favoritesViewModel.locationSuggestions.collect { state ->
                when (state) {
                    is ApiLocationState.Loading -> {showLoading(true)
                        recyclerView.visibility=View.GONE
                    }
                    is ApiLocationState.Success -> {
                        recyclerView.visibility=View.VISIBLE
                        showLoading(false)
                        adapter.submitList(emptyList())
                        adapter.submitList(state.list)
                        Log.i("TAG", "onCreate: ${state.list}")
                    }
                    else -> {
                        showLoading(false)
                        recyclerView.visibility=View.GONE
                    }
                }
            }
        }

        fabGetCurrentLocation= findViewById(R.id.fabGetCurrentLocation)
        mapView = findViewById(R.id.mapView)
        mapView.visibility = View.VISIBLE
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)

        mapView.overlays.add(MyLocationNewOverlay(GpsMyLocationProvider(this), mapView))
        val currentLocation = GeoPoint(
            30.0574087,
            31.2630744
        )

        mapView.controller.animateTo(currentLocation)

        fabGetCurrentLocation.setOnClickListener(){
            val marker = Marker(mapView)
            marker.position = currentLocation
            mapView.overlays.add(marker)
            searchEditText.hint = Editable.Factory.getInstance().newEditable("Current Location")
            mapView.controller.animateTo(currentLocation)
            mapView.controller.setZoom(18.0)

        }

        mapView.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                return false
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                return false
            }
        })

        mapView.overlays.add(
            object : SimpleLocationOverlay(
             applicationContext
            ) {
                override fun onSingleTapConfirmed(e: MotionEvent?, mapView: MapView?): Boolean {
                    if (selectedMarker != null) {
                        mapView!!.overlays.remove(selectedMarker)
                    }
                    val projection = mapView!!.projection
                    val touchedPoint = projection.fromPixels(e!!.x.toInt(), e.y.toInt())
                    selectedMarker = Marker(mapView)
                    selectedMarker!!.position = touchedPoint as GeoPoint?
                    selectedMarker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    mapView.overlays.add(selectedMarker)
                    mapView.controller.animateTo(touchedPoint)
                    val locationName = getAddressLocation(touchedPoint.latitude,touchedPoint.longitude)
                    searchEditText.hint = Editable.Factory.getInstance().newEditable(locationName)
                    showSaveLocationDialog(touchedPoint,locationName)
                    return true
                }
            })

        sharedPreferences =
            getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        Configuration.getInstance().load(this, getMapTilePath())



    }
    private fun getMapTilePath(): SharedPreferences {
        sharedPreferences.getString("tile_path", cacheDir.absolutePath + "/osmdroid/tiles/") ?: ""
        return sharedPreferences
    }
    private fun showSaveLocationDialog(geoPoint: GeoPoint,locationName:String) {
        val bottomSheetDialog = BottomSheetDialog(this)

        val customView = layoutInflater.inflate(R.layout.custom_alert_dialog_map, null)

        val btnSave = customView.findViewById<Button>(R.id.btnSave)
        val btnChooseAnother = customView.findViewById<Button>(R.id.btnChooseAnother)

        btnSave.setOnClickListener {
            val latitude = geoPoint.latitude
            val longitude = geoPoint.longitude
            mapView.controller.animateTo(geoPoint)
            val sharedPreferences = getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putFloat("latitudeFromMap", latitude.toFloat())
            editor.putFloat("longitudeFromMap", longitude.toFloat())
            editor.apply()
            val favoriteLocation=FavoriteLocation(locationName ,latitude,longitude)
            favoritesViewModel.addFavoriteLocation(favoriteLocation)
            Snackbar.make(findViewById(android.R.id.content), "Location added to favorites", Snackbar.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
            finish()
        }

        btnChooseAnother.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(customView)
        bottomSheetDialog.show()
    }
    private fun showLoading(isLoading: Boolean) {
        loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onClick(latitude: Double, longitude: Double, locationName: String) {
        val currentLocation = GeoPoint(
            latitude,
            longitude
        )
        selectedMarker = Marker(mapView)
        selectedMarker!!.position = currentLocation as GeoPoint?
        selectedMarker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(selectedMarker)
        mapView.controller.animateTo(currentLocation)
        searchEditText.hint = Editable.Factory.getInstance().newEditable(locationName)
        recyclerView.visibility=View.GONE
       GlobalScope.launch { delay(1000) }
        showSaveLocationDialog(currentLocation,locationName)
    }
    private fun getAddressLocation(latitude: Double, longitude: Double) :String {
        val geocoder = Geocoder(this).getFromLocation(latitude, longitude, 1)
        try {
            if (geocoder != null) {
                if (geocoder.isNotEmpty()) {
                    val address = geocoder[0]
                    Log.i("TAG", "getAddressLocation:${latitude}+--${longitude} ")
                    val addressText = "${address?.locality}, ${address?.countryName}"
                    return addressText
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}