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
import com.example.safehome.model.FamilyDetails
import com.example.safehome.residentview.AddMemeberActivity

class AllottedSlotsAdapter(
    var context: Context,
    private var familyList: ArrayList<FamilyDetails>
) :
    RecyclerView.Adapter<AllottedSlotsAdapter.MyViewHolder>() {
    private lateinit var addfamilyMemberActivity: AddMemeberActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.add_family_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val familyDetails: FamilyDetails = familyList[position]
        holder.firstName.tag = familyDetails

        if (familyDetails.type.equals("HadData")) {
            holder.added_member_layout.visibility = View.VISIBLE
            holder.addMember_layout.visibility = View.GONE
        } else {
            holder.added_member_layout.visibility = View.GONE
            holder.addMember_layout.visibility = View.VISIBLE
        }
        familyDetails.ageGroup.let {
            holder.member_type.text = it.replace("\"", "")
        }
        familyDetails.firstName.let {
            holder.member_name.text = it.replace("\"", "")
        }
        familyDetails.mobileNo.let {
            holder.member_mob_number.text = it.replace("\"", "")
        }

        holder.addMember_layout.setOnClickListener {
            addfamilyMemberActivity.selectAddMember(familyList[position])
        }

        holder.added_member_layout.setTag(familyList[position])
        holder.added_member_layout.setOnClickListener(View.OnClickListener {
                v -> addfamilyMemberActivity.longPressMember(v.tag as FamilyDetails)
        })

    }

    override fun getItemCount(): Int {
        if (familyList.isNotEmpty()) {
            return familyList.size
        }
        return 0
    }

    fun setCallback(addfamilyMemberActivity: AddMemeberActivity) {
        this.addfamilyMemberActivity = addfamilyMemberActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstName: TextView = itemView.findViewById(R.id.first_name)
        val member_type: TextView = itemView.findViewById(R.id.member_type)
        val member_name: TextView = itemView.findViewById(R.id.member_name)
        val member_mob_number: TextView = itemView.findViewById(R.id.member_mob_number)
        val addMember_layout: LinearLayout = itemView.findViewById(R.id.addMember_layout)
        val added_member_layout: LinearLayout = itemView.findViewById(R.id.added_member_layout)
    }

}