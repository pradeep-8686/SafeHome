package com.example.safehome.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.safehome.R
import com.example.safehome.residentview.MyFamilyActivity

class ImageAdapter(var context: Context, private var imageList: List<Int>) :
    RecyclerView.Adapter<ImageAdapter.MyViewHolder>() {
    private lateinit var myFamilyActivity: MyFamilyActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.image_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (imageList.isNotEmpty()) {

            Glide.with(context)
                .load(imageList[position])
                .fitCenter()
                .into(holder.complaint_image)

        }


    }

    override fun getItemCount(): Int {
        if (imageList.isNotEmpty()) {
            return imageList.size
        }
        return 0
    }

    fun setCallback(myFamilyActivity: MyFamilyActivity) {
        this.myFamilyActivity = myFamilyActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val complaint_image: ImageView = itemView.findViewById(R.id.complaint_image)
    }

}