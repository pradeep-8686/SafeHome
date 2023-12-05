package com.example.safehome.enews

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.safehome.R

class ENewsAdapter(
    private var eNewsActivity: ENewsActivity,
    private var eNewsUsersList: ArrayList<UserENewsListModel.Data.ENew>
) : RecyclerView.Adapter<ENewsAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.e_news_single_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (eNewsUsersList.isNotEmpty()) {
            return eNewsUsersList.size
        }
        return 0
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val eNewItem = eNewsUsersList[position]
        if (!eNewItem.topIcName.isNullOrEmpty()) {
            holder.eNewsToicTxt.text = eNewItem.topIcName
        }
        if (!eNewItem.eNews.isNullOrEmpty()) {
            holder.eNewsDescTxt.text = eNewItem.eNews
        }
        if (!eNewItem.createdByRole.isNullOrEmpty()) {
            holder.eNewsPostedByTxt.text = "By: " + eNewItem.createdByRole
        }
        if (eNewItem.eNewsImages.isNotEmpty()) {
            holder.eNewsImage.visibility = View.VISIBLE
            for (i in 0 until eNewItem.eNewsImages.size) {
                Glide.with(eNewsActivity)
                    .load("https://qa.msafehome.com/${eNewItem.eNewsImages[i].attachementPath}")
                    .fitCenter()
                    .into(holder.eNewsImage)
            }
        }else{
            holder.eNewsImage.visibility = View.GONE
        }

        if (!eNewItem.timeStamp.isNullOrEmpty()){
            holder.eNewTimeStampTxt.text = eNewItem.timeStamp
        }

        holder.itemView.setOnClickListener {
          eNewsActivity.itemClick(eNewItem)
        }
    }


    fun setCallback(eNewsActivity: ENewsActivity) {
    this.eNewsActivity = eNewsActivity
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val eNewsImage =  itemView.findViewById<ImageView>(R.id.e_news_item_img)
    val eNewsToicTxt = itemView.findViewById<TextView>(R.id.news_topic_txt)
    val eNewsPostedByTxt = itemView.findViewById<TextView>(R.id.news_by_txt)
    val eNewsDescTxt = itemView.findViewById<TextView>(R.id.news_content_txt)
    val shareImg = itemView.findViewById<ImageView>(R.id.share_icon_img)
    val eNewTimeStampTxt = itemView.findViewById<TextView>(R.id.news_time_stamp)
    val itemView = itemView.findViewById<LinearLayout>(R.id.e_news_item_layout)

    }
}