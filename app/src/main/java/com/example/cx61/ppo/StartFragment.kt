package com.example.cx61.ppo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ReadRss

class StartFragment : Fragment() {

    var RSSUrl: String? = null
    var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        val user = BaseController.getCurrentUser()
        if (user != null) {
            FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userData: User? = dataSnapshot.getValue(User::class.java)
                    RSSUrl = userData?.rssUrl
                    if (RSSUrl == null) {
                        val edit = EditText(context!!)
                        val builder = AlertDialog.Builder(context!!).setCancelable(false)
                                .setTitle("Enter RSS feed URL:")
                                .setView(edit)
                                .setPositiveButton("Set") { dialog, _ ->
                                    run {
                                        RSSUrl = edit.text.toString()
                                        FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).child("rssUrl").setValue(RSSUrl)
                                        dialog.cancel()
                                    }
                                }
                        builder.show()
                    }
                    recyclerView = view!!.findViewById<RecyclerView>(R.id.recyclerview)
                    val readRss = ReadRss(context!!, recyclerView!!, RSSUrl!!, activity!!.resources.configuration.orientation)
                    readRss.execute()
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
    }
}