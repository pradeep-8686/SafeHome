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
import com.example.safehome.model.Flat
import com.example.safehome.model.Tenant
import com.example.safehome.residentview.AddFlatActivity
import com.example.safehome.residentview.AddTenantActivity

class AddFlatAdapter(
    var context: Context,
    private var flatList: ArrayList<Flat>
) :
    RecyclerView.Adapter<AddFlatAdapter.MyViewHolder>() {
    private lateinit var addFlatActivity: AddFlatActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.add_flat_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val tenant: Flat = flatList[position]
        holder.flat_number.tag = tenant

        if (tenant.type.equals("HadData")) {
            holder.added_Flat_layout.visibility = View.VISIBLE
            holder.addflat_layout.visibility = View.GONE
        } else {
            holder.added_Flat_layout.visibility = View.GONE
            holder.addflat_layout.visibility = View.VISIBLE
        }
        holder.Name_txt.text = tenant.tenantsId.toString()
        holder.flat_number.text =
            tenant.firstName.replace("\"", "") +" "+ tenant.lastName.replace("\"", "")

       // new flat
        holder.addflat_layout.setOnClickListener {
            addFlatActivity.selectAddFlat(flatList[position])
        }
        // already added flat
        holder.added_Flat_layout.setOnClickListener {
            if (tenant.type == "HadData") {
                addFlatActivity.createNewFlatDetails(flatList[position])
            }

        }
    }

    override fun getItemCount(): Int {
        if (flatList.isNotEmpty()) {
            return flatList.size
        }
        return 0
    }

    fun setCallback(addFlatActivity: AddFlatActivity) {
        this.addFlatActivity = addFlatActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Name_txt: TextView = itemView.findViewById(R.id.Name_txt)
        val flat_number: TextView = itemView.findViewById(R.id.flat_number)
        val addflat_layout: LinearLayout = itemView.findViewById(R.id.addflat_layout)
        val added_Flat_layout: LinearLayout = itemView.findViewById(R.id.added_Flat_layout)
    }

}