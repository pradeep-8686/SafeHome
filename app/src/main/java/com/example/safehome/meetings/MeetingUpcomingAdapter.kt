package com.example.safehome.meetings

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.model.MeetingUpcomingModel

class MeetingUpcomingAdapter(
    var context: Context,
    private var personalComplaintsList : ArrayList<MeetingUpcomingModel>
) :
    RecyclerView.Adapter<MeetingUpcomingAdapter.MyViewHolder>() {
    private lateinit var personalFragment: MeetingsUpcomingFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.meeting_upcoming_item, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val meeting = personalComplaintsList[position]
        holder.selectResponse.tag = meeting

        if (meeting.meetingName != null) {
            holder.tvMeetingName.text = meeting.meetingName
        }

         if (meeting.location != null) {
            holder.tvLocation.text = meeting.location
        }

         if (meeting.organisedBy != null) {
            holder.tvOrganisedBy.text = "Organised By : ${meeting.organisedBy}"
        }

         if (meeting.meetingDate != null) {
            holder.tvMeetingDate.text = "Meeting Date : ${meeting.meetingDate}"
        }

         if (meeting.time != null) {
            holder.tvTime.text = "Time : ${meeting.time}"
        }

        holder.btnViewAgenda.setOnClickListener {
            personalFragment.viewAgendaClickAction(meeting)
        }

        holder.selectResponse.setOnClickListener {
            personalFragment.selectResponse(it.tag as MeetingUpcomingModel)
        }

        holder.itemView.setOnClickListener {
//            personalFragment.clickAction(meeting)
        }

    }

    override fun getItemCount(): Int {
        if (personalComplaintsList.isNotEmpty()) {
            return personalComplaintsList.size
        }
        return 0
    }

    fun setCallback(personalFragment: MeetingsUpcomingFragment) {
        this.personalFragment = personalFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMeetingName: TextView = itemView.findViewById(R.id.tvMeetingName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvOrganisedBy: TextView = itemView.findViewById(R.id.tvOrganisedBy)
        val tvMeetingDate: TextView = itemView.findViewById(R.id.tvMeetingDate)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val btnViewAgenda: TextView = itemView.findViewById(R.id.btnViewAgenda)
        val selectResponse: TextView = itemView.findViewById(R.id.selectResponse)
    }


    fun filterList(upcomingList: ArrayList<MeetingUpcomingModel>) {
        this.personalComplaintsList = upcomingList;
        notifyDataSetChanged();
    }


}