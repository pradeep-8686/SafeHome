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
import com.example.safehome.communityview.VendorsDetailsActivity
import com.example.safehome.model.EmergencyContact
import com.example.safehome.model.VendorDetailsModel
import com.example.safehome.model.VendorTypeModel

class VendorDetailsAdapter (var context: Context,
                            private var membersList: ArrayList<VendorDetailsModel.Data>
) :
RecyclerView.Adapter<VendorDetailsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.vendor_details_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = membersList[position]
        if (contact.serviceProvided  != null && contact.serviceProvided .isNotEmpty()) {
            holder.vendor_name.text = contact.serviceProvided
        }



    }

    override fun getItemCount(): Int {
        if (membersList.isNotEmpty()) {
            return membersList.size
        }
        return 0
    }



    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vendor_name: TextView = itemView.findViewById(R.id.vendor_name)

    }
    fun filterList( emergencyContact : ArrayList<VendorDetailsModel.Data>) {
        this.membersList = emergencyContact ;
        notifyDataSetChanged();
    }




}