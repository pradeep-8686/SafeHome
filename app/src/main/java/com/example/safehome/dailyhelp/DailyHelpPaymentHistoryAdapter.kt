package com.example.safehome.dailyhelp

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
import com.example.safehome.model.DailyHelpHistoryModel
import com.example.safehome.model.DailyHelpMemberList
import com.example.safehome.model.DailyHelpRoles
import com.example.safehome.model.FaciBookings

class DailyHelpPaymentHistoryAdapter(
    var context: Context,
    private var dailyHelpMemberList: ArrayList<DailyHelpHistoryModel.Data>
) :
    RecyclerView.Adapter<DailyHelpPaymentHistoryAdapter.MyViewHolder>() {
    private lateinit var dailyHelpPaymentHistoryFragment: DailyHelpPaymentHistoryFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.daily_help_payment_history_item, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dailyHelpMemberList = dailyHelpMemberList[position]
        holder.daily_help_payment_history_layout.tag = dailyHelpMemberList

        if (dailyHelpMemberList.staffName != null && dailyHelpMemberList.staffName.isNotEmpty()) {
            holder.name_tv.text = dailyHelpMemberList.staffName
        }
        if (dailyHelpMemberList.staffTypeName != null && dailyHelpMemberList.staffTypeName.isNotEmpty()) {
            holder.role_tv.text = dailyHelpMemberList.staffTypeName
        }
        if (dailyHelpMemberList.paymentMode != null && dailyHelpMemberList.paymentMode.isNotEmpty()) {
            holder.payment_mode.text = "Payment Mode : "+dailyHelpMemberList.paymentMode
        }
        if (dailyHelpMemberList.paidDate != null && dailyHelpMemberList.paidDate.isNotEmpty()) {
            holder.paid_on_tv.text = "Paid On : "+dailyHelpMemberList.paidDate
        }
        holder.amount_tv.text = "Amount: "+ context.getString(R.string.Rs)+ "500/-"
        holder.daily_help_payment_history_layout.setOnClickListener {
          //  dailyHelpPaymentHistoryFragment.selectedPayment(it.tag as DailyHelpMemberList)
        }

    }

    override fun getItemCount(): Int {
        if (dailyHelpMemberList.isNotEmpty()) {
            return dailyHelpMemberList.size
        }
        return 0
    }

    fun setCallback(dailyHelpPaymentHistoryFragment: DailyHelpPaymentHistoryFragment) {
        this.dailyHelpPaymentHistoryFragment = dailyHelpPaymentHistoryFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_tv: TextView = itemView.findViewById(R.id.name_tv)
        val role_tv: TextView = itemView.findViewById(R.id.role_tv)
        val paid_on_tv: TextView = itemView.findViewById(R.id.paid_on_tv)
        val amount_tv: TextView = itemView.findViewById(R.id.amount_tv)
        val payment_mode: TextView = itemView.findViewById(R.id.payment_mode)
        val daily_help_payment_history_layout: LinearLayout = itemView.findViewById(R.id.daily_help_payment_history_layout)
    }


    fun filterList( dailyHelpMemberList: ArrayList<DailyHelpHistoryModel.Data>) {
        this.dailyHelpMemberList = dailyHelpMemberList;
        notifyDataSetChanged();
    }



}