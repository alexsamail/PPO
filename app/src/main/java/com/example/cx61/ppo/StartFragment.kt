package com.example.cx61.ppo

import android.app.ProgressDialog
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
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import org.w3c.dom.Document

class StartFragment : Fragment() {

    lateinit var RSSUrl: String
    lateinit var recyclerView: RecyclerView
    lateinit var readRss: RSS
    var progressDialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        return view
    }

    override fun onDestroyView() {
        progressDialog = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        val user = BaseController.getCurrentUser()
        if (view != null){
            if (user != null) {
                progressDialog = ProgressDialog(context)
                progressDialog?.setMessage("Loading...")
                progressDialog?.show()
                view?.findViewById<TextView>(R.id.textHello)?.visibility = View.GONE
                FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val userData: User? = dataSnapshot.getValue(User::class.java)
                            RSSUrl = userData!!.rssUrl
                        if (RSSUrl == "") {
                            val edit = EditText(context!!)
                            val builder = AlertDialog.Builder(context!!).setCancelable(false)
                                    .setTitle("Enter RSS-URL:")
                                    .setView(edit)
                                    .setPositiveButton("Set") { dialog, _ ->
                                        run {
                                            RSSUrl = edit.text.toString()
                                            BaseController.saveUrl(RSSUrl)
                                            dialog.cancel()
                                            val dataStr: String? = dataSnapshot.child("rssCache").value.toString()
                                            var data: Document? = null
                                            if (dataStr != null)
                                                data = BaseController.stringToXML(dataStr)
                                            if (RSSUrl != ""){
                                                readRss = RSS(RSSUrl, data)
                                                readRss.execute()
                                                readRss.get()

                                                val adapter = NewsAdapter(context!!, readRss.feedItems)
                                                if (activity!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                                                    recyclerView.layoutManager = LinearLayoutManager(context)
                                                else
                                                    recyclerView.layoutManager = GridLayoutManager(context, 2)
                                                recyclerView.adapter = adapter
                                                val user = BaseController.getCurrentUser()
                                                if ((user != null) and (readRss.data != null))
                                                    BaseController.saveCache(readRss.data!!)
                                            }
                                            else {
                                                view?.findViewById<TextView>(R.id.textHello)?.visibility = View.VISIBLE
                                                view?.findViewById<TextView>(R.id.textHello)?.text = "Welcome!"

                                            }
                                        }
                                    }
                            builder.show()
                        }
                        recyclerView = view!!.findViewById(R.id.recyclerview) as RecyclerView
                        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        var data: Document? = null
                        if (cm.activeNetworkInfo == null || !cm.activeNetworkInfo.isConnected) {
                            val dataStr: String? = dataSnapshot.child("rssCache").value.toString()
                            if (dataStr != null)
                                data = BaseController.stringToXML(dataStr)
                        }
                        if (RSSUrl != "") {
                            readRss = RSS(RSSUrl, data)
                            readRss.execute()
                            readRss.get()

                            val adapter = NewsAdapter(context!!, readRss.feedItems)
                            if (activity!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                                recyclerView.layoutManager = LinearLayoutManager(context)
                            else
                                recyclerView.layoutManager = GridLayoutManager(context, 2)
                            recyclerView.adapter = adapter
                            val user = BaseController.getCurrentUser()
                            if ((user != null) and (readRss.data != null))
                                BaseController.saveCache(readRss.data!!)
                        }
                        progressDialog?.dismiss()
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
            }
            else{
                view?.findViewById<TextView>(R.id.textHello)?.visibility = View.VISIBLE
                view?.findViewById<TextView>(R.id.textHello)?.text = "Welcome!"
            }

        }


    }
}