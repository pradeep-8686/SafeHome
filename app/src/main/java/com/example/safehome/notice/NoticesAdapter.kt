package com.example.safehome.notice

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.model.GetAllNoticeStatus
import com.example.safehome.model.Notice
import com.example.safehome.model.NoticesListModel

class NoticesAdapter(var context: Context, private var noticesList: ArrayList<Notice>) :
    RecyclerView.Adapter<NoticesAdapter.MyViewHolder>() {

    private lateinit var noticeActivity: NoticeActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.notices_single_item_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (noticesList.isNotEmpty()) {
            return noticesList.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val notices = noticesList[position]
        holder.noticesListItemLayout.tag = notices

        if (notices.postedBy != null && notices.postedBy.isNotEmpty()) {
            holder.noticePostByNameTxt.text = "By: " + notices.postedBy
        }
        if (notices.createdOn != null && notices.createdOn.isNotEmpty()) {
            holder.noticePostedTimeTxt.text = notices.createdOn
        }
        if (notices.noticeTypeName != null && notices.noticeTypeName.isNotEmpty()) {
            holder.noticeTypeTxt.text = notices.noticeTypeName
        }
        holder.notices_description_txt.text = notices.description
        holder.noticesListItemLayout.setOnClickListener {
            holder.noticePostedTimeTxt.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                null,
                null
            )
            noticeActivity.noticesItemClick(it.tag as Notice)
        }
        if (!notices.readStatus) {

            val yourDrawable: Drawable? =
                ContextCompat.getDrawable(context, R.drawable.green_circle)
            holder.noticePostedTimeTxt.setCompoundDrawablesWithIntrinsicBounds(
                yourDrawable,
                null,
                null,
                null
            )
        }else{

            holder.noticePostedTimeTxt.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                null,
                null
            )
        }


    }

    fun setCallback(noticeActivity: NoticeActivity) {
        this.noticeActivity = noticeActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noticePostByNameTxt: TextView = itemView.findViewById(R.id.notices_posted_nameTxt)
        val noticePostedTimeTxt: TextView = itemView.findViewById(R.id.notice_posted_time_txt)
        val noticeTypeTxt: TextView = itemView.findViewById(R.id.notice_type_txt)
        val noticesListItemLayout: LinearLayout =
            itemView.findViewById(R.id.notices_list_item_layout)
        val notices_description_txt: TextView = itemView.findViewById(R.id.notices_description_txt)
    }
}