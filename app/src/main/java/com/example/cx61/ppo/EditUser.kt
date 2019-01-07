package com.example.cx61.ppo

import android.app.Activity.RESULT_OK
import android.Manifest
import android.app.ProgressDialog
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class EditUser : Fragment() {
    val PICK_IMAGE = 0
    val TAKE_PHOTO = 1
    var callbacksNeeded = 3
    var positiveCallbacks = 0
    var negativeCallbacks = 0
    var progressDialog: ProgressDialog? = null

    companion object {
        var AVATAR: Bitmap? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            progressDialog = ProgressDialog(context)
            progressDialog?.setMessage("Loading...")
            progressDialog?.show()

            AVATAR = BaseController.imageViewToBitmap(activity!!.findViewById<ImageView>(R.id.header_image))
            val user = BaseController.getCurrentUser()
            view.findViewById<EditText>(R.id.edit_email).setText(user?.email)

            var userData: User? = null

            BaseController.getTaskDataUser().addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    userData = dataSnapshot.getValue(User::class.java)
                    view.findViewById<EditText>(R.id.edit_first_name).setText(userData?.firstName)
                    view.findViewById<EditText>(R.id.edit_last_name).setText(userData?.lastName)
                    view.findViewById<EditText>(R.id.edit_phone).setText(userData?.phone)
                    view.findViewById<EditText>(R.id.edit_rss).setText(userData?.rssUrl)
                    progressDialog?.dismiss()
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
            view.findViewById<ImageView>(R.id.edit_profile_photo).setImageBitmap(AVATAR)
        }
        view.findViewById<Button>(R.id.edit_gallery).setOnClickListener { takeFromGallery() }
        view.findViewById<Button>(R.id.edit_camera).setOnClickListener { takePhoto() }
        view.findViewById<Button>(R.id.edit_save).setOnClickListener {
            val email = view.findViewById<EditText>(R.id.edit_email).text.toString()
            saveToBase(lastName = view.findViewById<EditText>(R.id.edit_last_name).text.toString(),
                    firstName = view.findViewById<EditText>(R.id.edit_first_name).text.toString(),
                    photo = AVATAR, email = email,
                    phone = view.findViewById<EditText>(R.id.edit_phone).text.toString(),
                    rss = view.findViewById<EditText>(R.id.edit_rss).text.toString())

            activity!!.findViewById<ImageView>(R.id.header_image).setImageBitmap(AVATAR)
            activity!!.findViewById<TextView>(R.id.header_email).text = email
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

    override fun onDestroyView() {
        progressDialog = null
        super.onDestroyView()
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
        if (view != null){
            outState.putString("first_name", view?.findViewById<EditText>(R.id.edit_first_name)?.text.toString())
            outState.putString("last_name", view?.findViewById<EditText>(R.id.edit_last_name)?.text.toString())
            outState.putString("email", view?.findViewById<EditText>(R.id.edit_email)?.text.toString())
            outState.putString("phone", view?.findViewById<EditText>(R.id.edit_phone)?.text.toString())
            outState.putString("rss", view?.findViewById<EditText>(R.id.edit_rss)?.text.toString())
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null){
            view?.findViewById<EditText>(R.id.edit_email)?.setText(savedInstanceState.getString("email"))
            view?.findViewById<EditText>(R.id.edit_first_name)?.setText(savedInstanceState.getString("first_name"))
            view?.findViewById<EditText>(R.id.edit_last_name)?.setText(savedInstanceState.getString("last_name"))
            view?.findViewById<EditText>(R.id.edit_phone)?.setText(savedInstanceState.getString("phone"))
            view?.findViewById<ImageView>(R.id.edit_profile_photo)?.setImageBitmap(AVATAR)
            view?.findViewById<EditText>(R.id.edit_rss)?.setText(savedInstanceState.getString("rss"))
        }
    }

    fun savingCallback(result: Boolean){
        if (result)
            positiveCallbacks++
        else
            negativeCallbacks++
        if (positiveCallbacks + negativeCallbacks == callbacksNeeded) {
            progressDialog?.dismiss()
            if (view != null){
                if (negativeCallbacks == 0)
                    Snackbar.make(view!!, "Saved.", Snackbar.LENGTH_LONG)
                            .show()
                else
                    Snackbar.make(view!!, "Task not complited.", Snackbar.LENGTH_LONG)
                            .show()
            }
        }
    }

    fun saveToBase(email: String, firstName: String, lastName: String, phone: String, photo: Bitmap?, rss: String){
        val user = BaseController.getCurrentUser()

        if (user != null) {
            progressDialog = ProgressDialog(context)
            progressDialog?.setMessage("Saving...")
            progressDialog?.show()

            user.updateEmail(email).addOnCompleteListener {
                savingCallback(it.isSuccessful)
            }.addOnCanceledListener { savingCallback(true) }

            BaseController.saveUser(User(email, firstName, lastName, phone, rss))?.addOnCompleteListener {
                savingCallback(it.isSuccessful) }?.addOnCanceledListener { savingCallback(true) }

            if (photo != null) {
                BaseController.saveAvatar(photo)?.addOnCompleteListener {
                    savingCallback(it.isSuccessful) }?.addOnCanceledListener { savingCallback(true) }
            }
        }
    }
}