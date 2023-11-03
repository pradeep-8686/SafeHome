package com.example.safehome.forums

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
import com.bumptech.glide.Glide
import com.example.safehome.R
import com.work.done.utils.setResizableText

class ForumAdapter(
    var context: Context,
    private var forumResponseModel: ArrayList<GetAllForumDetailsModel.Data.Forum>,
    private val userId :  String
) :
    RecyclerView.Adapter<ForumAdapter.MyViewHolder>() {
    private lateinit var forumsListActivity: ForumsListActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.forum_list_item, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val forumItem = forumResponseModel[position]

        if (!forumItem.postedBy.isNullOrEmpty()) {
            if (forumItem.postedBy == "Resident"){
                holder.tvRaisedBy.text = "By : ${forumItem.postedBy} Block " + forumItem.block + "-" + forumItem.flatNo
            }else {
                holder.tvRaisedBy.text = "By : ${forumItem.postedBy}"
            }
        }

        if (!forumItem.topic.isNullOrEmpty()) {
            holder.tvTopic.text = forumItem.topic
        }

        if (forumItem.attachment != null) {
            holder.ivForumImage.visibility = View.VISIBLE

            Glide.with(context)
                .load( forumItem.attachment)
                .fitCenter()
                .into(holder.ivForumImage)

        }else{

            holder.ivForumImage.visibility = View.GONE
        }

        if (!forumItem.description.isNullOrEmpty()) {
            holder.tvForumDescription.text = "${forumItem.description}"
            holder.tvForumDescription.setResizableText(holder.tvForumDescription.text.toString(),2,true)
        }

        if (!forumItem.viewCount.toString().isNullOrEmpty()) {
            holder.tvNoOfViews.text = "${forumItem.viewCount}"
        }

        if (!forumItem.commentCount.toString().isNullOrEmpty()) {
            holder.tvForumComment.text = "${forumItem.commentCount}"
        }


        holder.tvForumComment.setOnClickListener {
            //       forumsListActivity.viewComment(it,forumItem)
        }

        holder.ivEdit.setOnClickListener {
            forumsListActivity.editPoll(forumItem)

        }

        holder.forumItemCard.setOnClickListener {
            val intent = Intent(context, ForumsListDetailActivity::class.java)
            intent.putExtra("forumItem", forumItem)
            context.startActivity(intent)
        }

        holder.ivDelete.setOnClickListener {
            forumsListActivity.deletePoll(forumItem)
        }

        Log.d("CreatorLoginId", "${forumItem.createdBy} == $userId")
        if (forumItem.createdBy == userId.toInt()){
            holder.ivEdit.visibility = View.VISIBLE
            holder.ivDelete.visibility = View.VISIBLE

        }else{
            holder.ivEdit.visibility = View.GONE
            holder.ivDelete.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        if (forumResponseModel.isNotEmpty()) {
            return forumResponseModel.size
        }
        return 0
    }

    fun setCallback(forumsListActivity: ForumsListActivity) {
        this.forumsListActivity = forumsListActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTopic: TextView = itemView.findViewById(R.id.tvTopic)
        val tvRaisedBy: TextView = itemView.findViewById(R.id.tvRaisedBy)
        val ivForumImage: ImageView = itemView.findViewById(R.id.ivForumImage)
        val tvForumDescription: TextView = itemView.findViewById(R.id.tvForumDescription)
        val tvNoOfViews: TextView = itemView.findViewById(R.id.tvNoOfViews)
        val tvForumComment: TextView = itemView.findViewById(R.id.tvForumComment)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
        val forumItemCard: CardView = itemView.findViewById(R.id.forum_card_layout)
    }


}