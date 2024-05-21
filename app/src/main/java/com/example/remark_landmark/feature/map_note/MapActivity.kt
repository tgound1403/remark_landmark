package com.example.remark_landmark.feature.map_note

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.remark_landmark.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    // creating a variable
    // for search view.
    var searchView: SearchView? = null
    private var mMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private var db: FirebaseFirestore? = null

    private val binhThuan: LatLng = LatLng(11.0, 107.0)
    private val binhDinh: LatLng = LatLng(13.0, 109.0)
    private val giaLai: LatLng = LatLng(14.0, 108.0)
    private val tienGiang: LatLng = LatLng(10.0, 106.0)

    private val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setUpSearch()
        setUpMap()
        useFireStore()
    }

    private fun useFireStore() {
        db = FirebaseFirestore.getInstance()
        readDb(db)
        writeDb(db)
    }

    private fun writeDb(db: FirebaseFirestore?) {
        val city = hashMapOf(
            "name" to "Los Angeles",
            "state" to "CA",
            "country" to "USA",
        )

        db!!.collection("cities").document("LA")
            .set(city)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

        val user1 = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1815,
        )

        // Add a new document with a generated ID
        db.collection("users")
            .add(user1)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        // Create a new user with a first, middle, and last name
        val user2 = hashMapOf(
            "first" to "Alan",
            "middle" to "Mathison",
            "last" to "Turing",
            "born" to 1912,
        )

        // Add a new document with a generated ID
        db.collection("users")
            .add(user2)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }

    private fun readDb(db: FirebaseFirestore?) {
        val documentReference = db!!.collection("locations").document("0QmMX6WEJfopPvHGHxwn")

        documentReference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                Toast.makeText(this@MapActivity, "Error found is $e", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: ${snapshot.data}")
                // below line is to create a geo point and we are getting
                // geo point from firebase and setting to it.
                val geoPoint = snapshot.getGeoPoint("geoPoint")

                // getting latitude and longitude from geo point
                // and setting it to our location.
                val location = LatLng(
                    geoPoint?.latitude ?: 0.0, geoPoint?.longitude ?: 0.0
                )

                // adding marker to each location on google maps
                mMap!!.addMarker(MarkerOptions().position(location).title("Marker from DB"))

                // below line is use to move camera.
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(location))
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }

    private fun setUpMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        generateMarker("I'm in Binh Dinh", mMap!!, binhDinh)
        generateMarker("I'm in Binh Thuan", mMap!!, binhThuan)
        generateMarker("I'm in Gia Lai", mMap!!, giaLai)
        generateMarker("I'm in Tien Giang", mMap!!, tienGiang)

        // adding on click listener to marker of google maps.
        mMap!!.setOnMarkerClickListener { marker -> // on marker click we are getting the title of our marker
            // which is clicked and displaying it in a toast message.
            val markerName = marker.title
            Toast.makeText(
                this@MapActivity,
                "Clicked location is $markerName",
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }

    private fun generateMarker(note: String, map: GoogleMap, location: LatLng) {
        val markerView =
            (context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.marker_layout,
                null
            )
        val textView = markerView.findViewById<TextView>(R.id.markerText)
        val cardView = markerView.findViewById<CardView>(R.id.markerCardView)
        textView.text = note

        val icon = BitmapDescriptorFactory.fromBitmap(viewToBitmap(cardView))
        val markerOptions = MarkerOptions().position(location).icon(icon).title(note)
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
}