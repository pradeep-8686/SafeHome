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
import com.example.safehome.communityview.EmergencyContactActivity
import com.example.safehome.model.EmergencyContact

class EmergencyContactAdapter (var context: Context,
                               private var membersList: ArrayList<EmergencyContact.Data.EmergencyContact>
) :
RecyclerView.Adapter<EmergencyContactAdapter.MyViewHolder>() {

    private lateinit var emergencyContactActivity: EmergencyContactActivity

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
        val contact = membersList[position]
        if (contact.contactTypeName  != null && contact.contactTypeName .isNotEmpty()) {
            holder.member_type.text = contact.contactTypeName
        }
        if (contact.contactPerson  != null && contact.contactPerson.isNotEmpty()) {
            holder.member_name.text = contact.contactPerson
        }
      /*  if (residents.Jobtype != null && residents.Jobtype.isNotEmpty()) {
            holder.member_id.text = residents.Jobtype
        }*/
        holder.call_icon.setOnClickListener{
            emergencyContactActivity.callAction(contact)
        }
        holder.message_icon.setOnClickListener{
            emergencyContactActivity.messageAction(contact)
        }

    }

    override fun getItemCount(): Int {
        if (membersList.isNotEmpty()) {
            return membersList.size
        }
        return 0
    }

    fun setCallback(emergencyContactActivity: EmergencyContactActivity) {
        this.emergencyContactActivity = emergencyContactActivity
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val member_type: TextView = itemView.findViewById(R.id.member_type)
        val member_name: TextView = itemView.findViewById(R.id.member_name)
        val member_id: TextView = itemView.findViewById(R.id.member_id)
        val call_icon: ImageView = itemView.findViewById(R.id.call_icon)
        val message_icon: ImageView = itemView.findViewById(R.id.message_icon)

    }
    fun filterList( emergencyContact : ArrayList<EmergencyContact.Data.EmergencyContact>) {
        this.membersList = emergencyContact ;
        notifyDataSetChanged();
    }




}