package com.example.safehome.polls

import android.content.Context
import android.os.Build
import android.util.Log
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
import com.example.safehome.model.GetAllPollDetailsModel

class PollsAdapter(
    var context: Context,
    private var pollResponseModel: ArrayList<GetAllPollDetailsModel.Data.Poll>,
    private val userId :  String
) :
    RecyclerView.Adapter<PollsAdapter.MyViewHolder>() {
    private lateinit var pollsActivity : PollsActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.polls_item, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pollItem = pollResponseModel[position]


        if (!pollItem.postedBy.isNullOrEmpty()) {
            holder.tvRaisedBy.text = "By : ${pollItem.postedBy}"
        }

         if (!pollItem.timeStamp.isNullOrEmpty()) {
            holder.tvCreatedAt.text = pollItem.timeStamp
        }

         if (pollItem.attachPhoto != null) {
             holder.ivPollImage.visibility = View.VISIBLE
             holder.tvPollResult.visibility = View.VISIBLE

             Glide.with(context)
                 .load( pollItem.attachPhoto)
                 .fitCenter()
                 .into(holder.ivPollImage)

         }else{

//             holder.tvPollResult.visibility = View.GONE
     //        holder.ivPollImage.visibility = View.GONE
         }

         if (!pollItem.question.isNullOrEmpty()) {
            holder.tvPollTitle.text = "${pollItem.question}"
        }

        var totalVoteCount = 0
        for (i in 0 until pollItem.pollOptions.size) {
          val voteCount = pollItem.pollOptions[i].voteCount
          totalVoteCount+= voteCount
        }
        holder.tvNoOfVotes.text = "$totalVoteCount Votes"

        if (pollItem.resultTobePublic){
            holder.tvPollResult.visibility = View.VISIBLE
        }else{
            holder.tvPollResult.visibility = View.GONE

        }
        /* if (!pollItem.pollOptions.component1().voteCount.toString().isNullOrEmpty()) {
            holder.tvNoOfVotes.text = "${pollItem.pollOptions.component1().voteCount} Votes"
        }*/

        /* if (!pollItem.flatNo.isNullOrEmpty()) {
             holder.tvFlatNo.visibility = View.VISIBLE
             holder.ivEdit.visibility = View.VISIBLE
             holder.ivDelete.visibility = View.VISIBLE

            holder.tvFlatNo.text = pollItem.flatNo
        }else{

             holder.tvFlatNo.visibility = View.GONE
             holder.ivEdit.visibility = View.GONE
             holder.ivDelete.visibility = View.GONE
         }*/

        if (pollItem.pollOptions.isNotEmpty()){
            holder.optionRecyclerView.visibility = View.VISIBLE
            holder.optionRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val pollsOptionAdapter =
                PollsOptionAdapter(context, pollItem.pollOptions)
            holder.optionRecyclerView.adapter = pollsOptionAdapter
            pollsOptionAdapter.setCallback(pollsActivity)
            pollsOptionAdapter.notifyDataSetChanged()
        }else{
            holder.optionRecyclerView.visibility = View.GONE
        }

        holder.tvPollResult.setOnClickListener {
            pollsActivity.viewPollResult(pollItem.pollId)
        }

        holder.ivEdit.setOnClickListener {
            pollsActivity.editPoll(pollItem)

        }

        holder.ivDelete.setOnClickListener {
            pollsActivity.deletePoll(pollItem)
        }

        Log.d("CreatorLoginId", "${pollItem.createdBy} == $userId")
        if (pollItem.createdBy == userId.toInt()){
            holder.ivEdit.visibility = View.VISIBLE
            holder.ivDelete.visibility = View.VISIBLE

        }else{
            holder.ivEdit.visibility = View.GONE
            holder.ivDelete.visibility = View.GONE
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
        val tvRaisedBy: TextView = itemView.findViewById(R.id.tvRaisedBy)
        val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
        val ivPollImage: ImageView = itemView.findViewById(R.id.ivPollImage)
        val tvPollTitle: TextView = itemView.findViewById(R.id.tvPollTitle)
        val tvNoOfVotes: TextView = itemView.findViewById(R.id.tvNoOfVotes)
        val tvPollResult: TextView = itemView.findViewById(R.id.tvPollResult)
        val optionRecyclerView: RecyclerView = itemView.findViewById(R.id.optionRecyclerView)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
        val tvFlatNo: TextView = itemView.findViewById(R.id.tvFlatNo)
    }


}