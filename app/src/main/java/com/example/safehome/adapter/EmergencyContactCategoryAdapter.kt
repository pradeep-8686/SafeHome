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
import com.example.safehome.communityview.EmergencyContactActivity
import com.example.safehome.communityview.EmergencyContactCategoryActivity
import com.example.safehome.model.EmergencyContact
import com.example.safehome.model.EmergencyContactCategory

class EmergencyContactCategoryAdapter (var context: Context,
                                       private var membersList: ArrayList<EmergencyContactCategory.Data>
) :
RecyclerView.Adapter<EmergencyContactCategoryAdapter.MyViewHolder>() {

    private lateinit var emergencyContactActivity: EmergencyContactCategoryActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.emergency_contact_category_item, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = membersList[position]
        if (contact.contactTypeName  != null && contact.contactTypeName .isNotEmpty()) {
            holder.member_name.text = contact.contactTypeName
        }

        holder.itemView.setOnClickListener{
            emergencyContactActivity.clickAction(contact)
        }
        if (contact.contactTypeName == "Doctor"){

            Glide.with(context)
                .load(R.drawable.ic_doctor)
                .fitCenter()
                .into(holder.contact_icon)
        }else if (contact.contactTypeName == "Fire Officer"){
            Glide.with(context)
                .load(R.drawable.ic_fire)
                .fitCenter()
                .into(holder.contact_icon)
        }else{
           /* Glide.with(context)
                .load(R.drawable.guest_person)
                .fitCenter()
                .into(holder.contact_icon)*/
        }


    }

    override fun getItemCount(): Int {
        if (membersList.isNotEmpty()) {
            return membersList.size
        }
        return 0
    }

    fun setCallback(emergencyContactActivity: EmergencyContactCategoryActivity) {
        this.emergencyContactActivity = emergencyContactActivity
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val member_name: TextView = itemView.findViewById(R.id.member_name)
        val contact_icon: ImageView = itemView.findViewById(R.id.contact_icon)

    }
    fun filterList( emergencyContact : ArrayList<EmergencyContactCategory.Data>) {
        this.membersList = emergencyContact ;
        notifyDataSetChanged();
    }




}