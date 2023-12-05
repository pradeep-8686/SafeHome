package com.example.safehome.visitors

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.safehome.R
import com.example.safehome.visitors.cab.CabActivity
import com.example.safehome.visitors.delivery.DeliveryActivity
import com.example.safehome.visitors.guest.GuestActivity
import com.example.safehome.visitors.others.OthersActivity
import com.example.safehome.visitors.staff.StaffActivity

class InviteVisitorsAdapter(
    private val context: Context,
    private var visitorTypesList: ArrayList<VisitorsTypeDropdownModel.Data>
):
    RecyclerView.Adapter<InviteVisitorsAdapter.MyViewHolder>() {
   private lateinit var inviteVisitorsFragment: InviteVisitorsFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.invite_visitors_single_item, parent, false)
        return InviteVisitorsAdapter.MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (visitorTypesList.isNotEmpty()) {
            return visitorTypesList.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val inviteVisitorItem = visitorTypesList[position]
        if (!inviteVisitorItem.visitorTypeName.isNullOrEmpty()){
            holder.visitorType_txt.text = inviteVisitorItem.visitorTypeName
        }
        holder.itemLayout.setOnClickListener {
            inviteVisitorsFragment.itemCLick(inviteVisitorItem)
        }
        when (inviteVisitorItem.visitorTypeName) {
            "Guest" -> {
                Glide.with(context)
                    .load(R.drawable.visitor_guest)
                    .fitCenter()
                    .into(holder.iv_guest)
            }
            "Cab" -> {
                Glide.with(context)
                    .load(R.drawable.visitor_cab1)
                    .fitCenter()
                    .into(holder.iv_guest)
            }
            "Staff" -> {
                Glide.with(context)
                    .load(R.drawable.visitor_staff)
                    .fitCenter()
                    .into(holder.iv_guest)
            }
            "Delivery" -> {
                Glide.with(context)
                    .load(R.drawable.visitor_delivery1)
                    .fitCenter()
                    .into(holder.iv_guest)
            }
            "Other" -> {
                Glide.with(context)
                    .load(R.drawable.visitor_others)
                    .fitCenter()
                    .into(holder.iv_guest)
            }
        }
    }

    fun setCallback(inviteVisitorsFragment: InviteVisitorsFragment) {
        this.inviteVisitorsFragment = inviteVisitorsFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
     val visitorType_txt = itemView.findViewById<TextView>(R.id.tv_guest)
     val iv_guest = itemView.findViewById<ImageView>(R.id.iv_guest)
     val itemLayout = itemView.findViewById<ConstraintLayout>(R.id.cl_guest)
    }
}