package com.example.safehome.dailyhelp

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.model.DailyHelpRoles
import com.example.safehome.model.FaciBookings

class DailyHelpListAdapter(
    var context: Context,
    private var dailyHelpList: ArrayList<DailyHelpRoles.Data>
) :
    RecyclerView.Adapter<DailyHelpListAdapter.MyViewHolder>() {
    private lateinit var dailyHelpListFragment: DailyHelpListFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.daily_help_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dailyHelp = dailyHelpList[position]
        holder.daily_help_list_layout.tag = dailyHelp

        if (dailyHelp.staffTypeName != null && dailyHelp.staffTypeName.isNotEmpty()) {
            holder.member_designation.text = dailyHelp.staffTypeName
        }
        if (dailyHelp.staffCount != null && dailyHelp.staffCount.toString().isNotEmpty()) {
            holder.count.text = dailyHelp.staffCount.toString()
        }

        holder.daily_help_list_layout.setOnClickListener {
            dailyHelpListFragment.selectedRole(it.tag as DailyHelpRoles.Data)
        }

    }

    override fun getItemCount(): Int {
        if (dailyHelpList.isNotEmpty()) {
            return dailyHelpList.size
        }
        return 0
    }

    fun setCallback(dailyHelpListFragment: DailyHelpListFragment) {
        this.dailyHelpListFragment = dailyHelpListFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val member_designation: TextView = itemView.findViewById(R.id.member_designation)
        val count: TextView = itemView.findViewById(R.id.count)
        val daily_help_list_layout: LinearLayout = itemView.findViewById(R.id.daily_help_list_layout)
    }

    fun filterList( dailyHelpList: ArrayList<DailyHelpRoles.Data>) {
        this.dailyHelpList = dailyHelpList;
        notifyDataSetChanged();
    }


}