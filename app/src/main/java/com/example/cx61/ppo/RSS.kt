package com.example.cx61.ppo

import android.os.AsyncTask
import org.w3c.dom.Document
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class RSS(var address: String, var data: Document? = null) : AsyncTask<Void, Void, Void>() {
    lateinit var feedItems: ArrayList<NewsItem>
    lateinit var url: URL

    override fun doInBackground(vararg params: Void): Void? {
        if (data == null)
            data = Getdata()
        ProcessXml(data)
        return null
    }

    private fun ProcessXml(data: Document?) {
        feedItems = ArrayList()
        if (data != null) {
            val root = data.getDocumentElement()
            val channel = root.getChildNodes().item(1)
            val items = channel.getChildNodes()
            for (i in 0 until items.getLength()) {
                val cureentchild = items.item(i)
                if (cureentchild.getNodeName().equals("item")) {
                    val item = NewsItem()
                    val itemchilds = cureentchild.getChildNodes()
                    for (j in 0 until itemchilds.getLength()) {
                        val cureent = itemchilds.item(j)
                        if (cureent.getNodeName().equals("title")) {
                            item.title = cureent.getTextContent()
                        } else if (cureent.getNodeName().equals("description")) {
                            item.description = cureent.getTextContent()
                        } else if (cureent.getNodeName().equals("pubDate")) {
                            item.pubDate = cureent.getTextContent()
                        } else if (cureent.getNodeName().equals("link")) {
                            item.link = cureent.getTextContent()
                        } else if (cureent.getNodeName().equals("enclosure")) {
                            val url = cureent.getAttributes().item(0).getTextContent()
                            item.thumbnailUrl = url
                        } else if (cureent.getNodeName().equals("media:thumbnail")) {
                            val url = cureent.getAttributes().item(0).getTextContent()
                            item.thumbnailUrl = url
                        } else if (cureent.getNodeName().equals("image")) {
                            val url = cureent.getAttributes().item(1).getTextContent()
                            item.thumbnailUrl = url
                        }

                    }
                    feedItems.add(item)


                }
            }
        }
    }

    fun Getdata(): Document? {
        try {
            url = URL(address)
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestMethod("GET")
            val inputStream = connection.getInputStream()
            val builderFactory = DocumentBuilderFactory.newInstance()
            val builder = builderFactory.newDocumentBuilder()
            return builder.parse(inputStream)
        } catch (e: Exception) {
            return null
        }
    }
}