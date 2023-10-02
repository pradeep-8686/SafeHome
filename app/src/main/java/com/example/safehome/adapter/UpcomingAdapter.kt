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
import com.example.safehome.Utils
import com.example.safehome.facilitiesview.BookingsFragment
import com.example.safehome.model.Events
import com.example.safehome.model.MaintenanceHistoryModel
import com.example.safehome.model.Upcoming

class UpcomingAdapter(
    var context: Context,
    private var upcomingList: ArrayList<Events>
) :
    RecyclerView.Adapter<UpcomingAdapter.MyViewHolder>() {
    private lateinit var bookingsFragment: BookingsFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.events_upcoming_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val upcoming = upcomingList[position]

        if (upcoming.startDate != null) {
            if(upcoming.endDate!!.isNotEmpty()){
                val startDate = Utils.changeDateFormat(upcoming?.startDate!!.split("T")[0]).replace("-", "/")
                val endDate = Utils.changeDateFormat(upcoming.endDate!!.split("T")[0]).replace("-", "/")
                holder.booking_date_tv.text = "$startDate - $endDate"
//                upcoming.startTime = startDate
//                upcoming.endDate = endDate

                if (startDate == endDate){
                    holder.booking_date_tv.text = "$startDate"

                }else{
                    holder.booking_date_tv.text = "$startDate - $endDate"

                }
            }else{
                val startDate = Utils.changeDateFormat(upcoming?.startDate!!.split("T")[0]).replace("-", "/")

                holder.booking_date_tv.text = "$startDate"
//                upcoming.startTime = startDate

            }
        }
        if (upcoming.startTime != null) {
            if(upcoming.startTime!!.isNotEmpty()){
                holder.start_time_tv.text = "${upcoming.startTime} - ${upcoming.endTime}"
            }else{
                holder.start_time_tv.text = "${upcoming.startTime}"
            }
        }
        if (upcoming.endTime != null && upcoming.endTime!!.isNotEmpty()) {
            holder.end_time_tv.text = upcoming.endTime
        }
        if (upcoming.createdByName != null && upcoming.createdByName!!.isNotEmpty()) {
            holder.booked_by.text = upcoming.createdByName
        }

        holder.title_tv.setText(upcoming.eventName)
        holder.locate_tv.setText(upcoming.facilityName)

    }

    override fun getItemCount(): Int {
        if (upcomingList.isNotEmpty()) {
            return upcomingList.size
        }
        return 0
    }

    fun setCallback(bookingsFragment: BookingsFragment) {
        this.bookingsFragment = bookingsFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val booking_date_tv: TextView = itemView.findViewById(R.id.booking_date_tv)
        val start_time_tv: TextView = itemView.findViewById(R.id.start_time_tv)
        val end_time_tv: TextView = itemView.findViewById(R.id.end_time_tv)
        val booked_by: TextView = itemView.findViewById(R.id.booked_by)
        val title_tv: TextView = itemView.findViewById(R.id.title_tv)
        val locate_tv: TextView = itemView.findViewById(R.id.locate_tv)

    }


    fun filterList(upcomingList: ArrayList<Events>) {
        this.upcomingList = upcomingList;
        notifyDataSetChanged();
    }


}