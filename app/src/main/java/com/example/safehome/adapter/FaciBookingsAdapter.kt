package com.example.safehome.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.Utils.Companion.formatDateMonthYear
import com.example.safehome.facilitiesview.BookingsFragment
import com.example.safehome.model.FaciBookings

class FaciBookingsAdapter(
    var context: Context,
    private var bookingsList: ArrayList<FaciBookings.Data.Facilility>
) :
    RecyclerView.Adapter<FaciBookingsAdapter.MyViewHolder>() {
    private var startTime: String?= null
    private var endTime: String?= null
    private var endDate: String? = null
    private var endDates: List<String>?= null
    private var startDate: String?= null
    private var startDates: List<String>?= null
    private lateinit var bookingsFragment: BookingsFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.faci_bookings_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val faciBookings = bookingsList[position]
        holder.booking_info_edit_icon.tag = faciBookings

        if (faciBookings.name != null && faciBookings.name.isNotEmpty()) {
            holder.booking_type_tv.text = faciBookings.name
        }
        if (faciBookings.approvalStatusName != null && faciBookings.approvalStatusName.isNotEmpty()) {
            if (faciBookings.approvalStatusName != "Approved"){
                holder.payment_status_tv.setBackgroundResource(R.drawable.rectangler_vrify_bg)
                holder.payment_status_tv.setTextColor(context.resources.getColor(R.color.white))
                holder.payment_status_tv.text = "Escalate Approval"
                holder.status_text_tv.text = "Pending"
            }else{
                holder.status_text_tv.text = faciBookings.approvalStatusName

                if (faciBookings.paymentStatusName == "UnPaid") {
                    holder.payment_status_tv.setBackgroundResource(R.drawable.make_payment_green_bg)
                    holder.payment_status_tv.setTextColor(context.resources.getColor(R.color.white))
                    //   holder.payment_status_tv.setTextColor(context.resources.getColor(R.color.light_white))
                    holder.payment_status_tv.text = "Make Payment"

                } /*else {
                    holder.payment_status_tv.setBackgroundResource(R.drawable.rectangler_vrify_bg)
                    holder.payment_status_tv.setTextColor(context.resources.getColor(R.color.white))
                    holder.payment_status_tv.text = "Escalate Approval"

                }*/

            }

            holder.payment_status_tv.setOnClickListener {
                bookingsFragment.clickOnPaymentStatus(faciBookings)
            }

        }
        if (faciBookings.totalAmount != null) {
            "${context.getString(R.string.rupee)}${faciBookings.totalAmount}/-".also {
                holder.total_charge_tv.text = it
            }
        }
        if (faciBookings.dateOfBooking != null && faciBookings.dateOfBooking.isNotEmpty()) {
            var dateOfBookings = "${formatDateMonthYear(faciBookings.dateOfBooking)} "
            holder.booking_date_tv.text = dateOfBookings
           }



        if (faciBookings.endDate != null && faciBookings.endDate.isNotEmpty()) {
            holder.timeLayout.visibility = View.GONE
            if (faciBookings.startDate != null && faciBookings.startDate.isNotEmpty()) {
                startDate = formatDateMonthYear(faciBookings.startDate)
                endDate = formatDateMonthYear(faciBookings.endDate)
                if (startDate == endDate){
                    holder.start_date_tv.text = "$startDate"
                    holder.timeLayout.visibility = View.VISIBLE
                    startTime = faciBookings.startTime.replace("-", ":")
                    endTime = faciBookings.endTime.replace("-", ":")
                    holder.time_tv.text = "$startTime - $endTime"
                }else{
                    holder.start_date_tv.text = "$startDate  -  $endDate"
                }
            }
        }else{
            if (faciBookings.startDate != null && faciBookings.startDate.isNotEmpty()) {
                startDate = formatDateMonthYear(faciBookings.startDate)
                holder.start_date_tv.text = startDate
            }
            holder.timeLayout.visibility = View.VISIBLE
            startTime = faciBookings.startTime.replace("-", ":")
            endTime = faciBookings.endTime.replace("-", ":")
            holder.time_tv.text = "$startTime - $endTime"
        }

        holder.editBookingInfoImageview.setOnClickListener {

        }
        holder.cancel_booking_tv.setOnClickListener {
            bookingsFragment.cancelBooking(faciBookings.bookFacilityId)
        }
        holder.booking_info_edit_icon.setOnClickListener {v ->
            bookingsFragment.editBookingRequest(v.tag as FaciBookings.Data.Facilility)
        }

    }

    override fun getItemCount(): Int {
        if (bookingsList.isNotEmpty()) {
            return bookingsList.size
        }
        return 0
    }

    fun setCallback(bookingsFragment: BookingsFragment) {
        this.bookingsFragment = bookingsFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val booking_type_tv: TextView = itemView.findViewById(R.id.booking_type_tv)

        val status_text_tv: TextView = itemView.findViewById(R.id.status_text_tv)
        val total_charge_tv: TextView = itemView.findViewById(R.id.total_charge_tv)
        val booking_date_tv: TextView = itemView.findViewById(R.id.booking_date_tv)

        val start_date_tv: TextView = itemView.findViewById(R.id.start_date_tv)
        val time_tv: TextView = itemView.findViewById(R.id.time_tv)
        val timeLayout = itemView.findViewById<LinearLayout>(R.id.time_llayout)
        /*   val start_time_tv: TextView = itemView.findViewById(R.id.start_time_tv)
           val end_time_tv: TextView = itemView.findViewById(R.id.end_time_tv)*/
        //   val make_payment_tv: TextView = itemView.findViewById(R.id.make_payment_tv)
        val payment_status_tv: TextView = itemView.findViewById(R.id.payment_status_tv)
        val cancel_booking_tv: TextView = itemView.findViewById(R.id.cancel_booking_tv)
        val editBookingInfoImageview: ImageView = itemView.findViewById(R.id.booking_info_edit_icon)
        val booking_info_edit_icon: ImageView = itemView.findViewById(R.id.booking_info_edit_icon)

    }
}