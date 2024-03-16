package com.example.weatherguide

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fabGetCurrentLocation :FloatingActionButton
    private var selectedMarker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
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
                    showSaveLocationDialog(touchedPoint)
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
    private fun showSaveLocationDialog(geoPoint: GeoPoint) {
        val bottomSheetDialog = BottomSheetDialog(this)

        val customView = layoutInflater.inflate(R.layout.custom_alert_dialog, null)

        val btnSave = customView.findViewById<Button>(R.id.btnSave)
        val btnChooseAnother = customView.findViewById<Button>(R.id.btnChooseAnother)

        btnSave.setOnClickListener {
            val latitude = geoPoint.latitude
            val longitude = geoPoint.longitude
            mapView.controller.animateTo(geoPoint)
            val sharedPreferences = getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putFloat("latitude", latitude.toFloat())
            editor.putFloat("longitude", longitude.toFloat())
            editor.apply()
            bottomSheetDialog.dismiss()
            finish()
        }

        btnChooseAnother.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(customView)
        bottomSheetDialog.show()
    }
}