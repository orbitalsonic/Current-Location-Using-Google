package com.orbitalsonic.currentlocationgooglemap

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var tvCurrentLocation:TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCurrentLocation = findViewById(R.id.tvCurrentLocation)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (locationPermissionApproved()){
            getCurrentLocation()
        }else {
            requestLocationPermissions()
        }

    }

    private fun getCurrentLocation(){

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        tvCurrentLocation.text = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                    }
                }
        }

    }

    private fun locationPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }



    private fun requestLocationPermissions() {
        val provideRationale = locationPermissionApproved()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
            val alertDialog: AlertDialog= this.let {
                val builder = AlertDialog.Builder(it)
                builder.setTitle("Permission Alert!")
                builder.setMessage("Location Permission is needed for tracking the path")
                builder.apply {
                    setPositiveButton(
                        "Done"
                    ) { dialog, id ->
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            LOCATION_PERMISSION
                        )
                    }
                    setNegativeButton(
                        "Cancel"
                    ) { dialog, id ->
                        // User cancelled the dialog
                    }
                }

                // Create the AlertDialog
                builder.create()
            }

            alertDialog.show()


        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this,"Permission Denied!",Toast.LENGTH_SHORT).show()
            }
        }
    }
}