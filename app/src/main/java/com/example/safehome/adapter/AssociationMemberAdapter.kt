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
import com.example.safehome.communityview.AssociationMembersActivity
import com.example.safehome.model.GetAllAssociationMembersModel

class AssociationMemberAdapter(
    var context: Context,
    private var membersList: ArrayList<GetAllAssociationMembersModel.Data.AssociationMember>
) :
    RecyclerView.Adapter<AssociationMemberAdapter.MyViewHolder>() {

    private lateinit var associationMembersActivity: AssociationMembersActivity
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
        if (associationMember.roleName != null && associationMember.roleName.isNotEmpty()) {
            holder.member_type.text = associationMember.roleName
        }
        if (associationMember.firstName != null && associationMember.firstName.isNotEmpty()) {
            if (associationMember.lastName != null && associationMember.lastName.isNotEmpty()) {
                holder.member_name.text = associationMember.firstName + " "+ associationMember.lastName
            }else{
                holder.member_name.text = associationMember.firstName
            }
        }
        if (!associationMember.block.isNullOrEmpty() && !associationMember.flatNo.isNullOrEmpty()) {
            holder.member_id.text = associationMember.block +"-"+associationMember.flatNo
        }else{
            holder.member_id.text = associationMember.block +"-"+associationMember.flatNo
        }

        holder.call_img.setOnClickListener {
            associationMembersActivity.makeCall(associationMember)
        }

        holder.msg_img.setOnClickListener {
            associationMembersActivity.sendMsg(associationMember)
        }

    }

    override fun getItemCount(): Int {
        if (membersList.isNotEmpty()) {
            return membersList.size
        }
        return 0
    }

    fun setCallback(associationMembersActivity: AssociationMembersActivity) {
     this.associationMembersActivity = associationMembersActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val member_type: TextView = itemView.findViewById(R.id.member_type)
        val member_name: TextView = itemView.findViewById(R.id.member_name)
        val member_id: TextView = itemView.findViewById(R.id.member_id)
        val call_img = itemView.findViewById<ImageView>(R.id.call_icon)
        val msg_img = itemView.findViewById<ImageView>(R.id.message_icon)
    }
}