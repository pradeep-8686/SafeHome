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
import com.example.safehome.model.Residents

class ResidentsAdapter(
    var context: Context,
    private var membersList: ArrayList<Residents>
) :
    RecyclerView.Adapter<ResidentsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.association_member_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val residents = membersList[position]
        if (residents.memberType != null && residents.memberType.isNotEmpty()) {
            holder.member_type.text = residents.memberType
        }
        if (residents.memberName != null && residents.memberName.isNotEmpty()) {
            holder.member_name.text = residents.memberName
        }
        if (residents.memberId != null && residents.memberId.isNotEmpty()) {
            holder.member_id.text = residents.memberId
        }

        if(context is ResidentsActivity){
            holder.call_icon.visibility = View.GONE
        }else{
            holder.call_icon.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        if (membersList.isNotEmpty()) {
            return membersList.size
        }
        return 0
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val member_type: TextView = itemView.findViewById(R.id.member_type)
        val member_name: TextView = itemView.findViewById(R.id.member_name)
        val member_id: TextView = itemView.findViewById(R.id.member_id)
        val call_icon: ImageView = itemView.findViewById(R.id.call_icon)
    }
}