package com.example.prectice2

import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity()
{
    lateinit var latitudeInput : EditText
    lateinit var longitudeInput : EditText
    lateinit var getAddressButton : Button
    lateinit var resultTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        latitudeInput = findViewById(R.id.latitudeInput)
        longitudeInput = findViewById(R.id.longitudeInput)
        getAddressButton = findViewById(R.id.getAddressButton)
        resultTextView = findViewById(R.id.resultTextView)

        getAddressButton.setOnClickListener {

            val lat = latitudeInput.text.toString().toDoubleOrNull()
            val lon = longitudeInput.text.toString().toDoubleOrNull()

            if (lat != null && lon != null) {
                val address = getAddress(lat, lon)
                resultTextView.text = address
            } else {
                Toast.makeText(this, "Enter valid latitude and longitude", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun getAddress(latitude: Double, longitude: Double): String
    {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try
        {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty())
            {
                val address = addresses[0]
                """
                    DisplayName: ${address.getAddressLine(0)}
                    Road: ${address.thoroughfare ?: "N/A"}
                    Town: ${address.subLocality ?: "N/A"}
                    City: ${address.locality ?: "N/A"}
                    State: ${address.adminArea ?: "N/A"}
                    Pincode: ${address.postalCode ?: "N/A"}
                    Country: ${address.countryName ?: "N/A"}
                """.trimIndent()
            }
            else
            {
                "Address not found"
            }
        }
        catch (e: Exception)
        {
            "Error: ${e.message}"
        }
    }
}