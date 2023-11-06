package com.example.safehome.visitors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R

class VisitorsListAdapter(
    private val context: Context,
    private var visitorsList: ArrayList<VisitorsListModel>,
    var visitorType : String
):
    RecyclerView.Adapter<VisitorsListAdapter.MyViewHolder>() {
 private lateinit var visitorsListFragment: VisitorsListFragment
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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val visitorListItem = visitorsList[position]
        if(visitorListItem.visitorImage!= null){
            holder.visitorImage.setImageResource(visitorListItem.visitorImage)
        }
        if (!visitorListItem.entryTitle.isNullOrEmpty()){
            holder.visitorPurposeTitleTxt.text = visitorListItem.entryTitle
        }
        if (!visitorListItem.entryType.isNullOrEmpty()){
            holder.visitorPurposeByTxt.visibility = View.VISIBLE
            holder.visitorPurposeByTxt.text = visitorListItem.entryType
        }else{
            holder.visitorPurposeByTxt.visibility = View.GONE
        }

        if (!visitorListItem.entryCabNum.isNullOrEmpty()){
            holder.visitorVechileNoTxt.visibility = View.VISIBLE
            holder.visitorVechileNoTxt.text = "Cab Number: "+visitorListItem.entryCabNum
        }else{
            holder.visitorVechileNoTxt.visibility = View.GONE
        }

        if (!visitorListItem.entryPersonName.isNullOrEmpty()){
            holder.visitorNameTxt.visibility = View.VISIBLE
            if (visitorListItem.entryTitle == "Guest")
            holder.visitorNameTxt.text = "Guest Name: "+visitorListItem.entryPersonName
            else
                holder.visitorNameTxt.text = "Name: "+visitorListItem.entryPersonName
        }else{
            holder.visitorNameTxt.visibility = View.GONE
        }
        if (!visitorListItem.entryDate.isNullOrEmpty()){
            if(visitorType == "ExpectedVisitors"){
                holder.visitorDateTxt.visibility = View.GONE

            }else {
                holder.visitorDateTxt.visibility = View.VISIBLE
                holder.visitorDateTxt.text = visitorListItem.entryDate
            }
        }
        if (!visitorListItem.allowedBy.isNullOrEmpty()){
            holder.visitorAllowedByText.text = visitorListItem.allowedBy
        }
        if (!visitorListItem.inTime.isNullOrEmpty()){
            holder.visitorInTimingTxt.text = "In Time: "+visitorListItem.inTime
        }
        if (!visitorListItem.allowFor.isNullOrEmpty()){
            holder.allowForTxt.text = "Allow For: "+visitorListItem.allowFor
        }

        if ( visitorType == "ExpectedVisitors"){
            holder.ivEdit.visibility = View.VISIBLE
            holder.ivDelete.visibility = View.VISIBLE

        }else{
            holder.ivEdit.visibility = View.GONE
            holder.ivDelete.visibility = View.GONE
        }

        holder.ivDelete.setOnClickListener {
            visitorsListFragment.deleteVisitorItem(visitorListItem)
        }
    }

    fun setCallback(visitorsListFragment: VisitorsListFragment) {
       this.visitorsListFragment = visitorsListFragment
    }

    class MyViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
     val visitorImage = itemView.findViewById<ImageView>(R.id.visitor_img)
     val visitorPurposeTitleTxt = itemView.findViewById<TextView>(R.id.visitor_purpose_txt)
     val visitorPurposeByTxt =  itemView.findViewById<TextView>(R.id.visitor_purposeBy_txt)
     val visitorDateTxt = itemView.findViewById<TextView>(R.id.visitor_Timings_txt)
     val visitorAllowedByText = itemView.findViewById<TextView>(R.id.visitor_allowedBy_txt)
     val allowForTxt = itemView.findViewById<TextView>(R.id.visitor_allowFor_txt)
     val visitorInTimingTxt = itemView.findViewById<TextView>(R.id.visitor_InTime_txt)
     val visitorNameTxt = itemView.findViewById<TextView>(R.id.visitor_name_txt)
     val visitorVechileNoTxt = itemView.findViewById<TextView>(R.id.visitor_vechileNo_txt)
     val ivDelete = itemView.findViewById<ImageView>(R.id.ivDelete)
     val ivEdit = itemView.findViewById<ImageView>(R.id.ivEdit)
    }

}