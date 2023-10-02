package com.example.safehome.adapter

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
import com.example.safehome.model.Tenant
import com.example.safehome.residentview.AddTenantActivity

class AddTenantAdapter(
    var context: Context,
    private var tenantsList: ArrayList<Tenant>
) :
    RecyclerView.Adapter<AddTenantAdapter.MyViewHolder>() {
    private lateinit var addTenantActivity: AddTenantActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.add_tenant_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val tenant: Tenant = tenantsList[position]
        holder.tenant_name.tag = tenant

        if (tenant.type.equals("HadData")) {
            holder.added_tenant_layout.visibility = View.VISIBLE
            holder.add_tenant_layout.visibility = View.GONE
        } else {
            holder.added_tenant_layout.visibility = View.GONE
            holder.add_tenant_layout.visibility = View.VISIBLE
        }
        var block_flat: String
        try{
            block_flat = tenant.block.toString() + "-" + tenant.flatNo.toString()
            block_flat.replace("Block","")
        }catch (ex: Exception){
            block_flat ="Block"
        }

        holder.tenat_id.text = block_flat
        holder.tenant_name.text =
            tenant.firstName.replace("\"", "") +" "+ tenant.lastName.replace("\"", "")
        holder.tenant_mob_number.text = tenant.mobileNo

        holder.add_tenant_layout.setOnClickListener {
            addTenantActivity.selectAddMember(tenantsList[position])
        }
        // here  long press
        holder.added_tenant_layout.setOnClickListener {
            if (tenant.type == "HadData") {
                addTenantActivity.longPressMember(tenantsList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        if (tenantsList.isNotEmpty()) {
            return tenantsList.size
        }
        return 0
    }

    fun setCallback(addTenantActivity: AddTenantActivity) {
        this.addTenantActivity = addTenantActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val first_name: TextView = itemView.findViewById(R.id.first_name)
        val tenat_id: TextView = itemView.findViewById(R.id.tenant_id)
        val tenant_name: TextView = itemView.findViewById(R.id.tenant_name)
        val tenant_mob_number: TextView = itemView.findViewById(R.id.tenant_mob_number)
        val add_tenant_layout: LinearLayout = itemView.findViewById(R.id.add_tenant_layout)
        val added_tenant_layout: LinearLayout = itemView.findViewById(R.id.added_tenant_layout)
    }

}