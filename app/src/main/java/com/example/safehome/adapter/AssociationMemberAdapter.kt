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
import com.example.safehome.model.AssociationMember

class AssociationMemberAdapter(
    var context: Context,
    private var membersList: ArrayList<AssociationMember>
) :
    RecyclerView.Adapter<AssociationMemberAdapter.MyViewHolder>() {

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
        val associationMember = membersList[position]
        if (associationMember.memberType != null && associationMember.memberType.isNotEmpty()) {
            holder.member_type.text = associationMember.memberType
        }
        if (associationMember.memberName != null && associationMember.memberName.isNotEmpty()) {
            holder.member_name.text = associationMember.memberName
        }
        if (associationMember.memberId != null && associationMember.memberId.isNotEmpty()) {
            holder.member_id.text = associationMember.memberId
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

    }
}