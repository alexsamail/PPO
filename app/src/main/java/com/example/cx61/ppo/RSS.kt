import android.app.ProgressDialog
import android.content.Context
import android.content.res.Configuration
import androidx.recyclerview.widget.RecyclerView
import android.os.AsyncTask
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cx61.ppo.NewsAdapter
import com.example.cx61.ppo.NewsItem
import com.example.cx61.ppo.NewsMargin
import org.w3c.dom.Document
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class ReadRss(internal var context: Context, internal var recyclerView: RecyclerView,
              var address: String, var orientation: Int) : AsyncTask<Void, Void, Void>() {
    internal var progressDialog: ProgressDialog
    internal lateinit var feedItems: ArrayList<NewsItem>
    internal lateinit var url: URL
    init {
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
    }

    override fun onPreExecute() {
        progressDialog.show()
        super.onPreExecute()
    }
    //This method will execute in background so in this method download rss feeds
    override fun doInBackground(vararg params: Void): Void? {
        //call process xml method to process document we downloaded from getData() method
        ProcessXml(Getdata())
        return null
    }
    override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        progressDialog.dismiss()
        val adapter = NewsAdapter(context, feedItems)
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            recyclerView.layoutManager = GridLayoutManager(context, 1)
        else
            recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.addItemDecoration(NewsMargin(20))
        recyclerView.adapter = adapter
    }
    private fun ProcessXml(data: Document?) {
        if (data != null) {
            feedItems = ArrayList()
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
                        } else if (cureent.getNodeName().equals("media:thumbnail")) {
                            //this will return us thumbnail url
                            val url = cureent.getAttributes().item(0).getTextContent()
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
            e.printStackTrace()
            return null
        }
    }
} 