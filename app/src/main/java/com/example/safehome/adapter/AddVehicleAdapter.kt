package com.example.safehome.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.fragments.VehiclesFragment
import com.example.safehome.model.Vehicle
import com.example.safehome.residentview.AddVehicleActivity

class AddVehicleAdapter(
    var context: Context,
    private var vehicleList: ArrayList<Vehicle>
) :
    RecyclerView.Adapter<AddVehicleAdapter.MyViewHolder>() {

    private lateinit var vehiclesFragment: VehiclesFragment

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.add_vehicle_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val vehicle = vehicleList[position]
        holder.firstName.tag = vehicle
        if (vehicle.type.equals("HadData")) {
            holder.added_vehicle_layout.visibility = View.VISIBLE
            holder.add_vehicle_layout.visibility = View.GONE
        } else {
            holder.added_vehicle_layout.visibility = View.GONE
            holder.add_vehicle_layout.visibility = View.VISIBLE
        }
        holder.vehicle_type.text = vehicle.vehicleType
        holder.vehicle_model.text = vehicle.vehicleModel
        holder.vehicle_number.text = vehicle.vehicleNumber

        holder.add_vehicle_layout.setOnClickListener {
            vehiclesFragment.selectAddMember(vehicleList[position])
        }

        holder.added_vehicle_layout.setOnClickListener {
            if (vehicle.type == "HadData") {
                vehiclesFragment.longPressMember(vehicleList[position])
            }
        }

        try {
            if (vehicle.vehicleType.toString().split("-")[0].equals("2")) {
                holder.vehicle_picture.setImageResource(R.drawable.bike_with_circle_new)
            } else {
                holder.vehicle_picture.setImageResource(R.drawable.add_vehicle_icon1)
            }
        }catch (ex: Exception){
            holder.vehicle_picture.setImageResource(R.drawable.add_vehicle_icon1)
        }

    }

    override fun getItemCount(): Int {
        if (vehicleList.isNotEmpty()) {
            return vehicleList.size
        }
        return 0
    }

    fun setCallback(vehiclesFragment: VehiclesFragment) {
        this.vehiclesFragment = vehiclesFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstName: TextView = itemView.findViewById(R.id.first_name)
        val vehicle_type: TextView = itemView.findViewById(R.id.vehicle_type)
        val vehicle_model: TextView = itemView.findViewById(R.id.vehicle_model)
        val vehicle_number: TextView = itemView.findViewById(R.id.vehicle_number)
        val add_vehicle_layout: LinearLayout = itemView.findViewById(R.id.add_vehicle_layout)
        val added_vehicle_layout: LinearLayout = itemView.findViewById(R.id.added_vehicle_layout)
         val vehicle_picture: ImageView = itemView.findViewById(R.id.vehicle_picture)
    }

}