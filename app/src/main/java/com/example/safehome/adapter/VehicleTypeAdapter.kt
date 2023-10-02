package com.example.safehome.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.residentview.MyFamilyActivity
import com.example.safehome.residentview.MyVehicleActivity

class VehicleTypeAdapter(var context: Context, private var statesList: List<String>) :
    RecyclerView.Adapter<VehicleTypeAdapter.MyViewHolder>() {
    private lateinit var myVehicleActivity: MyVehicleActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.age_group_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       val stateName:String = statesList[position]
        holder.ageGroupTv.tag=stateName
        if (stateName.isNotEmpty()) {
            holder.ageGroupTv.text = stateName
        }

        holder.ageGroupTv.setOnClickListener {
            myVehicleActivity.selectVehicleType(it.tag as String)
        }
    }

    override fun getItemCount(): Int {
        if (statesList.isNotEmpty()) {
           return statesList.size
        }
        return 0
    }

    fun setCallback(myVehicleActivity: MyVehicleActivity) {
        this.myVehicleActivity = myVehicleActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
    }

}