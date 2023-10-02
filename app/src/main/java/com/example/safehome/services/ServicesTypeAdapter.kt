package com.example.safehome.services

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.model.GetAllHistoryServiceTypes

class ServicesTypeAdapter(var context: Context, private var servicesTypesList: List<GetAllHistoryServiceTypes.Data>) :
    RecyclerView.Adapter<ServicesTypeAdapter.MyViewHolder>() {
    private lateinit var servicesPaymentHistoryFragment: ServicesPaymentHistoryFragment

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
        val serviceTypes = servicesTypesList[position]
        holder.ageGroupTv.tag = serviceTypes
        if (servicesTypesList.isNotEmpty()) {
            holder.ageGroupTv.text = serviceTypes.serviceTypeName
        }

        holder.ageGroupTv.setOnClickListener {

                servicesPaymentHistoryFragment.setServiceType(it.tag as GetAllHistoryServiceTypes.Data)

        }
    }

    override fun getItemCount(): Int {
        if (servicesTypesList.isNotEmpty()) {
            return servicesTypesList.size
        }
        return 0
    }


    fun setCallbackServiceType(servicesPaymentHistoryFragment: ServicesPaymentHistoryFragment) {
        this.servicesPaymentHistoryFragment = servicesPaymentHistoryFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
    }

}