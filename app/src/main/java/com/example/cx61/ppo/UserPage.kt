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
        val user = BaseController.getCurrentUser()
        view.findViewById<TextView>(R.id.email).setText(user!!.email)

        val userData = BaseController.getDataUser()
        view.findViewById<TextView>(R.id.first_name).text = userData?.firstName
        view.findViewById<TextView>(R.id.last_name).text = userData?.lastName
        view.findViewById<TextView>(R.id.phone).text = userData?.phone
        view.findViewById<ImageView>(R.id.profile_photo).setImageResource(R.drawable.harley)

        BaseController.getTaskAvatarOfUser().addOnSuccessListener {
            view.findViewById<ImageView>(R.id.edit_profile_photo).setImageBitmap(
                    BaseController.byteArrayToBitmap(it))
        }

        view.findViewById<FloatingActionButton>(R.id.edit_button).setOnClickListener {
            activity!!.findNavController(R.id.nav_host).navigate(R.id.action_userPage_to_editUser)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_page, container, false)
    }
}
