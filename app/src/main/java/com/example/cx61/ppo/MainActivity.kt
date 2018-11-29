package com.example.cx61.ppo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.widget.TextView
import android.telephony.TelephonyManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity() {

    val PERMISSION_REQUEST_CODE = 0

    fun IMEI(){
        val manager: TelephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val imei: TextView = findViewById(R.id.imei)

        if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            imei.text = manager.deviceId
        }
        else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Permissions")
                    .setMessage("This application need permissions to show IMEI.")
                    .setCancelable(false)
                    .setNegativeButton("Ok") { dialog, _ -> dialog.cancel() }
                    .setOnCancelListener { ActivityCompat.requestPermissions(
                            this, arrayOf(Manifest.permission.READ_PHONE_STATE), PERMISSION_REQUEST_CODE) }
            val alert = builder.create()
            alert.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        IMEI()
        val version: TextView = findViewById(R.id.version)
        version.text = BuildConfig.VERSION_NAME
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode ) {
            PERMISSION_REQUEST_CODE -> {
                val manager: TelephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
                val imei: TextView = findViewById(R.id.imei)
                if (grantResults.isNotEmpty() and (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    imei.text = manager.deviceId
                else
                    imei.text = "-----"
            }
        }
    }
}
