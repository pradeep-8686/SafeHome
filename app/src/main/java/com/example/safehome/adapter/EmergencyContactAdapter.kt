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
import com.example.safehome.model.EmergencyContact
import com.example.safehome.model.Residents

class EmergencyContactAdapter (var context: Context,
                               private var membersList: ArrayList<EmergencyContact>
) :
RecyclerView.Adapter<EmergencyContactAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.association_member_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val residents = membersList[position]
        if (residents.Profession != null && residents.Profession.isNotEmpty()) {
            holder.member_type.text = residents.Profession
        }
        if (residents.Name != null && residents.Name.isNotEmpty()) {
            holder.member_name.text = residents.Name
        }
        if (residents.Jobtype != null && residents.Jobtype.isNotEmpty()) {
            holder.member_id.text = residents.Jobtype
        }

    }

    override fun getItemCount(): Int {
        if (membersList.isNotEmpty()) {
            return membersList.size
        }
        return 0
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val member_type: TextView = itemView.findViewById(R.id.member_type)
        val member_name: TextView = itemView.findViewById(R.id.member_name)
        val member_id: TextView = itemView.findViewById(R.id.member_id)

    }
}