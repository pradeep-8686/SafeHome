package com.example.safehome.services

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.model.ServiceTypesList
import com.example.safehome.model.ServicesBookingsList

class ServicesBookingListAdapter(
    var context: Context,
    private var serviceBookingsMemberList: ArrayList<ServicesBookingsList.Data>
) :
    RecyclerView.Adapter<ServicesBookingListAdapter.MyViewHolder>() {
    private lateinit var dailyHelpBookingsFragment: ServicesBookingsFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.daily_help_booking_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val servicesMemberList = serviceBookingsMemberList[position]
        holder.daily_help_booking_item_list_layout.tag = servicesMemberList
        holder.pay_now_btn.tag = servicesMemberList

        if (servicesMemberList.personName != null && servicesMemberList.personName.isNotEmpty()) {
            holder.name_tv.text = servicesMemberList.personName
        }
        if (servicesMemberList.serviceTypeName != null && servicesMemberList.serviceTypeName.isNotEmpty()) {
            holder.role_tv.text = servicesMemberList.serviceTypeName
        }
        holder.pay_now_btn.setOnClickListener {
            dailyHelpBookingsFragment.selectedBookingPayUsing(it.tag as ServicesBookingsList.Data)
        }

        holder.daily_help_booking_item_list_layout.setOnClickListener {
            dailyHelpBookingsFragment.selectedBookingItem(it.tag as ServicesBookingsList.Data)
        }

    }

    override fun getItemCount(): Int {
        if (serviceBookingsMemberList.isNotEmpty()) {
            return serviceBookingsMemberList.size
        }
        return 0
    }

    fun setCallback(dailyHelpBookingsFragment: ServicesBookingsFragment) {
        this.dailyHelpBookingsFragment = dailyHelpBookingsFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_tv: TextView = itemView.findViewById(R.id.name_tv)
        val role_tv: TextView = itemView.findViewById(R.id.role_tv)
        val pay_now_btn: TextView = itemView.findViewById(R.id.pay_now_btn)

        val daily_help_booking_item_list_layout: LinearLayout = itemView.findViewById(R.id.daily_help_booking_item_list_layout)
    }
    fun filterList(serviceBookingsMemberList: ArrayList<ServicesBookingsList.Data>) {
        this.serviceBookingsMemberList = serviceBookingsMemberList;
        notifyDataSetChanged();
    }



}