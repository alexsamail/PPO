package com.example.cx61.ppo

import android.app.Activity.RESULT_OK
import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class EditUser : Fragment() {
    val PICK_IMAGE = 0
    val TAKE_PHOTO = 1
    var photo: Bitmap? = null

    var EMAIL: String? = ""
    var FIRST_NAME: String? = ""
    var LAST_NAME: String? = ""
    var PHONE: String? = ""
    var AVATAR: Bitmap? = null
    var IS_AVATAR = 0

    fun init_vars(){
        val user = FirebaseAuth.getInstance().currentUser
        EMAIL = user?.email
        FirebaseDatabase.getInstance().getReference().child("users").child(user!!.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user_second: User? = dataSnapshot.getValue(User::class.java)
                FIRST_NAME = user_second?.firstName
                LAST_NAME = user_second?.lastName
                PHONE = user_second?.phone
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

        FirebaseStorage.getInstance().getReference().child("avatars/" + user.uid + ".jpg").getBytes(1024*1024*1024).addOnSuccessListener {
            AVATAR = BitmapFactory.decodeByteArray(it, 0, it.size)
            view?.findViewById<ImageView>(R.id.edit_profile_photo)?.setImageBitmap(AVATAR)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (savedInstanceState == null) {
            init_vars()
            IS_AVATAR = 0
        }
        else IS_AVATAR = 1
        return inflater.inflate(R.layout.fragment_edit_user, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<EditText>(R.id.edit_email).setText(EMAIL)
        val user = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().getReference().child("users").child(user!!.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                view.findViewById<EditText>(R.id.edit_first_name).setText(FIRST_NAME)
                view.findViewById<EditText>(R.id.edit_last_name).setText(LAST_NAME)
                view.findViewById<EditText>(R.id.edit_phone).setText(PHONE)
                if (IS_AVATAR == 1){
                    view.findViewById<ImageView>(R.id.edit_profile_photo).setImageBitmap(AVATAR)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

        view.findViewById<Button>(R.id.edit_gallery).setOnClickListener { takeFromGallery() }
        view.findViewById<Button>(R.id.edit_camera).setOnClickListener { takePhoto() }
        view.findViewById<Button>(R.id.edit_save).setOnClickListener {
            save()
            activity!!.findNavController(R.id.nav_host).navigateUp()
        }
    }

    fun takePhoto() {
        if (checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), TAKE_PHOTO)
        } else {
            val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, TAKE_PHOTO)
        }
    }

    fun takeFromGallery() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE -> {
                    photo = BitmapFactory.decodeStream(activity!!.contentResolver.openInputStream(data!!.data))
                    AVATAR = photo
                    view?.findViewById<ImageView>(R.id.edit_profile_photo)?.setImageBitmap(photo)
                }
                TAKE_PHOTO -> {
                    photo = data!!.extras.get("data") as Bitmap
                    AVATAR = photo
                    view?.findViewById<ImageView>(R.id.edit_profile_photo)?.setImageBitmap(photo)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode ) {
            TAKE_PHOTO -> {
                val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, TAKE_PHOTO)
            }
        }
    }

    private fun imageToBitmap(image: ImageView): ByteArray {
        val bitmap = (image.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)

        return stream.toByteArray()
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putString("first_name", view?.findViewById<EditText>(R.id.edit_first_name)!!.text.toString())
        outState.putString("last_name", view?.findViewById<EditText>(R.id.edit_last_name)!!.text.toString())
        outState.putString("email", view?.findViewById<EditText>(R.id.edit_email)!!.text.toString())
        outState.putString("phone", view?.findViewById<EditText>(R.id.edit_phone)!!.text.toString())
        outState.putByteArray("avatar", imageToBitmap(view!!.findViewById<ImageView>(R.id.edit_profile_photo)))

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null){
            FIRST_NAME = savedInstanceState.getString("first_name")
            LAST_NAME = savedInstanceState.getString("last_name")
            EMAIL = savedInstanceState.getString("email")
            PHONE = savedInstanceState.getString("phone")
            val image = savedInstanceState.getByteArray("avatar")

            if (image != null){
               AVATAR = BitmapFactory.decodeByteArray(image, 0, image.size)
            }
        }
    }

    fun save(){
        val user = FirebaseAuth.getInstance().currentUser
        val email = view?.findViewById<TextView>(R.id.edit_email)!!.text.toString()
        val firstName = view?.findViewById<TextView>(R.id.edit_first_name)!!.text.toString()
        val lastName = view?.findViewById<TextView>(R.id.edit_last_name)!!.text.toString()
        val phone = view?.findViewById<TextView>(R.id.edit_phone)!!.text.toString()
        user!!.updateEmail(email)
        val db = FirebaseDatabase.getInstance().getReference()
        db.child("users").child(user.uid).setValue(User(email, firstName, lastName, phone))

        val storage = FirebaseStorage.getInstance().getReference()
        val baos = ByteArrayOutputStream()
        AVATAR?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        storage.child("avatars/" + user.uid + ".jpg").putBytes(baos.toByteArray())
    }
}