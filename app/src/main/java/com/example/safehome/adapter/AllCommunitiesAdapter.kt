package com.example.safehome.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.activity.PersonalInfoActivity
import com.example.safehome.model.CommunityDetails
import com.example.safehome.residentview.FlatActivity

class AllCommunitiesAdapter(var context: Context, var communitiesList: List<CommunityDetails>) :
    RecyclerView.Adapter<AllCommunitiesAdapter.MyViewHolder>() {

    private lateinit var communityDetails: CommunityDetails
    private lateinit var personalInfoActivity: PersonalInfoActivity
    private lateinit var flatActivity: FlatActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.all_communities_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        communityDetails = communitiesList[position]
        holder.communityNameTv.tag = communityDetails
        if (communityDetails.communityName.isNotEmpty()) {
            holder.communityNameTv.text = communityDetails.communityName
        }

        holder.communityNameTv.setOnClickListener {
            if (context is PersonalInfoActivity) {
                personalInfoActivity.selectCommunity(it.tag as CommunityDetails)
            } else {
                flatActivity.selectCommunity(it.tag as CommunityDetails)
            }
        }
    }

    override fun getItemCount(): Int {
        if (communitiesList.isNotEmpty()) {
            return communitiesList.size
        }
        return 0
    }

    fun setCallback(personalInfoActivity: PersonalInfoActivity) {
        this.personalInfoActivity = personalInfoActivity
    }

    fun setCallbackFlat(flatActivity: FlatActivity) {
        this.flatActivity = flatActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val communityNameTv: TextView = itemView.findViewById(R.id.community_tv)
    }
}