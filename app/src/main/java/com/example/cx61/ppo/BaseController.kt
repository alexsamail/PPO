package com.example.cx61.ppo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

object BaseController {

    fun getCurrentUser() :FirebaseUser? {
        //user.email user.uid
        return FirebaseAuth.getInstance().currentUser
    }

    fun getOffline() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    fun xmlToString(doc: Document): String{
        val tf = TransformerFactory.newInstance()
        val transformer = tf.newTransformer()
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        val writer = StringWriter()
        transformer.transform(DOMSource(doc), StreamResult(writer))
        val output = writer.getBuffer().toString().replace("\n|\r", "")
        return output
    }

    fun stringToXML(str: String): Document?{
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val d1 = builder.parse(InputSource(StringReader(str)))
            return d1
        }
        catch (ex: Exception){
            return null
        }
    }

    fun saveCache(doc: Document){
        val user = getCurrentUser()
        if (user != null)
            FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).child("rssCache").setValue(xmlToString(doc))
    }
    fun saveUrl(str: String){
        val user = getCurrentUser()
        if (user != null)
            FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).child("rssUrl").setValue(str)
    }

    fun getTaskDataUser(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference().child("users").child(getCurrentUser()!!.uid)
    }

    fun getTaskAvatarOfUser(): Task<ByteArray> {
        return FirebaseStorage.getInstance().getReference().child(
                "avatars/" + getCurrentUser()!!.uid + ".jpg").getBytes(1024*1024*1024)
    }

    fun bitmapToByteArray(photo: Bitmap?): ByteArray {
        val baos = ByteArrayOutputStream()
        photo?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    fun byteArrayToBitmap(array: ByteArray?): Bitmap {
        return BitmapFactory.decodeByteArray(array, 0, array!!.size)
    }

    fun imageViewToByteArray(image: ImageView?): ByteArray {
        val bitmap = (image?.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        return stream.toByteArray()
    }

    fun imageViewToBitmap(image: ImageView?): Bitmap {
        return byteArrayToBitmap(imageViewToByteArray(image))
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    fun saveToBase(email: String, firstName: String,
                   lastName: String, phone: String,
                   photo: Bitmap, rss: String){

        val user = getCurrentUser()
        user?.updateEmail(email)

        val db = FirebaseDatabase.getInstance().getReference()
        db.child("users").child(user!!.uid).setValue(User(email, firstName, lastName, phone, rss))

        val storage = FirebaseStorage.getInstance().getReference()
        storage.child("avatars/" + user.uid + ".jpg").putBytes(bitmapToByteArray(photo))
    }

    fun saveUser(userData: User): Task<Void>? {
        val user = getCurrentUser()
        if (user != null)
            return FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).setValue(userData)
        return null
    }

    fun saveAvatar(bitmap: Bitmap?): UploadTask?{
        val user = getCurrentUser()
        if (user != null) {
            val storage = FirebaseStorage.getInstance().getReference()
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            return storage.child("avatars/" + user.uid + ".jpg").putBytes(baos.toByteArray())
        }
        return null
    }
}