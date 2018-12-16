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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.navigation.findNavController



class EditUser : Fragment() {
    val PICK_IMAGE = 0
    val TAKE_PHOTO = 1
    var photo: Bitmap? = null

    var EMAIL: String? = ""
    var FIRST_NAME: String? = ""
    var LAST_NAME: String? = ""
    var PHONE: String? = ""
    var AVATAR: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            val user = BaseController.getCurrentUser()
            EMAIL = user?.email
            val dataUser = BaseController.getDataUser()
            FIRST_NAME = dataUser?.firstName
            LAST_NAME = dataUser?.lastName
            PHONE = dataUser?.phone

            view.findViewById<ImageView>(R.id.edit_profile_photo).setImageResource(R.drawable.harley)
            AVATAR = BaseController.imageViewToBitmap(view.findViewById<ImageView>(R.id.edit_profile_photo))

            BaseController.getTaskAvatarOfUser().addOnSuccessListener {
                AVATAR = BaseController.byteArrayToBitmap(it)
                view.findViewById<ImageView>(R.id.edit_profile_photo).setImageBitmap(AVATAR)
            }

            view.findViewById<EditText>(R.id.edit_email).setText(EMAIL)
            view.findViewById<EditText>(R.id.edit_first_name).setText(FIRST_NAME)
            view.findViewById<EditText>(R.id.edit_last_name).setText(LAST_NAME)
            view.findViewById<EditText>(R.id.edit_phone).setText(PHONE)
            view.findViewById<ImageView>(R.id.edit_profile_photo).setImageBitmap(AVATAR)
        }

        view.findViewById<Button>(R.id.edit_gallery).setOnClickListener { takeFromGallery() }
        view.findViewById<Button>(R.id.edit_camera).setOnClickListener { takePhoto() }
        val email = view.findViewById<TextView>(R.id.edit_email)!!.text.toString()
        view.findViewById<Button>(R.id.edit_save).setOnClickListener {
            BaseController.saveToBase(
                    email = email,
                    firstName = view.findViewById<TextView>(R.id.edit_first_name)!!.text.toString(),
                    lastName = view.findViewById<TextView>(R.id.edit_last_name)!!.text.toString(),
                    phone = view.findViewById<TextView>(R.id.edit_phone)!!.text.toString(),
                    photo = AVATAR!!
            )
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("first_name", view?.findViewById<EditText>(R.id.edit_first_name)!!.text.toString())
        outState.putString("last_name", view?.findViewById<EditText>(R.id.edit_last_name)!!.text.toString())
        outState.putString("email", view?.findViewById<EditText>(R.id.edit_email)!!.text.toString())
        outState.putString("phone", view?.findViewById<EditText>(R.id.edit_phone)!!.text.toString())
        outState.putByteArray("avatar", BaseController.imageViewToByteArray(view!!.findViewById<ImageView>(R.id.edit_profile_photo)))
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
               AVATAR = BaseController.byteArrayToBitmap(image)
            }

            view?.findViewById<EditText>(R.id.edit_email)?.setText(EMAIL)
            view?.findViewById<EditText>(R.id.edit_first_name)?.setText(FIRST_NAME)
            view?.findViewById<EditText>(R.id.edit_last_name)?.setText(LAST_NAME)
            view?.findViewById<EditText>(R.id.edit_phone)?.setText(PHONE)
            view?.findViewById<ImageView>(R.id.edit_profile_photo)?.setImageBitmap(AVATAR)
        }
    }
}