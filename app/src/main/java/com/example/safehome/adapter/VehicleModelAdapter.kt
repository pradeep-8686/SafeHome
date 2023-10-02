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
import com.example.safehome.fragments.VehiclesFragment
import com.example.safehome.model.VehicleModelResp
import com.example.safehome.residentview.MyFamilyActivity
import com.example.safehome.residentview.MyVehicleActivity

class VehicleModelAdapter(var context: Context, private var statesList: List<VehicleModelResp>) :
    RecyclerView.Adapter<VehicleModelAdapter.MyViewHolder>() {
    private lateinit var myVehicleActivity: MyVehicleActivity
    private lateinit var vehiclesFragment: VehiclesFragment

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
        val vehicleModelName: VehicleModelResp = statesList[position]
        holder.ageGroupTv.tag = vehicleModelName.vehicleModel
        if (vehicleModelName.vehicleModel.isNotEmpty()) {
            holder.ageGroupTv.text = vehicleModelName.vehicleModel
        }

        holder.ageGroupTv.setOnClickListener {
            if (context is MyVehicleActivity) {
                myVehicleActivity.selectVehicleModel(it.tag as String)
            } else {
                vehiclesFragment.selectVehicleModel(it.tag as String)
            }
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

    fun setFragmentCallback(vehiclesFragment: VehiclesFragment) {

        this.vehiclesFragment = vehiclesFragment

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
    }

}