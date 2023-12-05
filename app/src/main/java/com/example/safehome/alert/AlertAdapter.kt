package com.example.safehome.alert

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.work.done.utils.setResizableText

class AlertAdapter(
    var context: Context,
    private var alertResponseModel: ArrayList<AlertResponse.Data.Forum>,
    private val userId :  String
) :
    RecyclerView.Adapter<AlertAdapter.MyViewHolder>() {
    private lateinit var alertActivity: AlertActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.alert_list_item, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val alertItem = alertResponseModel[position]

        if (!alertItem.sentBy.isNullOrEmpty()) {
            if (alertItem.sentBy == "Resident" ){
                holder.tvRaisedBy.text = "By : ${alertItem.createdByName} Block " + alertItem.block + "-" + alertItem.flatNo
            }else {
                holder.tvRaisedBy.text = "By : ${alertItem.sentBy}"
            }
        }

        if (!alertItem.emergencyTypeName.isNullOrEmpty()) {
            holder.tvTopic.text = alertItem.emergencyTypeName
        }


        if (!alertItem.description.isNullOrEmpty()) {
            holder.tvForumDescription.text = "${alertItem.description}"
//            holder.tvForumDescription.setResizableText(holder.tvForumDescription.text.toString(),2,true)
        }

        holder.ivEdit.setOnClickListener {
            alertActivity.editAlert(alertItem)

        }

        holder.forumItemCard.setOnClickListener {
            val intent = Intent(context, AlertDetailsActivity::class.java)
            intent.putExtra("alertItem", alertItem)
            context.startActivity(intent)
        }

        holder.ivDelete.setOnClickListener {
            alertActivity.deleteAlert(alertItem)
        }

        Log.d("CreatorLoginId", "${alertItem.createdBy} == $userId")
        if (alertItem.createdBy == userId.toInt()){
            holder.ivEdit.visibility = View.VISIBLE
            holder.ivDelete.visibility = View.VISIBLE

        }else{
            holder.ivEdit.visibility = View.GONE
            holder.ivDelete.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        if (alertResponseModel.isNotEmpty()) {
            return alertResponseModel.size
        }
        return 0
    }

    fun setCallback(alertActivity: AlertActivity) {
        this.alertActivity = alertActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTopic: TextView = itemView.findViewById(R.id.tvTopic)
        val tvRaisedBy: TextView = itemView.findViewById(R.id.tvRaisedBy)
        val tvForumDescription: TextView = itemView.findViewById(R.id.tvForumDescription)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
        val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
        val forumItemCard: CardView = itemView.findViewById(R.id.forum_card_layout)

    }


}