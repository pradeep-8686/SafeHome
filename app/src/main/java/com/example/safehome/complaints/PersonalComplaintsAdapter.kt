package com.example.safehome.complaints

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.ImageAdapter
import com.example.safehome.facilitiesview.BookingsFragment
import com.example.safehome.model.Events
import com.example.safehome.model.MaintenanceHistoryModel
import com.example.safehome.model.PersonalComplaintsModel
import com.example.safehome.model.Upcoming

class PersonalComplaintsAdapter(
    var context: Context,
    private var personalComplaintsList : ArrayList<PersonalComplaintsModel>
) :
    RecyclerView.Adapter<PersonalComplaintsAdapter.MyViewHolder>() {
    private lateinit var personalFragment: PersonalFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.personal_complaint_item, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val complaint = personalComplaintsList[position]

        if (complaint.status != null) {
            holder.tv_status.text = complaint.status

            if(complaint.status == "Pending"){

                Glide.with(context)
                    .load(R.drawable.c_pending)
                    .fitCenter()
                    .into(holder.iv_status_image)

            }else if (complaint.status == "In-Progress"){
                Glide.with(context)
                    .load(R.drawable.c_in_progress)
                    .fitCenter()
                    .into(holder.iv_status_image)

            }else if (complaint.status == "Resolved"){

                Glide.with(context)
                    .load(R.drawable.c_completed)
                    .fitCenter()
                    .into(holder.iv_status_image)
            }else{
                Glide.with(context)
                    .load(R.drawable.c_re_initiate)
                    .fitCenter()
                    .into(holder.iv_status_image)
            }
        }
        if (complaint.complaintType != null) {

            holder.tv_complaintType.text = complaint.category
        }
        if (complaint.description != null ) {
            holder.tv_description.text = complaint.description
        }
        if (complaint.assignTo != null) {
            holder.tv_assigned_to.text = "To : ${complaint.assignTo}"
        }

        if (complaint.createdAt != null) {
            holder.tv_created_at.text = "To : ${complaint.createdAt}"
        }

        if (complaint.priority != null){
            holder.tvPriority.text = complaint.priority
            if (complaint.priority == "High"){
                val customColor = Color.parseColor("#FC2224")
                val customColorStateList = ColorStateList.valueOf(customColor)
                holder.tvPriority.backgroundTintList = customColorStateList
            }else if (complaint.priority == "Medium"){
                val customColor = Color.parseColor("#FFBB0E")
                val customColorStateList = ColorStateList.valueOf(customColor)
                holder.tvPriority.backgroundTintList = customColorStateList
            }else{
                val customColor = Color.parseColor("#3E991C")
                val customColorStateList = ColorStateList.valueOf(customColor)
                holder.tvPriority.backgroundTintList = customColorStateList
            }
        }

        if (complaint.attachPhoto.isNotEmpty()){
            holder.rvImagesList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false )
            val imageAdapter = ImageAdapter(context, complaint.attachPhoto)
            holder.rvImagesList.adapter = imageAdapter
        }

        holder.itemView.setOnClickListener {
            personalFragment.clickAction(complaint)
        }

    }

    override fun getItemCount(): Int {
        if (personalComplaintsList.isNotEmpty()) {
            return personalComplaintsList.size
        }
        return 0
    }

    fun setCallback(personalFragment: PersonalFragment) {
        this.personalFragment = personalFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_status_image: ImageView = itemView.findViewById(R.id.iv_status_image)
        val tv_status: TextView = itemView.findViewById(R.id.tv_status)
        val tv_complaintType: TextView = itemView.findViewById(R.id.tv_complaintType)
        val tv_description: TextView = itemView.findViewById(R.id.tv_description)
        val tv_assigned_to: TextView = itemView.findViewById(R.id.tv_assigned_to)
        val tv_created_at: TextView = itemView.findViewById(R.id.tv_created_at)
        val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)
        val rvImagesList: RecyclerView = itemView.findViewById(R.id.rvImagesList)
      //  val tv_priority: TextView = itemView.findViewById(R.id.tv_priority_header)

    }


    fun filterList(upcomingList: ArrayList<PersonalComplaintsModel>) {
        this.personalComplaintsList = upcomingList;
        notifyDataSetChanged();
    }


}