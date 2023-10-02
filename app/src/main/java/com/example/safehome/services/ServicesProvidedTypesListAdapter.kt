package com.example.safehome.services

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.activity.PersonalInfoActivity
import com.example.safehome.model.GetAllHistoryServiceTypes
import com.example.safehome.model.ServiceProvidedTypesList

class ServicesProvidedTypesListAdapter(
    var context: Context,
    private var servicesTypesList: List<ServiceProvidedTypesList.Data>
) :
    RecyclerView.Adapter<ServicesProvidedTypesListAdapter.MyViewHolder>() {
    private lateinit var serviceSelectedMemberInfoActivity: ServiceSelectedMemberInfoActivity
    private lateinit var servicesMemberListActivity: ServicesMemberListActivity
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServicesProvidedTypesListAdapter.MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.age_group_item_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val serviceTypes = servicesTypesList[position]
        holder.ageGroupTv.tag = serviceTypes
        if (servicesTypesList.isNotEmpty()) {
            holder.ageGroupTv.text = serviceTypes.serviceProvideName
        }

        holder.ageGroupTv.setOnClickListener {

            if (context is ServiceSelectedMemberInfoActivity) {
                serviceSelectedMemberInfoActivity.setServiceProvidedType(it.tag as ServiceProvidedTypesList.Data)

            }else{
                servicesMemberListActivity.setCallbackServiceTypeService(it.tag as ServiceProvidedTypesList.Data)

            }

        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)

    }

    override fun getItemCount(): Int {
        if (servicesTypesList.isNotEmpty()) {
            return servicesTypesList.size
        }
        return 0
    }


    fun setCallbackServiceType(servicesPaymentHistoryFragment: ServiceSelectedMemberInfoActivity) {
        this.serviceSelectedMemberInfoActivity = servicesPaymentHistoryFragment
    }


    fun setCallbackServiceTypeService(servicesMemberListActivity: ServicesMemberListActivity) {
        this.servicesMemberListActivity = servicesMemberListActivity
    }


}