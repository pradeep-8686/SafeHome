package com.example.safehome.forums

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R

class ForumRepliesAdapter(var context: Context, private var forumCommentsList: List<GetAllForumCommentsModel.Data.CommentReply>):
    RecyclerView.Adapter<ForumRepliesAdapter.ViewHolder>() {
    private lateinit var forumsListActivity: ForumsListActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.forum_replies_single_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forumCommentItem = forumCommentsList[position]
        if (!forumCommentItem.replyCreatedByName.isNullOrEmpty()){
            holder.commentedByUserTxt.text = forumCommentItem.replyCreatedByName+ ":"
        }
        if (!forumCommentItem.message.isNullOrEmpty()){
            holder.commentTopic.text =  forumCommentItem.message
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
    }
}