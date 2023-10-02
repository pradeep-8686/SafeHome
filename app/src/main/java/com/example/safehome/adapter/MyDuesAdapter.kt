package com.example.safehome.adapter

import android.annotation.SuppressLint
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
import com.example.safehome.Utils.Companion.formatDateAndMonth
import com.example.safehome.Utils.Companion.formatDateToMonth
import com.example.safehome.Utils.Companion.getMonth
import com.example.safehome.maintenance.MyDuesFragment
import com.example.safehome.model.MyDuesMaintenanceDetails
import com.google.android.material.card.MaterialCardView

class MyDuesAdapter(
    var context: Context,
    private var myDuesMaintenanceDetailsList: ArrayList<MyDuesMaintenanceDetails>
) :
    RecyclerView.Adapter<MyDuesAdapter.MyViewHolder>() {
    private lateinit var myDuesFragment: MyDuesFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.my_dues_item_list, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SuspiciousIndentation", "ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val myDues = myDuesMaintenanceDetailsList[position]
        holder.view_invoice_tv.tag = myDues
        holder.paid_tv.tag = myDues

                if (myDues.catageroyName != null && myDues.catageroyName.isNotEmpty()) {
                    holder.due_type_tv.text = myDues.catageroyName
                }

                if (myDues.invoiceFromDate != null && myDues.invoiceFromDate.isNotEmpty()) {
                    if (myDues.invoiceToDate != null && myDues.invoiceToDate.isNotEmpty()){

                        var invoiceFromdate = "${formatDateToMonth(myDues.invoiceFromDate)} "
                        var invoiceTodate = "${formatDateToMonth(myDues.invoiceToDate)} "

//                        var invoiceFromdate = "${formatDateToMonth("08-02-2023T00:00:00")} "
//                        var invoiceTodate = "${formatDateToMonth("08-02-2023T00:00:00")} "

                        var monthYear: String? = null
                        val fromMonth = "${getMonth(myDues.invoiceFromDate)} "
                        val toMonth = "${getMonth(myDues.invoiceToDate)} "
                        try {
                           if(fromMonth.equals(toMonth)){
                               monthYear = invoiceFromdate
                           }else{
                               monthYear = invoiceFromdate + "- "+ invoiceTodate
                           }
                        } catch (ex: Exception){

                        }
                        holder.invoice_period_tv.text = monthYear
                    }
                }
                if (myDues.invoiceDueDate != null && myDues.invoiceDueDate.isNotEmpty()) {
//                    val invoiceDueDates = myDues.invoiceDueDate.split("T")
//                    val invoiceDueDate = Utils.changeDateFormat(invoiceDueDates[0])
                    val formattedDate = formatDateAndMonth(myDues.invoiceDueDate)
                    holder.due_date_tv.text = formattedDate
                }
                if(myDues.paymentStatus.isNotEmpty()){
                    if (myDues.paymentStatus == "UnPaid") {
                        holder.paid_tv.text = myDues.paymentStatus
                        holder.payNowText.visibility = View.VISIBLE
                        holder.cardPaymentButton.visibility = View.GONE
                    }else{
                        holder.paid_tv.text = myDues.paymentStatus
                        holder.payNowText.visibility = View.GONE
                        holder.cardPaymentButton.visibility = View.VISIBLE
                    }
                }

                if (myDues.invoiceAmount != null) {
                    "${context.getString(R.string.rupee)}${myDues.invoiceAmount}".also {
                        holder.total_invoice_amount_tv.text = it
                    }
                }

             //   holder.due_type_image_view.setImageResource(myDues.imageResource)
        holder.view_invoice_tv.setOnClickListener {
            myDuesFragment.selectedInvoice(it.tag as MyDuesMaintenanceDetails)
        }

        holder.paid_tv.setOnClickListener {
            myDuesFragment.selectedPaid(it.tag as MyDuesMaintenanceDetails)
        }

    }

    override fun getItemCount(): Int {
        if (myDuesMaintenanceDetailsList.isNotEmpty()) {
            return myDuesMaintenanceDetailsList.size
        }
        return 0
    }

    fun setCallback(myDuesFragment: MyDuesFragment) {
        this.myDuesFragment = myDuesFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val due_type_tv: TextView = itemView.findViewById(R.id.due_type_tv)
        val invoice_period_tv: TextView = itemView.findViewById(R.id.invoice_period_tv)
        val due_date_tv: TextView = itemView.findViewById(R.id.due_date_tv)
        val total_invoice_amount_tv: TextView = itemView.findViewById(R.id.total_invoice_amount_tv)
        val due_type_image_view: ImageView = itemView.findViewById(R.id.history_type_image_view)
        //   val status_text_tv : TextView = itemView.findViewById(R.id.status_text_tv)
        val payNowText = itemView.findViewById<TextView>(R.id.pay_now_txt)
        val cardPaymentButton = itemView.findViewById<MaterialCardView>(R.id.pay_now_card)
        val view_invoice_tv: TextView = itemView.findViewById(R.id.view_invoice_tv)
        val paid_tv: TextView = itemView.findViewById(R.id.paid_tv)

    }
}