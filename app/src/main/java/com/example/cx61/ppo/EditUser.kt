package com.example.cx61.ppo

import android.app.Activity.RESULT_OK
import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.content.pm.PackageManager
import android.media.Image
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.navigation.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class EditUser : Fragment() {
    val PICK_IMAGE = 0
    val TAKE_PHOTO = 1
    var AVATAR: Bitmap? = null
    var EMAIL: String = ""
    var LAST_NAME: String = ""
    var FIRST_NAME: String = ""
    var PHONE: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            AVATAR = BaseController.imageViewToBitmap(activity!!.findViewById<ImageView>(R.id.header_image))
        }
        catch (e: Exception){
            view.findViewById<ImageView>(R.id.edit_profile_photo).setImageResource(R.drawable.harley)
            AVATAR = BaseController.imageViewToBitmap(view.findViewById<ImageView>(R.id.edit_profile_photo))
        }
        if (savedInstanceState == null) {
            val user = BaseController.getCurrentUser()
            view.findViewById<EditText>(R.id.edit_email).setText(user?.email)

            var userData: User? = null
            BaseController.getTaskDataUser().addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    userData = dataSnapshot.getValue(User::class.java)
                    view.findViewById<EditText>(R.id.edit_first_name).setText(userData?.firstName)
                    view.findViewById<EditText>(R.id.edit_last_name).setText(userData?.lastName)
                    view.findViewById<EditText>(R.id.edit_phone).setText(userData?.phone)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })

            view.findViewById<ImageView>(R.id.edit_profile_photo).setImageBitmap(AVATAR)
        }

        view.findViewById<Button>(R.id.edit_gallery).setOnClickListener { takeFromGallery() }
        view.findViewById<Button>(R.id.edit_camera).setOnClickListener { takePhoto() }
        val email = view.findViewById<EditText>(R.id.edit_email).text.toString()
        view.findViewById<Button>(R.id.edit_save).setOnClickListener {
            BaseController.saveToBase(
                    email = email,
                    firstName = view.findViewById<EditText>(R.id.edit_first_name).text.toString(),
                    lastName = view.findViewById<EditText>(R.id.edit_last_name).text.toString(),
                    phone = view.findViewById<EditText>(R.id.edit_phone).text.toString(),
                    photo = AVATAR!!)
            activity!!.findViewById<ImageView>(R.id.header_image).setImageBitmap(AVATAR)
            activity!!.findViewById<TextView>(R.id.header_email).text = email
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
        var photo: Bitmap? = null
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE -> {
                    photo = BitmapFactory.decodeStream(activity!!.contentResolver.openInputStream(data?.data))
                    AVATAR = photo
                    view?.findViewById<ImageView>(R.id.edit_profile_photo)?.setImageBitmap(photo)
                }
                TAKE_PHOTO -> {
                    photo = data?.extras?.get("data") as Bitmap
                    AVATAR = photo
                    view?.findViewById<ImageView>(R.id.edit_profile_photo)?.setImageBitmap(photo)
                }
            }
        }
    }

    override fun onPause() {
        EMAIL = view?.findViewById<EditText>(R.id.edit_email)?.text.toString()
        FIRST_NAME = view?.findViewById<EditText>(R.id.edit_first_name)?.text.toString()
        LAST_NAME = view?.findViewById<EditText>(R.id.edit_last_name)?.text.toString()
        PHONE = view?.findViewById<EditText>(R.id.edit_phone)?.text.toString()
        super.onPause()
    }

    override fun onDestroy() {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage("Save changes?")
                .setCancelable(false)
                .setNegativeButton("Discard") { dialog, _ ->
                    run {
                        dialog.cancel()
                    }
                }
                .setPositiveButton("Save") { dialog, _ ->
                    run {
                        BaseController.saveToBase(
                                email = EMAIL,
                                firstName = FIRST_NAME,
                                lastName = LAST_NAME,
                                phone = PHONE,
                                photo = AVATAR!!)
                        dialog.cancel()
                    }
                }
        val alert = builder.create()
        alert.show()
        super.onDestroy()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("first_name", view?.findViewById<EditText>(R.id.edit_first_name)?.text.toString())
        outState.putString("last_name", view?.findViewById<EditText>(R.id.edit_last_name)?.text.toString())
        outState.putString("email", view?.findViewById<EditText>(R.id.edit_email)?.text.toString())
        outState.putString("phone", view?.findViewById<EditText>(R.id.edit_phone)?.text.toString())
        outState.putByteArray("avatar", BaseController.bitmapToByteArray(AVATAR))
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null){
            val image = savedInstanceState.getByteArray("avatar")
            if (image != null){
               AVATAR = BaseController.byteArrayToBitmap(image)
            }

            view?.findViewById<EditText>(R.id.edit_email)?.setText(savedInstanceState.getString("email"))
            view?.findViewById<EditText>(R.id.edit_first_name)?.setText(savedInstanceState.getString("first_name"))
            view?.findViewById<EditText>(R.id.edit_last_name)?.setText(savedInstanceState.getString("last_name"))
            view?.findViewById<EditText>(R.id.edit_phone)?.setText(savedInstanceState.getString("phone"))
            view?.findViewById<ImageView>(R.id.edit_profile_photo)?.setImageBitmap(AVATAR)
        }
    }
}