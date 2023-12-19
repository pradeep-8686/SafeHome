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
import com.example.safehome.communityview.VendorsActivity
import com.example.safehome.model.EmergencyContact
import com.example.safehome.model.VendorTypeModel

class VendorAdapter (var context: Context,
                     private var membersList: ArrayList<VendorTypeModel.Data>
) :
RecyclerView.Adapter<VendorAdapter.MyViewHolder>() {

    private lateinit var vendorsActivity: VendorsActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.vendor_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = membersList[position]
        if (contact.name  != null && contact.name .isNotEmpty()) {
            holder.vendor_name.text = contact.name
        }



        holder.itemView.setOnClickListener{
            vendorsActivity.onItemClick(contact)
        }

    }

    override fun getItemCount(): Int {
        if (membersList.isNotEmpty()) {
            return membersList.size
        }
        return 0
    }

    fun setCallback(vendorsActivity: VendorsActivity) {
        this.vendorsActivity = vendorsActivity
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vendor_name: TextView = itemView.findViewById(R.id.vendor_name)
        val call_icon: ImageView = itemView.findViewById(R.id.call_icon)
        val message_icon: ImageView = itemView.findViewById(R.id.message_icon)

    }
    fun filterList( emergencyContact : ArrayList<VendorTypeModel.Data>) {
        this.membersList = emergencyContact ;
        notifyDataSetChanged();
    }




}