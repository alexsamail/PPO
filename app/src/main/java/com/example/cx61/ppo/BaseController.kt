package com.example.cx61.ppo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.widget.ImageView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.lang.Exception

object BaseController {

    fun getCurrentUser() :FirebaseUser? {
        //user.email user.uid
        return FirebaseAuth.getInstance().currentUser
    }

    fun getDataUser(): User? {
        //user.last_name user.first_name user.phone
        var user: User? = null
        FirebaseDatabase.getInstance().getReference().child("users").child(getCurrentUser()!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(User::class.java)
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        return user
    }

    fun getTaskAvatarOfUser(): Task<ByteArray> {
        return FirebaseStorage.getInstance().getReference().child(
                "avatars/" + getCurrentUser()!!.uid + ".jpg").getBytes(1024*1024*1024)
    }

    fun bitmapToByteArray(photo: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    fun byteArrayToBitmap(array: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(array, 0, array.size)
    }

    fun imageViewToByteArray(image: ImageView): ByteArray {
        val bitmap = (image.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        return stream.toByteArray()
    }

    fun imageViewToBitmap(image: ImageView): Bitmap {
        return byteArrayToBitmap(imageViewToByteArray(image))
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    fun saveToBase(email: String, firstName: String,
                   lastName: String, phone: String, photo: Bitmap){

        val user = getCurrentUser()
        user!!.updateEmail(email)

        val db = FirebaseDatabase.getInstance().getReference()
        db.child("users").child(user.uid).setValue(User(email, firstName, lastName, phone))

        val storage = FirebaseStorage.getInstance().getReference()
        storage.child("avatars/" + user.uid + ".jpg").putBytes(bitmapToByteArray(photo))
    }
}