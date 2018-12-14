package com.example.cx61.ppo

import android.os.Bundle
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.ui.navigateUp
import com.google.firebase.storage.FirebaseStorage


class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        NavigationUI.setupWithNavController(nav_view, findNavController(R.id.nav_host))
        nav_view.getHeaderView(0).setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            findNavController(R.id.nav_host).navigate(R.id.userPage)
        }
    }

    override fun onResume() {
        super.onResume()
        val header = nav_view.getHeaderView(0)
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            nav_view.menu.setGroupVisible(R.id.menu_group, false)
            nav_view.menu.setGroupVisible(R.id.login_group, true)
            nav_view.menu.setGroupVisible(R.id.logout_group, false)
            header.isClickable = false
            header.findViewById<ImageView>(R.id.header_image).setImageResource(R.mipmap.ic_launcher_round)
            header.findViewById<TextView>(R.id.header_email).text = "Unauthorized"
        }
        else {
            nav_view.menu.setGroupVisible(R.id.menu_group, true)
            nav_view.menu.setGroupVisible(R.id.login_group, false)
            nav_view.menu.setGroupVisible(R.id.logout_group, true)
            nav_view.menu.getItem(4).setOnMenuItemClickListener { FirebaseAuth.getInstance().signOut(); recreate();true }
            header.isClickable = true
            FirebaseStorage.getInstance().getReference().child("avatars/" + user.uid + ".jpg").getBytes(1024*1024*1024).addOnSuccessListener {
                header.findViewById<ImageView>(R.id.header_image).setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            }.addOnFailureListener {
                header.findViewById<ImageView>(R.id.header_image).setImageResource(R.mipmap.ic_launcher_round)
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
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_about -> {
                findNavController(R.id.nav_host).navigate(R.id.AboutActivity)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
