package com.example.safehome.visitors

import android.annotation.SuppressLint
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
import com.bumptech.glide.Glide
import com.example.safehome.R
import com.example.safehome.Utils

class VisitorsListAdapter(
    private val context: Context,
    private var visitorsList: ArrayList<GetAllVisitorDetailsModel.Data.Event>,
    var visitorType: String
):
    RecyclerView.Adapter<VisitorsListAdapter.MyViewHolder>() {
 private lateinit var visitorsListFragment: VisitorsListFragment
 private  var historyVisitorsListFragment: HistoryVisitorsListFragment ?= null
 private  var currentVisitorsListFragment: CurrentVisitorsListFragment ?= null
 private  var expectedVisitorsListFragment: ExpectedVisitorsListFragment ?= null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.activity_visitors_list_item, parent, false)
            return MyViewHolder(view)
        }

    override fun getItemCount(): Int {
        if (visitorsList.isNotEmpty()) {
            return visitorsList.size
        }
        return 0

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val visitorListItem = visitorsList[position]
        if(visitorListItem.imagePath!= null){
            Glide.with(context)
                .load("https://qa.msafehome.com/${visitorListItem.imagePath}")
                .fitCenter()
                .into(holder.visitorImage)
        }else{
//            holder.visitorImage.setImageResource(R.drawable.visitor_cab)
            when (visitorListItem.visitorTypeName) {
                "Guest" -> {
                    Glide.with(context)
                        .load(R.drawable.visitor_guest)
                        .fitCenter()
                        .into(holder.visitorImage)
                }

                "Cab" -> {
                    Glide.with(context)
                        .load(R.drawable.visitor_cab)
                        .fitCenter()
                        .into(holder.visitorImage)
                }

                "Staff" -> {
                    Glide.with(context)
                        .load(R.drawable.visitor_staff)
                        .fitCenter()
                        .into(holder.visitorImage)
                }

                "Delivery" -> {
                    Glide.with(context)
                        .load(R.drawable.visitor_delivery)
                        .fitCenter()
                        .into(holder.visitorImage)
                }

                "Other" -> {
                    Glide.with(context)
                        .load(R.drawable.visitor_others)
                        .fitCenter()
                        .into(holder.visitorImage)
                }
            }


        }

        when (visitorListItem.visitorTypeName) {
            "Guest" -> {
             holder.visitorNameTxt.visibility = View.VISIBLE
            }

            "Cab" -> {
                holder.visitorNameTxt.visibility = View.GONE

            }

            "Staff" -> {
                holder.visitorNameTxt.visibility = View.VISIBLE

            }

            "Delivery" -> {
                holder.visitorNameTxt.visibility = View.GONE

            }

            "Other" -> {
                holder.visitorNameTxt.visibility = View.VISIBLE

            }
        }


        if (!visitorListItem.visitorTypeName.isNullOrEmpty()){
            holder.visitorPurposeTitleTxt.text = visitorListItem.visitorTypeName
        }
        if (!visitorListItem.seviceProviderName.isNullOrEmpty()){
            holder.visitorPurposeByTxt.visibility = View.VISIBLE
            holder.visitorPurposeByTxt.text = visitorListItem.seviceProviderName
        }else{
            holder.visitorPurposeByTxt.visibility = View.GONE
        }

        if (!visitorListItem.vehicleNumber.isNullOrEmpty()){
            holder.visitorVechileNoTxt.visibility = View.VISIBLE
            holder.visitorVechileNoTxt.text = "Cab Number: "+visitorListItem.vehicleNumber
        }else{
            holder.visitorVechileNoTxt.visibility = View.GONE
        }

        if (!visitorListItem.name.isNullOrEmpty()){
            holder.visitorNameTxt.visibility = View.VISIBLE
            if (visitorListItem.seviceProviderName == "Guest")
            holder.visitorNameTxt.text = "Guest Name: "+visitorListItem.name
            else
                holder.visitorNameTxt.text = "Name: "+visitorListItem.name
        }else{
            holder.visitorNameTxt.visibility = View.GONE
        }

        if (!visitorListItem.startDate.isNullOrEmpty()){
            if(visitorType == "ExpectedVisitors"){
                holder.visitorOutTimingTxt.visibility = View.GONE
                if (!visitorListItem.inTime.isNullOrEmpty()){
                    holder.visitorInTimingTxt.text = "Expected Time: "+visitorListItem.inTime
                }
            }else if(visitorType == "HistoryVisitors") {
                holder.visitorOutTimingTxt.visibility = View.VISIBLE

                if (!visitorListItem.inTime.isNullOrEmpty()){
                    holder.visitorInTimingTxt.text = "In Time: "+visitorListItem.inTime
                }

                if (!visitorListItem.outTime.isNullOrEmpty()){
                    holder.visitorOutTimingTxt.text = "Out Time: "+visitorListItem.outTime
                }

            }else{
                holder.visitorOutTimingTxt.visibility = View.GONE

                if (!visitorListItem.inTime.isNullOrEmpty()){
                    holder.visitorInTimingTxt.text = "In Time: "+visitorListItem.inTime
                }
            }
        }
        if (!visitorListItem.invitedBy.isNullOrEmpty()){
            holder.visitorAllowedByText.text = "Allowed By: "+visitorListItem.invitedBy
        }

        if(!visitorListItem.endDate.isNullOrEmpty()){
            holder.visitorDateTxt.text = Utils.formatDateAndMonth(visitorListItem.startDate!!) + " - "+ visitorListItem.endDate.let { Utils.formatDateAndMonth(it) }
        }else {
            holder.visitorDateTxt.text = Utils.formatDateAndMonth(visitorListItem.startDate!!)
        }
        if (!visitorListItem.allowFor.isNullOrEmpty()){
            holder.allowForTxt.text = "Allow For: "+visitorListItem.allowFor
        }

        if ( visitorType == "ExpectedVisitors"){
            holder.ivEdit.visibility = View.VISIBLE
            holder.ivDelete.visibility = View.VISIBLE
            holder.shareOtpLayout.visibility = View.VISIBLE
        }else{
            holder.ivEdit.visibility = View.GONE
            holder.ivDelete.visibility = View.GONE
            holder.shareOtpLayout.visibility = View.GONE

        }

        if (!visitorListItem.approvalStatusName.isNullOrEmpty()){
            if (visitorListItem.approvalStatusName== "Yes"){
                holder.preApprovedStatusLayout.visibility = View.VISIBLE
            }else{
                holder.preApprovedStatusLayout.visibility = View.GONE
            }
        }

        holder.ivDelete.setOnClickListener {
            if (currentVisitorsListFragment != null){

                currentVisitorsListFragment!!.deleteVisitorItem(visitorListItem)
            }else if (expectedVisitorsListFragment != null){

                expectedVisitorsListFragment!!.deleteVisitorItem(visitorListItem)
            }else{
                historyVisitorsListFragment!!.deleteVisitorItem(visitorListItem)

            }
        }

        holder.ivEdit.setOnClickListener {
            if (currentVisitorsListFragment != null){

                currentVisitorsListFragment!!.editVisitorItem(visitorListItem)
            }else if (expectedVisitorsListFragment != null){
                expectedVisitorsListFragment!!.editVisitorItem(visitorListItem)
            }else{

                historyVisitorsListFragment!!.editVisitorItem(visitorListItem)
            }
        }

        holder.shareOtpLayout.setOnClickListener {
            expectedVisitorsListFragment?.shareOtp(visitorListItem)
        }

    }

    fun setCallback(visitorsListFragment: VisitorsListFragment) {
       this.visitorsListFragment = visitorsListFragment
    }

    fun filteredList(visitorsList:  ArrayList<GetAllVisitorDetailsModel.Data.Event>) {
        this.visitorsList = visitorsList;
        notifyDataSetChanged();
    }

    fun setCurrentVisitorsCallback(currentVisitorsListFragment: CurrentVisitorsListFragment) {
        this.currentVisitorsListFragment = currentVisitorsListFragment

    }
    fun setCurrentVisitorsCallback(expectedVisitorsListFragment: ExpectedVisitorsListFragment) {
        this.expectedVisitorsListFragment = expectedVisitorsListFragment

    }

    fun setCurrentVisitorsCallback(historyVisitorsListFragment: HistoryVisitorsListFragment) {
        this.historyVisitorsListFragment = historyVisitorsListFragment
    }

    class MyViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
     val visitorImage = itemView.findViewById<ImageView>(R.id.visitor_img)
     val visitorPurposeTitleTxt = itemView.findViewById<TextView>(R.id.visitor_purpose_txt)
     val visitorPurposeByTxt =  itemView.findViewById<TextView>(R.id.visitor_purposeBy_txt)
     val visitorDateTxt = itemView.findViewById<TextView>(R.id.visitor_Date_txt)
     val visitorAllowedByText = itemView.findViewById<TextView>(R.id.visitor_allowedBy_txt)
     val allowForTxt = itemView.findViewById<TextView>(R.id.visitor_allowFor_txt)
     val visitorInTimingTxt = itemView.findViewById<TextView>(R.id.visitor_InTime_txt)
     val visitorOutTimingTxt = itemView.findViewById<TextView>(R.id.visitor_OutTime_txt)
     val visitorNameTxt = itemView.findViewById<TextView>(R.id.visitor_name_txt)
     val visitorVechileNoTxt = itemView.findViewById<TextView>(R.id.visitor_vechileNo_txt)
     val ivDelete = itemView.findViewById<ImageView>(R.id.ivDelete)
     val ivEdit = itemView.findViewById<ImageView>(R.id.ivEdit)
     val shareOtpLayout = itemView.findViewById<LinearLayout>(R.id.share_otp_layout)
     val preApprovedStatusLayout = itemView.findViewById<LinearLayout>(R.id.preApproved_status_layout)
    }

}