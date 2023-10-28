package com.example.safehome.polls

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.model.GetPollResultModel

class PollsResultAdapter(
    var context: Context,
    private var pollResponseModel: ArrayList<GetPollResultModel.Data.Polldata>
) :
    RecyclerView.Adapter<PollsResultAdapter.MyViewHolder>() {
    private lateinit var pollsActivity : PollsActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.polls_result_item, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val meeting = pollResponseModel[position]

        if (meeting.optionName != null) {
            holder.tvPollOption.text = meeting.optionName
        }
        if (meeting.votePercentage != null) {
            holder.tvPollPercentage.text = "${meeting.votePercentage}%"
        }

        if (meeting.optionName.equals("Yes")){
            holder.clMain.background = context.getDrawable(R.drawable.ic_green_rec_full)
        }else{
            holder.clMain.background = context.getDrawable(R.drawable.ic_gray_rec_full)

        }

        holder.itemView.setOnClickListener {
       //     pollsActivity.clickAction(meeting)
        }

    }

    override fun getItemCount(): Int {
        if (pollResponseModel.isNotEmpty()) {
            return pollResponseModel.size
        }
        return 0
    }

    fun setCallback(pollsActivity: PollsActivity) {
        this.pollsActivity = pollsActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPollOption: TextView = itemView.findViewById(R.id.tvPollOption)
        val tvPollPercentage: TextView = itemView.findViewById(R.id.tvPollPercentage)
        val clMain: ConstraintLayout = itemView.findViewById(R.id.clMain)
    }


}