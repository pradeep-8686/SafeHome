package com.example.safehome.forums

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R

class ForumCommentsAdapter
    (var context: Context, private var forumCommentsList : ArrayList<GetAllForumCommentsModel.Data>):
    RecyclerView.Adapter<ForumCommentsAdapter.ViewHolder>() {
    private lateinit var forumsListActivity: ForumsListActivity
    private lateinit var forumRepliesAdapter: ForumRepliesAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.forum_comments_single_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forumCommentItem = forumCommentsList[position]
        holder.repliesRecyclerView.visibility = View.GONE
        holder.forumReplyLayout.visibility = View.GONE

        if (!forumCommentItem.commentCreatedByName.isNullOrEmpty()){
            holder.commentedByUserTxt.text = forumCommentItem.commentCreatedByName+ ":"
        }
        if (!forumCommentItem.comment.isNullOrEmpty()){
            holder.commentTopic.text =  forumCommentItem.comment
        }
        holder.commentReplyText.setOnClickListener {
            holder.repliesRecyclerView.visibility = View.VISIBLE
            holder.forumReplyLayout.visibility = View.VISIBLE
            holder.repliesRecyclerView.layoutManager = LinearLayoutManager(context)
            forumRepliesAdapter = ForumRepliesAdapter(context, forumCommentItem.commentReplies)
            holder.repliesRecyclerView.adapter = forumRepliesAdapter
            forumRepliesAdapter.notifyDataSetChanged()
        }
    }
    override fun getItemCount(): Int {
        if (forumCommentsList.isNotEmpty()) {
            return forumCommentsList.size
        }
        return 0
    }

    fun setCallback(forumsListActivity: ForumsListActivity) {
        this.forumsListActivity = forumsListActivity
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       val commentedByUserTxt = itemView.findViewById<TextView>(R.id.forum_commented_user)
       val commentTopic = itemView.findViewById<TextView>(R.id.forum_comment_text)
       val commentReplyText = itemView.findViewById<TextView>(R.id.reply_comment_text)
       val repliesRecyclerView = itemView.findViewById<RecyclerView>(R.id.replies_recyclerview)
       val forumReplyLayout = itemView.findViewById<RelativeLayout>(R.id.forum_reply_layout)
    }
}