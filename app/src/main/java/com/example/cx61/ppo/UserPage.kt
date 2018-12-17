package com.example.cx61.ppo

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class UserPage : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = BaseController.getCurrentUser()
        try {
            view.findViewById<TextView>(R.id.email).setText(user!!.email)
        }
        catch (e: Exception) {
            view.findViewById<TextView>(R.id.email).text = ""
        }

        var userData: User? = null
        try{
            BaseController.getTaskDataUser().addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    userData = dataSnapshot.getValue(User::class.java)
                    view.findViewById<TextView>(R.id.first_name).text = userData?.firstName
                    view.findViewById<TextView>(R.id.last_name).text = userData?.lastName
                    view.findViewById<TextView>(R.id.phone).text = userData?.phone
                    view.findViewById<TextView>(R.id.rss).text = userData?.rssUrl
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
        catch (e: Exception) {
            view.findViewById<TextView>(R.id.first_name).text = ""
            view.findViewById<TextView>(R.id.last_name).text = ""
            view.findViewById<TextView>(R.id.phone).text = ""
            view.findViewById<TextView>(R.id.rss).text = ""
        }


        val avatar: Bitmap? = BaseController.imageViewToBitmap(activity!!.findViewById<ImageView>(R.id.header_image))
        view.findViewById<ImageView>(R.id.profile_photo).setImageBitmap(avatar)


        view.findViewById<FloatingActionButton>(R.id.edit_button).setOnClickListener {
            activity!!.findNavController(R.id.nav_host).navigate(R.id.action_userPage_to_editUser)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_page, container, false)
    }
}
