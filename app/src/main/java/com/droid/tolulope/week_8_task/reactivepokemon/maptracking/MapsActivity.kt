package com.droid.tolulope.week_8_task.reactivepokemon.maptracking

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.droid.tolulope.week_8_task.GET_LOCATION_UPDATE
import com.droid.tolulope.week_8_task.LOC_UPDATE_STATE
import com.droid.tolulope.week_8_task.MY_NODE
import com.droid.tolulope.week_8_task.R
import com.droid.tolulope.week_8_task.reactivepokemon.model.MyLocation
import com.droid.tolulope.week_8_task.reactivepokemon.ui.PokemonActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fbReference: FirebaseDatabase
    private lateinit var myMarker1: Marker
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true

        //  Adds my partner's marker, with an icon as his image and sets a default position
        val default = LatLng(0.0, 0.0)
        myMarker1 = map.addMarker(
            MarkerOptions().position(default).icon(
                BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.obh
                    )
                )
            ).alpha(0.7f)
        )
        getLocationUpdates()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        fbReference = FirebaseDatabase.getInstance()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // provides information such as latitude, longitude, bearing, altitude e.t.c.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Receives updates when device location changes
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                saveLocation(lastLocation)
            }
        }
        makeLocationRequest()
        readChanges()
    }

    /**
     * Removes location updates when application about to leave foreground
     */
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * Starts locationUpdates if update state is false
     */
    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    // Handles permission request for getting location update if granted for ACCESS_FINE_LOCATION
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == GET_LOCATION_UPDATE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationUpdates()
            } else {
                Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Starts update request for request check settings
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOC_UPDATE_STATE) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    /**
     * Requests for fine location permission and if already granted sets ups a blue dot for the device
     * current location
     */
    private fun getLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                GET_LOCATION_UPDATE
            )
            return
        }
        map.isMyLocationEnabled = true
    }

    /**
     * Creates an instance of location request,sets interval, fastest interval
     * and a high priority for the realtime update and makes a request for
     * the user to turn on location if disabled, after which it can start receiving
     * location updates
     */
    private fun makeLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this@MapsActivity, LOC_UPDATE_STATE)
                } catch (sendEx: IntentSender.SendIntentException) {

                }
            }
        }
    }

    /**
     * Requests permission if not granted, and if granted makes a call to the
     * fused location client to request location updates using the location request and callback
     */
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                GET_LOCATION_UPDATE
            )
            return
        }
        /*
        The Looper object whose message queue will be used to implement the callback mechanism, location
        request to make the request and callback for the location updates
         */
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    /**
     * Gets a reference to partners child and listens for changes in the location update. Gets the current
     * location latitude and longitude and sets on the partners marker
     */
    private fun readChanges() {
        fbReference.reference.child("Location").child("Godday").child("location")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        try {
                            val location = snapshot.getValue(MyLocation::class.java)
                            if (location != null) {
                                val latLng = location.latitude?.let {
                                    location.longitude?.let { it1 ->
                                        LatLng(
                                            it, it1
                                        )
                                    }
                                }
                                latLng?.let { partnerMarkerOnMap(it) }
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.0f))
                                myMarker1.title = "Obehi -> Latitude: ${location.latitude} Longitude: ${location.longitude}"
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@MapsActivity, e.message, Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("MapsActivity", error.message)
                }
            })
    }


    // Sets partner's position on map based on the received location from firebase
    private fun partnerMarkerOnMap(currentLatLng: LatLng) {
        myMarker1.position = currentLatLng
    }

    // Save location received from locationCallback into my reference on the firebase database
    private fun saveLocation(location: Location) {
        val loc = MyLocation()
        loc.latitude = location.latitude
        loc.longitude = location.longitude
        fbReference.reference.child(MY_NODE).setValue(loc)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
           R.id.pokemon -> {
               startActivity(Intent(this, PokemonActivity::class.java))
               return true
           }
            else -> super.onOptionsItemSelected(item)
        }

    }
}