package com.example.cx61.ppo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class StartFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            view!!.findViewById<TextView>(R.id.startScreenText).text = "Hello " + user.email
        }
    }
}