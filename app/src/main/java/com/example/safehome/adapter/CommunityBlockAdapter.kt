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
import com.example.safehome.R
import com.example.safehome.communityview.ResidentsActivity
import com.example.safehome.model.CommunityBlock

class CommunityBlockAdapter(var context: Context, private var blocksList: List<CommunityBlock>) :
    RecyclerView.Adapter<CommunityBlockAdapter.MyViewHolder>() {
    private lateinit var residentsActivity: ResidentsActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view =
            layoutInflater.inflate(R.layout.community_block_drop_down_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val communityBlock = blocksList[position]
        holder.ageGroupTv.tag = communityBlock
        if (communityBlock.blockName.isNotEmpty()) {
            holder.ageGroupTv.text = communityBlock.blockName
        }

        if (communityBlock.selected) {
            holder.select_image_view.visibility = View.VISIBLE
        }

        holder.ageGroupTv.setOnClickListener {
            residentsActivity.selectVehicleType(it.tag as CommunityBlock)
        }
    }

    override fun getItemCount(): Int {
        if (blocksList.isNotEmpty()) {
            return blocksList.size
        }
        return 0
    }

    fun setCallback(residentsActivity: ResidentsActivity) {
        this.residentsActivity = residentsActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
        val select_image_view: ImageView = itemView.findViewById(R.id.select_image_view)
    }

}