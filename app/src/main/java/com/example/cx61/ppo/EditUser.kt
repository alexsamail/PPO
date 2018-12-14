package com.example.cx61.ppo

import android.app.Activity.RESULT_OK
import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.android.synthetic.main.fragment_edit_user.*
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.lang.Exception
import kotlin.jvm.javaClass

class EditUser : Fragment() {
    val PICK_IMAGE = 0
    val TAKE_PHOTO = 1
    var photo: Bitmap? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_user, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        edit_email.setText(user!!.email)
        FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue(User::class.java)
                edit_first_name.setText(user?.firstName)
                edit_last_name.setText(user?.lastName)
                edit_phone.setText(user?.phone)
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        edit_profile_photo.setImageResource(R.mipmap.ic_launcher_round)
        FirebaseStorage.getInstance().getReference().child("avatars/" + user.uid + ".jpg").getBytes(1024*1024*1024).addOnSuccessListener {
            edit_profile_photo.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
        }
        edit_gallery.setOnClickListener { takeFromGallery() }
        edit_camera.setOnClickListener { takePhoto() }
        edit_save.setOnClickListener { save() }
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
                    edit_profile_photo.setImageBitmap(photo)
                }
                TAKE_PHOTO -> {
                    photo = data!!.extras.get("data") as Bitmap
                    edit_profile_photo.setImageBitmap(photo)
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

    fun save(){
        val user = FirebaseAuth.getInstance().currentUser
        val email = edit_email!!.text.toString()
        val firstName = edit_first_name!!.text.toString()
        val lastName = edit_last_name!!.text.toString()
        val phone = edit_phone!!.text.toString()
        user!!.updateEmail(email)
        val db = FirebaseDatabase.getInstance().getReference()
        db.child("users").child(user.uid).setValue(User(email, firstName, lastName, phone))
        if (photo != null) {
            val storage = FirebaseStorage.getInstance().getReference()
            val baos = ByteArrayOutputStream()
            photo?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            storage.child("avatars/" + user.uid + ".jpg").putBytes(baos.toByteArray())
        }
    }
}