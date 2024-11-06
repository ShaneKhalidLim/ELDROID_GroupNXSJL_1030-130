package com.limyusontudtud.souvseek

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.io.IOException

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)

        // Initialize Places SDK if not initialized already
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_map_api_key))
        }

        // Initialize AutocompleteSupportFragment
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.LAT_LNG, Place.Field.NAME))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                if (place.latLng != null) {
                    val latLng = place.latLng!!
                    Toast.makeText(this@GoogleMapActivity, latLng.toString(), Toast.LENGTH_SHORT).show()
                    moveMapCamera(latLng)
                }
            }

            override fun onError(status: Status) {
                Log.e("Error", status.statusMessage ?: "Unknown error")
            }
        })

        // Initialize MapFragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }


    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        val address = intent.getStringExtra("location")

        if (!address.isNullOrEmpty()) {
            val geocoder = Geocoder(this)
            try {
                val locationList = geocoder.getFromLocationName(address, 1)

                if (locationList!!.isNotEmpty()) {
                    val latLng = LatLng(locationList[0].latitude, locationList[0].longitude)

                    val markerOptions = MarkerOptions()
                        .position(latLng)
                        .title(intent.getStringExtra("title") ?: "")
                        .snippet("Custom Marker Snippet")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

                    mGoogleMap?.addMarker(markerOptions)
                    moveMapCamera(latLng)
                } else {
                    Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Log.e("Geocoding", "Error geocoding address: ${e.message}")
                Toast.makeText(this, "Error geocoding address", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Address not provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveMapCamera(latLng: LatLng) {
        mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun getLocationLatLng(address: String): LatLng {
        val geocoder = Geocoder(this)
        val locationList = geocoder.getFromLocationName(address, 1)
        return if (locationList!!.isNotEmpty()) {
            LatLng(locationList[0].latitude, locationList[0].longitude)
        } else {
            LatLng(0.0, 0.0) // Default location if geocoding fails
        }
    }

    // Generic function to add a marker to the map
    private fun addMarker(latLng: LatLng, title: String, snippet: String) {
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(title)
            .snippet(snippet)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)) // Set custom marker icon if needed
        mGoogleMap?.addMarker(markerOptions)
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }
}

