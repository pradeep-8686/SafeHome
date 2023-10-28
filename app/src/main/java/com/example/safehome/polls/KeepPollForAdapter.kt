package com.example.safehome.polls

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.complaints.RaiseComplaintActivity
import com.example.safehome.forums.RaiseForumActivity
import com.example.safehome.model.PollsKeepDropdownModel

class KeepPollForAdapter(var context: Context, private var statesList: ArrayList<PollsKeepDropdownModel.Data>) :
    RecyclerView.Adapter<KeepPollForAdapter.MyViewHolder>() {
    private lateinit var raiseComplaintActivity: RaiseComplaintActivity
    private lateinit var raisePollActivity:RaisePollActivity
    private lateinit var updatePollActivity:UpdatePollActivity
    private lateinit var raiseForumActivity:RaiseForumActivity


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.keep_poll_for_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stateName: String = statesList[position].keepFor
        holder.ageGroupTv.tag = stateName
        if (stateName.isNotEmpty()) {
            holder.ageGroupTv.text = stateName
        }

        holder.ageGroupTv.setOnClickListener {
            if( context is RaiseComplaintActivity) {
                raiseComplaintActivity.setCallbackComplaintType(it.tag as String)
            } else if (context is RaisePollActivity){
                raisePollActivity.setCallbackComplaintType(statesList[position])
            }else if (context is RaiseForumActivity){
                raiseForumActivity.setCallbackComplaintType(statesList[position])
            }else{
                updatePollActivity.setCallbackComplaintType(it.tag as String)

            }
        }
    }

    override fun getItemCount(): Int {
        if (statesList.isNotEmpty()) {
            return statesList.size
        }
        return 0
    }

    fun setCallbackComplaintType(raiseComplaintActivity: RaiseComplaintActivity) {
        this.raiseComplaintActivity = raiseComplaintActivity
    }

    fun setCallbackComplaintType(raisePollActivity: RaisePollActivity) {
        this.raisePollActivity = raisePollActivity
    }

    fun setCallbackComplaintType(updatePollActivity: UpdatePollActivity) {
        this.updatePollActivity = updatePollActivity
    }
    fun setCallbackForum(raiseForumActivity: RaiseForumActivity) {
        this.raiseForumActivity = raiseForumActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
    }

}