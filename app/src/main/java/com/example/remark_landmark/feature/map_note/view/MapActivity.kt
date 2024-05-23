package com.example.remark_landmark.feature.map_note.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import com.example.remark_landmark.R
import com.example.remark_landmark.feature.auth.presenter.AuthPresenter
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


class MapActivity : AppCompatActivity(), OnMapReadyCallback, IMapView {
    // creating a variable
    // for search view.
    var searchView: SearchView? = null
    private var mMap: GoogleMap? = null
    private var userLocation: Location? = null
    private var mapFragment: SupportMapFragment? = null
    private lateinit var openDialogBtn: Button
    private lateinit var getLocationBtn: Button
    private lateinit var closeDialogBtn: ImageButton
    private lateinit var addNoteBtn: Button
    private lateinit var noteContent: EditText
    private lateinit var addNoteDialog: CardView
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val context: Context = this
    lateinit var iMapPresenter: IMapPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_map)
        setUpSearch()
        setUpMap()
        initPresenter()
        findView()
        setListener()
    }

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
            getLastKnownLocation()
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
                    mMap!!.moveCamera(
                        CameraUpdateFactory.newLatLng(
                            userLatLong
                        )
                    )
                }
            }
    }

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
    }

    private fun findView() {
        getLocationBtn = findViewById(R.id.getLocationBtn)
        closeDialogBtn = findViewById(R.id.closeDialogBtn)
        openDialogBtn = findViewById(R.id.openDialogBtn)
        addNoteBtn = findViewById(R.id.addNoteBtn)
        noteContent = findViewById(R.id.markerNote)
        addNoteDialog = findViewById(R.id.addNoteDialog)
    }

    private fun initPresenter() {
        iMapPresenter = MapPresenter(iMapView = this)
    }

    private fun setUpMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        iMapPresenter.fetchMarkers()
    }

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

    private fun viewToBitmap(view: View): Bitmap {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }

    private fun setUpSearch() {
        // initializing our search view.
        searchView = findViewById(R.id.idSearchView)

        // adding on query listener for our search view.
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // on below line we are getting the
                // location name from search view.
                val location = searchView!!.query.toString()


                // below line is to create a list of address
                // where we will store the list of all address.
                var addressList: List<Address>? = null


                // checking if the entered location is null or not.
                if (location.isNotEmpty()) {
                    // on below line we are creating and initializing a geo coder.
                    val geocoder = Geocoder(this@MapActivity)
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    // on below line we are getting the location
                    // from our list a first position.
                    val address = addressList?.get(0) ?: return false


                    // on below line we are creating a variable for our location
                    // where we will add our locations latitude and longitude.
                    val latLng = LatLng(address.latitude, address.longitude)


                    // on below line we are adding marker to that position.
                    mMap!!.addMarker(MarkerOptions().position(latLng).title(location))


                    // below line is to animate camera to that position.
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun renderNotes(markers: List<MarkerInfoModel>) {

        for (marker in markers) {
            generateNoteMarker(mMap!!, marker)
        }
        mMap!!.moveCamera(
            CameraUpdateFactory.newLatLng(
                LatLng(
                    markers.first().lat,
                    markers.first().long
                )
            )
        )
    }

    override fun onFetchMarkerSuccess(markers: List<MarkerInfoModel>) {
        renderNotes(markers)
    }

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
}