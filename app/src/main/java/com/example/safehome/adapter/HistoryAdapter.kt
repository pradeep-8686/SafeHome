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
import com.example.safehome.Utils
import com.example.safehome.model.MaintenanceHistoryModel

class HistoryAdapter(
    var context: Context,
    private var myDuesList: ArrayList<MaintenanceHistoryModel.Data>
) :
    RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.history_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val myDues = myDuesList[position]
        if (myDues.catageroyName != null && myDues.catageroyName.isNotEmpty()) {
            holder.History_type_tv.text = myDues.catageroyName
        }
        if (myDues.paymentStatus != null && myDues.paymentStatus.isNotEmpty()) {
            holder.payment_mode_text_tv.text = ""
        }
        if (myDues.paidAmount != null) {
            "${context.getString(R.string.rupee)}${myDues.paidAmount}/-".also {
                holder.total_invoice_amount_tv.text = it
            }
        }
//        var invoiceDate: S    tring?= null
//        if (myDues.invoiceDate.isNotEmpty()){
//            val invoiceDates = myDues.invoiceDate.split("T")
//             invoiceDate = Utils.changeDateFormat(invoiceDates[0])
//
//            holder.paid_on_txt_tv.text = invoiceDate!!.replace("-", "/")
//            val monthYear = Utils.formatDateToMonth(invoiceDate)
//            holder.invoice_period_text_tv.text = monthYear
//
//
//
//        }
        if (myDues.invoiceFromDate != null && myDues.invoiceToDate != null) {

            var invoiceFromdate = ""
            var invoiceTodate = ""

            if (myDues.invoiceFromDate!!.contains("T")) {
                invoiceFromdate = "${Utils.dateToMonthYear(myDues.invoiceFromDate)} "

            }else{
                invoiceFromdate =myDues.invoiceFromDate

            }
            if (myDues.invoiceToDate!!.contains("T")) {
                invoiceTodate = "${Utils.dateToMonthYear(myDues.invoiceToDate)} "

            }else{
                invoiceTodate =myDues.invoiceToDate

            }

            myDues.invoiceFromDate = invoiceFromdate
            myDues.invoiceToDate = invoiceTodate

            if (invoiceFromdate == invoiceTodate) {
                holder.invoice_period_text_tv.text = "$invoiceFromdate"
            } else {
                holder.invoice_period_text_tv.text = "$invoiceFromdate, $invoiceTodate"

            }
        }

        holder.due_type_image_view.setImageResource(R.drawable.common_area_maintainance_icon)
    }

    override fun getItemCount(): Int {
        if (myDuesList.isNotEmpty()) {
            return myDuesList.size
        }
        return 0
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val History_type_tv: TextView = itemView.findViewById(R.id.History_type_tv)
        val payment_mode_text_tv: TextView = itemView.findViewById(R.id.payment_mode_text_tv)
        val paid_on_txt_tv: TextView = itemView.findViewById(R.id.paid_on_txt_tv)
        val invoice_period_text_tv: TextView = itemView.findViewById(R.id.invoice_period_text_tv)
        val total_invoice_amount_tv: TextView = itemView.findViewById(R.id.total_invoice_amount_tv)
        val due_type_image_view: ImageView = itemView.findViewById(R.id.history_type_image_view)

    }

    fun filterList(myDuesList: ArrayList<MaintenanceHistoryModel.Data>) {
        this.myDuesList = myDuesList;
        notifyDataSetChanged();
    }

}