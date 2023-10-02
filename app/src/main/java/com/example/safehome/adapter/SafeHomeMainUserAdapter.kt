package com.example.safehome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.model.FaciBookings
import com.example.safehome.model.SafeHomeUserModelList

class SafeHomeMainUserAdapter(var context: Context,
                              private var safeHomeUserModelList: ArrayList<SafeHomeUserModelList>) :
    RecyclerView.Adapter<SafeHomeMainUserAdapter.MyViewHolder>() {

    private var lastSelectedPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.safe_home_user_single_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem  = safeHomeUserModelList[position]
        if (currentItem .flatLocation!= null && currentItem .flatLocation.isNotEmpty()){
            holder.flatLocationTv.text = currentItem .flatLocation
        }
        if (currentItem .flatName!= null && currentItem .flatName.isNotEmpty()){
            holder.flatNameTv.text = currentItem .flatName
        }
        if (currentItem .flatNo!= null && currentItem .flatNo.isNotEmpty()){
            holder.flatNoTv.text = currentItem .flatNo
        }
        if (currentItem .flatImg!= null){
            holder.flatImage.setImageResource(currentItem .flatImg)
        }

        holder.defaultSwitchImg.isChecked = currentItem.isDefaultText

//        if (safeHomeUserModelList.isDefaultText){
//            if (safeHomeUserModelList.isDefaultText == "true"){
//                holder.defaultSwitchImg.isChecked = true
//                holder.default_tv.visibility = View.VISIBLE
//            }else{
//                holder.defaultSwitchImg.isChecked = false
//                holder.default_tv.visibility = View.GONE
//            }
//        }


        holder.defaultSwitchImg.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                // Turn off the previously selected switch
                if (lastSelectedPosition != -1 && lastSelectedPosition != position) {
                    safeHomeUserModelList[lastSelectedPosition].isDefaultText = false
                    notifyItemChanged(lastSelectedPosition)
                }
                lastSelectedPosition = position
                currentItem.isDefaultText = true

            }else{
                currentItem.isDefaultText = false

            }


//            val message = if (isChecked) "true" else "false"
//            if (message == "true"){
//                holder.defaultSwitchImg.isChecked = true
//                holder.default_tv.visibility = View.VISIBLE
//                safeHomeUserModelList.isDefaultText = true
//            }else{
//                holder.defaultSwitchImg.isChecked = false
//                holder.default_tv.visibility = View.GONE
//                safeHomeUserModelList.isDefaultText = false
//            }

        }
    }

    override fun getItemCount(): Int {
        if (safeHomeUserModelList.isNotEmpty()) {
            return safeHomeUserModelList.size
        }
        return 0
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
      val flatImage = itemView.findViewById<ImageView>(R.id.main_user_flatImg)
      val flatLocationTv = itemView.findViewById<TextView>(R.id.user_flat_location_txt)
      val flatNameTv = itemView.findViewById<TextView>(R.id.user_flat_name_txt)
      val flatNoTv = itemView.findViewById<TextView>(R.id.user_flat_no_txt)
      val defaultSwitchImg = itemView.findViewById<Switch>(R.id.default_user_switch)
      val default_tv = itemView.findViewById<TextView>(R.id.default_show_txt)
    }

}