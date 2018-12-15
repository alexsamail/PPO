package com.example.cx61.ppo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.graphics.BitmapFactory
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text

class UserPage : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        view.findViewById<TextView>(R.id.email).setText(user!!.email)
        FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue(User::class.java)
                view.findViewById<TextView>(R.id.first_name).setText(user?.firstName)
                view.findViewById<TextView>(R.id.last_name).setText(user?.lastName)
                view.findViewById<TextView>(R.id.phone).setText(user?.phone)
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        FirebaseStorage.getInstance().getReference().child("avatars/" + user.uid + ".jpg").getBytes(1024*1024*1024).addOnSuccessListener {
            view.findViewById<ImageView>(R.id.profile_photo)?.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
        }
        view.findViewById<FloatingActionButton>(R.id.edit_button).setOnClickListener { activity!!.findNavController(R.id.nav_host).navigate(R.id.action_userPage_to_editUser) }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_page, container, false)
    }
}
