package com.example.cx61.ppo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.widget.TextView
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AlertDialog
import android.content.pm.PackageManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

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
            val attention = builder.create()
            attention.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
     //   about_toolbar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.title = "About"

        IMEI()
        val version: TextView = findViewById(R.id.version)
        version.text = BuildConfig.VERSION_NAME

        val versionCode: TextView = findViewById(R.id.code)
        versionCode.text = BuildConfig.VERSION_CODE.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
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