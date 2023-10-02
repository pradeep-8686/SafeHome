package com.example.safehome.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.safehome.R
import com.example.safehome.facilitiesview.ListFragment
import com.example.safehome.model.AllFacilitiesModel
import com.example.safehome.model.FacilitiesGalleyImagesModel
import com.example.safehome.model.FacilitiesList

class FaciListAdapter(
    var context: Context,
    private var fList: ArrayList<AllFacilitiesModel.Data.Facility>
) :
    RecyclerView.Adapter<FaciListAdapter.MyViewHolder>() {
    private lateinit var listFragment: ListFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.faci_list_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val facilitiesList = fList[position]
        holder.book_noew_txt.tag = facilitiesList
        holder.view_details_txt.tag = facilitiesList

        if (facilitiesList.name != null && facilitiesList.name.isNotEmpty()) {
            holder.house_name_tv.text = facilitiesList.name
        }
        if (facilitiesList.facilityImages.isNotEmpty()){

            Glide.with(context)
                .load("https://qa.msafehome.com/${facilitiesList.facilityImages[0].imagePath}")
                .fitCenter()
                .into(holder.facilities_list_image_view)
            holder.facilities_list_image_view.visibility = View.VISIBLE
            holder.view_gallery_title.visibility = View.VISIBLE
        }else{
            holder.facilities_list_image_view.visibility = View.INVISIBLE
            holder.view_gallery_title.visibility = View.INVISIBLE


        }

        holder.facilities_list_image_view.setOnClickListener {
            if (facilitiesList.facilityImages.isNotEmpty() || facilitiesList.facilityImages != null){

                listFragment.showGalleryImages(facilitiesList.facilityImages)
            }
        }
        holder.book_noew_txt.setOnClickListener {
            listFragment.selectedBooknow(it.tag as AllFacilitiesModel.Data.Facility)
        }
        holder.view_details_txt.setOnClickListener {
            listFragment.selectedViewDetails(it.tag as AllFacilitiesModel.Data.Facility)
        }
    }

    override fun getItemCount(): Int {
        if (fList.isNotEmpty()) {
            return fList.size
        }
        return 0
    }

    fun setCallback(listFragment: ListFragment) {
        this.listFragment = listFragment
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val house_name_tv: TextView = itemView.findViewById(R.id.house_name_tv)
        val book_noew_txt: TextView = itemView.findViewById(R.id.book_noew_txt)
        val view_details_txt: TextView = itemView.findViewById(R.id.view_details_txt)
        val view_gallery_title: TextView = itemView.findViewById(R.id.view_gallery_title)
        val facilities_list_image_view: ImageView = itemView.findViewById(R.id.facilities_list_image_view)
    }
}