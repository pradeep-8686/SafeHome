package com.example.safehome.meetings

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.model.UpcomingMeetingsModel

class MeetingCompletedAdapter(
    var context: Context,
    private var completedMeetingsList: ArrayList<UpcomingMeetingsModel.Data.MeetingData>
) :
    RecyclerView.Adapter<MeetingCompletedAdapter.MyViewHolder>() {
    private lateinit var communityFragment: MeetingsCompletedFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.meeting_completed_item, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val meeting = completedMeetingsList[position]

        if (meeting.topicName != null) {

            holder.tvMeetingName.text = meeting.topicName
        }
        if (meeting.facilityName != null) {

            holder.tvLocation.text = meeting.facilityName
        }

        if (meeting.organisedBy != null) {

            holder.tvOrganisedBy.text = "Organised By : ${meeting.organisedBy}"
        }

        if (meeting.meetingDate != null) {

            holder.tvMeetingDate.text =
                "Meeting Date : ${Utils.formatDateMonthYear(meeting.meetingDate)}"

            if (meeting.meetingDate!!.contains("T")) {

                meeting.meetingDate = "${Utils.formatDateMonthYear(meeting.meetingDate)}"
            }

        }
        if (meeting.startTime != null && meeting.endTime != null) {
            holder.tvTime.text = "Time : ${meeting.startTime} - ${meeting.endTime}"
        }


        if (meeting.attendResponseStatus == "No") {
            holder.meetingAttended.text = "Not Attended"
            Glide.with(context)
                .load(R.drawable.not_attendance)
                .fitCenter()
                .into(holder.meetingAttendedImage)


        } else {
            holder.meetingAttended.text = "Attended"
            Glide.with(context)
                .load(R.drawable.attendance)
                .fitCenter()
                .into(holder.meetingAttendedImage)

        }

        holder.btnMeetingMinutes.setOnClickListener {
            communityFragment.viewAgendaClickAction(meeting)
        }


        holder.itemView.setOnClickListener {
            communityFragment.clickAction(meeting)
        }

    }

    override fun getItemCount(): Int {
        if (completedMeetingsList.isNotEmpty()) {
            return completedMeetingsList.size
        }
        return 0
    }

    fun setCallback(communityFragment: MeetingsCompletedFragment) {
        this.communityFragment = communityFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMeetingName: TextView = itemView.findViewById(R.id.tvMeetingName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvOrganisedBy: TextView = itemView.findViewById(R.id.tvOrganisedBy)
        val tvMeetingDate: TextView = itemView.findViewById(R.id.tvMeetingDate)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val btnMeetingMinutes: TextView = itemView.findViewById(R.id.btnMeetingMinutes)
        val meetingAttendedImage: ImageView = itemView.findViewById(R.id.meetingAttendedImage)
        val meetingAttended: TextView = itemView.findViewById(R.id.meetingAttended)

    }


    fun filterList(upcomingList: ArrayList<UpcomingMeetingsModel.Data.MeetingData>) {
        this.completedMeetingsList = upcomingList;
        notifyDataSetChanged();
    }


}