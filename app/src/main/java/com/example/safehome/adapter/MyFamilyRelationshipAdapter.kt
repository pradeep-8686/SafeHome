package com.example.safehome.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.model.RelationshipTypesModel
import com.example.safehome.residentview.MyFamilyActivity

class MyFamilyRelationshipAdapter(
    private val context : MyFamilyActivity,
    private var realationshipTypesList: ArrayList<RelationshipTypesModel.Data>
) :  RecyclerView.Adapter<MyFamilyRelationshipAdapter.MyViewHolder>() {
    private lateinit var myFamilyActivity: MyFamilyActivity

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
        val relationTypeName = realationshipTypesList[position]
        holder.ageGroupTv.tag = relationTypeName
        if (!relationTypeName.relationShipName.isNullOrEmpty()) {
            holder.ageGroupTv.text = relationTypeName.relationShipName
        }

        holder.ageGroupTv.setOnClickListener {
            myFamilyActivity.setRelationType(relationTypeName)
        }
    }

    override fun getItemCount(): Int {
        if (realationshipTypesList.isNotEmpty()) {
            return realationshipTypesList.size
        }
        return 0
    }

    fun setCallback(myFamilyActivity: MyFamilyActivity) {
        this.myFamilyActivity = myFamilyActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
    }

}