package com.example.safehome.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.model.Events
import com.example.safehome.model.FaciBookings

class FaciHistoryAdapter (
    var context: Context,
    private var bookingsList: ArrayList<FaciBookings.Data.Facilility>
) :
    RecyclerView.Adapter<FaciHistoryAdapter.MyViewHolder>() {
    private var dateOfBooking: String?= null
    private var endDate: String? = null
    private var endDates: List<String>?= null
    private var startDate: String?= null
    private var startDates: List<String>?= null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.faci_history_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val faciBookings = bookingsList[position]

//        if (faciBookings.paymentMode!= null && faciBookings.paymentMode.isNotEmpty()){
//            holder.payment_mode_tv.text = faciBookings.paymentMode
//        }
        if (faciBookings.name != null && faciBookings.name.isNotEmpty()) {
            holder.booking_type_tv.text = faciBookings.name
        }
        if (faciBookings.totalAmount != null) {
            "${context.getString(R.string.rupee)}${faciBookings.totalAmount}/-".also {
                holder.total_charge_tv.text = it
            }
        }
        if (faciBookings.dateOfBooking != null && faciBookings.dateOfBooking.isNotEmpty()) {
            val dateOfBookings = faciBookings.dateOfBooking.split("T")
             dateOfBooking = Utils.formatDateAndMonth(dateOfBookings[0])
            holder.booking_date_tv.text = dateOfBooking
        }
        if (faciBookings.startDate != null && faciBookings.startDate.isNotEmpty()) {
            if (faciBookings.endDate != null && faciBookings.endDate.isNotEmpty()) {
                startDates = faciBookings.startDate.split("T")
                startDate = Utils.changeDateFormat(startDates!![0])
                startDate = startDate!!.replace("-", "/")
                endDates = faciBookings.endDate.split("T")
                endDate = Utils.changeDateFormat(endDates!![0])
                endDate = endDate!!.replace("-", "/")
                holder.start_date_tv.text = "$startDate - $endDate"

            }else{
                holder.start_date_tv.text = startDate
            }
        }

        if (faciBookings.endDate.isNotEmpty() && faciBookings.endDate!= null){
            holder.history_time_period_layout.visibility = View.GONE
        }else{
            if (faciBookings.startTime != null && faciBookings.startTime.isNotEmpty()) {
                holder.history_time_period_layout.visibility = View.VISIBLE
                if (faciBookings.endTime != null && faciBookings.endTime.isNotEmpty()) {
                    holder.start_time_tv.text =
                        faciBookings.startTime + " - " + faciBookings.endTime
                }
            }
        }

        if (faciBookings.dateOfBooking != null && faciBookings.dateOfBooking.isNotEmpty()) {
            val dateOfBookings = faciBookings.dateOfBooking.split("T")
            dateOfBooking = Utils.changeDateFormat(dateOfBookings!![0])
            dateOfBooking = dateOfBooking!!.replace("-", "/")
            holder.paid_on_tv.text = dateOfBooking
        }

    }

    override fun getItemCount(): Int {
        if (bookingsList.isNotEmpty()) {
            return bookingsList.size
        }
        return 0
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val booking_type_tv: TextView = itemView.findViewById(R.id.booking_type_tv)
        //  val status_text_tv: TextView = itemView.findViewById(R.id.status_text_tv)
        val total_charge_tv: TextView = itemView.findViewById(R.id.total_charge_tv)
        val booking_date_tv: TextView = itemView.findViewById(R.id.booking_date_tv)

        val start_date_tv: TextView = itemView.findViewById(R.id.faci_date_period_txt)
        val start_time_tv: TextView = itemView.findViewById(R.id.faci_time_period_txt)
        val paid_on_tv: TextView = itemView.findViewById(R.id.paid_on_tv)
        val history_time_period_layout = itemView.findViewById<LinearLayout>(R.id.faci_time_period_layout)
        val payment_mode_tv = itemView.findViewById<TextView>(R.id.faci_payment_mode_txt)
    }

    fun filterList( bookingsList: ArrayList<FaciBookings.Data.Facilility>) {
        this.bookingsList = bookingsList;
        notifyDataSetChanged();
    }




}