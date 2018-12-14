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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_edit_user.*
import kotlinx.android.synthetic.main.fragment_user_page.*

class UserPage : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        email.setText(user!!.email)
        FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue(User::class.java)
                first_name.setText(user?.firstName)
                last_name.setText(user?.lastName)
                phone.setText(user?.phone)
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        profile_photo.setImageResource(R.mipmap.ic_launcher_round)
        FirebaseStorage.getInstance().getReference().child("avatars/" + user.uid + ".jpg").getBytes(1024*1024*1024).addOnSuccessListener {
            profile_photo.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
        }
        edit_button.setOnClickListener { activity!!.findNavController(R.id.nav_host).navigate(R.id.action_userPage_to_editUser) }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_page, container, false)
    }
}
