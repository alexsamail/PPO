package com.example.cx61.ppo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import RSSActivity
import android.content.Context
import android.net.ConnectivityManager
import org.w3c.dom.Document

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
                    if ((RSSUrl == null) or (RSSUrl == "")) {
                        val edit = EditText(context!!)
                        val builder = AlertDialog.Builder(context!!).setCancelable(false)
                                .setTitle("Enter RSS-URL:")
                                .setView(edit)
                                .setPositiveButton("Set") { dialog, _ ->
                                    run {
                                        RSSUrl = edit.text.toString()
                                        BaseController.saveUrl(RSSUrl!!)
                                        dialog.cancel()
                                    }
                                }
                        builder.show()
                    }
                    recyclerView = view!!.findViewById(R.id.recyclerview) as RecyclerView
                    val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    var data: Document? = null
                    if (cm.activeNetworkInfo == null || !cm.activeNetworkInfo.isConnected) {
                        val builder = android.app.AlertDialog.Builder(context!!)
                        builder.setMessage("Internet connection lost." +
                                " Data will be loaded from cache." +
                                "You can not use the built-in browser.")
                                .setCancelable(false)
                                .setPositiveButton("Ok") { dialog, _ ->
                                    run {

                                        dialog.cancel()
                                    }
                                }
                        val alert = builder.create()
                        alert.show()
                        val dataStr: String? = dataSnapshot.child("rssCache").value.toString()
                        if (dataStr != null)
                            data = BaseController.stringToXML(dataStr)
                    }
                    if (RSSUrl != null) {
                        val readRss = RSSActivity(context!!, recyclerView!!, RSSUrl!!, activity!!.resources.configuration.orientation, data)
                        readRss.execute()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
    }
}