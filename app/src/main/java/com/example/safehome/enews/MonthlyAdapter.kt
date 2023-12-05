package com.example.safehome.enews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R

class MonthlyAdapter(private var eNewsActivity: ENewsActivity, private  val monthList: ArrayList<String>) :
    RecyclerView.Adapter<MonthlyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.age_group_item_list, parent, false)
        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stateName: String = monthList[position]
        holder.ageGroupTv.tag = stateName
        if (stateName.isNotEmpty()) {
            holder.ageGroupTv.text = stateName
        }

        holder.ageGroupTv.setOnClickListener {
            eNewsActivity.setMonth(monthList[position])
        }
    }

    override fun getItemCount(): Int {
        if (monthList.isNotEmpty()) {
            return monthList.size
        }
        return 0
    }


    fun setCallbackType(eNewsActivity: ENewsActivity) {
     this.eNewsActivity = eNewsActivity
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
    }
}