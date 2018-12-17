package com.example.cx61.ppo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(){

    lateinit var cm: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
//        BaseController.getOffline()
        NavigationUI.setupWithNavController(nav_view, findNavController(R.id.nav_host))
        nav_view.getHeaderView(0).setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            findNavController(R.id.nav_host).navigate(R.id.userPage)
        }

        cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm.activeNetworkInfo == null || !cm.activeNetworkInfo.isConnected)
            network_icon.setImageResource(R.drawable.ic_network_off)
        cm.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network){
                runOnUiThread {network_icon.setImageResource(R.drawable.ic_network_on)}
            }
            override fun onUnavailable() {
                runOnUiThread {network_icon.setImageResource(R.drawable.ic_network_off)}
            }
            override fun onLosing(network: Network?, maxMsToLive: Int) {
                runOnUiThread {network_icon.setImageResource(R.drawable.ic_network_off)}
            }
            override fun onLost(network: Network){
                runOnUiThread {network_icon.setImageResource(R.drawable.ic_network_off)}
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val header = nav_view.getHeaderView(0)
        val user = BaseController.getCurrentUser()
        if (user == null) {
            nav_view.menu.setGroupVisible(R.id.menu_group, false)
            nav_view.menu.setGroupVisible(R.id.login_group, true)
            nav_view.menu.setGroupVisible(R.id.logout_group, false)
            header.isClickable = false
            header.findViewById<ImageView>(R.id.header_image).setImageResource(R.drawable.harley)
            header.findViewById<TextView>(R.id.header_email).text = "Unauthorized"
        }
        else {
            nav_view.menu.setGroupVisible(R.id.menu_group, true)
            nav_view.menu.setGroupVisible(R.id.login_group, false)
            nav_view.menu.setGroupVisible(R.id.logout_group, true)
            nav_view.menu.getItem(4).setOnMenuItemClickListener {
                BaseController.signOut(); recreate();true }
            header.isClickable = true

            BaseController.getTaskAvatarOfUser().addOnSuccessListener {
                header.findViewById<ImageView>(R.id.header_image).setImageBitmap(
                        BaseController.byteArrayToBitmap(it))
            }.addOnFailureListener{
                header.findViewById<ImageView>(R.id.header_image).setImageResource(R.drawable.harley)
            }

            header.findViewById<TextView>(R.id.header_email).text = user.email
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                findNavController(R.id.nav_host).navigate(R.id.AboutActivity)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
