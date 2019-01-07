package com.example.cx61.ppo

import android.app.ProgressDialog
import android.content.Context
import android.media.Image
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
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
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    lateinit var cm: ConnectivityManager
    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        NavigationUI.setupWithNavController(nav_view, findNavController(R.id.nav_host))
        nav_view.setNavigationItemSelectedListener(this)
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
        } else {

            nav_view.menu.setGroupVisible(R.id.menu_group, true)
            nav_view.menu.setGroupVisible(R.id.login_group, false)
            nav_view.menu.setGroupVisible(R.id.logout_group, true)
            header.isClickable = true

            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (!(cm.activeNetworkInfo == null || !cm.activeNetworkInfo.isConnected)) {
                progressDialog = ProgressDialog(this)
                progressDialog?.setMessage("Loading")
                progressDialog?.show()

                BaseController.getTaskAvatarOfUser().addOnSuccessListener {
                    header.findViewById<ImageView>(R.id.header_image).setImageBitmap(
                            BaseController.byteArrayToBitmap(it))
                }.addOnFailureListener {
                    header.findViewById<ImageView>(R.id.header_image).setImageResource(R.drawable.harley)
                    progressDialog?.dismiss()
                }.addOnCompleteListener { progressDialog?.dismiss()
                }.addOnCanceledListener { progressDialog?.dismiss() }
            }


            header.findViewById<TextView>(R.id.header_email).text = user.email

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog = null

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (findNavController(R.id.nav_host).getCurrentDestination()?.getId() == R.id.editUser) {
                askAndNavigateToFragment(R.id.userPage)
            } else super.onBackPressed()
        }
    }

    private fun askAndNavigateToFragment(fragmentId: Int){
        val builder = android.app.AlertDialog.Builder(this)
        builder.setMessage("You're about to loose unsaved changes!")
                .setCancelable(false)
                .setNegativeButton("Leave") { dialog, _ ->
                    run {
                        if (fragmentId == R.id.logout){
                            BaseController.signOut()
                            findNavController(R.id.nav_host).navigate(R.id.startFragment)}
                        else{
                        findNavController(R.id.nav_host).navigate(fragmentId)
                        dialog.cancel()
                        }
                    }
                }.setPositiveButton("Stay"){dialog, _ ->
                    run {
                        dialog.cancel()
                    }
                }
        val alert_complite = builder.create()
        alert_complite.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                if (findNavController(R.id.nav_host).getCurrentDestination()?.getId() == R.id.editUser) {
                    askAndNavigateToFragment(R.id.AboutActivity)
                } else findNavController(R.id.nav_host).navigate(R.id.AboutActivity)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host)
        item.isChecked = false
        if (navController.currentDestination?.id == R.id.editUser) {
            askAndNavigateToFragment(item.itemId)
            drawer_layout.closeDrawer(GravityCompat.START)
            return true
        }
        if (item.itemId == R.id.logout)
        {
            BaseController.signOut()
            recreate()
            drawer_layout.closeDrawer(GravityCompat.START)
            findNavController(R.id.nav_host).navigate(R.id.startFragment)
            return true
        }
        else {
            item.isChecked = true
            navController.navigate(item.itemId)
            drawer_layout.closeDrawer(GravityCompat.START)
            return true
        }
    }
}
