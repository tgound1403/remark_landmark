import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

private var currentLocation: Location? = null
lateinit var locationManager: LocationManager
//locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//------------------------------------------------------//
val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

val gpsLocationListener: LocationListener = object : LocationListener {
    override fun onLocationChanged(location: Location) {
        val locationByGps= location
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
}
//------------------------------------------------------//
val networkLocationListener: LocationListener = object : LocationListener {
    override fun onLocationChanged(location: Location) {
        val locationByNetwork= location
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
}

//if (hasGps) {
//    locationManager.requestLocationUpdates(
//        LocationManager.GPS_PROVIDER,
//        5000,
//        0F,
//        gpsLocationListener
//    )
//}
////------------------------------------------------------//
//if (hasNetwork) {
//    locationManager.requestLocationUpdates(
//        LocationManager.NETWORK_PROVIDER,
//        5000,
//        0F,
//        networkLocationListener
//    )
//}
//
//val lastKnownLocationByGps =
//    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//lastKnownLocationByGps?.let {
//    locationByGps = lastKnownLocationByGps
//}
////------------------------------------------------------//
//val lastKnownLocationByNetwork =
//    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//lastKnownLocationByNetwork?.let {
//    locationByNetwork = lastKnownLocationByNetwork
//}
////------------------------------------------------------//
//if (locationByGps != null && locationByNetwork != null) {
//    if (locationByGps.accuracy > locationByNetwork!!.accuracy) {
//        currentLocation = locationByGps
//        latitude = currentLocation.latitude
//        longitude = currentLocation.longitude
//        // use latitude and longitude as per your need
//    } else {
//        currentLocation = locationByNetwork
//        latitude = currentLocation.latitude
//        longitude = currentLocation.longitude
//        // use latitude and longitude as per your need
//    }
//}