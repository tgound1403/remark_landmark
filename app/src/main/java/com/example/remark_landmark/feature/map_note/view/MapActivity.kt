package com.example.remark_landmark.feature.map_note.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.example.remark_landmark.MainActivity
import com.example.remark_landmark.R
import com.example.remark_landmark.feature.auth.presenter.AuthPresenter
import com.example.remark_landmark.feature.auth.view.ComposeAuthActivity
import com.example.remark_landmark.feature.map_note.model.MarkerInfoModel
import com.example.remark_landmark.feature.map_note.presenter.IMapPresenter
import com.example.remark_landmark.feature.map_note.presenter.MapPresenter
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.IOException

// Activity to manage and display map with markers and handle location services
class MapActivity : AppCompatActivity(), OnMapReadyCallback, IMapView {
    var searchView: SearchView? = null
    private var mMap: GoogleMap? = null
    private var userLocation: Location? = null
    private var mapFragment: SupportMapFragment? = null
    private lateinit var openDialogBtn: Button
    private lateinit var getLocationBtn: Button
    private lateinit var closeDialogBtn: ImageButton
    private lateinit var addNoteBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var noteContent: EditText
    private lateinit var addNoteDialog: CardView
    private lateinit var auth: FirebaseAuth
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val context: Context = this
    lateinit var iMapPresenter: IMapPresenter

    // Lifecycle method called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_map)
        setUpSearch()  // Set up the search functionality
        setUpMap()  // Initialize the map
        initPresenter()  // Initialize the presenter
        findView()  // Bind UI components
        setListener()  // Set listeners for UI components
    }

    // Method to request user's location
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            getLastKnownLocation()  // Get the last known location if permission is granted
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    userLocation = it
                    val userLatLong = LatLng(it.latitude, it.longitude)
                    mMap!!.addMarker(
                        MarkerOptions().position(userLatLong)
                            .title("Your current location")
                    )
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLong, 10f))
                }
            }
    }

    // Handle the result of the location permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastKnownLocation()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    // Set up listeners for the buttons and other UI components
    private fun setListener() {
        openDialogBtn.setOnClickListener {
            addNoteDialog.visibility = View.VISIBLE
        }
        closeDialogBtn.setOnClickListener {
            addNoteDialog.visibility = View.GONE
        }
        addNoteBtn.setOnClickListener {
            val note = noteContent.text.toString()
            val currentUser = auth.currentUser
            Log.d("MapActivity", "${userLocation?.latitude}, ${userLocation?.longitude}")

            iMapPresenter.createMarker(
                owner = currentUser?.email ?: "",
                note = note,
                lat = userLocation?.latitude ?: 0.0,
                long = userLocation?.longitude ?: 0.0
            )
            addNoteDialog.visibility = View.GONE
        }
        getLocationBtn.setOnClickListener {
            getLocation()
        }
        logoutBtn.setOnClickListener {
            iMapPresenter.logout()
        }
    }

    // Bind UI components to their respective views
    private fun findView() {
        getLocationBtn = findViewById(R.id.getLocationBtn)
        closeDialogBtn = findViewById(R.id.closeDialogBtn)
        openDialogBtn = findViewById(R.id.openDialogBtn)
        addNoteBtn = findViewById(R.id.addNoteBtn)
        logoutBtn = findViewById(R.id.logoutBtn)
        noteContent = findViewById(R.id.markerNote)
        addNoteDialog = findViewById(R.id.addNoteDialog)
    }

    // Initialize the presenter
    private fun initPresenter() {
        iMapPresenter = MapPresenter(iMapView = this)
    }

    // Set up the map fragment and async load the map
    private fun setUpMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    // Callback when the map is ready to be used
    override fun onMapReady(googleMap: GoogleMap) {
        with(googleMap) {
            mMap = this
            iMapPresenter.fetchMarkers()  // Fetch existing markers to display on the map
        }
    }

    // Generate a custom marker with note information
    private fun generateNoteMarker(map: GoogleMap, markerInfoModel: MarkerInfoModel) {
        val markerView =
            (context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.marker_layout,
                null
            )
        val ownerText = markerView.findViewById<TextView>(R.id.markerOwner)
        val noteText = markerView.findViewById<TextView>(R.id.markerNote)
        val cardView = markerView.findViewById<CardView>(R.id.markerCardView)
        val location = LatLng(markerInfoModel.lat, markerInfoModel.long)
        noteText.text = markerInfoModel.note
        ownerText.text = markerInfoModel.owner

        val icon = BitmapDescriptorFactory.fromBitmap(viewToBitmap(cardView))
        val markerOptions = MarkerOptions().position(location).icon(icon)
        map.addMarker(markerOptions)
    }

    // Convert a view to a bitmap for custom marker icon
    private fun viewToBitmap(view: View): Bitmap {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }

    // Set up the search functionality for the map
    private fun setUpSearch() {
        // Initializing the search view
        searchView = findViewById(R.id.idSearchView)

        // Adding query listener for the search view
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Get the location name from search view
                val location = searchView!!.query.toString()

                // Create a list of addresses to store the list of all addresses
                var addressList: List<Address>? = null

                // Check if the entered location is not empty
                if (location.isNotEmpty()) {
                    // Create and initialize a geocoder
                    val geocoder = Geocoder(this@MapActivity)
                    try {
                        // Get location from the location name and add it to address list
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    // Get the location from the list at the first position
                    val address = addressList?.get(0) ?: return false

                    // Create a variable for the location's latitude and longitude
                    val latLng = LatLng(address.latitude, address.longitude)

                    // Add marker to that position
                    mMap!!.addMarker(MarkerOptions().position(latLng).title(location))

                    // Animate camera to that position
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    // Render the notes on the map
    private fun renderNotes(markers: List<MarkerInfoModel>) {
        for (marker in markers) {
            generateNoteMarker(mMap!!, marker)
        }
        mMap!!.moveCamera(
            CameraUpdateFactory.newLatLng(
                LatLng(
                    userLocation?.latitude ?:markers.first().lat,
                    userLocation?.longitude ?: markers.first().long
                )
            )
        )
    }

    // Callback for successfully fetching markers
    override fun onFetchMarkerSuccess(markers: List<MarkerInfoModel>) {
        renderNotes(markers)
    }

    // Callback for successfully creating a marker
    override fun onCreateMarkerSuccess() {
        iMapPresenter.fetchMarkers()
    }

    override fun onFoundMarker() {
        TODO("Not yet implemented")
    }

    override fun onSelectMarker() {
        TODO("Not yet implemented")
    }

    override fun onShowDialog() {
        addNoteDialog.visibility = View.VISIBLE
    }

    override fun onGetLocationSuccess(location: Location?) {
        TODO("Not yet implemented")
    }

    // Callback for logout action
    override fun onLogout() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
