package com.example.remark_landmark.feature.map_note.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.remark_landmark.R
import com.example.remark_landmark.feature.auth.presenter.AuthPresenter
import com.example.remark_landmark.feature.map_note.model.MarkerInfoModel
import com.example.remark_landmark.feature.map_note.presenter.IMapPresenter
import com.example.remark_landmark.feature.map_note.presenter.MapPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class MapActivity : AppCompatActivity(), OnMapReadyCallback, IMapView {
    // creating a variable
    // for search view.
    var searchView: SearchView? = null
    private var mMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null

    private val context: Context = this
    lateinit var iMapPresenter: IMapPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setUpSearch()
        setUpMap()
        initPresenter()
        iMapPresenter.fetchMarkers()
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
        // adding on click listener to marker of google maps.
//        mMap!!.setOnMarkerClickListener { marker -> // on marker click we are getting the title of our marker
//            // which is clicked and displaying it in a toast message.
//            val markerName = marker.title
//            Toast.makeText(
//                this@MapActivity,
//                "Clicked location is $markerName",
//                Toast.LENGTH_SHORT
//            ).show()
//            false
//        }
    }

    private fun generateNoteMarker(note: String, map: GoogleMap, location: LatLng) {
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

    override fun onFetchMarkerSuccess(markers: List<MarkerInfoModel>) {
        for (marker in markers) {
            val location = LatLng(marker.lat, marker.long)
            mMap!!.addMarker(MarkerOptions().position(location))
            generateNoteMarker(marker.note, mMap!!, location)
        }
        mMap!!.moveCamera(
            CameraUpdateFactory.newLatLng(
                LatLng(
                    markers.last().lat,
                    markers.last().long
                )
            )
        )
    }

    override fun onCreateMarkerSuccess() {
        TODO("Not yet implemented")
    }

    override fun onFoundMarker() {
        TODO("Not yet implemented")
    }

    override fun onSelectMarker() {
        TODO("Not yet implemented")
    }

    override fun onGenerateMarker(note: String, map: GoogleMap, location: LatLng) {
        TODO("Not yet implemented")
    }
}