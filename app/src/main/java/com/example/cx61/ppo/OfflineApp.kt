package com.example.cx61.ppo

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class PPOApp : Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}