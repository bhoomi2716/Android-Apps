package com.example.googlemap

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.CarrierConfigManager.Gps
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.googlemap.databinding.ActivityMapsBinding
import java.time.LocalTime

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    lateinit var Manager : LocationManager
    lateinit var Listener : LocationListener
    lateinit var location : Location
    var lattitude:Double=0.0
    var longitude:Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Manager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (!Manager.isProviderEnabled(GPS_PROVIDER))
        {
            Toast.makeText(applicationContext, "GPS Is Not Working..!!", Toast.LENGTH_SHORT).show()
        }

        if (!Manager.isProviderEnabled(NETWORK_PROVIDER))
        {
            Toast.makeText(applicationContext, "Network Is Not Working..!!", Toast.LENGTH_SHORT).show()
        }

        else
        {
            Toast.makeText(applicationContext, "Location Fetching", Toast.LENGTH_SHORT).show()

        }

        Listener = object : LocationListener
        {
            override fun onLocationChanged(location: Location)
            {
                lattitude = location.latitude
                longitude = location.longitude
            }
        }

        if (Manager.isProviderEnabled(NETWORK_PROVIDER))
        {
            if (ActivityCompat.checkSelfPermission(this,ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                return
            }

            Manager.requestLocationUpdates(NETWORK_PROVIDER,0,0.0F,Listener)
        }
        location = Manager.getLastKnownLocation(NETWORK_PROVIDER)!!

        if (location!=null)
        {
            lattitude = location.latitude
            longitude = location.longitude
            Toast.makeText(applicationContext, ""+location.latitude, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(lattitude, longitude)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker At Your Place"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}