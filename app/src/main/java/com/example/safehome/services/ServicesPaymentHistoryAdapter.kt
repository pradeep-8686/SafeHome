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
import com.example.safehome.Utils
import com.example.safehome.model.DailyHelpMemberList
import com.example.safehome.model.ServiceTypesList
import com.example.safehome.model.ServicesStaffHistoryList

class ServicesPaymentHistoryAdapter(
    var context: Context,
    private var serviceshistoryMemberList: ArrayList<ServicesStaffHistoryList.Data>
) :
    RecyclerView.Adapter<ServicesPaymentHistoryAdapter.MyViewHolder>() {
    private lateinit var servicesPaymentHistoryFragment: ServicesPaymentHistoryFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.services_payment_history_item, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val servicesHistoryMemberList = serviceshistoryMemberList[position]
        holder.daily_help_payment_history_layout.tag = servicesHistoryMemberList

        if (servicesHistoryMemberList.staffName != null && servicesHistoryMemberList.staffName.isNotEmpty()) {
            holder.name_tv.text = servicesHistoryMemberList.staffName
        }
        if (servicesHistoryMemberList.staffTypeName != null && servicesHistoryMemberList.staffTypeName.isNotEmpty()) {
            holder.serviceType_tv.text = servicesHistoryMemberList.staffTypeName
        }
        if (servicesHistoryMemberList.paidDate != null && servicesHistoryMemberList.paidDate.isNotEmpty()) {


            val paidDate = if (servicesHistoryMemberList.paidDate!!.contains("T")) {
                Utils.changeDateFormat(servicesHistoryMemberList?.paidDate!!.split("T")[0])
                    .replace("-", "/")
            } else {
                servicesHistoryMemberList?.paidDate!!.replace("-", "/")
            }

            servicesHistoryMemberList.paidDate = paidDate

            holder.paid_on_tv.text = "Paid On : $paidDate"
        }
        if (servicesHistoryMemberList.amount!=null && servicesHistoryMemberList.amount.isNotEmpty()){

            holder.amount_tv.text = "Amount : "+ context.getString(R.string.Rs)+"${servicesHistoryMemberList.amount}/-"
        }

        if (servicesHistoryMemberList.paymentMode!=null && servicesHistoryMemberList.paymentMode.isNotEmpty()){
            holder.paymentModeTv.text = "Payment Mode : "+servicesHistoryMemberList.paymentMode
        }
        holder.daily_help_payment_history_layout.setOnClickListener {
            servicesPaymentHistoryFragment.selectedPayment(it.tag as DailyHelpMemberList)
        }
    }

    override fun getItemCount(): Int {
        if (serviceshistoryMemberList.isNotEmpty()) {
            return serviceshistoryMemberList.size
        }
        return 0
    }

    fun setCallback(servicesPaymentHistoryFragment: ServicesPaymentHistoryFragment) {
        this.servicesPaymentHistoryFragment = servicesPaymentHistoryFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name_tv: TextView = itemView.findViewById(R.id.name_tv)
        val serviceType_tv: TextView = itemView.findViewById(R.id.service_type_tv)
        val paid_on_tv: TextView = itemView.findViewById(R.id.paid_on_tv)
        val amount_tv: TextView = itemView.findViewById(R.id.amount_tv)
        val paymentModeTv = itemView.findViewById<TextView>(R.id.payment_mode_tv)
        val daily_help_payment_history_layout: LinearLayout = itemView.findViewById(R.id.daily_help_payment_history_layout)
    }

    fun filterList(serviceshistoryMemberList: ArrayList<ServicesStaffHistoryList.Data>) {
        this.serviceshistoryMemberList = serviceshistoryMemberList;
        notifyDataSetChanged();
    }


}