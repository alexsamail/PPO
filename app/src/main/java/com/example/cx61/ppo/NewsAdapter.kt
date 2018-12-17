package com.example.cx61.ppo

import android.content.Context
import androidx.cardview.widget.CardView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent

class NewsAdapter(internal var context: Context, internal var feedItems: ArrayList<FeedItem>) : RecyclerView.Adapter<NewsAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false)
        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        YoYo.with(Techniques.FadeIn).playOn(holder.cardView)
        val current = feedItems[position]
        holder.Title.text = current.title
        holder.Description.text = current.description
        holder.Date.text = current.pubDate
        Picasso.with(context).load(current.thumbnailUrl).into(holder.Thumbnail)
        holder.cardView.setOnClickListener {
            val intent = Intent(context, BrowserActivity::class.java)
            intent.putExtra("url", current.link)
            startActivity(context, intent, null)
        }
    }
    override fun getItemCount(): Int {
        return feedItems.size
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var Title: TextView
        internal var Description: TextView
        internal var Date: TextView
        internal var Thumbnail: ImageView
        internal var cardView: CardView
        init {
            Title = itemView.findViewById(R.id.title_text)
            Description = itemView.findViewById(R.id.description_text)
            Date = itemView.findViewById(R.id.date_text)
            Thumbnail = itemView.findViewById(R.id.thumb_img) as ImageView
            cardView = itemView.findViewById(R.id.cardview) as CardView
        }
    }
}